package com.sp.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "boardLike")
@Getter @Setter
@ToString(exclude = { "board", "member" })
public class BoardLike {

    @EmbeddedId
    private BoardLikeId id = new BoardLikeId();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("boardId")
    @JoinColumn(name = "boardId", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("memberId")
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

}
