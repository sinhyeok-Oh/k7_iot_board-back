package org.example.boardback.entity.board.draft;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.boardback.entity.base.BaseTimeEntity;
import org.example.boardback.entity.user.User;

@Entity
@Table(
        name = "board_drafts",
        indexes = {
                @Index(name = "idx_board_drafts_user_id", columnList = "user_id"),
                @Index(name = "idx_board_drafts_updated_at", columnList = "updated_at"),
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardDraft extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(nullable = true, length = 150)
    private String title;

    @Lob
    @Column(nullable = true)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_board_draft_user"))
    private User writer;

    @Builder
    public BoardDraft(String title, String content, User writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}