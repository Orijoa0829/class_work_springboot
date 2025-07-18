package com.tenco.blog.board;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 게시글 관련 데이터베이스 접근을 담당
 * 기본적인 CRUD 제공
 */
// @Repository 생략 가능 --> JpaRepository -> 선언 되어 있음
public interface BoardJpaRepository extends JpaRepository<Board, Long> {

    // 게시글 목록 - 페이징 처리
    @Query("SELECT b FROM Board b JOIN FETCH b.user u ORDER BY b.id DESC")
    Page<Board> findAllJoinUser(Pageable pageable);

    // 게시글 상세 보기
    @Query("SELECT b FROM Board b JOIN FETCH b.user u WHERE b.id = :id")
    Optional<Board> findByIdJoinUser(@Param("id") Long id);


}
