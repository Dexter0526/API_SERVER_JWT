package com.api.dex.service;

import com.api.dex.domain.Board;
import com.api.dex.domain.BoardRepository;
import com.api.dex.domain.File;
import com.api.dex.domain.MemberRepository;
import com.api.dex.dto.BoardDto;
import com.api.dex.dto.FileDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class BoardService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String src = "https://vlaos-smartwork.com/api/files/";

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

    public BoardDto updateBoard(BoardDto boardDto, String account){
        Board board = boardRepository.findByIdAndBoardMember_Account(boardDto.getId(), account)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        board.setCategory(boardDto.getCategory());
        board.setContent(boardDto.getContent());
        board.setTitle(boardDto.getTitle());

        return boardDto;
    }

    public Map<String, Object> getBoardList(int page, String account){
        Page<Board> boards;

        if(account == null){
            boards = boardRepository.findAll(PageRequest.of(page, 10));
        }else{
            boards = boardRepository.findByBoardMember_Account(account, PageRequest.of(page, 10));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        List<BoardDto> boardDtos = new ArrayList<>();
        Iterator<Board> iterator = boards.getContent().iterator();

        while (iterator.hasNext()){
            Board board = iterator.next();
            BoardDto boardDto = new BoardDto();
            boardDto.setId(board.getId());
            boardDto.setTitle(board.getTitle());
            boardDto.setContent(board.getContent());
            boardDto.setCategory(board.getCategory());
            boardDto.setName(board.getBoardMember().getName());

            List<File> fileList = board.getFiles();
            Iterator<File> fileIterator = fileList.iterator();
            List<FileDto> fileDtos = new ArrayList<>();

            while (fileIterator.hasNext()){
                File file = fileIterator.next();
                FileDto fileDto = new FileDto();
                fileDto.setFileType(file.getFileType());
                fileDto.setPath(file.getPath());
                fileDto.setServerName(file.getServerName());
                fileDto.setOriginalName(file.getOriginalName());
                fileDto.setId(file.getId());
                fileDto.setSrc(src + fileDto.getId());

                fileDtos.add(fileDto);
            }

            boardDto.setFileDtos(fileDtos);
            boardDtos.add(boardDto);
        }
        result.put("boards", boardDtos);
        result.put("TotalElements", boards.getTotalElements());
        result.put("page", page);

        return result;
    }

    public Map<String, Object> getBoardById(Integer id){
        Map<String, Object> result = new LinkedHashMap<>();
        Board board = boardRepository.findById(id);

        BoardDto boardDto = new BoardDto();
        boardDto.setId(board.getId());
        boardDto.setTitle(board.getTitle());
        boardDto.setContent(board.getContent());
        boardDto.setCategory(board.getCategory());
        boardDto.setName(board.getBoardMember().getName());

        List<File> fileList = board.getFiles();
        Iterator<File> iterator = fileList.iterator();
        List<FileDto> fileDtos = new ArrayList<>();


        while (iterator.hasNext()){
            File file = iterator.next();
            FileDto fileDto = new FileDto();
            fileDto.setFileType(file.getFileType());
            fileDto.setPath(file.getPath());
            fileDto.setServerName(file.getServerName());
            fileDto.setOriginalName(file.getOriginalName());
            fileDto.setId(file.getId());
            fileDto.setSrc(src + fileDto.getId());

            fileDtos.add(fileDto);
        }
        boardDto.setFileDtos(fileDtos);
        result.put("board", boardDto);

        return result;
    }

    public void deleteBoard(long id, String account){
        boardRepository.deleteByIdAndBoardMember_Account(id, account);
    }

}
