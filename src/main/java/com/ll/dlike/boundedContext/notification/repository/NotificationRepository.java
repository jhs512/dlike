package com.ll.dlike.boundedContext.notification.repository;

import com.ll.dlike.boundedContext.instaMember.entity.InstaMember;
import com.ll.dlike.boundedContext.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByToInstaMemberOrderByIdDesc(InstaMember toInstaMember);

    List<Notification> findByToInstaMember_usernameOrderByIdDesc(String username);

    int countByToInstaMemberAndReadDateIsNull(InstaMember instaMember);
}
