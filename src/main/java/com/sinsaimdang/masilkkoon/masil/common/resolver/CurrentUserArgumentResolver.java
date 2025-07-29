package com.sinsaimdang.masilkkoon.masil.common.resolver;

import com.sinsaimdang.masilkkoon.masil.auth.dto.CurrentUser;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        Long userId = (Long) request.getAttribute("currentUserId");
        String userEmail = (String) request.getAttribute("currentUserEmail");
        String userName = (String) request.getAttribute("currentUserName");
        String userNickname = (String) request.getAttribute("currentUserNickname");
        String userRole = (String) request.getAttribute("currentUserRole");

        return new CurrentUser(userId, userEmail, userName, userNickname, userRole);
    }
}