package com.api.dex.controller;

import com.api.dex.domain.SecurityUser;
import com.api.dex.dto.MemberDto;
import com.api.dex.service.MemberService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/{id}")
    public ResponseEntity getMemberInfo(@PathVariable(value = "id") Integer id){
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();

        if(id != null){
            jsonObject.add("items", memberService.getMember(id));
            jsonObject.addProperty("message", "success!!");
            return new ResponseEntity<>(gson.toJson(jsonObject), HttpStatus.OK);
        }else{
            jsonObject.addProperty("message", "Not found!");
            return new ResponseEntity<>(gson.toJson(jsonObject), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity updateMemberInfo(@PathVariable(value = "id") Integer id, Authentication authentication, MemberDto memberDto){


        return null;
    }
}
