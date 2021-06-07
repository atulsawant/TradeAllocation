package com.highbridge.tradeallocate.service;

import com.highbridge.tradeallocate.model.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface TargetAllocation {

    public BigDecimal getTarMktValue(Float tarPer, BigDecimal capital);
    public BigDecimal getMaxShares(BigDecimal tarMktValue, BigDecimal curStockPrice);
    public Integer getAllPositions(List<TargetAllocationModel> investors, BigInteger newTradeQty);
    public List<TargetAllocationModel> getSugFinalPos(List<TargetAllocationModel> investors, Integer allPos);
    public Map<String, List<TargetAllocationModel>> getTarAlloc(Map<String, List<TargetAllocationModel>> preTargetAllocationMap, List<Trades> tradesData);
    public Map<String, List<TargetAllocationModel>> setTarAlloc(List<Capital> capData, List<Holdings> holdData,
                                                                List<Targets> targetData);
}
