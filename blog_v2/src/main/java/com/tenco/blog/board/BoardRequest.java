package com.tenco.blog.board;

import lombok.Data;

/**
 * 클라이언트에게서 넘어온 데이터를 오브젝트로 변환해서 전달하는 DTO 역할을 담당한다.
 */

public class BoardRequest {

    @Data
    public static class saveDTO {
        // 정적 내부 클래스로  기능별로 DTO 관리할것.
        // 게시글 저장 요청 데이터를 담을 것이다.
        // static 클래스이므로 BoardRequest.saveDTO 로 접근가능
        private String title;
        private String content;
        private String username;

        // br.save(req.Get ''') 코드 아름답지 않음..
        //  DTO 에서 엔티티로 변환하는 메서드 만들기->
        //  계층간 데이터 변환을 명확하게 분리하기 위함.
        public Board toEntity() {

            return new Board(title, content, username);
        }

    } // end of saveDTO

    @Data
    public static class UpdateDTO {
        // 정적 내부 클래스로  기능별로 DTO 관리할것.
        // 게시글 저장 요청 데이터를 담을 것이다.
        // static 클래스이므로 BoardRequest.saveDTO 로 접근가능
        private String title;
        private String content;
        private String username;

        // 검증 메서드 (유효성 검사 기능을 추가)
        public void validate() throws IllegalAccessException{
            if(title == null || title.trim().isEmpty()){
                throw new IllegalAccessException("제목은 필수입니다");
            }
            if(content == null || content.trim().isEmpty()){
                throw new IllegalAccessException("내용은 필수 입니다.");
            }
        }

        public Board toEntity() {

            return new Board(title, content, username);
        }

    } // end of updateDTO

}
