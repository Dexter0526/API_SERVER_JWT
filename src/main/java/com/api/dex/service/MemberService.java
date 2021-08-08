package com.api.dex.service;

import com.api.dex.domain.*;
import com.api.dex.dto.FileDto;
import com.api.dex.dto.MemberDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private BoardRepository boardRepository;

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

    public MemberDto getMember(long id){

//        JsonObject jsonObject = new JsonObject();
//        Gson gson = new Gson();

        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
//        Page<File> file = fileRepository.findByFileMember_Id(id, PageRequest.of(0, 1));

        MemberDto memberDto = new MemberDto();
        memberDto.setInfo(member.getInfo());
        memberDto.setAccount(member.getAccount());
        memberDto.setName(memberDto.getName());
//        jsonObject.add("member", gson.toJsonTree(memberDto));
//
//        FileDto fileDto = new FileDto();
//        fileDto.setId(file.getContent().get(0).getId());
//        fileDto.setOriginalName(file.getContent().get(0).getOriginalName());
//        fileDto.setFileType(file.getContent().get(0).getFileType());
//        fileDto.setPath(file.getContent().get(0).getPath());
//        fileDto.setServerName(file.getContent().get(0).getServerName());
//        jsonObject.add("file", gson.toJsonTree(fileDto));

        return memberDto;
    }

    public MemberDto getMemberByAuth(String account){

//        JsonObject jsonObject = new JsonObject();
//        Gson gson = new Gson();

        Member member = memberRepository.findByAccount(account).orElseThrow(() -> new IllegalArgumentException("Not found"));
//        Page<File> files = fileRepository.findByFileMember_Account(account, PageRequest.of(0, 1));
//        Page<Board> boards = boardRepository.findByBoardMember_Account(account, PageRequest.of(0, 6));

        MemberDto memberDto = new MemberDto();
        memberDto.setInfo(member.getInfo());
        memberDto.setAccount(member.getAccount());
        memberDto.setName(memberDto.getName());
//        jsonObject.add("member", gson.toJsonTree(memberDto));
//
//        FileDto fileDto = new FileDto();
//        fileDto.setId(files.getContent().get(0).getId());
//        fileDto.setOriginalName(files.getContent().get(0).getOriginalName());
//        fileDto.setFileType(files.getContent().get(0).getFileType());
//        fileDto.setPath(files.getContent().get(0).getPath());
//        fileDto.setServerName(files.getContent().get(0).getServerName());

        return memberDto;
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
