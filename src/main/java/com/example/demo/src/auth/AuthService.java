package com.example.demo.src.auth;


import com.example.demo.config.BaseException;
import com.example.demo.src.auth.model.PostLoginReq;
import com.example.demo.src.auth.model.PostLoginRes;
import com.example.demo.src.auth.model.User;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class AuthService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthDao authDao;
    private final AuthProvider authProvider;
    private final JwtService jwtService;


    @Autowired
    public AuthService(AuthDao authDao, AuthProvider authProvider, JwtService jwtService) {
        this.authDao = authDao;
        this.authProvider = authProvider;
        this.jwtService = jwtService;

    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        User user = authDao.getPwd(postLoginReq);
        String encryptPwd;      // 새로 받은 비번 암호화 (SHA256에 있는 암호화 알고리즘 사용)

        try {
            encryptPwd = new SHA256().encrypt(postLoginReq.getPwd());
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        if (user.getPwd().equals(encryptPwd)) {
            int userIdx = user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);     // 이상 없으면 jwt 발급 (JwtService 이용)
            return new PostLoginRes(userIdx, jwt);
        }
        else
            throw new BaseException(FAILED_TO_LOGIN);
    }
}
