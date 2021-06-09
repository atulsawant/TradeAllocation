package com.highbridge.tradeallocate.model;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/*
Beans for holdings.csv
 */
@Data
public class Holdings implements Model{

    private String account;
    private String stock;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal marketValue;

}
