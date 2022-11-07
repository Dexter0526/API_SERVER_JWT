package com.api.dex.service;

import com.api.dex.domain.*;
import com.api.dex.dto.MemberDto;
import com.api.dex.dto.SubscribeDto;
import com.api.dex.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final SubscribeRepository subscribeRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void insertMember(MemberDto memberDto) {
        logger.info("insert member:::" + memberDto.getAccount());

        if (memberRepository.findByAccount(memberDto.getAccount()).isEmpty()) {
            if (memberDto.getMemberRole() == null)
                memberDto.setMemberRole(new MemberRole(MemberRole.RoleType.ROLE_USER));

            Member.save(memberDto.getMemberRole(), memberDto.getAccount(), passwordEncoder.encode(memberDto.getPassword()),
                    memberDto.getName(), memberDto.getInfo(), memberDto.getToken());
        } else {
            throw new RuntimeException();
        }
    }

    @Transactional
    public Member OauthMember(MemberDto memberDto) {
        if (memberDto.getMemberRole() == null) memberDto.setMemberRole(new MemberRole(MemberRole.RoleType.ROLE_USER));
        String[] temp = UUID.randomUUID().toString().split("-");
        String password = "";

        for (String item : temp) {
            password += item;
        }

        memberDto.setPassword(password);

        return memberRepository.findByAccount(memberDto.getAccount())
                .orElseGet(() -> Member.save(memberDto.getMemberRole(), memberDto.getAccount(), passwordEncoder.encode(memberDto.getPassword()),
                        memberDto.getName(), memberDto.getInfo(), memberDto.getToken()));
    }

    @Transactional(readOnly = true)
    public MemberDto getMember(long id, Long fallowId) {

        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        Subscribe subscribe = subscribeRepository.findByOwner_IdAndFallow_Id(member.getId(), fallowId);

        MemberDto memberDto = MemberDto.builder()
                .account(member.getAccount())
                .name(member.getName())
                .info(member.getInfo())
                .build();

        if (subscribe != null) {
            SubscribeDto subscribeDto = new SubscribeDto();
            subscribeDto.setId(subscribe.getId());
            subscribeDto.setFallowId(subscribe.getFallow().getId());
            memberDto.setFallow(subscribeDto);
        }

        return memberDto;
    }

    @Transactional
    public Member updateMember(MemberDto memberDto, String account) {
        Member member = memberRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

        member.setInfo(memberDto.getInfo());
        if (memberDto.getPassword() != null) {
            member.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        }

        member.setName(memberDto.getName());

        return memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(String account) {
        memberRepository.deleteByAccount(account);
    }

    @Transactional
    public MemberDto login(String account, String pwd) {
        Member member = memberRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));

        logger.info("controller login:::" + member.getAccount());

        checkPwd(pwd, member.getPassword());

        String accessToken = jwtTokenProvider.createToken(member.getAccount(), member.getMemberRole());
        jwtTokenProvider.createRefreshToken(member.getAccount(), member.getMemberRole());

        return MemberDto.builder()
                .id(member.getId())
                .account(member.getAccount())
                .info(member.getInfo())
                .name(member.getName())
                .token(accessToken)
                .build();
    }

    private void checkPwd(String paramPwd, String memberPwd) {
        if (!passwordEncoder.matches(paramPwd, memberPwd)) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
    }
}
