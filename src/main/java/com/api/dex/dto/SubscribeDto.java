package com.api.dex.dto;

import lombok.Data;

@Data
public class SubscribeDto {
    private long id;
    private long ownerId;
    private long boardId;

    private long fallowId;
    private String fallowName;
    private String fallowSrc;

}
