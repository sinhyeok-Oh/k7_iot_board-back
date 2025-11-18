package org.example.boardback.repository.board;

import org.example.boardback.entity.board.BoardCategory;
import org.example.boardback.entity.board.draft.BoardDraft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Long> {
}
