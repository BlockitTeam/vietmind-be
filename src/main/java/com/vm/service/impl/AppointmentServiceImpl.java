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
    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment getAppointmentByConversationId(Integer id) {
        return appointmentRepository.findByConversationId(id);
    }
}
