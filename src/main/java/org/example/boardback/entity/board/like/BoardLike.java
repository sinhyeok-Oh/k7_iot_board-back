package org.example.boardback.entity.board.like;

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
        name = "board_likes",
        uniqueConstraints = @UniqueConstraint(name = "uk_board_like_user", columnNames = {
                "board_id", "user_id"
        }),
        indexes = {
                @Index(name = "idx_board_like_board", columnList = "board_id"),
                @Index(name = "idx_board_like_user", columnList = "user_id"),
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardLike extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", foreignKey = @ForeignKey(name = "fk_board_like_board"))
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_board_like_user"))
    private User user;

    @Builder
    public BoardLike(Board board, User user) {
        this.board = board;
        this.user = user;
    }
}