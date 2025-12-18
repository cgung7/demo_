package org.example.demo_ssr_v1_1.reply;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception401;
import org.example.demo_ssr_v1_1.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    /**
     * 댓글 작성 기능 요청
     * @param saveDTO
     * @param session
     * @return
     */
    @PostMapping("/reply/save")
    public String saveProc(ReplyRequest.SaveDTO saveDTO, HttpSession session) {
        // 1. 인증 검사 (로그인 여부 확인) -> 로그인 인터셉터가 인증검사 함 -> WebMvcConfig "reply/**" 설정
        User sessionUser = (User)session.getAttribute("sessionUser");
        // 2. 유효성 검사 (형식 검사)
        saveDTO.validate();
        // 3. 댓글 작성 서비스 요청
        replyService.댓글작성(saveDTO, sessionUser.getId());
        // 4. 게시글 상세보기 화면 전환(리다이렉트 처리)
        return "redirect:/board/" + saveDTO.getBoardId();
    }

    @PostMapping("/reply/{id}/delete")
    public String deleteProc(@PathVariable(name = "id") Long replyId, HttpSession session) {
        // 1. 인증 검사
        User sessionUser = (User)session.getAttribute("sessionUser");
        // 2. 댓글 삭제 서비스 요청
        Long boardId = replyService.댓글삭제(replyId, sessionUser.getId());

        // 3. 댓글 삭제 후 게시글 상세보기 리다이렉트 처리
        return "redirect:/board/" + boardId;
    }
}
