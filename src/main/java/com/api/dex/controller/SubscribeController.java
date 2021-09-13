package com.api.dex.controller;

import com.api.dex.domain.SecurityUser;
import com.api.dex.dto.SubscribeDto;
import com.api.dex.service.SubscribeService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/subscribes")
public class SubscribeController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubscribeService subscribeService;

    @GetMapping("/fallow/{ownerId}")
    public ResponseEntity getSubscribePageByOwnerId(@PathVariable(value = "ownerId") Long ownerId, @RequestParam(value = "page", required = false) Integer page){
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        if(page == null) page = 0;

        jsonObject.add("items", gson.toJsonTree(subscribeService.getSubscribePage(ownerId, null, page)));
        jsonObject.addProperty("message", "success!");

        return new ResponseEntity<>(gson.toJson(jsonObject), HttpStatus.OK);
    }
    @GetMapping("/like/{boardId}")
    public ResponseEntity getSubscribePageByBoardId(@PathVariable(value = "boardId") Long boardId, @RequestParam(value = "page", required = false) Integer page){
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        if(page == null) page = 0;

        jsonObject.add("items", gson.toJsonTree(subscribeService.getSubscribePage(null, boardId, page)));
        jsonObject.addProperty("message", "success!");

        return new ResponseEntity<>(gson.toJson(jsonObject), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity insertSubscribe(@RequestBody SubscribeDto subscribeDto, Authentication authentication){
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        subscribeDto.setFallowName(securityUser.getMember().getName());
        subscribeDto.setFallowAccount(securityUser.getMember().getAccount());

        subscribeDto = subscribeService.insertSubscribe(subscribeDto);

        jsonObject.add("items", gson.toJsonTree(subscribeDto));
        jsonObject.addProperty("message", "success!");

        return new ResponseEntity<>(gson.toJson(jsonObject), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteSubscribe(@PathVariable(value = "id") Long id, Authentication authentication){
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        subscribeService.deleteSubscribe(id, securityUser.getMember().getAccount());
    }

}
