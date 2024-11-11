package com.example.server.Models.Entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "straight_line_depreciation")
public class StraightLineDepreciation extends DepreciationCalculation {

    public StraightLineDepreciation() {}

}

