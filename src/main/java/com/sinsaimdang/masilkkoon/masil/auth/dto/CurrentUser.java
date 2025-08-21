package com.sinsaimdang.masilkkoon.masil.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CurrentUser {
    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String role;

    public boolean isAuthenticated() {
        return id != null && email != null;
    }
}
