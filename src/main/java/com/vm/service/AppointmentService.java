package com.vm.service;

import com.vm.dto.AppointmentEventDTO;
import com.vm.dto.UserDoctorDTO;
import com.vm.model.Appointment;

import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    public UserDoctorDTO createAppointment(Appointment appointment) throws Exception;
    public Object getAppointmentByConversationId(Integer id);
    public Optional<Appointment> getAppointmentByUserId(String userId);
    public void deleteAppointmentsByUserId(String userId);
    public List<AppointmentEventDTO> getAppointmentsByDoctorId(String doctorId);
    public List<Appointment> getFinishedAppointmentsByUserId(String userId);
    public Optional<Appointment>  getCurrentAppointmentByUserId(String userId);
    public Optional<Appointment> getFutureAppointmentByUserId(String userId);
    public Appointment doctorCreateAppointment(Appointment appointment);
    public Appointment updateAppointment(Appointment appointment);
}
