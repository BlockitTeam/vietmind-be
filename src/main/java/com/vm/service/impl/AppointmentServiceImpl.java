package com.vm.service.impl;

import com.vm.dto.AppointmentEventDTO;
import com.vm.dto.UserDoctorDTO;
import com.vm.enums.AppointmentStatus;
import com.vm.model.Appointment;
import com.vm.model.Conversation;
import com.vm.model.User;
import com.vm.repo.AppointmentRepository;
import com.vm.repo.UserRepository;
import com.vm.service.*;
import com.vm.util.KeyManagement;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    private ConversationService conversationService;

    private UserRepository userRepo;

    private ModelMapper modelMapper;

    private JobSchedulerService jobSchedulerService;

    private PushNotificationService pushNotificationService;

    private UserService userService;

    private final EmailService emailService;

    @Override
    public UserDoctorDTO createAppointment(Appointment appointment) throws Exception {
        Conversation conversation = conversationService.getConversationByUserIdAndDoctorId(appointment.getUserId(), appointment.getDoctorId());
        Integer conversationId;

        if (conversation == null) {
            conversation = new Conversation();
            conversation.setUserId(appointment.getUserId());
            conversation.setDoctorId(appointment.getDoctorId());

            // Generate AES session key and stored
            SecretKey conversationKey = KeyManagement.generateAESKey();
            // Load the pre-initialized AES key from KeyManagement
            SecretKey preInitializedAESKey = KeyManagement.loadKey();

            // Encrypt the conversationKey with the pre-initialized AES key
            String encryptedConversationKey = KeyManagement.encryptWithAES(preInitializedAESKey, Base64.getEncoder().encodeToString(conversationKey.getEncoded()));
            conversation.setEncryptedConversationKey(encryptedConversationKey);
            conversation.setConversationKey(Base64.getEncoder().encodeToString(conversationKey.getEncoded()));

            Conversation newConversation = conversationService.saveConversation(conversation);
            conversationId = newConversation.getConversationId();
        } else {
            conversationId = conversation.getConversationId();
            //conversation already have Appointment
            Appointment originAppointment = appointmentRepository.findByConversationId(conversationId);
            if (originAppointment != null)
                throw new Exception("Error when create Appointment, this conversation " + conversationId + " already have Appointment");
        }
        appointment.setConversationId(conversationId);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Schedule reminder jobs for the new appointment
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime());
        jobSchedulerService.scheduleAppointmentReminderJobs(savedAppointment.getAppointmentId().toString(), appointmentDateTime);
        User userDetails = userService.getUserById(appointment.getUserId());

        // Todo: Test fornow
//        emailService.sendAppointmentReminderEmail(userDetails, appointment, 0);
        pushNotificationService.sendAppointmentReminderNotification(userDetails, appointment, 0);


        User user = userRepo.findById(UUID.fromString(appointment.getDoctorId()))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return modelMapper.map(user, UserDoctorDTO.class);
    }

    @Override
    public Object getAppointmentByConversationId(Integer id) {
        String userId = conversationService.getConversationById(id).get().getUserId();
        LocalDateTime now = LocalDateTime.now(); // Lấy thời gian hiện tại
        LocalDate currentDate = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        // Lấy danh sách cuộc hẹn lớn hơn ngày hiện tại
        List<Appointment> futureAppointmentsByDate =
                appointmentRepository.findByUserIdAndAppointmentDateGreaterThanOrderByAppointmentIdDesc(userId, currentDate);

        // Lấy danh sách cuộc hẹn trong ngày nhưng có thời gian lớn hơn hiện tại
        List<Appointment> futureAppointmentsByTime =
                appointmentRepository.findByUserIdAndAppointmentDateEqualsAndStartTimeGreaterThanOrderByAppointmentIdDesc(userId, currentDate, currentTime);

        // Kết hợp hai danh sách
        List<Appointment> allFutureAppointments = new ArrayList<>();
        allFutureAppointments.addAll(futureAppointmentsByDate);
        allFutureAppointments.addAll(futureAppointmentsByTime);

        // Lấy appointment có id lớn nhất
        if (!allFutureAppointments.isEmpty())
            return allFutureAppointments.stream().max(Comparator.comparing(Appointment::getAppointmentId)).get();

        return "No appointments found for the current user.";
    }

    @Override
    public Optional<Appointment> getAppointmentByUserId(String userId) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findTopByUserIdOrderByAppointmentIdDesc(userId);
        appointmentOptional.ifPresent(appointment -> {
            LocalDateTime now = LocalDateTime.now();

            // Chuyển đổi thành LocalDateTime để so sánh
            LocalDateTime appointmentStart = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime());
            LocalDateTime appointmentEnd = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getEndTime());

            // Cập nhật trạng thái dựa trên thời gian hiện tại
            if (now.isAfter(appointmentEnd)) {
                appointment.setStatus(AppointmentStatus.FINISH);
            } else if (now.isAfter(appointmentStart) && now.isBefore(appointmentEnd)) {
                appointment.setStatus(AppointmentStatus.IN_PROGRESS);
            }

            // Lưu thay đổi vào database nếu trạng thái được cập nhật
            appointmentRepository.save(appointment);
        });

        return appointmentOptional;
    }

    @Override
    @Transactional
    public void deleteAppointmentsByUserId(String userId) {
        try {
            appointmentRepository.deleteByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete appointments");
        }
    }

    public List<AppointmentEventDTO> getAppointmentsByDoctorId(String doctorId) {
        List<Appointment> appointments = appointmentRepository.findAllByDoctorIdAndFutureAppointments(doctorId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        return appointments.stream()
                .map(appointment -> new AppointmentEventDTO(
                        String.valueOf(appointment.getAppointmentId()),
                        appointment.getContent(),
                        formatDateTime(appointment.getAppointmentDate(), appointment.getStartTime(), formatter),
                        formatDateTime(appointment.getAppointmentDate(), appointment.getEndTime(), formatter)
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Appointment> getFinishedAppointmentsByUserId(String userId) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Lấy tất cả các cuộc hẹn FINISH hoặc đã qua thời gian hiện tại
        List<Appointment> appointments = appointmentRepository.findAllFinishedOrPastAppointments(
                AppointmentStatus.FINISH, userId, currentDate, currentTime
        );

        // Cập nhật trạng thái tất cả các cuộc hẹn thành FINISH nếu chưa phải
        appointments.forEach(appointment -> {
            if (appointment.getStatus() != AppointmentStatus.FINISH) {
                appointment.setStatus(AppointmentStatus.FINISH);
                appointmentRepository.save(appointment); // Lưu thay đổi
            }
        });
        return appointments;
    }

    @Override
    @Transactional
    public Optional<Appointment>  getCurrentAppointmentByUserId(String userId) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Query lấy cuộc hẹn đang diễn ra
        Optional<Appointment> appointmentOpt = appointmentRepository.findCurrentAppointment(userId, currentDate, currentTime);
        if (!appointmentOpt.isPresent()) {
            //Query lấy cuộc hẹn gần nhất đã finish
            appointmentOpt = appointmentRepository.findLatestCompletedAppointment(userId, currentDate, currentTime);
            appointmentOpt.ifPresent(appointment -> {
                //Change status finish luôn
                appointment.setStatus(AppointmentStatus.FINISH);
                appointmentRepository.save(appointment);
            });
        }

        appointmentOpt.ifPresent(appointment -> {
            if (appointment.getAppointmentDate().isEqual(currentDate) &&
                    !currentTime.isBefore(appointment.getStartTime()) &&
                    !currentTime.isAfter(appointment.getEndTime())) {
                // Nếu cuộc hẹn đang diễn ra, cập nhật trạng thái thành IN_PROGRESS
                appointment.setStatus(AppointmentStatus.IN_PROGRESS);
                appointmentRepository.save(appointment);
            }
        });
        return appointmentOpt;
    }

    @Override
    public Optional<Appointment> getFutureAppointmentByUserId(String userId) {
        LocalDateTime now = LocalDateTime.now(); // Lấy thời gian hiện tại
        LocalDate currentDate = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        // Lấy danh sách cuộc hẹn lớn hơn ngày hiện tại
        List<Appointment> futureAppointmentsByDate =
                appointmentRepository.findByUserIdAndAppointmentDateGreaterThanOrderByAppointmentIdDesc(userId, currentDate);

        // Lấy danh sách cuộc hẹn trong ngày nhưng có thời gian lớn hơn hiện tại
        List<Appointment> futureAppointmentsByTime =
                appointmentRepository.findByUserIdAndAppointmentDateEqualsAndStartTimeGreaterThanOrderByAppointmentIdDesc(userId, currentDate, currentTime);

        // Kết hợp hai danh sách
        List<Appointment> allFutureAppointments = new ArrayList<>();
        allFutureAppointments.addAll(futureAppointmentsByDate);
        allFutureAppointments.addAll(futureAppointmentsByTime);

        // Lấy appointment có id lớn nhất
        return allFutureAppointments.stream()
                .max(Comparator.comparing(Appointment::getAppointmentId));
    }

    @Override
    @Transactional
    public Appointment doctorCreateAppointment(Appointment appointment) {
        //Create new Appointment
        User userDetails = userService.getUserById(appointment.getUserId());
        if (appointment.getAppointmentId() == null) {
            String userId = appointment.getUserId();
            // Lấy cuộc hẹn hiện tại hoặc đang diễn ra
            Optional<Appointment> currentAppointment = getCurrentAppointmentByUserId(userId);

            if (currentAppointment.isPresent()) {
                Appointment current = currentAppointment.get();

                // Thời điểm hiện tại
                LocalDateTime now = LocalDateTime.now();

                // Tính thời điểm bắt đầu và kết thúc của cuộc hẹn hiện tại
                LocalDateTime currentAppointmentStartTime = LocalDateTime.of(
                        current.getAppointmentDate(),
                        current.getStartTime()
                );
                LocalDateTime currentAppointmentEndTime = LocalDateTime.of(
                        current.getAppointmentDate(),
                        current.getEndTime()
                );

                // Kiểm tra nếu cuộc hẹn hiện tại chưa diễn ra
                if (currentAppointmentStartTime.isAfter(now)) {
                    throw new IllegalStateException(
                            "The user already has an upcoming appointment. Cannot create a new one."
                    );
                }

                // Thời điểm bắt đầu của cuộc hẹn mới
                LocalDateTime newAppointmentStartTime = LocalDateTime.of(
                        appointment.getAppointmentDate(),
                        appointment.getStartTime()
                );

                // Kiểm tra nếu thời gian cuộc hẹn mới <= thời gian kết thúc của cuộc hẹn hiện tại
                if (!newAppointmentStartTime.isAfter(currentAppointmentEndTime)) {
                    throw new IllegalStateException(
                            "The new appointment must be scheduled after the current appointment ends."
                    );
                }
            }

            // Lấy cuộc hẹn sắp tới
            Optional<Appointment> futureAppointment = getFutureAppointmentByUserId(userId);

            boolean beAbleHaveFutureAppointment = false;


            if (futureAppointment.isPresent()) {
                if (currentAppointment.isPresent()
                        && currentAppointment.get().getAppointmentId().equals(futureAppointment.get().getAppointmentId())) {
                    // Nếu futureAppointment giống currentAppointment, trả về Not Found
                    beAbleHaveFutureAppointment = true;
                }
            } else
                beAbleHaveFutureAppointment = true;

            if (!beAbleHaveFutureAppointment) {
                throw new IllegalStateException("Cannot create a future appointment for this user.");
            }
            Appointment savedAppointment = appointmentRepository.save(appointment);
            
            // Schedule reminder jobs for the new appointment
            LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime());
            jobSchedulerService.scheduleAppointmentReminderJobs(savedAppointment.getAppointmentId().toString(), appointmentDateTime);
            pushNotificationService.sendAppointmentReminderNotification(userDetails, appointment, 0);
            
            return savedAppointment;
        } else {
            if (AppointmentStatus.CANCELLED.equals(appointment.getStatus())) {
                // Cancel reminder jobs when appointment is cancelled
                jobSchedulerService.cancelJobsByEntity("appointment", appointment.getAppointmentId().toString());
                appointmentRepository.deleteByAppointmentId(appointment.getAppointmentId());
                return appointment;
            }
        }

        //Update status
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        
        // If appointment time is updated, reschedule reminder jobs
        if (appointment.getAppointmentDate() != null && appointment.getStartTime() != null) {
            LocalDateTime newAppointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime());
            jobSchedulerService.updateJobsByEntity("appointment", appointment.getAppointmentId().toString(), newAppointmentDateTime);
        }
        
        return updatedAppointment;
    }

    @Override
    @Transactional
    public Appointment updateAppointment(Appointment appointment) {
        Appointment existAppointment = appointmentRepository.findByAppointmentId(appointment.getAppointmentId())
                .orElseThrow(() -> new NoSuchElementException("Appointment with ID " + appointment.getAppointmentId() + " not found"));
        existAppointment.setStatus(appointment.getStatus());

        if (AppointmentStatus.CANCELLED.equals(appointment.getStatus())) {
            // Cancel reminder jobs when appointment is cancelled
            jobSchedulerService.cancelJobsByEntity("appointment", appointment.getAppointmentId().toString());
            appointmentRepository.deleteByAppointmentId(appointment.getAppointmentId());
            return appointment;
        }
        
        Appointment updatedAppointment = appointmentRepository.save(existAppointment);
        
        // If appointment time is updated, reschedule reminder jobs
        if (appointment.getAppointmentDate() != null && appointment.getStartTime() != null) {
            LocalDateTime newAppointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime());
            jobSchedulerService.updateJobsByEntity("appointment", appointment.getAppointmentId().toString(), newAppointmentDateTime);
        }
        
        return updatedAppointment;
    }

    private String formatDateTime(LocalDate date, LocalTime time, DateTimeFormatter formatter) {
        return date.atTime(time).format(formatter); // Kết hợp LocalDate và LocalTime rồi format
    }
}
