package com.api.dex.service;

import com.api.dex.domain.Board;
import com.api.dex.domain.BoardRepository;
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

    public Board getBoardById(long id){
        return boardRepository.getById(id);
    }
}
