package com.api.dex.controller;

import com.api.dex.domain.SecurityUser;
import com.api.dex.dto.BoardDto;
import com.api.dex.service.BoardService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/board")
public class BoardController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private BoardService boardService;


    @PostMapping("/")
    public ResponseEntity insertBoard(@RequestBody BoardDto boardDto, Authentication authentication){
        logger.info("Insert board controller:::" + boardDto.getTitle());
        Gson gson = new Gson();
        JsonObject items = new JsonObject();

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        boardDto = boardService.insertBoard(boardDto, securityUser.getMember().getAccount());

        items.add("items", gson.toJsonTree(boardDto));
        items.addProperty("message", "success!");


        return new ResponseEntity(gson.toJson(items), HttpStatus.OK);
    }

}
