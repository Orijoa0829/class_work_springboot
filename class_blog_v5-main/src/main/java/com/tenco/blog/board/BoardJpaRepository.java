package com.tenco.blog.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 게시글 관련 데이터 베이스 접근을 담당
 * 기본적인 CRUD 제공
 */
// @Repository 생략 가능 : JpaRepository에 선언 되어있음.

public interface BoardJpaRepository extends JpaRepository<Board, Long> {

    // 기본 CRUD 기능 제외한 추가적인 기능은 직접 선언해주어야 한다.
    // 게시글과 사용자 정보가 포함된 엔티티를 만들어주어야 한다(게시글 리스트용)
    @Query("SELECT b FROM Board b JOIN FETCH b.user u ORDER BY b.id DESC")
    List<Board> findAllJoinUser();
    // JOIN FETCH 는 모든 Board 엔티티와 연관된 User 를 한방 쿼리로 가져옴
    // LAZY 방식이기 때문에 쿼리 N+1 문제 방지를 할 수 있음.
    // LAZY: 게시글 10개가 있다면 지연 로딩1 (Board 조회) + 10(User 조회) = 11번 쿼리가 발생

    // 게시글 ID 로 한방에 유저 정보도 가져오기 - JOIN FETCH 사용하면 됨.
    @Query("SELECT b FROM Board b JOIN FETCH b.user u WHERE b.id = :id")
    Optional<Board> findByIdJoinUser(@Param("id")Long id);
}
