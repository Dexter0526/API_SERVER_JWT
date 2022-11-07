package com.api.dex.service;

import com.api.dex.domain.*;
import com.api.dex.dto.FileDto;
import com.api.dex.dto.MemberDto;
import com.api.dex.dto.SubscribeDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final SubscribeRepository subscribeRepository;

    @Transactional
    public void insertMember(MemberDto memberDto){
        logger.info("insert member:::" + memberDto.getAccount());

        if(memberRepository.findByAccount(memberDto.getAccount()).isEmpty()){
            if(memberDto.getMemberRole() == null) memberDto.setMemberRole(new MemberRole(MemberRole.RoleType.ROLE_USER));

            Member.save(memberDto.getMemberRole(), memberDto.getAccount(), passwordEncoder.encode(memberDto.getPassword()),
                    memberDto.getName(), memberDto.getInfo(), memberDto.getToken());
        }else{
            throw new RuntimeException();
        }
    }

    @Transactional
    public Member OauthMember(MemberDto memberDto){
        if(memberDto.getMemberRole() == null) memberDto.setMemberRole(new MemberRole(MemberRole.RoleType.ROLE_USER));
        String[] temp = UUID.randomUUID().toString().split("-");
        String password = "";

        for(String item : temp){
            password += item;
        }

        memberDto.setPassword(password);

        return memberRepository.findByAccount(memberDto.getAccount())
                .orElseGet(() -> Member.save(memberDto.getMemberRole(), memberDto.getAccount(), passwordEncoder.encode(memberDto.getPassword()),
                        memberDto.getName(), memberDto.getInfo(), memberDto.getToken()));
    }

    @Transactional(readOnly = true)
    public MemberDto getMember(long id, Long fallowId){

        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        Subscribe subscribe = subscribeRepository.findByOwner_IdAndFallow_Id(member.getId(), fallowId);
        MemberDto memberDto = new MemberDto();
        memberDto.setInfo(member.getInfo());
        memberDto.setAccount(member.getAccount());
        memberDto.setName(member.getName());

        if(subscribe != null){
            SubscribeDto subscribeDto = new SubscribeDto();
            subscribeDto.setId(subscribe.getId());
            subscribeDto.setFallowId(subscribe.getFallow().getId());
            memberDto.setFallow(subscribeDto);
        }

        return memberDto;
    }

    @Transactional
    public Member updateMember(MemberDto memberDto, String account){
        Member member = memberRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

        member.setInfo(memberDto.getInfo());
        if(memberDto.getPassword() != null){
            member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        }

        member.setName(memberDto.getName());

        return memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(String account){
        memberRepository.deleteByAccount(account);
    }
}
