package org.example.demo_ssr_v1_1.board;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.PageRequest;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Table(name = "board_tb")
@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private String username;

    // pc --> db
    @CreationTimestamp
    private Timestamp createdAt;

    public Board(String title, String content, String username) {
        this.title = title;
        this.content = content;
        this.username = username;
    }

    // Board 상태값 수정하는 로직
    public void update(BoardRequest.UpdateDTO updateDTO) {
        // 유효성 검사 처리
        updateDTO.validate();

        this.title = updateDTO.getTitle();
        this.content = updateDTO.getContent();
        this.username = updateDTO.getUsername();
    }

    // 개별 필드 수정 - title
    public void updateTitle(String newTitle) {

        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("게시글의 제목은 공백일 수 없습니다");
        }
        this.title = newTitle;
    }

    // 개별 필드 수정 - content
    public void updateContent(String newContent) {

        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("게시글의 내용을 필히 입력해주세요.");
        }
        this.content = newContent;
    }

}
