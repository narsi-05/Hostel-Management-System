package com.hostel.management.repository;

import com.hostel.management.model.Hostel;
import com.hostel.management.model.Message;
import com.hostel.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderOrReceiverOrderBySentAtDesc(User sender, User receiver);

    List<Message> findBySenderAndReceiverOrderBySentAtAsc(User sender, User receiver);

    long countByReceiverAndIsRead(User receiver, Boolean isRead);

    List<Message> findByReceiverAndIsRead(User receiver, Boolean isRead);
}
