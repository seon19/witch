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
@Table(name = "notice")
@Getter
@Setter
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noticeid")
    private Long noticeId;

    @Column(name = "noticename", nullable = false, length = 100)
    private String noticeName;

    @Column(name = "noticecontent", nullable = false, length = 1000)
    private String noticeContent;

    @CreationTimestamp
    @Column(name = "noticedate", nullable = false)
    private LocalDateTime noticeDate; 

    @Column(name = "noticeupdatedate")
    private LocalDateTime noticeUpdateDate; 

    @Column(name = "visibility", nullable = false)
    private Integer visibility = 1; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberid", nullable = false)
    private Member member; 
}