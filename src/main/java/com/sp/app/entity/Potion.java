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
@Table(name = "potion")
@Getter
@Setter
public class Potion {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "potionid")
    private Long potionId;
	
	@Column(name = "potionname", nullable = false, length = 100)
    private String potionName;

    @Column(name = "potiondescription", length = 500)
    private String potionDescription;

    @Column(name = "potionlevel")
    private Integer potionLevel;

    @Column(name = "tastdescription", length = 500)
    private String tasteDescription;

    @Column(name = "materialcomposition", length = 500)
    private String materialComposition;
    
    @Column(name = "potionphoto", length = 255)
    private String potionPhoto;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "exp")
    private Integer exp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firstmaterialid", nullable = false)
    private Material firstMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secondmaterialid", nullable = false)
    private Material secondMaterial;

}
