package com.api.dex.service;

import com.api.dex.domain.File;
import com.api.dex.domain.Member;
import com.api.dex.domain.MemberRepository;
import com.api.dex.domain.MemberRole;
import com.api.dex.dto.FileDto;
import com.api.dex.dto.MemberDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class MemberService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    public Member save(MemberDto memberDto){
        Member member = Member.builder()
                .account(memberDto.getAccount())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .name(memberDto.getName())
                .info(memberDto.getInfo())
                .token(memberDto.getToken())
                .memberRole(memberDto.getMemberRole())
                .build();
        return memberRepository.save(member);
    }

    public Member insertMember(MemberDto memberDto){
        logger.info("insert member:::" + memberDto.getAccount());
        Boolean result = memberRepository.findByAccount(memberDto.getAccount()).isEmpty();
        if(result){
            if(memberDto.getMemberRole() == null) memberDto.setMemberRole(new MemberRole(MemberRole.RoleType.ROLE_USER));
            return save(memberDto);
        }else{
            return null;
        }
    }

    public JsonObject getMember(long id){

        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();

        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        List<File> fileList = member.getFiles();

        MemberDto memberDto = new MemberDto();
        memberDto.setInfo(member.getInfo());
        memberDto.setAccount(member.getAccount());
        memberDto.setName(memberDto.getName());
        jsonObject.add("member", gson.toJsonTree(memberDto));

        if(fileList.size() > 0){
            FileDto fileDto = new FileDto();
            fileDto.setId(fileList.get(0).getId());
            fileDto.setOriginalName(fileList.get(0).getOriginalName());
            fileDto.setFileType(fileList.get(0).getFileType());
            fileDto.setPath(fileList.get(0).getPath());
            fileDto.setServerName(fileList.get(0).getServerName());
            jsonObject.add("file", gson.toJsonTree(fileDto));
        }

        return jsonObject;
    }

    public Member updateMember(MemberDto memberDto, String account){
        Member member = memberRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

        member.setInfo(memberDto.getInfo());
        member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        member.setName(memberDto.getName());

        return memberRepository.save(member);
    }

    public void deleteMember(String account){
        memberRepository.deleteByAccount(account);
    }


}
