package org.example.demo_ssr_v1_1._core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 로그인 인증 확인용 인터셉터 구축
 *
 * 컨터롤러에 진입하기 전에 세션에 로그인 정보가 있는지 없는지 확인
 * 로그인 하지 않은 사용자는 E..401 throw 예정
 */
// 1. HandlerInterceptor 구현
@Component // IoC 컨테이너에 이 클래스를 빈 객체로 등록 함 (싱글톤 패턴으로 관리 됨)
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * preHandle : 컨트롤러 진입 전에 실행되는 메서드
     * 동작 흐름 - 요청이 들어오면 (등록된 URI시) 컨트롤러 보다 먼저 동작
     * 
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
