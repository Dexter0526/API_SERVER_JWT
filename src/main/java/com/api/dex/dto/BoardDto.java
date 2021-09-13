package com.api.dex.dto;

import com.api.dex.domain.Member;
import lombok.Data;

import java.util.List;

@Data
public class BoardDto {
    private long id;
    private String category;
    private String title;
    private String content;
    private String name;
//    private long memberId;
    private List<FileDto> fileDtos;
//    보드 주인
    private MemberDto memberDto;
//    접속자 팔로우 여부
    private SubscribeDto fallow;
}
