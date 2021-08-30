package com.api.dex.controller;

import com.api.dex.domain.SecurityUser;
import com.api.dex.dto.SubscribeDto;
import com.api.dex.service.SubscribeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/subscribes")
public class SubscribeController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubscribeService subscribeService;

    @GetMapping("/fallow/{ownerId}")
    public ResponseEntity getSubscribePageByOwnerId(@PathVariable(value = "ownerId") Long ownerId, @RequestParam(value = "page", required = false) Integer page){
        if(page == null) page = 0;

        return null;
    }
    @GetMapping("/like/{boardId}")
    public ResponseEntity getSubscribePageByBoardId(@PathVariable(value = "boardId") Long boardId, @RequestParam(value = "page", required = false) Integer page){
        if(page == null) page = 0;

        return null;
    }

    @PostMapping("/")
    public ResponseEntity insertSubscribe(@RequestBody SubscribeDto subscribeDto, Authentication authentication){


        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteSubscribe(@PathVariable(value = "id") Long id, Authentication authentication){
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        subscribeService.deleteSubscribe(id, securityUser.getMember().getAccount());
    }

}
