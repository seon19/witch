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
@Table(name = "shop")
@Getter
@Setter
public class Shop {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shopid")
	private Long shopId;

	@Column(name = "sellingprice", nullable = false)
	private Integer sellingPrice;

	@Column(name = "isavailable", nullable = false)
	private Boolean isAvailable;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "materialid", nullable = false)
	private Material material;
	
}
