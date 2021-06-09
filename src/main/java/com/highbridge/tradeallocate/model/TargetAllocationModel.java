package com.highbridge.tradeallocate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class TargetAllocationModel implements Model{

    @JsonProperty("Account")
    private String account;
    @JsonProperty("Capital")
    private BigDecimal capital;
    @JsonProperty("Stock")
    private String stock;
    @JsonProperty("Stock_Price")
    private BigDecimal stockPrice;
    @JsonProperty("Quantity_Held")
    private Integer qtyHeld;
    @JsonProperty("Target")
    private Float target;
    @JsonProperty("TARGET_MARKET_VALUE")
    private BigDecimal tarMktValue;
    @JsonProperty("MAX_SHARES")
    private BigDecimal maxShares;
    @JsonProperty("ALL_IN_POSITION")
    private Integer allPosition;
    @JsonProperty("SUGGESTED_FINAL_POSITION")
    private BigDecimal sugFinalPos;
    @JsonProperty("SUGGESTED_TRADE_ALLOCATION")
    private BigDecimal sugTradeAlloc;

}
