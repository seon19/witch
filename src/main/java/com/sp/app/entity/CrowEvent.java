package com.sp.app.entity;

import java.time.LocalDate;

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
@Table(name = "crowevent")
@Getter
@Setter
public class CrowEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "croweventid")
    private Long crowEventId;

    @Column(name = "eventname", nullable = false, length = 100)
    private String eventName;

    @Column(name = "eventstarttime", nullable = false)
    private LocalDate eventStartTime;

    @Column(name = "eventendtime", nullable = false)
    private LocalDate eventEndTime;

    @Column(name = "crowspawncondition", nullable = false, length = 100)
    private String crowSpawnCondition;

    @Column(name = "eventstatus")
    private Integer eventstatus;

    @Column(name = "givequantity")
    private Integer giveQuantity;

    @Column(name = "receivequantity")
    private Integer receiveQuantity;

    @Column(name = "salequantity")
    private Integer saleQuantity;

    @Column(name = "saleprice")
    private Integer salePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "givematerialid")
    private Material giveMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receivematerialid")
    private Material receiveMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salematerialid")
    private Material saleMaterial;
}

