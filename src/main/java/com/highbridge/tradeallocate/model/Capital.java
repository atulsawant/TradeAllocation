package com.highbridge.tradeallocate.model;

import lombok.Data;

import java.math.BigDecimal;
/*
Beans for capital.csv data
 */
@Data
public class Capital implements Model{

    private String account;
    private BigDecimal capital;
}
