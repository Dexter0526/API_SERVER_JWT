package com.api.dex.listener;

import com.api.dex.domain.Member;
import com.api.dex.domain.MemberRepository;
import com.api.dex.domain.MemberRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServletContextListenerImpl implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ServletContextListenerImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) { // 웹 어플리케이션이 초기화(실행)될 때 호출
        logger.info("**************************************************");
        logger.info("**************************************************");
        logger.info("ServletContextListenerImpl:contextInitialized");
        logger.info("**************************************************");
        logger.info("**************************************************");

        // Listener 에서 Bean 가져오기
        AutowireCapableBeanFactory autowireCapableBeanFactory = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContextEvent.getServletContext()).getAutowireCapableBeanFactory();
        autowireCapableBeanFactory.autowireBean(this);

        if(memberRepository.count() == 0){
            Member member = new Member(new MemberRole(MemberRole.RoleType.ROLE_ADMIN),"dexter", passwordEncoder.encode("0526"), "박정수", null, null);
            memberRepository.save(member);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) { // 웹 어플리케이션이 종료될 때 호출
        logger.info("**************************************************");
        logger.info("**************************************************");
        logger.info("ServletContextListenerImpl:contextDestroyed");
        logger.info("**************************************************");
        logger.info("**************************************************");
    }

}
