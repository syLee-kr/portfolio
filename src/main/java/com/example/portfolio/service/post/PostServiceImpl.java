package com.example.portfolio.service.post;

import com.example.portfolio.domain.Post;
import com.example.portfolio.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    /**
     * 모든 게시물 가져오기
     */
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getLatestPost() {
        List<Post> posts = postRepository.findAllByOrderByPseqDesc();
        if (posts.isEmpty()) {
            System.out.println("게시물이 없습니다.");
            return null;
        } else {
            System.out.println("가장 최근 게시물: " + posts.get(0));
            return posts.get(0);
        }
    }
}
