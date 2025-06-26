package com.tenco.blog.board;

import com.tenco.blog.user.User;
import com.tenco.blog.utils.MyDateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 기본 생성자 - JPA 엔티티는 기본 생성자가 필요(기본 생성자를 자주 활용함)
@Data
@Table(name = "board_tb")
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String username;
    // V2에서 사용했던 방식
    // private String username;
    // V3 에서 Board 엔티티는 User 엔티티와 연관관계가 성립이 된다.

    // 다대일
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")// 외래키 컬러명 명시
    private User user;

    @CreationTimestamp
    private Timestamp createdAt; // created_at () (스네이크 케이스로 자동 변환)

    // 게시글에 소유자를 직접 확인하는 기능을 만들자
    public boolean isOwner(Long checkUserId) {
        return this.user.getId().equals(checkUserId);
    }

    public String getTime() {
        return MyDateUtil.timestampFormat(createdAt);
    }

}
