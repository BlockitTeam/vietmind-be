package com.vm.service;

import com.vm.dto.UserDoctorDTO;
import com.vm.model.Appointment;

import java.util.Optional;

public interface AppointmentService {
    public UserDoctorDTO createAppointment(Appointment appointment) throws Exception;
    public Appointment getAppointmentByConversationId(Integer id);
    public Optional<Appointment> getAppointmentByUserId(String userId);
    public void deleteAppointmentsByUserId(String userId);
}
