package com.tenco.blog.board;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@RequiredArgsConstructor // ->final 필드나 @NonNull 필드를 파라미터로 받는 생성자를 자동으로 만들어줌.
@Repository // Ioc 대상이 된다 - 싱글톤 패턴으로 관리됨
public class BoardPersistRepository {

    //JPA 핵심 인터페이스

    // DI
    // @Autowired
    private final EntityManager em;

    // 게시글 삭제하기 (영속성 컨텍스트를 활용)
    @Transactional
    public void deleteById (Long id) {
        // 1. 먼저 삭제할 엔티티를 영속 상태로 조회
        Board board = em.find(Board.class, id);
        // 영속 상태의 엔티티를 삭제 상태로 변경
        em.remove(board);
        // 트랜젝션이 커밋 되는 순간 삭제 처리
        // 삭제 과정
        // 1. Board 엔티티가 영속 상태에서 remove() 호출시 삭제 상태로 변경
        // 2. 1차 캐쉬에서 해당 엔티티를 제거
        // 3. 트랜젝션 커밋 시점에 DELETE SQL 자동 실행
        // 4. 연관관계 처리 자동 수행 (cascade 설정 시)

    }










//    @Transactional
//    public void updateById(Long id, Board board) {
//        String jpql = "UPDATE Board b SET b.username = :username, b.title = :title, b.content = :content WHERE b.id = :id";
//        Query query = em.createQuery(jpql)
//                .setParameter("username",board.getUsername())
//                .setParameter("title",board.getTitle())
//                .setParameter("content",board.getContent())
//                .setParameter("id",id);
//        query.executeUpdate();
//
//    }
    @Transactional
    public void update (Long Id, BoardRequest.UpdateDTO updateDTO) {

        Board board = findById(Id);
        // board -> 영속성 컨텍스트 1차 캐쉬에 key=value 값이 저장 되어 있다.
        board.setTitle(updateDTO.getTitle());
        board.setContent(updateDTO.getContent());
        board.setUsername(updateDTO.getUsername());
        // 트랜젝션 끝나면 영속성 컨텍스트에서 변경 감지를 한다.
        // 변경감지 (Dirty Checking)
        // 1. 영속성 컨텍스트가 엔티티 최초 상태를 스냅샷으로 보관.
        // 2. 필드 멤버변수 값 변경 시 현재 상태와 스냅샷 비교
        // 3. 트랜젝션 커밋 시점에 변경된 필드만 UPDATE 쿼리를 자동 생성.
        // 4. Update board_tb set title=?, content=?, username=?, where id=?
    }


    // jpql을 사용한 조회 방법 (비교용)
    public Board findByWithJPQL(Long id) {
        // ? 을 사용하지않는것을 권장 -> 네임드 파라미터 사용
        String jpql = "SELECT b.content FROM Board b WHERE b.id = :id";

        try {
            return em.createQuery(jpql, Board.class)
                    .setParameter("id", id)
                    .getSingleResult();
            //주의 : 결과 없으면 NoResultException
        } catch (Exception e) {
            return null;
        }
        //JPQL 단점 :
        // 1. 1차 캐시 우회하여 항상 DB 먼저 접근함
        // 2. 코드가 복잡할 수 있음.
        // 3. getSingleResult() 를 호출함 -> 예외처리 해줘야 됨.

    }

        // 게시글 한 건 조회
    public Board findById(Long id) {
        return em.find(Board.class, id);
    }

    // JPQL 을 사용한 게시글 목록 조회
    public List<Board> findAll() {
        // JPQL : 엔티티 객체를 대상으로 하는 객체지향 쿼리
        // Board 는 엔티티 클래스명, b는 별칭
        String jpql = " SELECT b FROM Board b ORDER BY b.createdAt DESC";
        //            " select b.* FROM board_tb b ORDER BY b.created_at DESC;
        //v1에서는 em.nativeQuery()
//        Query query = em.createQuery(jpql, Board.class);
//        List<Board> boardList = query.getResultList();
//        return boardList;
        return em.createQuery(jpql, Board.class).getResultList();
    }


    // 게시글 저장 기능 - 영속성 컨텍스트 활용
    @Transactional
    public Board save(Board board) {
        // v1 -> 네이티브 쿼리 활용했다.

        // 1. 매개변수로 받은 board 는 현재 비영속 상태이다 .
        // - 아직 영속성 컨텍스트에 관리되지 않는 상태
        // - 데이터베이스와 아직은 연관이 없는 순수 Java 객체 상태
        // 2. em.persist(board); - 이 엔티티를 영속성 컨텍스트에 저장하는 개념
        // - 영속성 컨텍스트가 board 객체를 관리하게 된다.
        em.persist(board);
        // 3. 트랜잭션 커밋 시점에 Insert 쿼리 실행
        // - 이때 영속성 컨텍스트의 변경사항이 자동으로 Db에 반영 됨.
        // - board 객체의 id 필드에 자동으로 생성된 값이 설정 됨.
        // insert --> DB --> (pk 값 알수 있다)
        // select --> DB --> (할당된 pk 값 조회) 이 과정을 거치지 않아도 된다

        // 4. 영속 상태로 된 board 객체를 반환
        // - 이 시점에는 자동으로 board id 멤버 변수에 db pk 값이 할당된 상태이다.
        return board;
    }

}
