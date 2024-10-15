package com.vm.repo;

import com.vm.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    // Tìm tất cả các Availability của một bác sĩ
    List<Availability> findByUserId(String userId);

    // Tìm tất cả các Availability của một bác sĩ trong một ngày cụ thể trong tuần
    List<Availability> findByUserIdAndDayOfWeek(String userId, Integer dayOfWeek);

    // Thêm phương thức mới để tìm Availability theo ngày trong tuần (cho tất cả bác sĩ)
    List<Availability> findByDayOfWeek(Integer dayOfWeek);
}
