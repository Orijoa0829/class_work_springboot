package com.tenco.blog.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor // 생성자 의존 주입 - DI 처리
@Repository
public class UserRepository {

    private final EntityManager em;


    /**
     * 로그인 요청 기능 (사용자 정보 조회)
     *
     * @param username
     * @param password
     * @return 성공시 유저 엔티티, 실패시 null 반환
     */
    public User findByUsernameAndPassword(String username, String password) {
        try {
            String jpql = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password";
            TypedQuery typedQuery = em.createQuery(jpql, User.class);
            typedQuery.setParameter("username", username);
            typedQuery.setParameter("password", password);
            return (User) typedQuery.getSingleResult();
        } catch (Exception e) {
            // 일치하는 사용자가 없거나 에러 발생 시 null 반환
            // 즉, 로그인 실패를 의미함
            return null;
        }

    }

    /**
     * 회원 정보 저장 처리
     *
     * @param user (비영속 상태)
     * @return user 엔티티 반환
     */
    @Transactional // 셀렉트를 제외하고 트랜젝션 필수
    public User save(User user) {
        //매개 변수에 들어오는 user object 는 비 영속화 된 상태이다.
        em.persist(user); //이때가 영속화되는 시점 - 영속성 컨텍스트에 user 객체를 관리하기 시작함.
        //트랜잭션 커밋 시점에 실제 INSERT 쿼리를 실행한다.
        return user; //
    }

    // 사용자명 중복 체크용 조회 기능
    public User findByUsername(String username) {
        // select --> 네이티브 쿼리, JPQL
//        String jpql = "SELECT u FROM User u WHERE u.username = :username";
//        TypedQuery typedQuery = em.createQuery(jpql, User.class);
//        typedQuery.setParameter("username",username);
//        return typedQuery.getSingleResult();
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        try {
            return em.createQuery(jpql, User.class).setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            return null;
        }


    }

    public User findById(Long id) {
        User user = em.find(User.class, id);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다");
        }
        return user;

    }

    @Transactional
    public User updateById(Long id, UserRequest.UpdateDTO reqDTO) {
        // 조회, 객체의 상태값 변경, 트랜잭션처리 --> update
        User user = findById(id);
        // 객체의 상태값을 메서드 통해서 변경한다
        user.setPassword(reqDTO.getPassword());
        // 수정된 영속 엔티티 반환(세션 동기화 용)
        return user;
    }
}
