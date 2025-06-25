package com.tenco.blog.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor //DI 처리
@Controller
public class UserController {

    private final UserRepository userRepository;
    // httpsession <<--- 세션 메모리에 접근 할 수 있음.
    private final HttpSession httpsession;



    // 주소 설계 : http://localhost:8080/user/update-form
    @GetMapping("/user/update-form")
    public String updateForm (HttpServletRequest request) {
        User sessionUser = (User) httpsession.getAttribute("sessionUser");
        if(sessionUser == null){
            return "redirect:/login-form";
        }
        request.setAttribute("user",sessionUser);
        return "user/update-form";
    }
    // 회원 정보 수정 액션 처리
    @PostMapping("/user/update") // 세션에서 id값 있기때문에 /user/{id}/update 안해도됨
    public String update (UserRequest.UpdateDTO reqDTO,
                          HttpServletRequest request) {
        User sessionUser =(User)httpsession.getAttribute("sessionUser");
        if(sessionUser==null){
            return "redirect:/login-form";
        }
        //데이터 유효성 검사 처리
        reqDTO.validate();

        User updateUser = userRepository.updateById(sessionUser.getId(), reqDTO);
        // User user = userRepository.findById(sessionUser.getId());

        // 세션 동기화
        httpsession.setAttribute("sessionUser",updateUser);

        // 다시 회원 정보 보기 화면 요청
        return "redirect:/user/update-form?";//시작줄에 공백,한글 안됨
    }



    /**
     * 회원 가입 화면 요청
     *
     * @return join-form.mustache
     */
    // 요청 되어 오는 주소 -> /join-form
    @GetMapping("/join-form")
    public String joinForm() {
        return "user/join-form";
    }


    //회원 가입 액션 처리
    @PostMapping("/join")
    public String join(UserRequest.JoinDTO joinDTO, HttpServletRequest request) {
        System.out.println("-----------------------------회원가입 요청 ---------------------------------------");
        System.out.println("사용자 명" + joinDTO.getUsername());
        System.out.println("사용자 이메일" + joinDTO.getEmail());

        try {
            // 1. 입력된 데이터 검증 (유효성 검사)
            joinDTO.validate();
            // 2. 사용자명 중복 체크
            User existUser = userRepository.findByUsername(joinDTO.getUsername());
            if (existUser != null) {
                throw new IllegalArgumentException("이미 존재하는 사용자 명 입니다." + joinDTO.getUsername());
            }
            // 3. DTO 를 User Object 로 변환
            User user = joinDTO.toEntity();

            // 4. User Object 를 영속화 처리
            userRepository.save(user);

            return "redirect:/login-form";
        } catch (Exception e) {
            // 검증 실패 시 보통 에러 메세지와 함께 다시 폼으로 보냄.
            request.setAttribute("errorMessage", "잘못된 요청입니다");
            return "user/join-form";
        }

    }


    /**
     * 로그인 화면 요청
     *
     * @return login-form.mustache
     */
    @GetMapping("/login-form")
    public String loginForm() {
        // 반환값이 뷰(파일) 이름이 됨 (뷰 리졸버가 실제 파일 경로를 찾아 감)
        return "user/login-form";
    }

    //로그인 액션 처리
    //자원의 요청은 get 방식이지만 단 로그인요청만 제외-보안상 이유

    @PostMapping("/login")
    public String login(UserRequest.LoginDTO loginDTO) {
        System.out.println("=========로그인시도=========");
        System.out.println("사용자명 : " + loginDTO.getUsername());

        try {
            loginDTO.validate();
            User user = userRepository.findByUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword());
            //로그인 실패
            if (user == null) {
                //로그인 실패 : 일치된 사용자 없음
                throw new IllegalArgumentException("사용자 명 혹은 비밀번호를 확인하세요");
            }
            // 로그인 성공
            httpsession.setAttribute("sessionUser", user);

            // 로그인 성공 후 리스트 페이지 이동
            return "redirect:/";
        } catch (Exception e) {
            return "user/login-form";
        }
    }
    //로그아웃 처리
    @GetMapping("/logout")
    public String logout() {
        // "redirect: " 스프링 에서 접두사를 사용하면 다른 URL 로 리다이렉트 됨
        // 즉 리다렉트 한다는 것은 뷰를 렌더링 하지 않고 브라우저가 재 요청을
        // 다시 하게끔 유도 한다.
        httpsession.invalidate();
        return "redirect:/";
    }






}
