package com.nequi.franquicias.drivenadapter.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("products")
public record ProductEntity(@Id Long id, String nombre, Integer stock, @Column("branch_id") Long branchId) {}
