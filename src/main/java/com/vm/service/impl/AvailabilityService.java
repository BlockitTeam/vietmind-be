package com.vm.service.impl;

import com.vm.dto.AvailabilityDTO;
import com.vm.model.Availability;
import com.vm.model.User;
import com.vm.repo.AvailabilityRepository;
import com.vm.repo.SurveyRepository;
import com.vm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.vm.model.Survey;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SurveyRepository surveyRepository;

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

    public void clearAvailabilities(String userId) {
        // Xóa các Availability hiện tại của bác sĩ
        availabilityRepository.deleteAll(availabilityRepository.findByUserId(userId));
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

    public List<AvailabilityDTO> getAvailableShiftsByDate(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            int dayOfWeek = date.getDayOfWeek().getValue(); // 1 = Monday, ..., 7 = Sunday
            List<Availability> availabilities = availabilityRepository.findByDayOfWeek(dayOfWeek);
            LocalDateTime now = LocalDateTime.now();

            Integer surveyDetailId = userService.getSurveyDetail();
            // Lấy tên survey từ surveyId
            String surveyDetailName = surveyRepository.findBySurveyId(surveyDetailId)
                    .map(Survey::getTitle) // Nếu survey tồn tại, lấy title
                    .orElse(null); // Nếu không tồn tại, trả về null hoặc giá trị mặc định

            // Lọc và chuyển đổi danh sách `Availability`
            List<AvailabilityDTO> result = availabilities.stream()
                    .filter(availability -> {
                        // Chỉ lấy các thời gian lớn hơn hiện tại
                        LocalDateTime availabilityStart = LocalDateTime.of(date, availability.getStartTime());
                        if (!availabilityStart.isAfter(now)) {
                            return false;
                        }

                        // Nếu surveyDetailName là null, bỏ qua kiểm tra `specializations`
                        if (surveyDetailName == null) {
                            return true;
                        }

                        // Lấy User dựa trên userId từ Availability
                        String userId = availability.getUserId();
                        User user = userService.getUserById(userId);

                        // Chuyển `specializations` từ String thành List<String>
                        String specializations = user.getSpecializations(); // Dữ liệu dạng "Giấc Ngủ,Trầm Cảm,PTSD"
                        List<String> specializationList = Arrays.asList(specializations.split(","));

                        // Kiểm tra nếu surveyDetailName thuộc danh sách `specializationList`
                        return specializationList.contains(surveyDetailName);
                    })
                    .map(availability -> new AvailabilityDTO(availability, dateStr))
                    .collect(Collectors.toList());
            return result;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd");
        }
    }
}
