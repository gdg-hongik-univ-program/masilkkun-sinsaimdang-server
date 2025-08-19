package com.sinsaimdang.masilkkoon.masil.user.service;

import com.sinsaimdang.masilkkoon.masil.auth.validator.SignupValidator;
import com.sinsaimdang.masilkkoon.masil.user.dto.UserDto;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // Lombok에서 제공하는 Logger 필드 자동 생성기 *** 공부 필요 ***
public class UserService {

    @Value("${user.default-profile-image-url}")
    private String defaultProfileImageUrl;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignupValidator signupValidator;

    public Optional<UserDto> findById(Long id){
        log.debug("ID으로 사용자 조회: {}", id);

        Optional<UserDto> result = userRepository.findById(id).map(UserDto::from);

        if (result.isPresent()) {
            log.debug("사용자 조회 성공 - ID = {}, 이메일 = {}", id, result.get().getEmail());
        } else {
            log.debug("사용자 조회 실패 - 존재하지 않는 ID = {}", id);
        }
        return result;
    }

    public Optional<UserDto> findByEmail(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        log.debug("사용자 이메일로 조회 요청 - 이메일: {}", normalizedEmail);

        Optional<UserDto> result = userRepository.findByEmail(normalizedEmail).map(UserDto::from);

        if (result.isPresent()) {
            log.debug("사용자 조회 성공 - 이메일: {}, ID: {}", normalizedEmail, result.get().getId());
        } else {
            log.debug("사용자 조회 실패 - 존재하지 않는 이메일: {}", normalizedEmail);
        }

        return result;
    }

    @Transactional
    public UserDto updateNickname(Long id, String newNickname) {
        log.info("사용자 닉네임 수정 요청 - ID = {}, 새 닉네임 = {}", id, newNickname);

        signupValidator.validateNickname(newNickname);

        User user = getUserEntity(id);
        String normalizedNickname = newNickname != null ? newNickname.trim() : null;

        if (Objects.equals(normalizedNickname, user.getNickname())) {
            log.debug("변경하고자 하는 닉네임이 기존과 동일함 ID = {}, 닉네임 = {}", id, user.getNickname());
            return UserDto.from(user);
        }

        if (userRepository.existsByNickname(normalizedNickname)) {
            log.warn("이미 존재하는 닉네임으로 변경 요청 {}", normalizedNickname);
            throw new IllegalArgumentException("이미 존재하는 닉네임 입니다");
        }

        user.updateNickname(normalizedNickname);
        User savedUser = userRepository.save(user);

        log.info("사용자 닉네임 수정 완료 - ID = {}, 닉네임 = {}", id, normalizedNickname);
        return UserDto.from(savedUser);
    }

    @Transactional
    public UserDto updatePassword(Long userId, String newPassword) {
        log.info("사용자 비밀번호 변경 요청 - ID: {}", userId);

        User user = getUserEntity(userId);

        signupValidator.validatePassword(newPassword);

        String encodedPassword = passwordEncoder.encode(newPassword);

        user.updatePassword(encodedPassword);
        User savedUser = userRepository.save(user);

        log.info("사용자 비밀번호 변경 완료 - ID: {}", userId);
        return UserDto.from(savedUser);
    }

    @Transactional
    public UserDto updateProfileImage(Long userId, String profileImageUrl) {
        log.info("프로필 이미지 업데이트 요청 - ID: {}", userId);

        User user = getUserEntity(userId);

        String normalizedProfileImageUrl = profileImageUrl != null ? profileImageUrl.trim() : null;
        if (normalizedProfileImageUrl != null && normalizedProfileImageUrl.trim().isEmpty()) {
            normalizedProfileImageUrl = null;
        }

        user.updateProfileImageUrl(normalizedProfileImageUrl);
        User savedUser = userRepository.save(user);

        log.info("프로필 이미지 업데이트 완료 - ID: {}", userId);
        return UserDto.from(savedUser);
    }

    @Transactional
    public UserDto removeProfileImage(Long userId) {
        log.info("프로필 사진 삭제 요청 - ID: {}", userId);

        User user = getUserEntity(userId);
        user.updateProfileImageUrl(defaultProfileImageUrl);
        User savedUser = userRepository.save(user);

        log.info("프로필 이미지 삭제 요청 - ID: {}", userId);
        return UserDto.from(savedUser);
    }

    public User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("사용자 조회 실패 - 존재하지 않는 ID: {}", userId);
                    return new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId);
                });
    }

    @Transactional
    public void deleteUser(Long userId) {
        log.info("사용자 삭제 요청 - ID = {}", userId);

        if(!userRepository.existsById(userId)) {
            log.warn("존재하지 않는 사용자입니다 - ID = {}", userId);
            throw new IllegalArgumentException("존재하지 않는 사용자입니다");
        }

        userRepository.deleteById(userId);
        log.info("사용자 삭제 완료 - ID = {}", userId);
    }
}
