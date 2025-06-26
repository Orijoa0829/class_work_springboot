package com.tenco.blog.user;

import com.tenco.blog._core.errors.exception.Exception400;
import com.tenco.blog.board.BoardRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class UserRepository {

    private static final Logger log = LoggerFactory.getLogger(BoardRepository.class);
    private final EntityManager em;

    @Transactional
    public User updateById(Long id, UserRequest.UpdateDTO reqDTO) {
        log.info("회원 정보 수정 - 시작 ID : {}", id);
        User user = findById(id);
        user.setPassword(reqDTO.getPassword());
        return user;
    }

    public User findByUsernameAndPassword(String username, String password) {
        // 필요 하면 직접 예외 처리 설정
        try {
            String jpql = " SELECT u FROM User u " +
                    " WHERE u.username = :username AND u.password = :password ";
            TypedQuery typedQuery = em.createQuery(jpql, User.class);
            typedQuery.setParameter("username", username);
            typedQuery.setParameter("password", password);
            return (User) typedQuery.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public User save(User user) {
        log.info("회원 정보 저장 시작");
        em.persist(user);
        return user;
    }

    public User findByUsername(String username) {
        log.info("중복 사용자 이름 조회");
        // 필요하다면 직접 예외 처리
        try {
            String jqpl = " SELECT u FROM User u WHERE u.username = :username ";
            return em.createQuery(jqpl, User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public User findById(Long id) {
        log.info("사용자 조회 - ID : {}", id);
        User user = em.find(User.class, id);
        if (user == null) {
            throw new Exception400("사용자를 찾을 수 없습니다");
        }
        return user;
    }
}
