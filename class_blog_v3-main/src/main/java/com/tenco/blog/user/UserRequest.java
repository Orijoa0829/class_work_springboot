package com.tenco.blog.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinDTO {
        private String username;
        private String password;
        private String email;

        // JoinDTO 를 User Object 로 반환 하는 메서드 추가
        public User toEntity() {
            return User.builder()
                    .username(this.username)
                    .password(this.password)
                    .email(this.email)
                    .build();
        }

        // 회원가입시 유효성 검증 메서드 추가
        public void validate() {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("사용자 명은 필수입니다");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("비밀번호는 필수입니다");
            }
            // 간단한 이메일 형식 검증 (정규화 표현식)
            if (!email.contains("@")) {
                throw new IllegalArgumentException("이메일 형식을 확인하세요");
            }
        }
    }

    // 로그인용 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDTO {
        private String username;
        private String password;

        // 유효성 검사
        public void validate() {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("사용자 명 을 확인하세요");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("비밀번호를 확인하세요");
            }
        }
    }
    // 회원 정보 수정용 DTO
    @Data
    public static class UpdateDTO {

        private String password;
        private String email;
        // username <-- 유니크 - 변경불가
        // toEntity() x 더티체킹 사용

        public void validate () {
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("비밀번호는 필수입니다");
            }
            if(password.length() < 4){
                throw new IllegalArgumentException("비밀번호는 4자 이상이어야 합니다");
            }
            // 간단한 이메일 형식 검증 (정규화 표현식)
            if (!email.contains("@")) {
                throw new IllegalArgumentException("이메일 형식을 확인하세요");
            }


        }


    }


}
