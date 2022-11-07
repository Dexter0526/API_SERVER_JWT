package com.api.dex.controller;

import com.api.dex.domain.Member;
import com.api.dex.domain.MemberRepository;
import com.api.dex.domain.SecurityUser;
import com.api.dex.dto.MemberDto;
import com.api.dex.dto.ResponseDto;
import com.api.dex.service.MemberService;
import com.api.dex.utils.JwtTokenProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    // 회원가입
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto sign(@RequestBody MemberDto memberDto) {
        logger.info("controller sign:::" + memberDto.getAccount());

        memberService.insertMember(memberDto);

        return ResponseDto.builder()
                .code(HttpStatus.OK)
                .message("Success")
                .data(memberDto)
                .build();
    }

    // 로그인
    @PutMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto login(@RequestBody Map<String, String> user) {
        MemberDto memberDto = memberService.login(user.get("account"), user.get("password"));

        return ResponseDto.builder()
                .code(HttpStatus.OK)
                .message("Success")
                .data(memberDto)
                .build();
    }

    @PutMapping("/{account}")
    public ResponseEntity updateMember(@RequestBody MemberDto memberDto, @PathVariable String account, Authentication authentication){
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Gson gson = new Gson();
        HttpHeaders httpHeaders = new HttpHeaders();
        JsonObject items = new JsonObject();
        JsonObject data = new JsonObject();
        if(securityUser.getMember().getAccount().contains(account)){
            Member member = memberService.updateMember(memberDto, securityUser.getMember().getAccount());

            data.addProperty("id", member.getId());
            data.addProperty("account", member.getAccount());
            data.addProperty("info", member.getInfo());
            data.addProperty("name", member.getName());
            items.add("items", data);

            return new ResponseEntity(gson.toJson(items), httpHeaders, HttpStatus.OK);
        }else{
            data.addProperty("message", "Check your account");
            items.add("items", data);
            return new ResponseEntity(gson.toJson(items), httpHeaders, HttpStatus.FORBIDDEN);
        }

    }

    @DeleteMapping("/{account}")
    public ResponseEntity deleteMember(Authentication authentication, @PathVariable String account){
        Gson gson = new Gson();
        JsonObject items = new JsonObject();
        JsonObject data = new JsonObject();

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

        if(securityUser.getMember().getAccount().contains(account)){
            memberService.deleteMember(securityUser.getMember().getAccount());

            data.addProperty("account", securityUser.getMember().getAccount());
            data.addProperty("message", "Success");
            items.add("items", data);

            return new ResponseEntity(gson.toJson(items), HttpStatus.OK);
        }else{
            data.addProperty("account", securityUser.getMember().getAccount());
            data.addProperty("message", "Fail");
            items.add("items", data);

            return new ResponseEntity(gson.toJson(items), HttpStatus.BAD_GATEWAY);
        }
    }


    @PostMapping("/authority")
    public ResponseEntity<JsonObject> isValidateEmail(@RequestBody Map<String, String> user){
        Gson gson = new Gson();
        JsonObject items = new JsonObject();
        String account = user.get("account");
        account = account.replaceAll("\"", "");

        if(!account.contains("@") || !account.split("@")[1].contains(".")){
            items.addProperty("message", "이메일 형식이 아닙니다.");
            return new ResponseEntity(gson.toJson(items), HttpStatus.BAD_REQUEST);
        }

        Boolean result = memberRepository.findByAccount(account).isPresent();
        logger.info("auth result:::" + result);
        if(result){
            items.addProperty("message", "이미 존재하는 이메일 입니다.");
            return new ResponseEntity(gson.toJson(items), HttpStatus.CONFLICT);
        }else{
            items.addProperty("account", account);
            items.addProperty("message", "가입 가능한 이메일 입니다.");
            return new ResponseEntity(gson.toJson(items), HttpStatus.OK);
        }


    }

    @GetMapping("/")
    public ResponseEntity info(Authentication authentication, @RequestHeader HttpHeaders headers){
        Gson gson = new Gson();
        JsonObject items = new JsonObject();
        JsonObject data = new JsonObject();

        if(authentication != null){
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            String account = securityUser.getMember().getAccount();
            String name = securityUser.getUsername();
            String role = securityUser.getMember().getMemberRole().getRoleName().name();
            Long id = securityUser.getMember().getId();

            data.addProperty("id", id);
            data.addProperty("account", account);
            data.addProperty("name", name);
            data.addProperty("role", role);
            items.add("items", data);

            logger.info("controller info:::" + data.get("name"));
            logger.info("controller info:::" + items.get("items"));

            return new ResponseEntity<>(gson.toJson(items), headers, HttpStatus.OK);
        }else{
            data.addProperty("message", "Member info is null");
            items.add("items", data);
            return new ResponseEntity<>(gson.toJson(items), headers, HttpStatus.BAD_REQUEST);
        }


    }
}
