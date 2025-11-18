package org.example.boardback.entity.comment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.boardback.entity.base.BaseTimeEntity;
import org.example.boardback.entity.board.Board;
import org.example.boardback.entity.user.User;

@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comments_board_id", columnList = "board_id"),
                @Index(name = "idx_comments_user_id", columnList = "user_id"),
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", foreignKey = @ForeignKey(name = "fk_comment_board"))
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_comment_user"))
    private User writer;

    @Builder
    public Comment(String content, Board board, User writer) {
        this.content = content;
        this.board = board;
        this.writer = writer;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}