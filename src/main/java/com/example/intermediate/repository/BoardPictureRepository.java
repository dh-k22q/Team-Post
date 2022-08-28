package com.example.intermediate.repository;

import com.example.intermediate.domain.BoardPicture;

import org.springframework.data.repository.CrudRepository;


import java.util.List;

public interface BoardPictureRepository extends CrudRepository<BoardPicture, Long> {
    BoardPicture save(BoardPicture boardPicture);

    List<BoardPicture> findAllByBoardIdx(Long boardIdx);
}
