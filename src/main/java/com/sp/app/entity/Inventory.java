package com.sp.app.entity;

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
@Table(name = "inventory")
@Getter
@Setter
public class Inventory {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventoryid")
    private Long inventoryId;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberid", nullable = false)
    private Member member;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materialid")
    private Material material;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "potionid")
    private Potion potion;
}
