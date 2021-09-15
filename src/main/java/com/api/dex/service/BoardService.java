package com.api.dex.service;

import com.api.dex.domain.*;
import com.api.dex.dto.BoardDto;
import com.api.dex.dto.FileDto;
import com.api.dex.dto.MemberDto;
import com.api.dex.dto.SubscribeDto;
import com.api.dex.utils.S3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class BoardService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private S3 s3;

    public Board save(BoardDto boardDto, String account){
        Board board = Board.builder()
                .category(boardDto.getCategory())
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .member(memberRepository.findByAccount(account).orElseThrow(() -> new IllegalArgumentException("가입 하지 않는 이메일 입니다.")))
                .build();
        return boardRepository.save(board);
    }

//    public Board getBoardById(long id){
//        return boardRepository.findById(id);
//    }

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

        boardRepository.save(board);

        return boardDto;
    }

    public Map<String, Object> getBoardList(int page, Long memberId, Long fallowId){
        Page<Board> boards;

        if(memberId == null){
            boards = boardRepository.findAll(PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id")));
        }else{
            boards = boardRepository.findByBoardMember_Id(memberId, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id")));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        List<BoardDto> boardDtos = new ArrayList<>();
        Iterator<Board> iterator = boards.getContent().iterator();

        while (iterator.hasNext()){
            Board board = iterator.next();

            BoardDto boardDto = new BoardDto();
            MemberDto memberDto = new MemberDto();

            Member member = board.getBoardMember();
            Page<File> files = fileRepository.findByFileMember_Account(member.getAccount(), PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id")));
            Subscribe subscribe = subscribeRepository.findByLike_IdAndFallow_Id(board.getId(), fallowId);

            boardDto.setId(board.getId());
            boardDto.setTitle(board.getTitle());
            boardDto.setContent(board.getContent());
            boardDto.setCategory(board.getCategory());

            if(subscribe != null){
                SubscribeDto subscribeDto = new SubscribeDto();
                subscribeDto.setId(subscribe.getId());
                subscribeDto.setFallowId(subscribe.getFallow().getId());
                boardDto.setFallow(subscribeDto);
            }

            memberDto.setId(member.getId());
            memberDto.setInfo(member.getInfo());
            memberDto.setName(member.getName());
//            if(files.getTotalElements() != 0) memberDto.setSrc(PathManagement.src + files.getContent().get(0).getId());
            if(files.getTotalElements() != 0) memberDto.setSrc(s3.getSrc(files.getContent().get(0).getPath(), files.getContent().get(0).getServerName()));

            boardDto.setMemberDto(memberDto);

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
//                fileDto.setSrc(PathManagement.src + fileDto.getId());
                fileDto.setSrc(s3.getSrc(file.getPath(), file.getServerName()));

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

    public Map<String, Object> getBoardById(long id, Long fallowId){
        Map<String, Object> result = new LinkedHashMap<>();
        Board board = boardRepository.findById(id);
        Subscribe subscribe = subscribeRepository.findByLike_IdAndFallow_Id(board.getId(), fallowId);

        BoardDto boardDto = new BoardDto();
        MemberDto memberDto = new MemberDto();

        Member member = board.getBoardMember();
        Page<File> files = fileRepository.findByFileMember_Account(member.getAccount(), PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id")));

        boardDto.setId(board.getId());
        boardDto.setTitle(board.getTitle());
        boardDto.setContent(board.getContent());
        boardDto.setCategory(board.getCategory());

        if(subscribe != null){
            SubscribeDto subscribeDto = new SubscribeDto();
            subscribeDto.setId(subscribe.getId());
            subscribeDto.setFallowId(subscribe.getFallow().getId());
            boardDto.setFallow(subscribeDto);
        }

        memberDto.setId(member.getId());
        memberDto.setInfo(member.getInfo());
        memberDto.setName(member.getName());
//        if(files.getTotalElements() != 0) memberDto.setSrc(PathManagement.src + files.getContent().get(0).getId());
        if(files.getTotalElements() != 0) memberDto.setSrc(s3.getSrc(files.getContent().get(0).getPath(), files.getContent().get(0).getServerName()));

        boardDto.setMemberDto(memberDto);

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
//            fileDto.setSrc(PathManagement.src + fileDto.getId());
            fileDto.setSrc(s3.getSrc(fileDto.getPath(), fileDto.getServerName()));

            fileDtos.add(fileDto);
        }
        boardDto.setFileDtos(fileDtos);
        result.put("board", boardDto);

        return result;
    }

    public void deleteBoard(long id, String account){

        boardRepository.deleteByIdAndBoardMember_Account(id, account);
        s3.fileDelete(Long.toString(id), "");
    }

}
