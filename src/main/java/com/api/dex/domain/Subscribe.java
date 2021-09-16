package com.api.dex.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="subscribe")
public class Subscribe extends BaseEntity {
    @ManyToOne
    @JsonBackReference
    @JoinColumn
    private Member owner;

    @ManyToOne
    @JsonBackReference
    @JoinColumn
    private Board like;

    @ManyToOne
    @JsonBackReference
//    @JoinColumn(name = "member_id")
    @JoinColumn
    private Member fallow;

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
