package com.api.dex.dto;

import com.api.dex.domain.MemberRole;
import com.google.gson.JsonElement;
import lombok.Data;

import javax.persistence.Column;

@Data
public class MemberDto {

    private long id;
    private String account;
    private String password;
    private String name;
    private String token;
    private String info;
    private String src;
    private MemberRole memberRole;

    //    접속자 팔로우 여부
    private SubscribeDto fallow;
}
