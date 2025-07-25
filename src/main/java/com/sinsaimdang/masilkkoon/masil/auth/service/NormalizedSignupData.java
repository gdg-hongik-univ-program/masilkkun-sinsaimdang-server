package com.sinsaimdang.masilkkoon.masil.auth.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NormalizedSignupData {
    private final String email;
    private final String password;
    private final String name;
    private final String nickname;
}
