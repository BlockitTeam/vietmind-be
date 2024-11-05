package com.vm.service;

import com.vm.dto.UserDoctorDTO;
import com.vm.model.Appointment;

public interface AppointmentService {
    public UserDoctorDTO createAppointment(Appointment appointment) throws Exception;
    public Appointment getAppointmentByConversationId(Integer id);
    public Appointment getAppointmentByUserId(String userId);
}
