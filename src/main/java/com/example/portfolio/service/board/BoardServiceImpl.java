package com.example.portfolio.service.board;

import com.example.portfolio.domain.Board;
import com.example.portfolio.domain.Users;
import com.example.portfolio.repository.BoardRepository;
import com.example.portfolio.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private FileService fileService;

    @Override
    public Board createBoard(Board board, List<MultipartFile> files, Users user) throws Exception {
        board.setUser(user);

        // regdate가 null이면 기본값 설정
        if (board.getRegdate() == null) {
            board.setRegdate(new Date());
        }

        Board savedBoard = boardRepository.save(board);

        if (files != null && !files.isEmpty()) {
            fileService.uploadFiles(files, user, savedBoard);
        }

        return savedBoard;
    }

    // 모든 게시글 가져오기 (중요 게시글 우선 정렬)
    public Page<Board> getAllBoards(Pageable pageable) {
        return boardRepository.findAllOrderByPriority(pageable);
    }

    @Override
    public Board getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다. ID: " + id));

        // 조회수 증가
        board.setViews(board.getViews() + 1);
        boardRepository.save(board); // 변경된 조회수 저장

        // 저장된 board 객체 반환
        return board;
    }


    @Override
    public Board updateBoard(Long id, Board board) {
        Board existingBoard = boardRepository.findById(id).orElse(null);
        if (existingBoard != null) {
            existingBoard.setTitle(board.getTitle());
            existingBoard.setContent(board.getContent());
            return boardRepository.save(existingBoard);
        }
        return null;
    }

    @Override
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

}
