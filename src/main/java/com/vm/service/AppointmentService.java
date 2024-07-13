package com.vm.service;

import com.vm.model.Appointment;

public interface AppointmentService {
    public Appointment createAppointment(Appointment appointment) throws Exception;
    public Appointment getAppointmentByConversationId(Integer id);
}
