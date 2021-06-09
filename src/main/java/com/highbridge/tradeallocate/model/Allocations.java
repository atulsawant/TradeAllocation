package com.highbridge.tradeallocate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Allocations implements Model{

    @JsonProperty("Account")
    private String account;
    @JsonProperty("Stock")
    private String stock;
    @JsonProperty("Quantity")
    private String Quantity;
}
