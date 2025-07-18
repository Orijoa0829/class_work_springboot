package com.tenco.blog.board;


import com.tenco.blog._core.utils.MyDateUtil;
import com.tenco.blog.reply.Reply;
import com.tenco.blog.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "board_tb")
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 외래키 컬러명 명시
    private User user;

    @CreationTimestamp
    private Timestamp createdAt;

    @Transient
    private boolean isBoardOwner;

    public boolean isOwner(Long checkUserId) {
        return this.user.getId().equals(checkUserId);
    }

    public String getTime() {
        return MyDateUtil.timestampFormat(createdAt);
    }

    /**
     * 게시글과 댓글을 양방향 맵핑으로 설계 하겠다.
     * 하나의 게시글(one)에는 여러개의 댓글(Many)을 가질 수 있다.
     * <p>
     * 테이블 기준으로 고민을 해 본다면 게시글 테이블과 댓글 테이블 관계를
     * 형성할 때 FK는 누가 들고 있어야 맞는가?
     * 연관관계에 주인은 Reply 이 되어야 한다 (FK)
     * mappedBy : 외래키 주인이 아닌 엔티티에 설정해야 한다.
     * <p>
     * cascade = CascadeType.REMOVE
     * 영속성 전이
     * - 게시글 삭제 시 관련된 모든 댓글도 자동 삭제 처리 함
     * - 데이터 무결성 보장
     */
    @OrderBy("id DESC") // 정렬 옵션 설정
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", cascade = CascadeType.REMOVE)
    List<Reply> replies = new ArrayList<>(); // List 선언과 동시에 초기화

    public void update(BoardRequest.UpdateDTO updateDTO) {
        this.title = updateDTO.getTitle();
        this.content = updateDTO.getContent();
    }

    public String getWriterName() {
        return this.getUser().getUsername();
    }

}



