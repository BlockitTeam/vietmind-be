package com.vm.service.impl;

import com.vm.dto.AppointmentEventDTO;
import com.vm.dto.UserDoctorDTO;
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
import java.time.LocalTime;
import java.time.ZoneId;
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
        return appointmentRepository.findTopByUserIdOrderByAppointmentIdDesc(userId);
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

    @Override
    public List<AppointmentEventDTO> getAppointmentsByDoctorId(String doctorId) {
        List<Appointment> appointments = appointmentRepository.findAllByDoctorIdAndFutureAppointments(doctorId);

        return appointments.stream()
                .map(appointment -> new AppointmentEventDTO(
                        String.valueOf(appointment.getAppointmentId()),
                        appointment.getContent(),
                        convertToDate(appointment.getAppointmentDate(), appointment.getStartTime()),
                        convertToDate(appointment.getAppointmentDate(), appointment.getEndTime())
                ))
                .collect(Collectors.toList());
    }

    private Date convertToDate(LocalDate date, LocalTime time) {
        return Date.from(date.atTime(time).atZone(ZoneId.systemDefault()).toInstant());
    }
}
