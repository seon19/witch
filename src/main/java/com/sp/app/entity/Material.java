package com.sp.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "material")
@Getter
@Setter
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "materialid")
    private Long materialId;

    @Column(name = "materialname", nullable = false, length = 100)
    private String materialName;

    @Column(name = "materialdescription", length = 500)
    private String materialDescription;

    @Column(name = "materialprice")
    private Integer materialPrice;

    @Column(name = "materiallevel")
    private Integer materialLevel;

    @Column(name = "materialeffect", length = 100)
    private String materialEffect;

    @Column(name = "materialphoto", length = 500)
    private String materialPhoto;
}

