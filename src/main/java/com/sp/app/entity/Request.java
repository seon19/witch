package com.sp.app.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "request")
@Getter
@Setter
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requestid")
    private Long requestId;

    @Column(name = "requestlevel", nullable = false)
    private Integer requestLevel;

    @Column(name = "requestname", nullable = false, length = 100)
    private String requestName;

    @Column(name = "requestcontent", nullable = false, length = 1000)
    private String requestContent;

    @Column(name = "requestdate", nullable = false)
    private LocalDateTime requestDate; 

    @Column(name = "client", nullable = false, length = 20)
    private String client;

    @Column(name = "rewardexp", nullable = false)
    private Integer rewardExp;  

    @Column(name = "rewardgold", nullable = false)
    private Integer rewardGold; 

    @Column(name = "rewardenable", nullable = false)
    private Integer rewardEnable; 
    
    @Column(name = "goalcount", nullable = false)
    private Integer goalCount = 1;

    @Column(name = "deadline")
    private LocalDateTime deadline;
    
    public enum RequestItem { MATERIAL, POTION }

    @Enumerated(EnumType.STRING)
    @Column(name = "requestitem", nullable = false, length = 20)
    private RequestItem requestItem; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materialid")
    private Material targetMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "potionid")
    private Potion targetPotion;
    
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MaterialReward> rewards = new HashSet<>();
}