package com.sinsaimdang.masilkkoon.masil.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * - 사용자 정보를 저장하는 Entity 클래스<br>
 * - 회원가입 시 사용자 정보 저장<br>
 * - 로그인 시 사용자 인증 정보 제공<br>
 * - 토큰 생성을 위한 정보 제공<br>
 * - 회원가입시 이메일&로그인 중복 검사를 통한 고유성 보장<br>
 * - 사용자 권한 관리 (추후 구현 예정)<br>
 *
 * @see UserRole
 * @see com.sinsaimdang.masilkkoon.masil.user.repository.UserRepository
 * @see com.sinsaimdang.masilkkoon.masil.user.service.UserService
 * @see com.sinsaimdang.masilkkoon.masil.auth.service.AuthService
 */

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    /**
     * Primary Key<br>
     * - DB 에서 자동으로 생성, AUTO_INCREMENT 방식으로 자동 증가<br>
     * - 사용자 식별자로 사용됨
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User Email <br>
     * - 회원가입 시 중복검사로 고유성 보장 <br>
     * - 로그인 시 사용자 아이디로 사용됨 <br>
     *<br>
     * 제약조건<br>
     * - UNIQUE, NOT_NULL<br>
     * - 최대 길이 100<br>
     * - SignupRequest DTO 에서 이메일 형식 검증 수행<br>
     * - AuthService 에서 중복 여부 검증
     *
     * @see com.sinsaimdang.masilkkoon.masil.auth.dto.SignupRequest
     * @see com.sinsaimdang.masilkkoon.masil.auth.dto.LoginRequest
     */
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    /**
     * User Password <br>
     * - 회원가입 시 BCrypt 알고리즘으로 암호화하여 저장 <br>
     * {@code String encodedPassword = PasswordEncoder.encode(password)}<br>
     * - 로그인 시 평문과 암호화된 암호 검증<br>
     * {@code passwordEncoder.matches(password, user.getPassword())}<br>
     *
     * 요구사항
     * - SignUpRequest 에서 8-20자리 길이 요구<br>
     * - (구현필요) 영문 대소문자, 숫자, 특수기호 포함<br>
     * - (구현필요) 비밀번호 변경 기능<br>
     * <br>
     * 제약조건<br>
     * - NOT_NULL<br>
     * - 암호화되어 저장되므로 충분한 길이 확보 : 최대 길이 255 <br>
     * - API Response 에 포함 금지
     *
     * @see com.sinsaimdang.masilkkoon.masil.auth.dto.SignupRequest
     * @see com.sinsaimdang.masilkkoon.masil.auth.service.AuthService
     */
    @Column(nullable = false)
    private String password;

    /**
     * User Name<br>
     * - 사용자의 실제 이름
     * <br>
     * 제약조건<br>
     * - NOT_NULL
     * - SignUpRequest Dto 에서 길이 제약 : 최대 길이 10
     * @see com.sinsaimdang.masilkkoon.masil.auth.dto.SignupRequest
     */
    @Column(nullable = false)
    private String name;

    /**
     * User NickName<br>
     * - 서비스 내에서 다른 사람에게 표시되는 별명<br>
     * - 게시글, 댓글 작성자명으로 사용됨<br>
     * <br>
     * 제약조건<br>
     * - NOT_NULL
     * - SignUpRequest Dto 에서 길이 제약 : 최대 길이 10
     * @see com.sinsaimdang.masilkkoon.masil.auth.dto.SignupRequest
     */
    @Column
    private String nickname;

    /**
     * User Role<br>
     * - 사용자 / 관리자 역할 구분하기 위해 사용됨<br>
     * - 관리자는 계정은 별도로 생성하고, 회원가입을 사용하는 Entity 에 대해서는 모두 USER Role 을 부여함
     *
     * @see UserRole
     */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.USER;

    /**
     * 계정 생성 일시<br>
     * <br>
     * - 사용자 계정이 생성된 시기를 기록합니다<br>
     * - JPA Auditing 기능을 통해서 Entity 생성시 자동으로 기록됩니다.
     * @see com.sinsaimdang.masilkkoon.masil.common.config.JpaAuditing
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 사용자 정보 수정 일시<br>
     * <br>
     * - 사용자 닉네임, 비밀번호가 수정된 시간을 기록합니다 (이후 수정 기능 구현 필요)
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}