package com.example.demo.src.auth.model;


import com.example.demo.src.post.model.PostImgsUrlReq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLoginReq {
    private String email;   // id
    private String pwd;
}
