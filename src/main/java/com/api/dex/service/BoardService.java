package com.api.dex.service;

import com.api.dex.domain.Board;
import com.api.dex.domain.BoardRepository;
import com.api.dex.domain.MemberRepository;
import com.api.dex.dto.BoardDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class BoardService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    public Board save(BoardDto boardDto, String account){
        Board board = Board.builder()
                .category(boardDto.getCategory())
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .member(memberRepository.findByAccount(account).orElseThrow(() -> new IllegalArgumentException("가입 하지 않는 이메일 입니다.")))
                .build();
        return boardRepository.save(board);
    }

    public Board getBoardById(long id){
        return boardRepository.getById(id);
    }

    public BoardDto insertBoard(BoardDto boardDto, String account){
        Board board = save(boardDto, account);
        logger.info("Insert board id:::"+ board.getId());
        boardDto.setCategory(board.getCategory());
        boardDto.setId(board.getId());
        boardDto.setContent(board.getContent());
        return boardDto;
    }

}
