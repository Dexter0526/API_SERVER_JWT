package com.api.dex.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

//    Optional<Board> findById(long id);

    Board findById(long id);

    Optional<Board> findByIdAndBoardMember_Account(long id, String account);

    Page<Board> findByCategory(String category, Pageable pageable);

    Page<Board> findAll(Pageable pageable);

    Page<Board> findByBoardMember_Account(String account, Pageable pageable);

    Page<Board> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    @Transactional
    void deleteById(long id);

    @Transactional
    void deleteByIdAndBoardMember_Account(long id, String account);
}
