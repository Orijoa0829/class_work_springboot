package com.tenco.blog.board;


import com.tenco.blog.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller // IoC 대상 - 싱글톤 패턴으로 관리 됨
public class BoardController {


    // DI 처리
    private final BoardRepository boardRepository;
    private final HttpSession httpSession;

    // 게시글 삭제 액션 처리
    // 1. 로그인 여부 (인증 검사)
    // - 로그인 X -> 로그인 페이지로 리다이렉트처리
    // - 로그인 O -> 게시물의 존재 여부 다시 확인 - > 있으면 ? - > 이미 삭제된 게시물 입니다.
    // 2. 권한 체크
    // 3. 삭제 -> 리다이렉트처리
    @PostMapping("board/{id}/delete")
    public String delete(@PathVariable(name = "id") Long id) {
        // 1. 로그인 체크 - Define.SESSION_USER
        User sessionUser = (User) httpSession.getAttribute("sessionUser");
        if (sessionUser == null) {
            // 로그인 페이지로 리다이렉트 처리
            // redirect:/ --> 내부에서 페이지를 찾는게 아닌 다시 클라이언트에와서 Get
            return "redirect:/login-form";
        }
        // 관리자가 게시물 강제 삭제 할 경우 예외 발생확률 있음.
        // -- > 게시물 존재 여부 다시 확인
        Board board = boardRepository.findById(id);
        if (board == null) {
            throw new IllegalArgumentException("이미 삭제된 게시물 입니다.");
        }
        // 소유자 확인 : 권한 체크
        if (!board.isOwner(sessionUser.getId())) {
            throw new RuntimeException("삭제 권한이 없습니다");
        }
//        if (!(sessionUser.getId() == board.getUser().getId())) {
//            throw new RuntimeException("삭제 권한이 없습니다.");
//        }
        // 권한 확인 이후 삭제 처리
        boardRepository.deleteById(id);

        // 삭제 성공시 리다이렉트 처리.
        return "redirect:/";

    }


    /**
     * 게시글 수정하기 화면 요청
     * 주소설계 : http://localhost:8080/board/{id}/update-form
     * 1. 인증 검사
     * 2. 수정할 게시글 존재 여부 확인
     * 3. 권한 체크
     * 4. 수정 폼에 기존 데이터 뷰 바인딩 처리 하기.
     */
    @GetMapping("/board/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        User sessionUser = (User) httpSession.getAttribute("sessionUser");

        if (sessionUser == null) {
            //로그인 화면으로 팅구기
            return "redirect:/login-form";
        }
        Board board = boardRepository.findById(id);
        if (board == null) {
            throw new RuntimeException("수정할 게시글이 존재하지 않습니다");
        }

        if (!board.isOwner(sessionUser.getId())) {
            throw new RuntimeException("수정 권한이 없습니다");
        }
        request.setAttribute("board", board);
        return "board/update-form";
    }

    /**
     * 게시글 수정하기 액션
     * 더티체킹 활용
     * 로직 생각해보기
     * 글 수정하기 버튼 클릭
     * 인증 검사
     * - 인증 통과O
     * - 미인증 -> 예외처리 팅구기
     * 게시글 존재 여부 확인
     * 존재 O
     * - 수정 권한이 있는가 ? 세션유저아이디와 보드.유저 아이디 값 비교
     * -같다면 ? ->보드레파지토리.업데이트 메서드 실행
     * -다르다면 ?->예외 처리: 수정 권한이 없습니다.
     * 존재 X
     * - 예외처리 팅구기
     */
    @PostMapping("/board/{id}/update-form")
    public String update(@PathVariable(name = "id") Long id, BoardRequest.UpdateDTO reqDTO) {
        User sessionUser = (User) httpSession.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }
        // 2. 사용자 입력값 유효성 검사
        reqDTO.validate();

        // 3. 권한 체크를 위한 조회
        Board board = boardRepository.findById(id);
        // board - 1차 캐시에 들어가 있음.
        if (!board.isOwner(sessionUser.getId())) {
            throw new RuntimeException("수정 권한이 없습니다");
        }
        boardRepository.updateById(id, reqDTO);
        return "redirect:/board/{id}";
    }


    // 게시글 작성 화면 요청

    /**
     * 주소 설계 : http://localhost:8080/board/save-form
     *
     * @return
     */
    @GetMapping("/board/save-form")
    public String saveForm() {
        User sessionUser = (User) httpSession.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }
        // 권한 체크 : 로그인된 사용자만 이동
        return "board/save-form";
    }

    // http://localhost:8080/board/save
    //게시글 저장 액션 처리
    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO reqDTO) {
        try {
            // 1. 권한 체크
            User sessionUser = (User) httpSession.getAttribute("sessionUser");
            if (sessionUser == null) {
                return "redirect:/login-form";
            }
            // 2. 유효성 검사
            reqDTO.validate();
            // 3. 저장
            boardRepository.save(reqDTO.toEntity(sessionUser));
            return "redirect:/";

        } catch (Exception e) {
            e.printStackTrace();
            return "board/save-form";
        }


    }

    @GetMapping("/")
    public String index(HttpServletRequest request) {

        // 1. 게시글 목록 조회
        List<Board> boardList = boardRepository.findByAll();
        // 2. 생각해볼 사항 - Board 엔티티에는 User 엔티티와 연관관계 중
        // 연관 관계 호출 확인
        // boardList.get(0).getUser().getUsername();
        // 3. 뷰에 데이터 전달
        request.setAttribute("boardList", boardList);
        return "index";
    }


    /**
     * 게시글 상세 보기 화면 요청
     *
     * @param id      - 게시글 pk
     * @param request (뷰에 데이터 전달)
     * @return detail.mustache
     */
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        Board board = boardRepository.findById(id);
        request.setAttribute("board", board);
        return "board/detail";
    }
}
