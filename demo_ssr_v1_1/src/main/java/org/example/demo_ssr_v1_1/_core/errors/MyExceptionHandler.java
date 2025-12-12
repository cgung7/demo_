package org.example.demo_ssr_v1_1._core.errors;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.demo_ssr_v1_1._core.errors.exception.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @ControllerAdvice - 모든 컨트롤러에서 발생하는 예외를 이 클래스에서 중앙 집중화 시킴
// RestControllerAdvice = @ControllerAdvice + @ResponseBody
@RestControllerAdvice
@Slf4j
public class MyExceptionHandler {

    // 내가 지켜볼 예외를 명시를 해주면 ControllerAdvice 가 가지고와 처리 함
    @ExceptionHandler(Exception400.class)
    @ResponseBody
    public ResponseEntity<String> ex400(Exception400 e) {
        String script = "<script>alert('"+e.getMessage()+"');" +
                "history.back();" +
                "</script>";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    // 401 인증 오류
    @ExceptionHandler(Exception401.class)
    @ResponseBody
    public ResponseEntity<String> ex401(Exception401 e) {
        String script = "<script>alert('"+e.getMessage()+"');" +
                "history.back();" +
                "</script>";

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    // 403 인가 오류
    @ExceptionHandler(Exception403.class)
    @ResponseBody
    public ResponseEntity<String> ex403(Exception403 e) {
        String script = "<script>alert('"+e.getMessage()+"');" +
                "history.back();" +
                "</script>";

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    // 404 인가 오류
    @ExceptionHandler(Exception404.class)
    @ResponseBody
    public ResponseEntity<String> ex404(Exception404 e) {
        String script = "<script>alert('"+e.getMessage()+"');" +
                "history.back();" +
                "</script>";

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    // 500 서버 내부 오류
    @ExceptionHandler(Exception500.class)
    @ResponseBody
    public ResponseEntity<String> ex500(Exception500 e) {
        String script = "<script>alert('"+e.getMessage()+"');" +
                "history.back();" +
                "</script>";

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    // 기타 모든 실행시점 오류 처리
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        String script = "<script>alert('"+e.getMessage()+"');" +
                "history.back();" +
                "</script>";

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }
}
