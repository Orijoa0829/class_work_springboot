package com.tenco.blog.temp;


import com.tenco.blog.user.User;
import com.tenco.blog.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController // @Controller + @Response
//@CrossOrigin(origins = "*") //모든 앱에서 요청 허용
public class ApiTest {

    private final UserService userService;

    @GetMapping("/api-test/user")
    public User getUsers() {

        return userService.findById(1L);
    }
}
