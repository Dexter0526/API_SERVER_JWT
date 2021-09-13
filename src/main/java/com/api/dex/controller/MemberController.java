package com.api.dex.controller;

import com.api.dex.domain.SecurityUser;
import com.api.dex.dto.FileDto;
import com.api.dex.dto.MemberDto;
import com.api.dex.service.FileService;
import com.api.dex.service.MemberService;
import com.api.dex.utils.S3;
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

    @Autowired
    private FileService fileService;

    @Autowired
    private S3 s3;

    @GetMapping("/{id}")
    public ResponseEntity getMemberInfo(@PathVariable(value = "id") Integer id, Authentication authentication){
        JsonObject jsonObject = new JsonObject();
        JsonObject dtoObject = new JsonObject();
        Gson gson = new Gson();

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        if(id != null){
            MemberDto memberDto = memberService.getMember(id, securityUser.getMember().getId());
            FileDto fileDto = fileService.getFileByMember(id, memberDto.getAccount(), 0);

            dtoObject.add("member", gson.toJsonTree(memberDto));
            dtoObject.add("file", gson.toJsonTree(fileDto));
            dtoObject.addProperty("src", s3.getSrc(fileDto.getPath(), fileDto.getServerName()));
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
