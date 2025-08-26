package com.sinsaimdang.masilkkoon.masil.user.repository;

import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자를 찾습니다.
     *
     * @param email 사용자 이메일
     * @return Optional<User> 객체
     */
    Optional<User> findByEmail(String email);

    /**
     * 닉네임으로 사용자를 찾습니다.
     *
     * @param nickname 사용자 닉네임
     * @return Optional<User> 객체
     */
    Optional<User> findByNickname(String nickname);

    /**
     * 이메일 중복 여부를 확인합니다.
     *
     * @param email 확인할 이메일
     * @return 중복 여부
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임 중복 여부를 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 중복 여부
     */
    boolean existsByNickname(String nickname);
}