package com.sp.app.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable   
public class MaterialRewardId implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "materialId", nullable = false)
    private Long materialId;

    @Column(name = "requestId", nullable = false)
    private Long requestId;
}
