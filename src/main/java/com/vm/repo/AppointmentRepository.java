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
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    // Lấy appointment mới nhất theo conversationId (tránh NonUniqueResultException khi có nhiều appointment)
    Optional<Appointment> findTopByConversationIdOrderByAppointmentIdDesc(Integer conversationId);

    Optional<Appointment> findByAppointmentId(Integer appointmentId);

    // Lấy cuộc hẹn có id lớn nhất cho một userId cụ thể
    Optional<Appointment> findTopByUserIdOrderByAppointmentIdDesc(String userId);

    // Xóa tất cả cuộc hẹn theo userId
    void deleteByUserId(String userId);

    // Xóa cuộc hẹn theo id
    void deleteByAppointmentId(Integer appointmentId);

    // Lấy lịch hẹn theo doctorId
    List<Appointment> findAllByDoctorId(String doctorId);

    // Lấy lịch hẹn theo doctorId và appointmentDate >= ngày hiện tại
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.appointmentDate >= CURRENT_DATE")
    List<Appointment> findAllByDoctorIdAndFutureAppointments(@Param("doctorId") String doctorId);

    // Lấy tất cả cuộc hẹn FINISH theo userId
    List<Appointment> findAllByStatusAndUserId(AppointmentStatus status, String userId);

    //tìm các cuộc hẹn đã có trạng thái FINISH hoặc có appointmentDate + endTime < thời điểm hiện tại
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.status = :status " +
            "AND a.userId = :userId " +
            "OR (a.userId = :userId AND " +
            "(a.appointmentDate < :currentDate OR " +
            "(a.appointmentDate = :currentDate AND a.endTime < :currentTime)))")
    List<Appointment> findAllFinishedOrPastAppointments(
            @Param("status") AppointmentStatus status,
            @Param("userId") String userId,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime);

    // Lấy cuộc hẹn có id lớn nhất của user và trước thời gian hiện tại
    Optional<Appointment> findTopByUserIdAndAppointmentDateBeforeAndEndTimeBeforeOrderByAppointmentIdDesc(
            String userId,
            LocalDate currentDate,
            LocalTime currentTime
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.userId = :userId " +
            "AND a.appointmentDate = :currentDate " +
            "AND a.startTime <= :currentTime " +
            "AND a.endTime >= :currentTime")
    Optional<Appointment> findCurrentAppointment(
            @Param("userId") String userId,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.userId = :userId " +
            "AND (a.appointmentDate > :currentDate " +
            "OR (a.appointmentDate = :currentDate AND a.startTime > :currentTime)) " +
            "ORDER BY a.appointmentDate ASC, a.startTime ASC")
    Optional<Appointment> findUpcomingAppointment(
            @Param("userId") String userId,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime);

    @Query(value = "SELECT * FROM appointments a " +
            "WHERE a.user_id = :userId " +
            "AND (a.appointment_date < :currentDate " +
            "OR (a.appointment_date = :currentDate AND a.end_time < :currentTime)) " +
            "ORDER BY a.appointment_id DESC LIMIT 1",
            nativeQuery = true)
    Optional<Appointment> findLatestCompletedAppointment(
            @Param("userId") String userId,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime);

    // Lấy cuộc hẹn có ngày lớn hơn ngày hiện tại
    List<Appointment> findByUserIdAndAppointmentDateGreaterThanOrderByAppointmentIdDesc(
            String userId,
            LocalDate currentDate
    );

    // Lấy cuộc hẹn có ngày bằng ngày hiện tại và thời gian bắt đầu lớn hơn thời gian hiện tại
    List<Appointment> findByUserIdAndAppointmentDateEqualsAndStartTimeGreaterThanOrderByAppointmentIdDesc(
            String userId,
            LocalDate currentDate,
            LocalTime currentTime
    );

    // Đếm số appointment trong cùng slot (cùng doctorId, appointmentDate, startTime, endTime) và status != CANCELLED
    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.doctorId = :doctorId " +
            "AND a.appointmentDate = :appointmentDate " +
            "AND a.startTime = :startTime " +
            "AND a.endTime = :endTime " +
            "AND a.status != :cancelledStatus")
    long countAppointmentsInSlot(
            @Param("doctorId") String doctorId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );

    // Đếm số appointment trong cùng slot nhưng loại trừ một appointmentId cụ thể (dùng khi update)
    @Query("SELECT COUNT(a) FROM Appointment a " +
            "WHERE a.doctorId = :doctorId " +
            "AND a.appointmentDate = :appointmentDate " +
            "AND a.startTime = :startTime " +
            "AND a.endTime = :endTime " +
            "AND a.status != :cancelledStatus " +
            "AND a.appointmentId != :excludeAppointmentId")
    long countAppointmentsInSlotExcluding(
            @Param("doctorId") String doctorId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus,
            @Param("excludeAppointmentId") Integer excludeAppointmentId
    );
}
