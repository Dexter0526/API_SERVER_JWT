package com.api.dex.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="file")
public class File extends BaseEntity implements Serializable {

    @Column
    private String fileType;

    @Column
    private String originalName;

    @Column
    private String serverName;

    @Column
    private String path;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "board_id")
    private Board fileBoard;

    @Builder
    public File(String fileType, String originalName, String serverName, String path, Board board){
        this.fileType = fileType;
        this.originalName = originalName;
        this.serverName = serverName;
        this.path = path;
        this.fileBoard = board;
    }
}
