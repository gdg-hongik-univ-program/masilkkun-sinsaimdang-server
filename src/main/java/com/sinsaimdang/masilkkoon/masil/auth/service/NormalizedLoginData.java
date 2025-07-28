package com.sinsaimdang.masilkkoon.masil.auth.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NormalizedLoginData {
    private final String email;
    private final String password;
}