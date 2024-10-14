package com.vm.repo;

import com.vm.model.SpecializedResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpecializedResponseRepository extends JpaRepository<SpecializedResponse, Long> {

    // Tìm tất cả các phản hồi chuyên sâu cho một người dùng và khảo sát cụ thể, sắp xếp theo version giảm dần
    List<SpecializedResponse> findByUserIdAndSurveyIdOrderByVersionDesc(String userId, Integer surveyId);

    // Tìm phản hồi chuyên sâu với version cao nhất
    Optional<SpecializedResponse> findTopByUserIdAndSurveyIdOrderByVersionDesc(String userId, Integer surveyId);

    // Phương thức mới để lấy tất cả các phản hồi với version lớn nhất
    @Query("SELECT sr FROM SpecializedResponse sr " +
            "WHERE sr.userId = :userId AND sr.surveyId = :surveyId AND sr.version = " +
            "(SELECT MAX(s.version) FROM SpecializedResponse s WHERE s.userId = :userId AND s.surveyId = :surveyId)")
    List<SpecializedResponse> findAllByUserIdAndSurveyIdWithMaxVersion(@Param("userId") String userId,
                                                                       @Param("surveyId") Integer surveyId);
}
