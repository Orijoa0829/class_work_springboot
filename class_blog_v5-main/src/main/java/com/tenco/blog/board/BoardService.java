package com.tenco.blog.board;

import com.tenco.blog._core.errors.exception.Exception403;
import com.tenco.blog._core.errors.exception.Exception404;
import com.tenco.blog.user.User;
import com.tenco.blog.utils.Define;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Board 관련 비즈니스 로직을 처리하는 Service 계층
 */
@Service // IoC 대상
@RequiredArgsConstructor
@Transactional(readOnly = true)
// 모든 메서드를 읽기 전용 트랜잭션으로 실행(findAll, findById 최적화)
// 성능 최적화 (변경 감지 비활성화), 데이터 수정 방지 ()
// 데이터베이스 lock 최소화 하여 동시성 성능 개선

public class BoardService {

    private static final Logger log = LoggerFactory.getLogger(BoardService.class);
    private final BoardJpaRepository boardJpaRepository;

    /**
     * 게시글 저장
     */
    @Transactional //데이터 수정이 필요하므로 읽기 전용 설정을 해제하고, 쓰기 전용으로 전환함
    public Board save(BoardRequest.SaveDTO saveDTO, User sessionUser) {
        // 1. 로그 기록 - 게시글 저장 요청 정보
        // 2. DTO 를 Entity로 변환(작성자 정보 포함)
        // 3. 데이터베이스 게시글 저장
        // 4. 저장 완료 로그 기록
        // 5. 저장된 Board 를 Controller 로 반환
        log.info("게시글 저장 서비스 처리 시작 - 제목 {}, 작성자 {}",
                saveDTO.getTitle(), sessionUser.getUsername());
        Board board = saveDTO.toEntity(sessionUser);
        boardJpaRepository.save(board);
        log.info("게시글 저장 완료 - 제목 {} ,내용 {}, 작성자 {}",
                saveDTO.getTitle(), saveDTO.getContent().length(), sessionUser.getUsername());
        return board;
    }

    /**
     * 게시글 목록 조회
     */
    public List<Board> findAll() {
        // 1. 로그 기록
        // 2. 데이터베이스 게시글 조회
        // 3. 로그 기록
        // 4. 조회된 게시글 목록 반환
        log.info("게시글 조회 서비스 처리 시작");
        List<Board> boardList = boardJpaRepository.findAllJoinUser();
        log.info("게시글 목록 조회 완료- 총 {} 개", boardList.size());
        return boardList;
    }

    /**
     * 게시글 상세 조회
     */
    public Board findById(Long id) {
        // 1. 로그 기록
        // 2. 데이터베이스에서 해당 board id 로 조회 - WHERE 절 : 데이터 존재 여부 확실치않음
        // 3. 게시글이 없다면 404 에러 처리.
        // 4. 조회 성공시 로그 기록
        // 5. 조회된 게시글 반환 처리
        log.info("게시글 상세 조회 서비스 시작 - 조회한 게시글 ID {}", id);
        Board board = boardJpaRepository.findByIdJoinUser(id).orElseThrow(() -> {
            log.warn("게시글 조회 실패 - {ID}", id);
            return new Exception404("게시글을 찾을 수 없습니다");
        });
        log.info("게시글 상세 조회 완료 - 제목 {}", board.getTitle());
        return board;

    }

    /**
     * 게시글 수정(권한 체크 포함)
     */
    @Transactional
    public Board updateById(Long id, BoardRequest.UpdateDTO updateDTO,
                            User sessionUser) {
        // 1. 로그 기록
        // 2. 수정하려는 게시글 조회
        // 3. 권한 체크
        // 4. 권한이 없다면 403 예외 발생
        // 5. Board 엔티티 상태값 변경 (더티 체킹)
        // 6. 로그 기록 - 수정 완료
        // 7. 수정된 게시글 반환
        log.info("게시글 수정 서비스 시작 - 게시글 ID {}", id);
        Board board = boardJpaRepository.findById(id).orElseThrow(() -> {
            log.warn("수정할 게시글 조회 실패 - 게시글 ID {} ", id);
            return new Exception404("해당 게시글은 존재하지 않습니다");
        });
        if (!board.isOwner(sessionUser.getId())) {
            throw new Exception403("본인이 작성한 게시글만 수정 가능");
        }
        board.setTitle(updateDTO.getTitle());
        board.setTitle(updateDTO.getContent());
        // TODO - board 엔티티에 update () 만들어 주기
        // 더티 체킹
        log.warn("게시글 수정 완료됨 - 게시글 ID {}, 게시글 제목 {}", id, board.getTitle());
        return board;
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deleteById(Long id, User sessionUser) {
        // 1. 로그 기록
        // 2. 삭제할 게시글 조회
        // 3. 권한 체크
        //     - 없다면 : 에러 발생 예외처리
        //     - 있다면 : --게시글 작성자가 본인인지 확인--
        //              ==> 본인이 아니라면 : 예외처리
        // 4. 삭제
        // 5. 삭제 완료 로그 기록
        log.info("게시글 삭제 서비스 시작 - 게시글 ID {}", id);
        Board board = boardJpaRepository.findById(id).orElseThrow(() -> {
            log.warn("삭제할 게시글이 존재하지 않음.");
            return new Exception404("게시글이 존재하지 않습니다");
        });
        if(!board.isOwner(sessionUser.getId())){
            throw new Exception404("본인이 작성한 게시글만 삭제할 수 있습니다");
        }
        boardJpaRepository.deleteById(id);
        log.info("게시글 삭제 되었습니다. 게시글 ID {}", id);
    }

    /**
     * 게시글 소유자 확인 (수정 화면 요청 확인용)
     */
    public void checkBoardOwner(Long boardId, Long userId) {
        Board board = findById(boardId);
        if(!board.isOwner(userId)){
            throw new Exception403("본인 게시글만 수정 가능합니다.");
        }
    }
}













