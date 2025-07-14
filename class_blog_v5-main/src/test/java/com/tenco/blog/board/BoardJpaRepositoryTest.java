package com.tenco.blog.board;

import com.tenco.blog.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class BoardJpaRepositoryTest {
    @Autowired
    private BoardJpaRepository boardJpaRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    public void save_test() {
        //given
        User testUser = User.builder()
                .username("testUser")
                .password("1234")
                .email("AA@naver.com")
                .build();

        Board board = Board.builder()
                .title("테스트")
                .content("테스트 내용")
                .user(testUser)
                .build();
        Board board2 = Board.builder()
                .title("테스트2")
                .content("테스트 내용2")
                .user(testUser)
                .build();
        //게시글 저장 테스트
        boardJpaRepository.save(board);
        boardJpaRepository.save(board2);
        Assertions.assertThat(board.getUser().getId()).isEqualTo(board2.getUser().getId());

    }


}
