package com.tenco.blog.board;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {
    private final BoardPersistRepository br;


    // 게시글 삭제 구현하기
    // 주소 설계 : /board/{{board.id}}/delete
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable(name = "id") Long id) {
        br.deleteById(id);
        return "redirect:/";
    }


    /**
     * 게시글 수정
     * Get 맵핑
     *
     * @param id (board pk)
     * @return update-form.mustache
     */
    @GetMapping("/board/{id}/update-form")
    public String update(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        // 클라이언트가 수정 버튼 액션 -> 서버에서 update-form 머스테치 파일
        Board board = br.findById(id);
        // 머스태치 파일에 조회된 데이터를 바인딩 처리
        request.setAttribute("board", board);
        return "board/update-form";

        //
    }

    //게시글 수정 구현 하기 - 전에 쓰던 내용 나오도록 : 클라이언트가 서버에게 전에 쓰던 내용 데이터를 보내게-post방식
    @PostMapping("/board/{id}/update-form")
    public String update(@PathVariable(name = "id") Long id, BoardRequest.UpdateDTO reqDTO) {
        //트랜젝션
        // 수정 -- select -- 값을 확인해서 -- 데이터를 수정 -- update
        // JPA 영속성 컨텍스트 활용
        br.update(id, reqDTO);
        // 수정 전략 더티 체킹을 활용
        // 장점
        // 1. UPDATE 쿼리 자동생성
        // 2. 변경된 필드만 업데이트 (성능 최적화)
        // 3. 영속성 컨텍스트 일관성 유지
        // 4.

        return "redirect:/";
    }

    //상세보기 구현 하기
    // 주소 설계 : http://localhost:8080/board/{id}
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        //클라이언트가 게시글{id} 클릭 -> 서버에서 detail.mustache 파일 - 화면 보여줌 . GET 방식
        //detail.mustache 파일 변환하는 기능 만들기
        Board board = br.findById(id);
        //-> 1차 캐시 효과 : DB 에 접근하지않고 바로 영속성 컨텍스트에서 꺼낸다.
        request.setAttribute("board", board);
        return "board/detail";
    }


    //메인화면에 리스트 출력하기

    // 주소설계 : http://localhost:8080/, http://localhost:8080/index
    @GetMapping({"/", "/index"})
    public String boardList(HttpServletRequest request) {
        //클라이언트가 메인화면 버튼 클릭 -> 서버에서 클라이언트한테 index.mustache 파일 - 화면 보여줌.Get 방식
        // 1. index.mustache 파일을 반환 시키는 기능 만들기
        List<Board> boardList = br.findAll();
        request.setAttribute("boardList", boardList);
        // 2.
        return "index";
    }

    //게시글 작성 화면 요청 처리
    @GetMapping("/board/save-form")
    public String SaveForm() {
        return "board/save-form";
    }

    //게시글 작성 액션(수행) 처리
    @PostMapping("/board/save")
    public String save(BoardRequest.saveDTO reqDTO) {
        // HTTP 요청 본문 : title="값&content=값&username=값
        // form MIME (application/x-www-form-urlencoded)

        //br.save(reqDTO); < 타입달라서 작동x   ->   DTO -- Board -- DB
//        Board board = new Board(reqDTO.getTitle(), reqDTO.getContent(), reqDTO.getUsername());
//        br.save(board);
        Board board = reqDTO.toEntity();
        br.save(board);
        // Post-Get-Redirect 패턴
        return "redirect:/";
    }


}
