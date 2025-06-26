package com.tenco.blog.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.SqlReturnType;

@Import(UserRepository.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired // DI 처리
    private UserRepository userRepository;

    @Test
    public void findByUsernameAndPassword_로그인_성공_테스트() {
        String username = "ssar";
        String password = "1234";

        User user = userRepository.findByUsernameAndPassword(username,password);

        Assertions.assertThat(user).isNotNull(); // 로그인 성공
        Assertions.assertThat(user.getUsername()).isEqualTo("ssar");
    }




    @Test
    public void save_회원가입_테스트() {
        //given
        User user = User.builder()
                .username("testUser")
                .email("a@naver.com")
                .password("asd1234")
                .build();

            User saveUser = userRepository.save(user);

            // then
            // id
            Assertions.assertThat(saveUser.getId()).isNotNull();
            // 데이터가 정상 등록 확인
            Assertions.assertThat(saveUser.getUsername()).isEqualTo("testUser");

            // 원본 user Object 와 영속화된 Object 가 동일한 객체인지 (참조) 확인
            // 영속성 컨텍스트는 같은 엔티티에 대해 같은 인스턴스 보장
            Assertions.assertThat(user).isSameAs(saveUser);

        }
    @Test
    public void findByUsername_사용자_조회_테스트() {
            // given
        String username = "insertUser";
            // when
        User user = userRepository.findByUsername(username);
            // then
        Assertions.assertThat(user).isEqualTo(username);
    }

    @Test
    public void findByUsername_존재하지_않는_사용자_테스트() {
        // given
        String username = "nonUser";
        // when
        User user = userRepository.findByUsername(username);
        // then
        Assertions.assertThat(user).isNull();
    }
}
