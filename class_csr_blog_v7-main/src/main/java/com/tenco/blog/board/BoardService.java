package com.tenco.blog.board;

import com.tenco.blog._core.errors.exception.Exception403;
import com.tenco.blog._core.errors.exception.Exception404;
import com.tenco.blog.reply.Reply;
import com.tenco.blog.user.User;
import com.tenco.blog.utils.Define;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Board 관련 비즈니스 로직을 처리하는 Service 계층
 */
@RequiredArgsConstructor
@Service // IoC 대상
@Transactional(readOnly = true)
// 모든 메서드를 일기 전용 트랜잭션으로 실행(findAll, findById 최적화)
// 성능 최적화 (변경 감지 비활성화), 데이터 수정 방지 ()
// 데이터이스 락(lock) 최소화 하여 동시성 성능 개선
public class BoardService {

    private static final Logger log = LoggerFactory.getLogger(BoardService.class);
    private final BoardJpaRepository boardJpaRepository;

    /**
     * 게시글 목록 조회 - 페이징 처리
     */
    public List<BoardResponse.MainDTO> list(int page, int size) {

        Pageable pageable = PageRequest.of(page,size, Sort.by("id").descending());
        Page<Board> boardPage = boardJpaRepository.findAllJoinUser(pageable);
        List<BoardResponse.MainDTO> boardList = new ArrayList<>();
        for(Board board : boardPage.getContent()){
            BoardResponse.MainDTO mainDTO = new BoardResponse.MainDTO(board);
            // new MainDTO(..);
            //[(MainDTo), (MainDTo), (MainDTo)]
            boardList.add(mainDTO);
        }
        return boardList;
    }

    /**
     * 게시글 상세 보기 - DTO 변환 책임(댓글 포함)
     * Request -->> Controller (토큰(JWT)정보 <- User)
     */
    public BoardResponse.DetailDTO detail(Long id, User sessionUser) {
        // 1. 게시글 조회
        Board board = boardJpaRepository.findByIdJoinUser(id).orElseThrow(
                () -> new Exception404("게시글을 찾을 수 없습니다"));
        // 2. 게시글 작성자 정보 포함해 주어야 함
        // 3. 게시글 소유권 설정(수정/ 삭제버튼 표 시용)
        if (sessionUser != null) {
            boolean isBoardOwner = board.isOwner(sessionUser.getId());
            board.setBoardOwner(isBoardOwner);
        }
        // DTO 변환 책임( 댓글 정보 안에서 처리)
        return new BoardResponse.DetailDTO(board,sessionUser);
    }

    /**
     * 게시글 저장
     */
    // 메서드 레벨에서의 트랜잭선 선언
    @Transactional // 데이 수정이 필요하는 읽지 전용 설정을 해제하고 쓰기 전용로 변환
    public BoardResponse.saveDTO save(BoardRequest.SaveDTO saveDTO, User sessionUser) {
        log.info("게시글 저장 서비스 처리 시작 - 제목 {} , 작성자 {}",
                saveDTO.getTitle(), sessionUser.getUsername());

        // 영속성 컨텍스트 [1차 캐시, 쓰기 지연 개념]

        Board board = saveDTO.toEntity(sessionUser);
        boardJpaRepository.save(board); // 영속성 컨텍스트에서 관리가 됨
        // 응답 DTO 변환 처리
        log.info("게시글 저장 완료 - ID {} , 제목 {}",
                board.getId(), board.getTitle());
        return new BoardResponse.saveDTO(board);
    }

    /**
     * 게시글 삭제 (권한 체크)
     */
    @Transactional
    public void deleteById(Long id, User sessionUser) {
        log.info("게시글 삭제 서비스 시작 - ID {}", id);

        Board board = boardJpaRepository.findById(id).orElseThrow(() -> {
            return new Exception404("삭제하려는 게시글이 없습니다");
        });
        if(!board.isOwner(sessionUser.getId())) {
            throw new Exception403("본인이 작성한 게시글만 삭제할 수 있습니다");
        }
        boardJpaRepository.deleteById(id);
    }



    /**
     *  게시글 수정 - DTO 변환 (권한 체크 포함)
     */
    @Transactional
    public BoardResponse.UpdateDTO update(Long id, BoardRequest.UpdateDTO updateDTO,
                            User sessionUser) {
        log.info("게시글 수정 서비스 시작 - 게시글 ID {}", id);

        Board board = boardJpaRepository.findById(id).orElseThrow(() -> {
            log.warn("게시글 조회 실패 - ID {}", id);
            return new Exception404("해당 게시글이 존재하지 않습니다");
        });

        if(!board.isOwner(sessionUser.getId())) {
            throw new Exception403("본인이 작성한 게시글만 수정 가능");
        }
        board.update(updateDTO);

        log.info("게시글 수정 완료 - 게시글 ID {}, 게시글 제목 {}", id, board.getTitle());
        return new BoardResponse.UpdateDTO(board);
    }
}
