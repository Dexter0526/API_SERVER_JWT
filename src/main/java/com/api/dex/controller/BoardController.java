package com.api.dex.controller;

import com.api.dex.domain.SecurityUser;
import com.api.dex.dto.BoardDto;
import com.api.dex.dto.FileDto;
import com.api.dex.service.BoardService;
import com.api.dex.service.FileService;
import com.api.dex.utils.JwtTokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @GetMapping("")
    public ResponseEntity getBoardPage(@RequestParam(value = "page", required = false) Integer page,
                                       @RequestParam(value = "search", required = false) String search,
                                       @RequestParam(value = "account", required = false) String account,
                                       Authentication authentication) throws JsonProcessingException {
        Gson gson = new Gson();
        JsonObject items = new JsonObject();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        if(page == null) page = 0;
        items.add("items", gson.toJsonTree(boardService.getBoardList(page, null, securityUser.getMember().getId())));

//        items.addProperty("items", new ObjectMapper().writeValueAsString(boardService.getBoardList(page, null)));
        items.addProperty("message", "success!");

        return new ResponseEntity(gson.toJson(items), HttpStatus.OK);
    }

    @GetMapping("/users/{memberId}")
    public ResponseEntity getUserBoardPage(@PathVariable(value = "memberId") Long memberId, @RequestParam(value = "page", required = false) Integer page, Authentication authentication) throws JsonProcessingException {
        Gson gson = new Gson();
        JsonObject items = new JsonObject();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        if(page == null) page = 0;
        items.add("items", gson.toJsonTree(boardService.getBoardList(page, memberId, securityUser.getMember().getId())));

//        items.addProperty("items", new ObjectMapper().writeValueAsString(boardService.getBoardList(page, null)));
        items.addProperty("message", "success!");

        return new ResponseEntity(gson.toJson(items), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getBoardById(@PathVariable(value = "id") Long id, Authentication authentication){
        Gson gson = new Gson();
        JsonObject items = new JsonObject();

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        if(id != null){
            items.add("items", gson.toJsonTree(boardService.getBoardById(id, securityUser.getMember().getId())));
            items.addProperty("message", "success!");
            return new ResponseEntity(gson.toJson(items), HttpStatus.OK);
        }else{
            items.addProperty("message", "Check board id!");
            return new ResponseEntity(gson.toJson(items), HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("/{category}")
//    public ResponseEntity getBoardPageByCategory(@PathVariable(value = "category") String category, @RequestParam(value = "start", required = false) Integer start, @RequestParam(value = "search", required = false) String search){
//        return null;
//    }

    @PostMapping("/")
    public ResponseEntity insertBoard(@RequestBody BoardDto boardDto, Authentication authentication) throws IOException {
        logger.info("Insert board controller:::" + boardDto.getTitle());
        Gson gson = new Gson();
        JsonObject items = new JsonObject();
        JsonObject data = new JsonObject();

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        boardService.insertBoard(boardDto, securityUser.getMember().getAccount());

        data.add("board", gson.toJsonTree(boardDto));
        items.add("items", data);
        items.addProperty("message", "success!");


        return new ResponseEntity(gson.toJson(items), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateBoard(@PathVariable("id") long id, @RequestBody BoardDto boardDto, Authentication authentication){
        Gson gson = new Gson();
        JsonObject items = new JsonObject();
        JsonObject data = new JsonObject();

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        boardDto.setId(id);

        data.add("board", gson.toJsonTree(boardService.updateBoard(boardDto, securityUser.getMember().getAccount())));
        items.add("items", data);
        items.addProperty("message", "success!");

        return new ResponseEntity(gson.toJson(items), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBoard(@PathVariable("id") long id, Authentication authentication){
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        boardService.deleteBoard(id, securityUser.getMember().getAccount());

        return new ResponseEntity(HttpStatus.OK);
    }

}
