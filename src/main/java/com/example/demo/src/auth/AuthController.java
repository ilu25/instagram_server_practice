package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

import com.example.demo.src.auth.model.PostLoginReq;
import com.example.demo.src.auth.model.PostLoginRes;
import com.example.demo.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AuthProvider authProvider;
    @Autowired
    private final AuthService authService;
    @Autowired
    private final JwtService jwtService;


    public AuthController(AuthProvider authProvider, AuthService authService, JwtService jwtService) {
        this.authProvider = authProvider;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    // 로그인
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            // 이메일 입력 여부 validation
            if (postLoginReq.getEmail() == null)
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            // 비번 입력 여부 validation
            if (postLoginReq.getPwd() == null)
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            // 이메일 형식적 validation (정규식)
            if (!isRegexEmail(postLoginReq.getEmail()))
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);

            PostLoginRes postLoginRes = authService.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            // log.error(exception.getMessage());   로그인 시 pwd 크기 작아서 오류 났었음
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}