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
    private long memberId;
    private List<FileDto> fileDtos;
}
