package com.vm.service.impl;

import com.vm.dto.AppointmentEventDTO;
import com.vm.dto.UserDoctorDTO;
import com.vm.enums.AppointmentStatus;
import com.vm.model.Appointment;
import com.vm.model.Conversation;
import com.vm.model.User;
import com.vm.repo.AppointmentRepository;
import com.vm.repo.UserRepository;
import com.vm.service.AppointmentService;
import com.vm.service.ConversationService;
import com.vm.util.KeyManagement;
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

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDoctorDTO createAppointment(Appointment appointment) throws Exception {
        Integer appointmentId = appointment.getAppointmentId();
        if (appointmentId == null) {
            Integer conversationId = appointment.getConversationId();
            Appointment appointmentExist = getAppointmentByConversationId(conversationId);
            if (appointmentExist != null)
                throw new Exception("Error when create Appointment, this conversation " + conversationId + " already have Appointment");
        }

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
        appointmentRepository.save(appointment);
        User user = userRepo.findById(UUID.fromString(appointment.getDoctorId()))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return modelMapper.map(user, UserDoctorDTO.class);
    }

    @Override
    public Appointment getAppointmentByConversationId(Integer id) {
        return appointmentRepository.findByConversationId(id);
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

    public List<Appointment> getFinishedAppointmentsByUserId(String userId) {
        return appointmentRepository.findAllByStatusAndUserId(AppointmentStatus.FINISH, userId);
    }

    @Override
    @Transactional
    public Optional<Appointment>  getCurrentAppointmentByUserId(String userId) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        Optional<Appointment> appointmentOpt = appointmentRepository.findCurrentOrUpcomingAppointment(userId, currentDate, currentTime);

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

    private String formatDateTime(LocalDate date, LocalTime time, DateTimeFormatter formatter) {
        return date.atTime(time).format(formatter); // Kết hợp LocalDate và LocalTime rồi format
    }
}
