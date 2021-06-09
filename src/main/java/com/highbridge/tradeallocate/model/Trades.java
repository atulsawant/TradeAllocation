package com.highbridge.tradeallocate.model;

import lombok.Data;
import java.math.BigDecimal;
import java.math.BigInteger;

/*
Beans for trades.csv
 */
@Data
public class Trades implements Model{

    private String stock;
    private String type;
    private Integer quantity;
    private BigDecimal price;

}
