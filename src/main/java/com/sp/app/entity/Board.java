package com.sp.app.entity;

import java.time.LocalDateTime;
import java.util.*;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board")
@Getter @Setter
@ToString(exclude = { "member", "replies", "likes" })
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardId")
    private Long boardId;

    @Column(name = "postName", nullable = false, length = 100)
    private String postName;

    @Column(name = "postContent", nullable = false, length = 3000)
    private String postContent;

    @CreationTimestamp
    @Column(name = "postDate", nullable = false, updatable = false)
    private LocalDateTime postDate;

    @Column(name = "hitCount", nullable = false)
    private Integer hitCount = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardReply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BoardLike> likes = new HashSet<>();
}
