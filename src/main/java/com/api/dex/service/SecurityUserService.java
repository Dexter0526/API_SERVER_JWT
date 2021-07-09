package com.api.dex.service;

import com.api.dex.domain.Member;
import com.api.dex.domain.MemberRepository;
import com.api.dex.domain.MemberRole;
import com.api.dex.domain.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SecurityUserService implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(SecurityUserService.class);

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        logger.info("*************************************");
        logger.info("SecurityUserService:loadUserByUsername:ACCOUNT = " + account);
        logger.info("*************************************");

        Set<MemberRole> memberRoles = new HashSet<MemberRole>();
        Member member = memberRepository.findByAccount(account).get();

        if(member != null) {
            logger.info("*************************************************************");
            logger.info("loadUserByUsername" + member.getId());
            logger.info("loadUserByUsername" + member.getAccount());
            logger.info("loadUserByUsername" + member.getName());
            logger.info("*************************************************************");

//            if(member.getToken() != null) {
//                logger.info("loadUserByUsername:TOKEN IS NOT NULL!!!");
//                return SecurityUser.builder().build();
//            }

//            MemberRole auth = member.getRole();
//            memberRoles.add(auth);
            logger.info("loadUserByUsername:LOGIN SUCCESS!");
            return SecurityUser.builder()
                    .member(member)
                    .roles(memberRoles)
                    .build();
        } else {
            return SecurityUser.builder().build();
        }
    }
}
