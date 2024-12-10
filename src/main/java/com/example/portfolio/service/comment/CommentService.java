package com.example.portfolio.service.comment;

import com.example.portfolio.domain.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByBoardId(Long boardId);
    Comment getCommentById(Long id); // 댓글 조회
    void deleteComment(Long commentId); // 댓글 삭제
    Comment saveComment(Comment comment); // 댓글 저장
    void deleteCommentsByBoardId(Long boardId);
}
