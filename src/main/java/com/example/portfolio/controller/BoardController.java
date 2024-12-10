package com.example.portfolio.controller;

import com.example.portfolio.domain.Board;
import com.example.portfolio.domain.Comment;
import com.example.portfolio.domain.File;
import com.example.portfolio.domain.Users;
import com.example.portfolio.service.GoogleCloudStorageService;
import com.example.portfolio.service.board.BoardService;
import com.example.portfolio.service.comment.CommentService;
import com.example.portfolio.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    private final GoogleCloudStorageService gcsService;
    private final BoardService boardService;
    private final UserService userService;
    private final CommentService commentService;

    @Autowired
    public BoardController(GoogleCloudStorageService gcsService, BoardService boardService, UserService userService, CommentService commentService) {
        this.gcsService = gcsService;
        this.boardService = boardService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping
    public String getBoardList(@RequestParam(defaultValue = "0") int page, Model model) {
        logger.info("Fetching board list for page: {}", page);
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Board> boards = boardService.getAllBoards(pageable);

        int totalPages = boards.getTotalPages();
        int currentPage = boards.getNumber() + 1;

        int pageGroup = (int) Math.ceil((double) currentPage / 5);
        int startPage = (pageGroup - 1) * 5 + 1;
        int endPage = Math.min(pageGroup * 5, totalPages);

        logger.debug("Pagination calculated: startPage={}, endPage={}, totalPages={}", startPage, endPage, totalPages);

        model.addAttribute("boards", boards);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "board/board";
    }

    @GetMapping("/{id}")
    public String getBoardDetail(@PathVariable Long id, Model model, HttpSession session) {
        logger.info("Fetching details for board ID: {}", id);
        Board board = boardService.getBoard(id);
        if (board == null) {
            logger.warn("Board with ID {} not found", id);
            return "error/404";
        }

        Users currentUser = (Users) session.getAttribute("user");
        List<File> files = board.getFiles() != null ? board.getFiles() : new ArrayList<>();
        List<Comment> comments = commentService.getCommentsByBoardId(id);

        logger.debug("Board details loaded: files={}, comments={}", files.size(), comments.size());

        model.addAttribute("board", board);
        model.addAttribute("files", files);
        model.addAttribute("comments", comments != null ? comments : new ArrayList<>());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("loggedInUsername", currentUser != null ? currentUser.getUserId() : "anonymous");

        return "board/detail";
    }

    @PostMapping("/del/{id}")
    public String deleteBoard(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Attempting to delete board ID: {}", id);
        try {
            // 해당 게시글의 댓글 삭제
            commentService.deleteCommentsByBoardId(id);
            logger.debug("Deleted comments for board ID: {}", id);

            // 게시글 삭제
            boardService.deleteBoard(id);
            logger.info("Board ID {} deleted successfully", id);

            redirectAttributes.addFlashAttribute("success", "게시글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            logger.error("Error deleting board ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "게시글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/board";
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id, @RequestParam("content") String content, HttpSession session) {
        logger.info("Adding comment to board ID: {}", id);
        try {
            Users currentUser = (Users) session.getAttribute("user");
            if (currentUser == null) {
                logger.warn("Add comment failed: User not logged in");
                return ResponseEntity.status(403).body("로그인이 필요합니다.");
            }

            Board board = boardService.getBoard(id);
            if (board == null) {
                logger.warn("Add comment failed: Board ID {} not found", id);
                return ResponseEntity.badRequest().body("게시글을 찾을 수 없습니다.");
            }

            Comment comment = Comment.builder()
                    .board(board)
                    .user(currentUser)
                    .content(content)
                    .build();

            commentService.saveComment(comment);
            logger.info("Comment added successfully to board ID: {}", id);
            return ResponseEntity.ok("댓글이 추가되었습니다.");
        } catch (Exception e) {
            logger.error("Error adding comment to board ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, HttpSession session) {
        logger.info("Loading new board creation form");
        Users currentUser = (Users) session.getAttribute("user");
        if (currentUser == null) {
            logger.warn("User not logged in. Setting default user object.");
            currentUser = new Users();
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("board", Board.builder().priority(0).build());
        return "board/new";
    }

    @PostMapping("/create")
    public String createBoard(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            @RequestParam("userId") String userId,
            @RequestParam(value = "priority", defaultValue = "0") int priority,
            RedirectAttributes redirectAttributes
    ) {
        logger.info("Creating new board: title={}, userId={}", title, userId);
        try {
            // 유저 확인
            Users user = userService.findById(userId);
            if (user == null) {
                logger.warn("User ID {} not found", userId);
                redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 유저입니다.");
                return "redirect:/board/new"; // 에러 발생 시 다시 게시글 작성 페이지로 리다이렉트
            }

            // 게시글 생성
            Board board = Board.builder()
                    .title(title)
                    .content(content)
                    .user(user)
                    .priority(priority)
                    .build();
            logger.debug("Board object created: {}", board);

            // 파일 첨부 처리
            if (files != null && files.length > 0) {
                logger.debug("Processing {} uploaded files", files.length);
                List<File> uploadedFiles = new ArrayList<>();
                for (MultipartFile file : files) {
                    logger.debug("Processing file: {}", file.getOriginalFilename());
                    if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
                        logger.warn("Skipping file with empty name");
                        continue; // 파일 이름이 없는 경우 무시
                    }
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");
                    String fileUrl = gcsService.uploadFile(file);
                    logger.debug("File uploaded: name={}, url={}", fileName, fileUrl);

                    File uploadedFile = File.builder()
                            .name(fileName)
                            .type(file.getContentType())
                            .size(file.getSize())
                            .url(fileUrl)
                            .isImage(file.getContentType() != null && file.getContentType().startsWith("image"))
                            .board(board)
                            .user(user)
                            .build();

                    uploadedFiles.add(uploadedFile);
                }
                board.setFiles(uploadedFiles);
                logger.debug("Attached {} files to board ID: {}", uploadedFiles.size(), board.getId());
            }

            // 게시글 저장
            boardService.createBoard(board, files != null ? List.of(files) : null, user);
            logger.info("Board created successfully: title={}, id={}", title, board.getId());

            redirectAttributes.addFlashAttribute("successMessage", "게시글이 성공적으로 저장되었습니다.");
            return "redirect:/board";
        } catch (Exception e) {
            logger.error("Error creating board: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "게시글 저장 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/board/new";
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBoard(@PathVariable Long id, @RequestBody Board board) {
        logger.info("Updating board ID: {}", id);
        try {
            Board updatedBoard = boardService.updateBoard(id, board);
            if (updatedBoard == null) {
                logger.warn("Board update failed for ID: {}", id);
                return ResponseEntity.badRequest().body("게시글 업데이트 실패");
            }
            logger.info("Board updated successfully: ID={}, title={}", id, updatedBoard.getTitle());
            return ResponseEntity.ok(updatedBoard);
        } catch (Exception e) {
            logger.error("Error updating board ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body("게시글 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long id) {
        logger.info("Deleting board ID: {}", id);
        try {
            boardService.deleteBoard(id);
            logger.info("Board ID {} deleted successfully", id);
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } catch (Exception e) {
            logger.error("Error deleting board ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body("게시글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/comments/add")
    public String addComment(
            @RequestParam("boardId") Long boardId,
            @RequestParam("content") String content,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        logger.info("Adding comment to board ID: {}", boardId);
        try {
            Users currentUser = (Users) session.getAttribute("user");
            if (currentUser == null) {
                logger.warn("Add comment failed: User not logged in");
                redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
                return "redirect:/board/" + boardId;
            }

            // 게시글 가져오기
            Board board = boardService.getBoard(boardId);
            if (board == null) {
                logger.warn("Add comment failed: Board ID {} not found", boardId);
                redirectAttributes.addFlashAttribute("error", "게시글을 찾을 수 없습니다.");
                return "redirect:/board";
            }

            // 댓글 저장
            Comment comment = Comment.builder()
                    .board(board)
                    .content(content)
                    .user(currentUser)
                    .build();
            commentService.saveComment(comment);
            logger.info("Comment added successfully to board ID: {}", boardId);

            redirectAttributes.addFlashAttribute("success", "댓글이 성공적으로 등록되었습니다.");
            return "redirect:/board/" + boardId;
        } catch (Exception e) {
            logger.error("Error adding comment to board ID {}: {}", boardId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "댓글 저장 중 오류 발생: " + e.getMessage());
            return "redirect:/board/" + boardId;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteComment(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        logger.info("Delete request received for comment ID: {}", id);
        try {
            // 현재 로그인된 사용자 확인
            Users currentUser = (Users) session.getAttribute("user");
            if (currentUser == null) {
                logger.warn("User is not logged in.");
                redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
                return "redirect:/board";
            }

            // 댓글 존재 여부 확인
            Comment comment = commentService.getCommentById(id);
            if (comment == null) {
                logger.warn("Comment with ID {} not found.", id);
                redirectAttributes.addFlashAttribute("error", "댓글을 찾을 수 없습니다.");
                return "redirect:/board";
            }

            // 권한 확인
            if (!comment.getUser().getUserId().equals(currentUser.getUserId())) {
                logger.warn("User {} does not have permission to delete comment ID: {}", currentUser.getUserId(), id);
                redirectAttributes.addFlashAttribute("error", "댓글 삭제 권한이 없습니다.");
                return "redirect:/board/" + comment.getBoard().getId();
            }

            // 댓글 삭제
            Long boardId = comment.getBoard().getId();
            commentService.deleteComment(id);
            logger.info("Comment ID {} deleted successfully from board ID {}", id, boardId);
            redirectAttributes.addFlashAttribute("success", "댓글이 성공적으로 삭제되었습니다.");
            return "redirect:/board/" + boardId;
        } catch (Exception e) {
            logger.error("Error deleting comment ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());

            // 댓글을 찾을 수 없는 경우 처리
            Comment comment = commentService.getCommentById(id);
            if (comment != null) {
                return "redirect:/board/" + comment.getBoard().getId();
            }
            return "redirect:/board";
        }
    }
}
