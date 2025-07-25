package com.sinsaimdang.masilkkoon.masil.user.service;

import com.sinsaimdang.masilkkoon.masil.user.dto.UserDto;
import com.sinsaimdang.masilkkoon.masil.user.entity.User;
import com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j // Lombok에서 제공하는 Logger 필드 자동 생성기 *** 공부 필요 ***
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserDto> findById(Long id){
        return userRepository.findById(id).map(UserDto::from);
    }

    public Optional<UserDto> findByEmail(String email){
        return userRepository.findByEmail(email).map(UserDto::from);
    }

    // 새로운 사용자 생성
    @Transactional
    public UserDto createUser(String email, String password, String name, String nickname){

        // 이메일 중복 확인
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다:" + email);
        }

        // 닉네임 중복 확인
        if(userRepository.existsByNickname(nickname)){
            throw new IllegalArgumentException("이미 존재하는 닉네임 입니다:" + nickname);
        }

        // User Entity 생성
        User user = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();

        // DB 저장
        User savedUser = userRepository.save(user);
        log.info("새로운 사용자가 생성됨. ID:{}, Email:{}", savedUser.getId(), savedUser.getEmail());

        // 저장한 User Entitiy를 DTO로 변환
        return UserDto.from(savedUser);
    }

    // 이메일 중복 여부 확인 (회원 가입 페이지에서 사용함)
    public boolean isEmailExists(String email){
        return userRepository.existsByEmail(email);
    }

    // 닉네임 중복 여부 확인
    public boolean isNicknameExists(String nickname){
        return userRepository.existsByNickname(nickname);
    }
}
