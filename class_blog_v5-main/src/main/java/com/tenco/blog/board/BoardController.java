package com.tenco.blog.board;

import com.tenco.blog._core.errors.exception.Exception403;
import com.tenco.blog.user.User;
import com.tenco.blog.utils.Define;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private static final Logger log = LoggerFactory.getLogger(BoardController.class);
    private final BoardService boardService;

    // 게시글 수정 화면 요청
    @GetMapping("/board/{id}/update-form")
    public String updateForm(@PathVariable(name = "id") Long boardId,
                             HttpServletRequest request, HttpSession session) {
        User sessionUser = (User) session.getAttribute(Define.SESSION_USER);
        boardService.checkBoardOwner(boardId, sessionUser.getId());
        request.setAttribute("board", boardService.findById(boardId));
        return "board/update-form";
    }



    @PostMapping("/board/{id}/update-form")
    public String update(@PathVariable(name = "id") Long boardId,
                         BoardRequest.UpdateDTO reqDTO,
                         HttpSession session) {
        // 1. 인증 검사
        // 2. 데이터 유효성 검사
        // 3. 수정 요청 서비스단에 위임
        // 4. 리다이렉트
        reqDTO.validate();
        User sessionUser = (User) session.getAttribute("sessionUser");
        boardService.updateById(boardId, reqDTO, sessionUser);
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable(name = "id") Long id, HttpSession session) {
        // 1. 인증 검사
        // 2. 세션 유저 정보 추출
        // 3. 서비스단에 삭제 처리 위임
        // 4. 메인페이지로 리다이렉트 처리
        User sessionUser = (User) session.getAttribute("sessionUser");
        boardService.deleteById(id, sessionUser);
        return "redirect:/";
    }

    @GetMapping("/board/save-form")
    public String saveForm() {
        log.info("게시글 작성 화면 요청");
        return "board/save-form";
    }

    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO reqDTO, HttpSession session) {
        // 1. 인증검사
        // 2. 유효성 검사
        // 3. 서비스단에 저장기능 위임
        reqDTO.validate();
        User sessionUser = (User) session.getAttribute("sessionUser");
        boardService.save(reqDTO, sessionUser);
        return "redirect:/";
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Board> boardList = boardService.findAll();
        model.addAttribute("boardList", boardList);
        return "index";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("board", boardService.findById(id));
        return "board/detail";
    }
}
