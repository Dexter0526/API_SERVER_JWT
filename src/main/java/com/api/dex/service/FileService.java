package com.api.dex.service;

import com.api.dex.domain.BoardRepository;
import com.api.dex.domain.File;
import com.api.dex.domain.FileRepository;
import com.api.dex.domain.MemberRepository;
import com.api.dex.dto.FileDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static String path = "/home/ubuntu/files/";

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MemberRepository memberRepository;

    public File save(FileDto fileDto){
        File file = File.builder()
                .board(boardRepository.findById(fileDto.getBoardId()))
                .member(memberRepository.findById(fileDto.getMemberId()))
                .originalName(fileDto.getOriginalName())
                .fileType(fileDto.getFileType())
                .serverName(fileDto.getServerName())
                .path(fileDto.getPath())
                .build();

        return fileRepository.save(file);
    }

    public File getFileById(long id){
        return fileRepository.getById(id);
    }

    public List<FileDto> insertFileList(MultipartFile[] multipartFiles, long boardId) throws IOException {
        List<FileDto> fileDtos = new ArrayList<>();
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String realTime = timeFormat.format(date);

        for(int i = 0; i < multipartFiles.length; i++){
            FileDto fileDto = new FileDto();

            java.io.File file = new java.io.File(path, (realTime+multipartFiles[i].getOriginalFilename()));
            multipartFiles[i].transferTo(file);

            fileDto.setBoardId((int) boardId);
            fileDto.setOriginalName(multipartFiles[i].getOriginalFilename());
            fileDto.setFileType(multipartFiles[i].getOriginalFilename().substring(multipartFiles[i].getOriginalFilename().lastIndexOf(".") + 1));
            fileDto.setServerName(realTime+multipartFiles[i].getOriginalFilename());
//            fileDto.setPath();
            fileDto.setId(save(fileDto).getId());

            fileDtos.add(fileDto);
        }

        return fileDtos;
    }

    public FileDto insertFile(MultipartFile multipartFiles, long memberId) throws IOException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String realTime = timeFormat.format(date);

        FileDto fileDto = new FileDto();

        java.io.File file = new java.io.File(path, (realTime+multipartFiles.getOriginalFilename()));
        multipartFiles.transferTo(file);

        fileDto.setMemberId((int) memberId);
        fileDto.setOriginalName(multipartFiles.getOriginalFilename());
        fileDto.setFileType(multipartFiles.getOriginalFilename().substring(multipartFiles.getOriginalFilename().lastIndexOf(".") + 1));
        fileDto.setServerName(realTime+multipartFiles.getOriginalFilename());
//            fileDto.setPath();
        fileDto.setId(save(fileDto).getId());


        return fileDto;
    }

}
