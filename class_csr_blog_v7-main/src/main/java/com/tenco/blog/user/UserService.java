package com.tenco.blog.user;

import com.tenco.blog._core.errors.exception.Exception400;
import com.tenco.blog._core.errors.exception.Exception401;
import com.tenco.blog._core.errors.exception.Exception403;
import com.tenco.blog._core.errors.exception.Exception404;
import com.tenco.blog._core.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserJpaRepository userJpaRepository;


    // 회원 가입 처리
    @Transactional
    public UserResponse.JoinDTO join(UserRequest.JoinDTO joinDTO) {
        userJpaRepository.findByUsername(joinDTO.getUsername())
                .ifPresent(user1 -> {
                    throw new Exception400("이미 존재하는 사용자명입니다");
                });
        User savedUser = userJpaRepository.save(joinDTO.toEntity());
        return new UserResponse.JoinDTO(savedUser);
    }

    // 로그인 처리
    public String login(UserRequest.LoginDTO loginDTO) {
        User selectedUser = userJpaRepository
                .findByUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword())
                .orElseThrow(() -> {
                    throw new Exception401("사용자명 또는 비밀번호가 틀렸어요");
                });
        // JWT 발급해서 Controller 단으로 넘겨 주면 된다.
        String jwt = JwtUtil.create(selectedUser);
        return jwt;
    }

    // 회원 정보 조회
    public UserResponse.DetailDTO findUserById(Long requestUserId, Long sessionUserId) {
        // 권한 검사
        if (!requestUserId.equals(sessionUserId)) {
            throw new Exception403("본인 정보만 조회 가능합니다");
        }
        // 정보 조회
        User selectedUser = userJpaRepository.findById(requestUserId).orElseThrow(() -> {
            throw new Exception404("사용자를 찾을 수 없습니다");
        });
        return new UserResponse.DetailDTO(selectedUser);
    }


    // 회원 정보 수정
    @Transactional
    public UserResponse.UpdateDTO updateById(Long requestUserId,
                                             Long sessionUserId,
                                             UserRequest.UpdateDTO updateDTO) {
        if (!requestUserId.equals(sessionUserId)) {
            throw new Exception403("본인 정보만 조회 가능합니다");
        }
        User selectedUser = userJpaRepository.findById(requestUserId).orElseThrow(() -> {
            throw new Exception404("사용자를 찾을 수 없습니다");
        });
        selectedUser.update(updateDTO);
        return new UserResponse.UpdateDTO(selectedUser);
    }

}
