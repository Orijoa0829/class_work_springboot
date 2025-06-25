package com.tenco.blog.board;

import com.tenco.blog.user.User;
import jakarta.servlet.http.HttpSession;
import lombok.Data;

/**
 * 클라이언트에게 넘어온 데이터를
 * Object로 변화해서 전달하는 DTO 역할을 담당한다
 */
public class BoardRequest {

    /**
     * 게시글 저장(쓰기) DTO
     */
    @Data
    public static class SaveDTO {
        private String title;
        private String content;


        public Board toEntity(User user) {
            return Board.builder()
                    .title(this.title)
                    .user(user)
                    .content(this.content)
                    .build();
        }

        public void validate() {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("제목은 필수 입니다");
            }
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수 입니다");
            }
        }
    }
    /**
     * 게시글 수정 DTO
     */
    @Data
    public static class UpdateDTO {
        private String title;
        private String content;
        //유효성 검사
        public void validate() {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("제목은 필수 입니다");
            }
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("내용은 필수 입니다");
            }
        }
//        public Board toEntity(){
//
//            return Board.builder()
//                    .title(this.title)
//                    .content(this.content)
//                    .build();
        }

    }

