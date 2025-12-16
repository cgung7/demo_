package org.example.demo_ssr_v1_1.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


/**
 * 사용자 Controller (표현 계층)
 * 핵심 개념 :
 * - HTTP 요청을 받아서 처리
 * - 요청 데이터 검증 및 파라미터 바인딩
 * - Service 레이어에 비즈니스 로직을 위임
 * - 응답 데이터를 View에 전달
 */


@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    // 회원 정보 수정 화면 요청
    // http://localhost:8080/user/update
    @GetMapping("/user/update")
    public String updateUserForm(Model model, HttpSession session) {
        /**
        // HttpServletRequest <--
        // A 사용자 요청 시 -> 웹 서버
        //      -> 톰캣(WAS) Request 객체와 Response 객체를 만들어서 스프링 컨테이너에게 전달

        // 1. 인증 검사 (O)
        // 인증 검사를 할려면 세션 메모리에 접근해서 사용자의 정보 존재 여부 확인
        User sessionUser = (User)session.getAttribute("sessionUser");
        // LoginInterceptor 가 알아서 처리

        // 2. 인가 처리
        // 로그인 사용자 확인 O
        User user = userService.findById(sessionUser.getId());
        // 세션의 사용자 ID로 회원 정보 조회
        if (user == null) {
            throw new Exception404("사용자를 찾을 수 없습니다.");
        }

        if (!user.isOwner(sessionUser.getId())) {
            throw new Exception403("회원 정보 수정 권한이 없습니다.");
        }

        model.addAttribute("user", user);

        return "user/update-form";
     */
        User sessionUser = (User) session.getAttribute("sessionUser");

        User user = userService.회원정보수정화면(sessionUser.getId());
        model.addAttribute("user", user);

        return "user/update-form";
    }

    // 회원 정보 수정 기능 요청 - 더티 체킹
    // http://localhost:8080/user/update
    @PostMapping("/user/update")
    public String updateProc(UserRequest.UpdateDTO updateDTO, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        try {
            // 유효성 검사 (형식 검사)
            updateDTO.validate();
            User updateUser = userService.회원정보수정(updateDTO, sessionUser.getId());
            // 회원 정보 수정은 - session 갱신
            session.setAttribute("sessionUser", updateUser);
            return "redirect:/";
        } catch (Exception e) {
            return "user/update-form";
        }
    }

    // 로그아웃 기능 요청
    // http://localhost:8080/logout
    @GetMapping("logout")
    public String logout(HttpSession session) {

        // 세션 무효화
        session.invalidate();

        return "redirect:/";
    }

    // 로그인 화면 요청
    // http://localhost:8080/login
    @GetMapping("/login")
    public String loginForm() {

        return "user/login-form";
    }

    // JWT 토근 기반 인증 X -> 세션 기반 인증 처리
    // 로그인 기능 요청
    // http://localhost:8080/login
    @PostMapping("/login")
    public String loginProc(UserRequest.LoginDTO loginDTO, HttpSession session) {
        // 1. 인증 검사 X - 로그인 요청

        // 4. 로그인 성공 또는 실패 처리
        try {
            // 2. 유효성 검사
            loginDTO.validate();
            User sessionUser = userService.로그인(loginDTO);

            session.setAttribute("sessionUser", sessionUser);
            return "redirect:/";
        } catch (Exception e) {
            // 로그인 실패 시 다시 로그인 화면 전환
            return "user/login-form";
        }
    }


    // 회원가입 화면 요청
    // http://localhost:8080/join
    @GetMapping("/join")
    public String joinForm() {

        return "user/join-form";
    }

    // 회원가입 기능 요청
    // http://localhost:8080/join
    @PostMapping("/join")
    public String joinProc(UserRequest.JoinDTO joinDTO) {
        joinDTO.validate();
        userService.회원가입(joinDTO);
        return "redirect:/login";
    }
}
