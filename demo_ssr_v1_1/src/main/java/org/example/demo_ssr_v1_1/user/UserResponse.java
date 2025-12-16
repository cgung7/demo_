package org.example.demo_ssr_v1_1.user;

import lombok.Data;

/**
 * 사용자 응답 DTO
 */
public class UserResponse {

    /**
     * 회원 정보 수정화면 DTO
     */
    @Data
    public static class UpdateFormDTO {
        private Long id;
        private String username;
        private String email;

        public UpdateFormDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    } // end if static inner class

    /**
     * 로그인 응답 DTO (세션 저장용)
     * - 세션에 엔티티 정보를 저장하지만
     * 다를 곳으로 전달할 때는 DTO를 사용하는 것을 권장 사항
     */
    @Data
    public static class LoginDTO {
        private Long id;
        private String username;
        private String email;

        public LoginDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
        }
    }
}
