package org.example.demo_ssr_v1_1.reply;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception401;
import org.example.demo_ssr_v1_1.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
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
        User sessionUser =  (User) session.getAttribute("sessionUser");
        saveDTO.validate();
        replyService.댓글작성(saveDTO, sessionUser.getId());
        return "redirect:/board/" + saveDTO.getBoardId();
    }

    @PostMapping("/reply/{id}/delete")
    public String deleteProc(@PathVariable(name = "id") Long replyId, HttpSession session) {
       User sessionUser = (User) session.getAttribute("sessionUser");
       Long boardId = replyService.댓글삭제(replyId, sessionUser.getId());
       return "redirect:/board/" + boardId;
    }
}
