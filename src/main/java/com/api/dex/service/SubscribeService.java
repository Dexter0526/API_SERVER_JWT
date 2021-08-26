package com.api.dex.service;

import com.api.dex.domain.*;
import com.api.dex.dto.SubscribeDto;
import org.hibernate.search.exception.SearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscribeService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    public Subscribe save(SubscribeDto subscribeDto){
        Member owner = null;
        Board board = null;

        if(subscribeDto.getOwnerId() != 0){
            owner = memberRepository.findById(subscribeDto.getOwnerId()).orElseThrow(() -> new SearchException("Not found member!"));
        }else{
            board = boardRepository.findById(subscribeDto.getBoardId());
        }

        Subscribe subscribe = Subscribe.builder()
                .owner(owner)
                .like(board)
                .fallow(memberRepository.findById(subscribeDto.getFallowId()).orElseThrow(() -> new SearchException("Not found member!")))
                .build();

        return subscribeRepository.save(subscribe);
    }

    public SubscribeDto insertSubscribe(SubscribeDto subscribeDto){
        Subscribe subscribe = save(subscribeDto);

        subscribeDto.setFallowName(subscribe.getFallow().getName());
        subscribeDto.setId(subscribe.getId());

//        컨트롤러에서 set src
//        subscribeDto.setFallowSrc();

        return subscribeDto;
    }


}
