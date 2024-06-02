package com.vm.repo;

import com.vm.model.Option;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OptionRepository extends CrudRepository<Option, Long> {
	@Query("SELECT u FROM Option u WHERE u.questionId = :question_id")
	public List<Option> getOptionsByQuestionId(@Param("question_id") Long question_id);
}
