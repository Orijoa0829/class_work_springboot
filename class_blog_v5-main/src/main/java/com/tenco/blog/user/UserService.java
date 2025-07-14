package com.tenco.blog.user;

import com.tenco.blog._core.errors.exception.Exception400;
import com.tenco.blog._core.errors.exception.Exception404;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Data
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 클래스 레벨에서의 읽기 전용 설정
public class UserService {

    private final UserJpaRepository userJpaRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * 회원 가입 처리
     */
    @Transactional
    public User join(UserRequest.JoinDTO joinDTO) {
        // 1. 사용자명 중복체크
        userJpaRepository.findByUsername(joinDTO.getUsername()).ifPresent(user1 -> {
            throw new Exception400("이미 존재하는 사용자명 입니다.");
        });
        return userJpaRepository.save(joinDTO.toEntity());
        // TODO- 왜 jpa의 save 가 유저객체 반환하지 ?
    }

    /**
     * 로그인 처리
     */
    public User login(UserRequest.LoginDTO loginDTO) {
        return userJpaRepository.findByUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword())
                .orElseThrow(() -> {
                    return new Exception400("사용자명 또는 비밀번호가 틀렸어요");
                });
    }

    /**
     * 사용자 정보 조회
     */
    public User findById(Long id) {
        return userJpaRepository.findById(id).orElseThrow(() -> {
            log.warn("사용자 정보 조회 실패 - ID {}", id);
            return new Exception404("사용자를 찾을 수 없음.");
        });
    }

    /**
     * 회원 정보 수정 처리 (더티 체킹)
     */
    @Transactional
    public User updateById(Long userId, UserRequest.UpdateDTO updateDTO) {
        // 1. 사용자 조회
        // 2. 수정된 User 반환 왜? -> 세션 동기화 때문.
        User user = findById(userId);
        //user.update(updateDTO); TODO 추후 추가
        user.setPassword(updateDTO.getPassword());
        return user;
    }


}
