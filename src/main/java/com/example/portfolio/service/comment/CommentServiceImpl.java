package com.example.portfolio.service.comment;

import com.example.portfolio.domain.Comment;
import com.example.portfolio.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    @Override
    public List<Comment> getCommentsByBoardId(Long boardId) {
        return commentRepository.findByBoardId(boardId);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public Comment saveComment(Comment comment) {
        if (comment.getBoard() == null || comment.getContent() == null || comment.getUser() == null) {
            throw new IllegalArgumentException("유효하지 않은 댓글 데이터입니다.");
        }
        return commentRepository.save(comment);
    }

    @Override
    public void deleteCommentsByBoardId(Long boardId) {
            commentRepository.deleteByBoardId(boardId);
    }
}
