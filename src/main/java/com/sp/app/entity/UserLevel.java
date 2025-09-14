package com.sp.app.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "userlevel")
@Getter
@Setter
public class UserLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "levelid")
    private Long levelId;

    @Column(name = "changereason", nullable = false)
    private String changeReason;

    @Column(name = "finallevel", nullable = false)
    private Integer finalLevel;

    @Column(name = "finalexp", nullable = false)
    private Integer finalExp;

    @Column(name = "gainedexp", nullable = false)
    private Integer gainedExp;

    @CreationTimestamp
    @Column(name = "createdate", updatable = false)
    private LocalDateTime createDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberid", nullable = false)
    private Member member;
}

