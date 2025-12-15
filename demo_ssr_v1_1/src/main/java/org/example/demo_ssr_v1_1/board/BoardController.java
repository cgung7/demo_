package org.example.demo_ssr_v1_1.board;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception401;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception500;
import org.example.demo_ssr_v1_1.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor // DI 처리
@Controller // IoC
public class BoardController {

    private final BoardPersistRepository repository;

    /**
     * 게시글 수정 화면 요청
     * @param id
     * @param model
     * @param session
     * @return
     */
    @GetMapping("/board/{id}/update")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {

        // 인증 검사 O
        User sessionUser = (User)session.getAttribute("sessionUser"); // sessionUser -> 상수

        // 인가 검사 O
        Board board = repository.findById(id);
        if (board == null) {
            throw new Exception500("게시글이 존재하지 않습니다.");
        }

        if (!board.isOwner(sessionUser.getId())) {
            throw new Exception403("게시글 권한이 없습니다.");
        }

        model.addAttribute("board", board);

        return "board/update-form";
    }

    /**
     * 게시글 수정 요청 기능
     * @param id
     * @param updateDTO
     * @param session
     * @return
     */
    @PostMapping("/board/{id}/update")
    public String updateProc(@PathVariable Long id,
                             BoardRequest.UpdateDTO updateDTO,
                             HttpSession session
    ) {
        // 1. 인증 처리 O
        User sessionUser = (User)session.getAttribute("sessionUser");

        // 2. 인가 처리 O
        Board board =repository.findById(id);
        if (!board.isOwner(sessionUser.getId())) {
            throw new Exception403("게시글 권한이 없습니다.");
        }

        try {
            repository.updateById(id, updateDTO);
            // 더티 체킹 활용
        } catch (Exception e) {
            throw new RuntimeException("게시글 수정 실패");
        }

        return "redirect:/board/list";
    }

    /**
     * 게시글 목록 화면 요청
     * @param model
     * @return
     */
    @GetMapping({"/board/list"})
    public String boardList(Model model) {
        List<Board> boardList = repository.findAll();

        model.addAttribute("boardList", boardList);

        return "board/list";
    }

    /**
     * 게시글 작성 화면 요청
     * @param session
     * @return
     */
    @GetMapping("/board/save")
    public String saveForm(HttpSession session) {
        // 인증 처리
        User sessionUser = (User)session.getAttribute("sessionUser");

        return "board/save-form";
    }

    /**
     * 게시글 작성 요청 기능
     * @param saveDTO
     * @param session
     * @return
     */
    @PostMapping("/board/save")
    public String saveProc(BoardRequest.SaveDTO saveDTO, HttpSession session) {
        // HTTP 요청 : username=값&title=값&content=값
        // 스프링이 처리 : new SaveDTO(), setter 메서드 호충해서 값을 넣어줌
        // 인증 처리
        User sessionUser = (User)session.getAttribute("sessionUser");

        Board board = saveDTO.toEntity(sessionUser);

        repository.save(board);

        return "redirect:/";
    }

    /**
     * 게시글 상세 보기 화면 요청
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/board/{id}")
    public String detail(@PathVariable Long id, Model model) {

        Board board = repository.findById(id);
        if (board == null) {
            // 404
            throw new Exception404("게시글을 찾을 수 없습니다.");
        }

        model.addAttribute("board", board);

        return "board/detail";
    }

    /**
     * 게시글 삭제 요청 기능
     * @param id
     * @param session
     * @return
     */
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        // 1. 인증처리 O
        User sessionUser = (User)session.getAttribute("sessionUser");
        
        // 2. 인가처리 O + || 관리자 권한
//        if (!board.getUser().getId().equals(sessionUser.getId())) {
//            System.out.println("게시글 권한이 없습니다.");
//             throw new RuntimeException("권한이 없습니다.");
//        }
        Board board = repository.findById(id);
        if (!board.isOwner(sessionUser.getId())) {
            System.out.println("게시글 권한이 없습니다.");
            throw new Exception403("게시글 권한이 없습니다.");
        }

        repository.deleteById(id);
        return "redirect:/";
    }

}
