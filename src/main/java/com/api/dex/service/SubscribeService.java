package com.api.dex.service;

import com.api.dex.domain.*;
import com.api.dex.dto.SubscribeDto;
import com.api.dex.utils.S3;
import org.hibernate.search.exception.SearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SubscribeService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private S3 s3;

    public Subscribe save(SubscribeDto subscribeDto){
        Member owner = null;
        Board board = null;
        Member member = memberRepository.findByAccount(subscribeDto.getFallowAccount()).orElseThrow(() -> new SearchException("Not found member!"));
        if(subscribeDto.getOwnerId() != 0){
            owner = memberRepository.findById(subscribeDto.getOwnerId()).orElseThrow(() -> new SearchException("Not found member!"));
            if(subscribeRepository.findByOwner_IdAndFallow_Id(owner.getId(), member.getId()) != null){
                logger.info("exception !!!!");
                throw new IllegalArgumentException("이미 존재합니다");
            }
        }else if (subscribeDto.getBoardId() != 0){
            board = boardRepository.findById(subscribeDto.getBoardId());
            if(subscribeRepository.findByLike_IdAndFallow_Id(board.getId(), member.getId()) != null){
                throw new IllegalArgumentException("이미 존재합니다");
            }
        }

        Subscribe subscribe = Subscribe.builder()
                .owner(owner)
                .like(board)
                .fallow(member)
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

    public Map<String, Object> getSubscribePage(Long ownerId, Long likeId, int page){
        Map<String, Object> result = new HashMap<>();
        Page<Subscribe> subscribePage = null;
        List<SubscribeDto> subscribeDtoList = new ArrayList<>();

        if(ownerId != null){
            subscribePage = subscribeRepository.findByOwner_Id(ownerId, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id")));
        }else{
            subscribePage = subscribeRepository.findByLike_Id(likeId, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id")));
        }

        List<Subscribe> subscribeList = subscribePage.getContent();
        Iterator<Subscribe> iterator = subscribeList.iterator();

        while (iterator.hasNext()){
            Subscribe subscribe = iterator.next();
            SubscribeDto subscribeDto = new SubscribeDto();
            File file = fileRepository.findFirstByFileMember_IdOrderByIdDesc(subscribe.getFallow().getId());

            subscribeDto.setId(subscribe.getId());
            if(subscribe.getOwner() != null){
                subscribeDto.setOwnerId(subscribe.getOwner().getId());
            }
            if(subscribe.getLike() != null){
                subscribeDto.setBoardId(subscribe.getLike().getId());
            }
            subscribeDto.setFallowId(subscribe.getFallow().getId());
            subscribeDto.setFallowName(subscribe.getFallow().getName());
//            subscribeDto.setFallowSrc(PathManagement.src + fileRepository.findFirstByFileMember_IdOrderByIdDesc(subscribe.getFallow().getId()).getId());
            subscribeDto.setFallowSrc(s3.getSrc(file.getPath(), file.getServerName()));

            subscribeDtoList.add(subscribeDto);
        }

        result.put("subscribes", subscribeDtoList);
        result.put("TotalElements", subscribePage.getTotalElements());
        result.put("page", page);

        return result;
    }

    public void deleteSubscribe(Long id, String account){
        subscribeRepository.deleteByIdAndFallow_Account(id, account);
    }

}
