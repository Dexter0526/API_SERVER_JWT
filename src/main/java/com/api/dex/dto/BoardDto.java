package com.api.dex.dto;

import com.api.dex.domain.Member;
import lombok.Data;

@Data
public class BoardDto {
    private long id;
    private String category;
    private String title;
    private String content;
    private String name;
}
