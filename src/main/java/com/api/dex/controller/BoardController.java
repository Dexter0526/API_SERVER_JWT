package com.api.dex.controller;

import com.api.dex.domain.SecurityUser;
import com.api.dex.dto.BoardDto;
import com.api.dex.dto.FileDto;
import com.api.dex.service.BoardService;
import com.api.dex.service.FileService;
import com.api.dex.utils.JwtTokenProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/boards")
public class BoardController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private BoardService boardService;

    @Autowired
    private FileService fileService;

    @GetMapping("/")
    public ResponseEntity getBoardPage(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "search", required = false) String search){



        return null;
    }

    @GetMapping("/{category}")
    public ResponseEntity getBoardPageByCategory(@PathVariable(value = "category") String category, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "search", required = false) String search){
        return null;
    }

    @PostMapping("/")
    public ResponseEntity insertBoard(BoardDto boardDto, Authentication authentication, @RequestParam("files") MultipartFile[] multipartFile) throws IOException {
        logger.info("Insert board controller:::" + boardDto.getTitle());
        Gson gson = new Gson();
        JsonObject items = new JsonObject();

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        boardDto = boardService.insertBoard(boardDto, securityUser.getMember().getAccount());
        List<FileDto> fileDtoList = fileService.insertFileList(multipartFile, boardDto.getId());

        boardDto.setFileDtos(fileDtoList);

        items.add("items", gson.toJsonTree(boardDto));
        items.addProperty("message", "success!");


        return new ResponseEntity(gson.toJson(items), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateBoard(@PathVariable("id") long id, @RequestBody BoardDto boardDto, Authentication authentication, @RequestParam("files") MultipartFile[] multipartFile){
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBoard(@PathVariable("id") long id, Authentication authentication){
        return null;
    }



}
