package com.vm.repo;

import com.vm.enums.AppointmentStatus;
import com.vm.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

//public interface AppointmentRepository extends CrudRepository<Appointment, Integer> {
//}
//

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Appointment findByConversationId(Integer conversationId);

    // Lấy cuộc hẹn có id lớn nhất cho một userId cụ thể
    Optional<Appointment> findTopByUserIdOrderByAppointmentIdDesc(String userId);

    // Xóa tất cả cuộc hẹn theo userId
    void deleteByUserId(String userId);

    // Lấy lịch hẹn theo doctorId
    List<Appointment> findAllByDoctorId(String doctorId);

    // Lấy lịch hẹn theo doctorId và appointmentDate >= ngày hiện tại
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.appointmentDate >= CURRENT_DATE")
    List<Appointment> findAllByDoctorIdAndFutureAppointments(@Param("doctorId") String doctorId);

    // Lấy tất cả cuộc hẹn FINISH theo userId
    List<Appointment> findAllByStatusAndUserId(AppointmentStatus status, String userId);

    // Lấy cuộc hẹn có id lớn nhất của user và trước thời gian hiện tại
    Optional<Appointment> findTopByUserIdAndAppointmentDateBeforeAndEndTimeBeforeOrderByAppointmentIdDesc(
            String userId,
            LocalDate currentDate,
            LocalTime currentTime
    );

    // Lấy lịch hẹn trong tương lai với appointmentId lớn nhất
    Optional<Appointment> findTopByUserIdAndAppointmentDateAfterAndStartTimeAfterOrderByAppointmentIdDesc(
            String userId,
            LocalDate currentDate,
            LocalTime currentTime
    );
}