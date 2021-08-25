package com.api.dex.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="log")
public class Subscribe extends BaseEntity {
    @ManyToOne
    @JsonBackReference
    @JoinColumn
    private Member owner;

    @ManyToOne
    @JsonBackReference
//    @JoinColumn(name = "member_id")
    @JoinColumn
    private Member fallow;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "board_id")
    private Board like;

//    @OneToMany(mappedBy = "fileMember", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
//    @JsonManagedReference
//    private List<Member> members;

    @Builder
    public Subscribe(Member owner, Member fallow, Board like){
        this.owner = owner;
        this.fallow = fallow;
        this.like = like;
    }
}
