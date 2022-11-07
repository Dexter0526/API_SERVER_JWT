package com.api.dex.controller;

import com.api.dex.domain.Member;
import com.api.dex.domain.MemberRepository;
import com.api.dex.dto.MemberDto;
import com.api.dex.service.MemberService;
import com.api.dex.utils.JwtTokenProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${myapp.secret}")
    private String myappKey;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;

    @PostMapping("/")
    public ResponseEntity<String> oauth(@RequestBody MemberDto memberDto) {
        Gson gson = new Gson();
        HttpHeaders httpHeaders = new HttpHeaders();
        JsonObject items = new JsonObject();
        JsonObject data = new JsonObject();

        logger.info("controller sign:::" + memberDto.getAccount());

        if(memberDto.getToken() != null && memberDto.getToken().equals(myappKey)){
            Member member = memberService.OauthMember(memberDto);

            String accessToken = jwtTokenProvider.createToken(member.getAccount(), member.getMemberRole());
            String refreshToken = jwtTokenProvider.createRefreshToken(member.getAccount(), member.getMemberRole());

            httpHeaders.add("accessToken", accessToken);
            httpHeaders.add("refreshToken", refreshToken);

            data.addProperty("id", member.getId());
            data.addProperty("account", member.getAccount());
            data.addProperty("info", member.getInfo());
            data.addProperty("name", member.getName());
            items.add("items", data);

            return new ResponseEntity(gson.toJson(items), httpHeaders, HttpStatus.OK);
        }else{
            data.addProperty("account", memberDto.getAccount());
            items.add("items", data);
            items.addProperty("message", "Please Retry OAuth!");

            return new ResponseEntity(gson.toJson(items), httpHeaders, HttpStatus.FORBIDDEN);
        }

    }
}
