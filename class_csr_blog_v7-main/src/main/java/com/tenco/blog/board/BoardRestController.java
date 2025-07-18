package com.tenco.blog.board;

import com.tenco.blog._core.common.ApiUtil;
import com.tenco.blog._core.utils.Define;
import com.tenco.blog.user.SessionUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class BoardRestController {

    private final BoardService boardService;

    @GetMapping("/")
    public ResponseEntity<ApiUtil<List<BoardResponse.MainDTO>>> main(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) {


        List<BoardResponse.MainDTO> boardList = boardService.list(page, size);
        return ResponseEntity.ok(new ApiUtil<>(boardList));
    }

    @GetMapping("/api/boards/{id}/detail")
    public ResponseEntity<ApiUtil<BoardResponse.DetailDTO>> detail(
            @PathVariable(name = "id") Long id,
            @RequestAttribute(value = Define.SESSION_USER, required = false) SessionUser sessionUser) {
        BoardResponse.DetailDTO detailDTO = boardService.detail(id, sessionUser);
        return ResponseEntity.ok(new ApiUtil<>(detailDTO));
    }

    // 게시글 작성 API
    @PostMapping("/api/boards")
    public ResponseEntity<?> save(
            @Valid @RequestBody BoardRequest.SaveDTO saveDTO,
            Errors errors,
            @RequestAttribute(value = Define.SESSION_USER, required = false) SessionUser sessionUser) {
        System.out.println("코드 내려옴 1");
        BoardResponse.SaveDTO savedBoard = boardService.save(saveDTO, sessionUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiUtil<>(savedBoard));
    }

    // 게시글 수정 API
    @PutMapping("/api/boards/{id}")
    public ResponseEntity<?> update(@PathVariable(name = "id") Long id,
                                    @RequestAttribute(Define.SESSION_USER) SessionUser sessionUser,
                                    @Valid @RequestBody BoardRequest.UpdateDTO updateDTO,
                                    Errors errors) {
        BoardResponse.UpdateDTO updateBoard = boardService.update(id, updateDTO, sessionUser);
        return ResponseEntity.ok(new ApiUtil<>(updateBoard));
    }

    // 게시글 삭제 API
    @DeleteMapping("/api/boards/{id}")
    public ResponseEntity<ApiUtil<String>> delete(
            @PathVariable(name = "id") Long id,
            @RequestAttribute(Define.SESSION_USER) SessionUser sessionUser) {
        boardService.deleteById(id, sessionUser);
        return ResponseEntity.ok(new ApiUtil<>("게시글 삭제 성공"));
    }
}




