package com.vm.repo;

import com.vm.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//public interface AppointmentRepository extends CrudRepository<Appointment, Integer> {
//}
//

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Appointment findByConversationId(Integer conversationId);
}