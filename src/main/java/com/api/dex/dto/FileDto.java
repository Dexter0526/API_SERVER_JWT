package com.api.dex.dto;

import com.api.dex.domain.Board;
import lombok.Data;

@Data
public class FileDto {
    private long id;
    private String fileType;
    private String originalName;
    private String serverName;
    private String path;
    private Board fileBoard;
}
