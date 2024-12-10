package com.example.portfolio.service.board;

import com.example.portfolio.domain.Board;
import com.example.portfolio.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {

    Board createBoard(Board board, List<MultipartFile> files, Users user) throws Exception;

    Board getBoard(Long id);

    Page<Board> getAllBoards(Pageable pageable);

    Board updateBoard(Long id, Board board);

    void deleteBoard(Long id);

}
