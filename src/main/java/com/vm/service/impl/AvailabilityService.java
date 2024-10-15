package com.vm.service.impl;

import com.vm.model.Availability;
import com.vm.repo.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    /**
     * Tạo hoặc cập nhật danh sách Availability cho một bác sĩ.
     *
     * @param userId      ID của bác sĩ
     * @param availabilities Danh sách Availability để lưu trữ
     * @return Danh sách Availability đã lưu
     */
    @Transactional
    public List<Availability> saveAvailabilities(String userId, List<Availability> availabilities) {
        // Xóa các Availability hiện tại của bác sĩ
        availabilityRepository.deleteAll(availabilityRepository.findByUserId(userId));

        // Thiết lập userId cho từng Availability và lưu trữ
        availabilities.forEach(availability -> availability.setUserId(userId));

        return availabilityRepository.saveAll(availabilities);
    }

    /**
     * Lấy danh sách Availability của một bác sĩ.
     *
     * @param userId ID của bác sĩ
     * @return Danh sách Availability
     */
    public List<Availability> getAvailabilities(String userId) {
        return availabilityRepository.findByUserId(userId);
    }

    /**
     * Lấy các Availability của bác sĩ trong một ngày cụ thể trong tuần.
     *
     * @param userId      ID của bác sĩ
     * @param dayOfWeek   Ngày trong tuần
     * @return Danh sách Availability
     */
    public List<Availability> getAvailabilitiesByDay(String userId, Integer dayOfWeek) {
        return availabilityRepository.findByUserIdAndDayOfWeek(userId, dayOfWeek);
    }

    /**
     * Lấy tất cả các khung giờ làm việc có sẵn vào một ngày cụ thể cho tất cả bác sĩ.
     *
     * @param dateStr Ngày cần kiểm tra định dạng "yyyy-MM-dd"
     * @return Danh sách các Availability có sẵn
     */
    public List<Availability> getAvailableShiftsByDate(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            int dayOfWeek = date.getDayOfWeek().getValue(); // 1 = Monday, ..., 7 = Sunday
            return availabilityRepository.findByDayOfWeek(dayOfWeek);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd");
        }
    }
}
