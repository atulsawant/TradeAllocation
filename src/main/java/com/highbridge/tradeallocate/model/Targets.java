package com.highbridge.tradeallocate.model;

import lombok.Data;

/*
Beans for targets.csv
 */
@Data
public class Targets implements Model{

    private String stock;
    private String account;
    private Float targetPercent;

}
