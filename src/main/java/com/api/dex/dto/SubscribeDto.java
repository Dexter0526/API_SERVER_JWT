package com.api.dex.dto;

import lombok.Data;

@Data
public class SubscribeDto {
    private long id;
    private long ownerId = 0;
    private long boardId = 0;

    private long fallowId;
    private String fallowAccount;
    private String fallowName;
    private String fallowSrc;

}
