package com.api.dex.dto;

import com.api.dex.domain.MemberRole;
import com.google.gson.JsonElement;
import lombok.Data;

import javax.persistence.Column;

@Data
public class MemberDto {

    private String account;
    private String password;
    private String name;
    private String token;
    private MemberRole memberRole;

}
