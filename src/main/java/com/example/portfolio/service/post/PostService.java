package com.example.portfolio.service.post;

import com.example.portfolio.domain.Post;

import java.util.List;

public interface PostService {
    List<Post> getAllPosts();
    Post getLatestPost();
}
