package com.sp.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "materialReward")
@Getter
@Setter
public class MaterialReward {
    @EmbeddedId   
    private MaterialRewardId id = new MaterialRewardId();

    @Column(name = "qty", nullable = false)
    private Integer qty = 1;  

    @MapsId("materialId")   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materialId", nullable = false)
    private Material material;

    @MapsId("requestId")   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestId", nullable = false)
    private Request request;
}
