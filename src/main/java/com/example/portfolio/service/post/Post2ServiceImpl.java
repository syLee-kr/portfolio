package com.example.portfolio.service.post;

import com.example.portfolio.domain.Post2;
import com.example.portfolio.repository.Post2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Post2ServiceImpl implements Post2Service {

    @Autowired
    private Post2Repository post2Repository;

    public List<Post2> getRecentTenPosts() {
        // 0번째 페이지, 10개 항목 요청
        Pageable pageable = PageRequest.of(0, 10);
        return post2Repository.findAllByOrderByCountDesc(pageable).getContent();
    }
}