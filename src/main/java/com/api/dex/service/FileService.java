package com.api.dex.service;

import com.api.dex.domain.*;
import com.api.dex.dto.FileDto;
import com.api.dex.utils.S3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FileService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    private static String path = "/home/ubuntu/files/";
//    private static String path = "E://temp/files/";

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private S3 s3;

    public File save(FileDto fileDto){
        logger.info("File save getBoardId:::" + fileDto.getBoardId());

        File file = File.builder()
                .board(boardRepository.findById(fileDto.getBoardId()))
                .member(memberRepository.findByAccount(fileDto.getAccount()).orElseGet(() -> null))
                .originalName(fileDto.getOriginalName())
                .fileType(fileDto.getFileType())
                .serverName(fileDto.getServerName())
                .path(fileDto.getPath())
                .build();

        return fileRepository.save(file);
    }

    public File getFileById(long id){
        return fileRepository.findById(id);
    }

    public List<FileDto> insertFileList(List<MultipartFile> multipartFiles, long boardId) throws IOException {
        List<FileDto> fileDtos = new ArrayList<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String realTime = timeFormat.format(date);

        for(int i = 0; i < multipartFiles.size(); i++){
            FileDto fileDto = new FileDto();

//            java.io.File file = new java.io.File(PathManagement.path, (realTime+multipartFiles.get(i).getOriginalFilename()));
//            multipartFiles.get(i).transferTo(file);

            s3.upload(multipartFiles.get(i), Long.toString(boardId), realTime);

            fileDto.setBoardId(boardId);
            fileDto.setOriginalName(multipartFiles.get(i).getOriginalFilename());
            fileDto.setFileType(multipartFiles.get(i).getOriginalFilename().substring(multipartFiles.get(i).getOriginalFilename().lastIndexOf(".") + 1));
            fileDto.setServerName(realTime+multipartFiles.get(i).getOriginalFilename());
            fileDto.setPath(Long.toString(boardId));
            fileDto.setId(save(fileDto).getId());

            fileDtos.add(fileDto);
        }

        return fileDtos;
    }

    public FileDto insertFile(MultipartFile multipartFiles, String account) throws IOException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String realTime = timeFormat.format(date);

        FileDto fileDto = new FileDto();

//        java.io.File file = new java.io.File(PathManagement.path, (realTime+multipartFiles.getOriginalFilename()));
//        multipartFiles.transferTo(file);

        s3.upload(multipartFiles, account, realTime);

        fileDto.setAccount(account);
        fileDto.setOriginalName(multipartFiles.getOriginalFilename());
        fileDto.setFileType(multipartFiles.getOriginalFilename().substring(multipartFiles.getOriginalFilename().lastIndexOf(".") + 1));
        fileDto.setServerName(realTime+multipartFiles.getOriginalFilename());
        fileDto.setPath(account);
        fileDto.setId(save(fileDto).getId());


        return fileDto;
    }

//    프로필 사진
    public FileDto getFileByMember(long memberId, String account, int page){
        Page<File> files = null;

        if(account != null){
//            로그인 당사자
            files = fileRepository.findByFileMember_Account(account, PageRequest.of(page, 1, Sort.by(Sort.Direction.DESC, "id")));
        }else{
//            이벤트 발생 회원
            files = fileRepository.findByFileMember_Id(memberId, PageRequest.of(page, 1, Sort.by(Sort.Direction.DESC, "id")));
        }

        FileDto fileDto = new FileDto();
        if(files.getContent() != null && files.getContent().size() > 0){
            fileDto.setId(files.getContent().get(0).getId());
            fileDto.setOriginalName(files.getContent().get(0).getOriginalName());
            fileDto.setFileType(files.getContent().get(0).getFileType());
            fileDto.setPath(files.getContent().get(0).getPath());
            fileDto.setServerName(files.getContent().get(0).getServerName());
        }


        return fileDto;
    }

    public FileDto updateFile(MultipartFile multipartFiles, long memberId) throws IOException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String realTime = timeFormat.format(date);

        FileDto fileDto = new FileDto();

//        java.io.File file = new java.io.File(PathManagement.path, (realTime+multipartFiles.getOriginalFilename()));
//        multipartFiles.transferTo(file);

        fileDto.setOriginalName(multipartFiles.getOriginalFilename());
        fileDto.setFileType(multipartFiles.getOriginalFilename().substring(multipartFiles.getOriginalFilename().lastIndexOf(".") + 1));
        fileDto.setServerName(realTime+multipartFiles.getOriginalFilename());
//        fileDto.setPath(PathManagement.path);
        fileDto.setId(save(fileDto).getId());


        return fileDto;
    }

    @Transactional
    public void deleteFile(long id, String account){
        File file = fileRepository.findById(id);

        Member member = null;
        if(file.getFileBoard() != null){
            member = file.getFileBoard().getBoardMember();
        }else if(file.getFileMember() != null){
            member = file.getFileMember();
        }else{
            return;
        }

        if(member.getAccount().equals(account)){
            s3.fileDelete(file.getPath(), file.getServerName());
            fileRepository.delete(file);
        }
    }

}
