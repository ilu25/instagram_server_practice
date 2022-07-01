package com.example.demo.src.post;


import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class PostService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final PostProvider postProvider;
    private final JwtService jwtService;


    @Autowired
    public PostService(PostDao postDao, PostProvider postProvider, JwtService jwtService) {
        this.postDao = postDao;
        this.postProvider = postProvider;
        this.jwtService = jwtService;

    }

    // 게시물 생성
    public PostPostsRes createPosts(int userIdx, PostPostsReq postPostsReq) throws BaseException {
        try {
           int postIdx = postDao.insertPosts(userIdx, postPostsReq.getContent());
           for (int i = 0; i < postPostsReq.getPostImgUrls().size(); i++) {
               postDao.insertPostImgs(postIdx, postPostsReq.getPostImgUrls().get(i));
           }
           return new PostPostsRes(postIdx);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 게시물 수정 (내용만 수정 가능)
    public void modifyPost(int userIdx, int postIdx, PatchPostsReq patchPostsReq) throws BaseException {
        if (postProvider.checkUserExist(userIdx) == 0) {
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if (postProvider.checkPostExist(postIdx) == 0) {
            throw new BaseException(POSTS_EMPTY_POST_ID);
        }
        if (postProvider.checkUserPostExist(userIdx, postIdx)==0){
            throw new BaseException(POSTS_EMPTY_USER_POST);
        }
        try {
            // DAO에서 성공 시 1, 아니면 0 리턴
            int result = postDao.updatePost(postIdx, patchPostsReq.getContent());
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_POST);
            }
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 게시물 삭제
    public void deletePost(int userIdx, int postIdx) throws BaseException {
        if (postProvider.checkUserExist(userIdx) ==0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if (postProvider.checkPostExist(postIdx) == 0) {
            throw new BaseException(POSTS_EMPTY_POST_ID);
        }
        if (postProvider.checkUserPostExist(userIdx, postIdx)==0){
            throw new BaseException(POSTS_EMPTY_USER_POST);
        }
        try {
            // DAO에서 성공 시 1, 아니면 0 리턴
            int result = postDao.deletePost(postIdx);
            if (result == 0) {
                throw new BaseException(DELETE_FAIL_POST);
            }
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
