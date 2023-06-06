package com.ll.dlike.boundedContext.instaMember.repository;

import com.ll.dlike.boundedContext.instaMember.entity.InstaMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstaMemberRepository extends JpaRepository<InstaMember, Long> {
    Optional<InstaMember> findByUsername(String username);

    Optional<InstaMember> findByOauthId(String oauthId);
}
