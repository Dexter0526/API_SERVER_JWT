package com.api.dex.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="board")
public class Board extends BaseEntity implements Serializable {

    @Column
    private String category;

    @Column
    private String title;

    @Column
    private String content;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "member_id")
    private Member boardMember;

    @OneToMany(mappedBy = "fileBoard", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<File> files;

    @Builder
    public Board(String category, String title, String content, Member member){
        this.category = category;
        this.title = title;
        this.content = content;
        this.boardMember = member;
    }
}
