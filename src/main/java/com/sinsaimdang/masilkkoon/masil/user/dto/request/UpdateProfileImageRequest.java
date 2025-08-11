package com.sinsaimdang.masilkkoon.masil.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateProfileImageRequest {

    @Size(max = 1000, message = "포르필 이미지 URL은 1000자리를 초과할 수 없습니다.")
    private String profileImageUrl;
}
