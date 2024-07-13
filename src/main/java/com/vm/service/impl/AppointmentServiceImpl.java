package com.vm.service.impl;

import com.vm.model.Appointment;
import com.vm.repo.AppointmentRepository;
import com.vm.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public Appointment createAppointment(Appointment appointment) throws Exception {
        Integer appointmentId = appointment.getAppointmentId();
        if (appointmentId == null) {
            Integer conversationId = appointment.getConversationId();
            Appointment appointmentExist = getAppointmentByConversationId(conversationId);
            if (appointmentExist != null)
                throw new Exception("Error when create Appointment, this conversation " + conversationId + " already have Appointment");
        }
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getAppointmentByConversationId(Integer id) {
        return appointmentRepository.findByConversationId(id);
    }
}
