package com.vm.service;

import com.vm.model.Appointment;

public interface AppointmentService {
    public Appointment createAppointment(Appointment appointment) ;
    public Appointment getAppointmentByConversationId(Integer id);
}
