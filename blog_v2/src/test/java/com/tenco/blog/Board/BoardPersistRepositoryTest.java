package com.tenco.blog.Board;

import com.tenco.blog.board.Board;
import com.tenco.blog.board.BoardPersistRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

@Import(BoardPersistRepository.class)
@DataJpaTest
public class BoardPersistRepositoryTest {

    @Autowired
    private BoardPersistRepository br;

    @Test
    public void deleteById_test () {
        //given
        Long id = 1L;
        //when
        //삭제 할 게시글이 존재하는지 확인
        Board targetBoard = br.findById(id);
        Assertions.assertThat(targetBoard).isNotNull();
        //영속성 컨텍스트에서 삭제 실행
        br.deleteById(id);
        //then
        List<Board> afterDeleteBoardList = br.findAll();
        Assertions.assertThat(afterDeleteBoardList.size()).isEqualTo(3);

    }




    @Test
    public void findById_test () {

        //given
        //더미데이터 4개 있음
        //insert into board_tb(title, content, username, created_at) values('제목1','내용1','ssar',now());
        //insert into board_tb(title, content, username, created_at) values('제목2','내용2','ssar',now());
        //insert into board_tb(title, content, username, created_at) values('제목3','내용3','cos',now());
        //insert into board_tb(title, content, username, created_at) values('제목4','내용4','love',now());
        //when
        // id 기반으로 1건 조회 했을 때,
        Board board = br.findById(1L);
        //than
        //유저네임이 "", 제목이 "", 내용이 "" 이어야 한다.
        Assertions.assertThat(board.getId()).isNotNull();
        Assertions.assertThat(board.getTitle()).isEqualTo("제목1");
        Assertions.assertThat(board.getContent()).isEqualTo("내용1");
    }

    @Test
    public void save_test () {
        //given
        Board board = new Board("제목123","내용123","승민군");
        // 저장 전 객체의 상태값 확인
        Assertions.assertThat(board.getId()).isNull();
        System.out.println("db에 저장 전 board : " + board);
        //when
        // 영속성 컨텍스트를 통한 엔티티 저장
        Board savedBoard = br.save(board);
        //then
        // 1. 저장 후에 자동생성된 ID 확인
        Assertions.assertThat(savedBoard.getId()).isNotNull();
        Assertions.assertThat(savedBoard.getId()).isGreaterThan(0);
        // 2. 내용 일치 여부 확인
        Assertions.assertThat(savedBoard.getTitle()).isEqualTo("제목123");

        // 3. JPA 가 자동으로 생성된 생성 시간 확인
        Assertions.assertThat(savedBoard.getCreatedAt()).isNotNull();

        // 4. 원본 객체 board , 영속성 컨텍스트에 저장한 - savedBoard
        Assertions.assertThat(board).isSameAs(savedBoard);

        System.out.println(savedBoard);

    }

    @Test
    public void findAll_test() {
        // given
        // db/data.sql (4개의 더미 데이터 있음)

        // when
        List<Board> boardList = br.findAll();

        // then
        System.out.println("size 테스트 :" + boardList.size());
        System.out.println("첫번째 게시글 제목 확인 : " + boardList.get(0).getTitle());

        //네이티브 쿼리를 사용한다는 것은.. 영속성 컨텍스트를 우회 해서 일 처리 하는 것이고,
        //JPQL 도 바로 영속성 컨텍스트를 우회하지만, 조회된 이후에는 영속성 상태가 된다.

        // -> JPQL 은 처음에는 DB 에서 직접 데이터를 가져오지만, 가져온 후에는 해당 객체를
        // 영속성 컨텍스트에 등록해서 ‘영속 상태’로 관리한다는 뜻입니다.
        int cnt = 0;
        for(Board board : boardList) {
            //Assertions.assertThat(board.getId()).isNotNull();
            //System.out.println("id 값 확인 : ");
            System.out.print("");
        }
    }
}
