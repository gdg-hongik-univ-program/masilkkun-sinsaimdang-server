package com.sinsaimdang.masilkkoon.masil.user.repository;

import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // email으로 사용자 찾기
    Optional<User> findByEmail(String email);

    // nickname으로 사용자 찾기
    Optional<User> findByNickname(String nickname);

    // email 중복 검사
    boolean existsByEmail(String email);

    // nickname 중복 검사
    boolean existsByNickname(String nickname);
}