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

    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", id);
        userMap.put("email", email);
        userMap.put("name", name);
        userMap.put("nickname", nickname);
        userMap.put("role", role);
        return userMap;
    }

    public Map<String, Object> toMapWithoutRole() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", id);
        userMap.put("email", email);
        userMap.put("name", name);
        userMap.put("nickname", nickname);
        return userMap;
    }

    public boolean isAuthenticated() {
        return id != null && email != null;
    }
}
