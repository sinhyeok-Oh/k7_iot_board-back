package org.example.boardback.entity.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.boardback.entity.base.BaseTimeEntity;
import org.example.boardback.entity.board.like.BoardLike;
import org.example.boardback.entity.comment.Comment;
import org.example.boardback.entity.user.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "boards",
        indexes = {
                @Index(name = "idx_boards_created_at", columnList = "created_at"),
                @Index(name = "idx_boards_updated_at", columnList = "updated_at"),
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "view_count", nullable = false)
    private long viewCount = 0L;

    @Column(name = "is_pinned", nullable = false)
    private boolean pinned = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_board_user"))
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_board_category"))
    private BoardCategory category;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BoardLike> likes = new HashSet<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    // == 편의 메서드 == //
    public void increaseViewCount() { this.viewCount++; }
    public void pin() { this.pinned = true; }
    public void unpin() { this.pinned = false; }

    @Builder
    public Board(String title, String content, User writer, BoardCategory category) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.category = category;
    }

    public void changeContent(String title, String content, BoardCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}