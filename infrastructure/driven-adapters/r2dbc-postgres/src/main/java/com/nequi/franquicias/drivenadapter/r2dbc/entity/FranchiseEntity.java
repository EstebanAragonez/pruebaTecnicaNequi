package com.nequi.franquicias.drivenadapter.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("franchises")
public record FranchiseEntity(@Id Long id, String nombre) {}
