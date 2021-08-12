package com.api.dex.controller;

import com.api.dex.domain.SecurityUser;
import com.api.dex.dto.FileDto;
import com.api.dex.dto.MemberDto;
import com.api.dex.service.FileService;
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
    private final static String src = "https://vlaos-smartwork.com/api/files/";

    @Autowired
    private MemberService memberService;

    @Autowired
    private FileService fileService;

    @GetMapping("/{id}")
    public ResponseEntity getMemberInfo(@PathVariable(value = "id") Integer id){
        JsonObject jsonObject = new JsonObject();
        JsonObject dtoObject = new JsonObject();
        Gson gson = new Gson();

        if(id != null){
            MemberDto memberDto = memberService.getMember(id);
            FileDto fileDto = fileService.getFileByMember(id,memberDto.getAccount(), 0);

            dtoObject.add("member", gson.toJsonTree(memberDto));
            dtoObject.add("file", gson.toJsonTree(fileDto));
            dtoObject.addProperty("src", src + fileDto.getId());
            jsonObject.add("items", dtoObject);
            jsonObject.addProperty("message", "success!!");

            return new ResponseEntity<>(gson.toJson(jsonObject), HttpStatus.OK);
        }else{
            jsonObject.addProperty("message", "Not found!");
            return new ResponseEntity<>(gson.toJson(jsonObject), HttpStatus.BAD_REQUEST);
        }

    }

//    @PutMapping("/{id}")
//    public ResponseEntity updateMemberInfo(@PathVariable(value = "id") Integer id, Authentication authentication, MemberDto memberDto){
//
//
//        return null;
//    }
}
