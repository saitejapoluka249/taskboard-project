package com.taskboard.repository;

import com.taskboard.model.Board;

public interface BoardRepository {
    Board loadBoard();
    void saveBoard(Board board);
}
