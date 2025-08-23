package com.sinsaimdang.masilkkoon.masil.user.service;

import com.sinsaimdang.masilkkoon.masil.article.entity.Article;
import com.sinsaimdang.masilkkoon.masil.article.repository.ArticleLikeRepository;
import com.sinsaimdang.masilkkoon.masil.article.repository.ArticleRepository;
import com.sinsaimdang.masilkkoon.masil.article.repository.ArticleScrapRepository;
import com.sinsaimdang.masilkkoon.masil.auth.validator.SignupValidator;
import com.sinsaimdang.masilkkoon.masil.common.s3.Uploader;
import com.sinsaimdang.masilkkoon.masil.user.dto.UserDto;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.repository.FollowRepository;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import com.sinsaimdang.masilkkoon.masil.visit.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // Lombok에서 제공하는 Logger 필드 자동 생성기 *** 공부 필요 ***
public class UserService {

    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleScrapRepository articleScrapRepository;
    private final FollowRepository followRepository;
    private final VisitRepository visitRepository;
    @Value("${user.default-profile-image-url}")
    private String defaultProfileImageUrl;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Uploader uploader;
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

    private void validatePasswordSecurity(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new SecurityException("비밀번호는 필수 항목입니다.");
        }

        if (password.length() < 8 || password.length() > 20) {
            throw new SecurityException("비밀번호는 8~20 자리이어야 합니다.");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new SecurityException("비밀번호에 영문 소문자를 포함해야 합니다.");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new SecurityException("비밀번호에 영문 대문자를 포함해야 합니다.");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new SecurityException("비밀번호에 숫자를 포함해야 합니다.");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new SecurityException("비밀번호에 특수문자를 포함해야 합니다.");
        }

        if (!password.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]*$")) {
            throw new SecurityException("비밀번호는 영문, 숫자, 특수문자만 사용할 수 있습니다.");
        }

        log.debug("비밀번호 보안 정책 검증 통과");
    }

    @Transactional
    public UserDto updateProfileImage(Long userId, MultipartFile profileImageFile) throws IOException {
        log.info("프로필 이미지 업데이트 요청 - ID: {}", userId);

        User user = getUserEntity(userId);
        String oldImageUrl = user.getProfileImageUrl();

        // 새 이미지 업로드
        String newImageUrl = uploader.upload(profileImageFile, "profile-images");

        // 기존 이미지가 기본 이미지가 아닐 경우에만 삭제
        if (oldImageUrl != null && !oldImageUrl.equals(defaultProfileImageUrl)) {
            uploader.delete(oldImageUrl);
            log.info("기존 프로필 이미지 삭제 완료 - URL: {}", oldImageUrl);
        }

        user.updateProfileImageUrl(newImageUrl);
        User savedUser = userRepository.save(user);

        log.info("프로필 이미지 업데이트 완료 - ID: {}", userId);
        return UserDto.from(savedUser);
    }

    @Transactional
    public UserDto removeProfileImage(Long userId) {
        log.info("프로필 사진 삭제 요청 - ID: {}", userId);

        User user = getUserEntity(userId);
        String oldImageUrl = user.getProfileImageUrl();

        // 기존 이미지가 기본 이미지가 아닐 경우에만 삭제
        if (oldImageUrl != null && !oldImageUrl.equals(defaultProfileImageUrl)) {
            uploader.delete(oldImageUrl);
            log.info("기존 프로필 이미지 삭제 완료 - URL: {}", oldImageUrl);
        }

        user.updateProfileImageUrl(defaultProfileImageUrl); // 기본 이미지로 설정
        User savedUser = userRepository.save(user);

        log.info("프로필 이미지 삭제 후 기본 이미지로 변경 완료 - ID: {}", userId);
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

        articleLikeRepository.deleteAllByUserId(userId);
        log.info("사용자가 남긴 좋아요 삭제 - ID {}", userId);

        articleScrapRepository.deleteAllByUserId(userId);
        log.info("사용자가 남긴 스크랩 삭제 - ID {}", userId);

        List<Article> articlesByUser = articleRepository.findAllByUserId(userId);
        articleRepository.deleteAll(articlesByUser);
        log.info("사용자가 작성한 게시글 삭제 - ID {}", userId);

        followRepository.deleteAllByUserId(userId);
        log.info("사용자의 팔로우/팔로잉 삭제 - ID {}", userId);

        visitRepository.deleteAllByUserId(userId);
        log.info("사용자의 지역 인증 기록 삭제 - ID {}", userId);

        userRepository.deleteById(userId);
        log.info("사용자 삭제 완료 - ID = {}", userId);
    }
}
