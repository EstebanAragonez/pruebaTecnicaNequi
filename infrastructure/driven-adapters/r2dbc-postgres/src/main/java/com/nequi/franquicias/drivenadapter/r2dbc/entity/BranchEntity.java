package com.nequi.franquicias.drivenadapter.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("branches")
public record BranchEntity(@Id Long id, String nombre, @Column("franchise_id") Long franchiseId) {}
