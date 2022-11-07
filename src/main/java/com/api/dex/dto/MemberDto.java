package com.api.dex.dto;

import com.api.dex.domain.MemberRole;
import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Setter
@Getter
@Builder
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

    @Setter
    @Getter
    @Builder
    public class LoginDto{
        private String account;
        private String password;
    }
}
