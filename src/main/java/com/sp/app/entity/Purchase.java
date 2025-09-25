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
@Table(name = "purchase")
@Getter
@Setter
public class Purchase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "purchaseid")
	private Long purchaseId;

	@Column(name = "purchaseprice", nullable = false)
	private Integer purchasePrice;

	@Column(name = "isavailable", nullable = false)
	private Boolean isAvailable;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "materialid")
	private Material material;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "potionId")
    private Potion potion;
	
}
