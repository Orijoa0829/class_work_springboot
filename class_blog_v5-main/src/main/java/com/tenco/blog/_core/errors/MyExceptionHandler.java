package com.tenco.blog._core.errors;

import com.tenco.blog._core.errors.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// 모든 컨트롤러에서 발생하는 예외 처리를 이 클래스에서 처리하겠다.
// RuntimeException 발생하면 해당 파일로 예외 처리가 집중 됨
@ControllerAdvice // 에러 페이지로 연결 처리 IoC 대상이된다
// @RestControllerAdvice // 데이터를 반환해서 내려 줄 때 사용
public class MyExceptionHandler {

    // slf4j 로거 생성 - 로깅 사용시 Sysout 대신 활용하는것이 좋다.
    private static final Logger log = LoggerFactory.getLogger(MyExceptionHandler.class);

    @ExceptionHandler(Exception400.class) // 403오류가 터졌을때 이 메서드를 실행해라
    public String ex400(Exception400 e, HttpServletRequest request) {
        log.warn("=== 400 Bad Request 에러 발생 ===");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("인증 오류: {}", e.getMessage());
        log.warn("User-Agent: {}", request.getHeader("User-Agent"));
        request.setAttribute("msg", e.getMessage());
        return "err/400";
    }

    @ExceptionHandler(Exception401.class) // 403오류가 터졌을때 이 메서드를 실행해라
    @ResponseBody // 데이터를 반환 함
    public ResponseEntity<String> ex401ByData(Exception401 e, HttpServletRequest request) {
        String script = "<script> alert('" + e.getMessage() + "'); history.back(); </script>";
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }
//    public String ex401(Exception401 e, HttpServletRequest request) {
//        log.warn("=== 401 UnAuthorized 에러 발생 ===");
//        log.warn("요청 URL: {}",request.getRequestURL());
//        log.warn("인증 오류: {}", e.getMessage());
//        log.warn("User-Agent: {}",request.getHeader("User-Agent"));
//        request.setAttribute("msg",e.getMessage());
//        return "err/401";
//    }

    @ExceptionHandler(Exception403.class) // 403오류가 터졌을때 이 메서드를 실행해라
    public String ex403(Exception403 e, HttpServletRequest request) {

        log.warn("=== 403 Forbidden 에러 발생 ===");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("인증 오류: {}", e.getMessage());
        log.warn("User-Agent: {}", request.getHeader("User-Agent"));

        request.setAttribute("msg", e.getMessage());
        return "err/403";
    }

    @ExceptionHandler(Exception404.class) // 403오류가 터졌을때 이 메서드를 실행해라
    public String ex404(Exception404 e, HttpServletRequest request) {
        log.warn("=== 404 Not Found 에러 발생 ===");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("인증 오류: {}", e.getMessage());
        log.warn("User-Agent: {}", request.getHeader("User-Agent"));
        request.setAttribute("msg", e.getMessage());
        return "err/404";
    }

    @ExceptionHandler(Exception500.class) // 403오류가 터졌을때 이 메서드를 실행해라
    public String ex500(Exception500 e, HttpServletRequest request) {
        log.warn("=== 500 Internal Server Error 에러 발생 ===");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("인증 오류: {}", e.getMessage());
        log.warn("User-Agent: {}", request.getHeader("User-Agent"));
        request.setAttribute("msg", e.getMessage());
        return "err/500";
    }

    // 기타 모든 RuntimeException 처리
    @ExceptionHandler(RuntimeException.class) // 403오류가 터졌을때 이 메서드를 실행해라
    public String handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.warn("=== 예상 못한 런타임 에러 발생 ===");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("인증 오류: {}", e.getMessage());
        log.warn("User-Agent: {}", request.getHeader("User-Agent"));
        request.setAttribute("msg", "시스템 오류 발생, 관리자에게 문의 하세요");
        return "err/500";
    }

}
