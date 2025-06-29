package com.vm.controllers;

import com.vm.dto.AvailabilityDTO;
import com.vm.model.Availability;
import com.vm.service.UserService;
import com.vm.service.impl.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private UserService userService;

    /**
     * Endpoint để tạo hoặc cập nhật lịch làm việc cho bác sĩ.
     *
     * @param availabilities Danh sách Availability gửi từ client
     * @return Danh sách Availability đã lưu
     */
    @PostMapping("")
    public ResponseEntity<?> saveAvailabilities(@RequestBody List<Availability> availabilities) {
        try {
            log.info("/api/v1/availabilities save --- ");
            List<Availability> savedAvailabilities = availabilityService.saveAvailabilities(userService.getStringCurrentUserId(), availabilities);
            return ResponseEntity.ok("Save availabilities successfully");
        } catch (Exception e) {
            log.error("/api/v1/availabilities save error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearAvailabilities() {
        try {
            log.info("/api/v1/clear availabilities --- ");
            availabilityService.clearAvailabilities(userService.getStringCurrentUserId());
            return ResponseEntity.ok("Clear availabilities successfully");
        } catch (Exception e) {
            log.error("/api/v1/clear availabilities save error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Endpoint để lấy lịch làm việc của bác sĩ.
     *
     * @return Danh sách Availability
     */
    @GetMapping("")
    public ResponseEntity<?> getAvailabilitiesCurrentUser() {
        try {
            log.info("/api/v1/availabilities get --- ");
            List<Availability> availabilities = availabilityService.getAvailabilities(userService.getStringCurrentUserId());
            if (availabilities.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(availabilities);
        } catch (Exception e) {
            log.error("/api/v1/availabilities get error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Endpoint để lấy các Availability của bác sĩ trong một ngày cụ thể trong tuần.
     *
     * @param userId    ID của bác sĩ
     * @param dayOfWeek Ngày trong tuần (1 = Thứ Hai, ..., 7 = Chủ Nhật)
     * @return Danh sách Availability
     */
    @GetMapping("/{userId}/day/{dayOfWeek}")
    public ResponseEntity<List<Availability>> getAvailabilitiesByDay(
            @PathVariable String userId,
            @PathVariable Integer dayOfWeek) {
        List<Availability> availabilities = availabilityService.getAvailabilitiesByDay(userId, dayOfWeek);
        if (availabilities.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Availability>> getAvailableShiftsByUserAndDate(@RequestParam String userId,
            @RequestParam String date) { // date định dạng "yyyy-MM-dd"

        // Chuyển đổi date sang dayOfWeek
        LocalDate localDate = LocalDate.parse(date);
        int dayOfWeek = localDate.getDayOfWeek().getValue(); // 1 = Monday, ..., 7 = Sunday

        List<Availability> availabilities = availabilityService.getAvailabilitiesByDay(userId, dayOfWeek);
        if (availabilities.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/available-by-date")
    public ResponseEntity<List<AvailabilityDTO>> getAvailableShiftsByDate(@RequestParam String date) {
        try {
            log.info("/available-by-date --- ");
            List<AvailabilityDTO> availabilities = availabilityService.getAvailableShiftsByDate(date);
            return ResponseEntity.ok(availabilities);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
