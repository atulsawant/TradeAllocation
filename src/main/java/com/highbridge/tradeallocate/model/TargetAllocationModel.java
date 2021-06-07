package com.highbridge.tradeallocate.model;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class TargetAllocationModel implements Model{

    private String account;
    private BigDecimal capital;
    private String stock;
    private BigDecimal stockPrice;
    private BigInteger qtyHeld;
    private Float target;
    private BigDecimal tarMktValue;
    private BigDecimal maxShares;
    private Integer allPosition;
    private BigDecimal sugFinalPos;
    private BigDecimal sugTradeAlloc;

    public String toString(){
        return "Account: " + account + "Capital:" + capital + " Stock:" + stock +
                " Stock Price:" + stockPrice + " Quantity Held:" + qtyHeld +
                " Target:" + target + " Tarket Market Value:" + tarMktValue +
                " Max Shares:" + maxShares + " All In Position:" + allPosition +
                " Suggested Final Position:" + sugFinalPos + " Suggested Trade Allocation:" + sugTradeAlloc;
    }
}
