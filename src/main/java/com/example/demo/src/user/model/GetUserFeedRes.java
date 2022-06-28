package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserFeedRes {
    private boolean _isMyFeed;  // 내 피드와 다른 사람 피드 볼 때 다르게 하기 위해
    private GetUserInfoRes getUserInfo;
    private List<GetUserPostsRes> getUserPosts;
}
