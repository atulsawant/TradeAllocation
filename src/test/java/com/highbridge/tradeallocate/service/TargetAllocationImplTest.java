package com.highbridge.tradeallocate.service;

import com.highbridge.tradeallocate.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TargetAllocationImplTest {

    static Capital capital = new Capital();
    static Targets targets = new Targets();
    static Trades trades = new Trades();
    static Holdings holdings = new Holdings();
    static TargetAllocationModel targetAllocationModel = new TargetAllocationModel();
    static Map<String, List<TargetAllocationModel>> targetAllocMapList = new HashMap<>();
    static List<TargetAllocationModel> targetAllocationModelList = new ArrayList<>();
    static List<Trades> tradesList = new ArrayList<>();
    static final String account = "John";
    static final String stock = "GOOGLE";
    static final Integer allPos = 160;

    @BeforeAll
    static void setUp(){
        //setting up capital model
        capital.setAccount(account);
        capital.setCapital(BigDecimal.valueOf(50000));

        //setting up targets model
        targets.setAccount(account);
        targets.setTargetPercent(4.0f);
        targets.setStock(stock);

        //setting up trades model
        trades.setStock(stock);
        trades.setPrice(BigDecimal.valueOf(20));
        trades.setQuantity(100);
        trades.setType("BUY");

        //setting up holdings model
        holdings.setAccount(account);
        holdings.setQuantity(50);
        holdings.setStock(stock);
        holdings.setPrice(BigDecimal.valueOf(20));
        holdings.setMarketValue(BigDecimal.valueOf(1000));

        //setting up list of target allocation model
        targetAllocationModel.setAccount(capital.getAccount());
        targetAllocationModel.setCapital(capital.getCapital());
        targetAllocationModel.setStock(targets.getStock());
        targetAllocationModel.setQtyHeld(holdings.getQuantity());
        targetAllocationModel.setTarget(targets.getTargetPercent());
        targetAllocationModel.setStockPrice(holdings.getPrice());
        targetAllocationModel.setTarMktValue(BigDecimal.valueOf(2000));
        targetAllocationModel.setMaxShares(BigDecimal.valueOf(100));
        targetAllocationModel.setAllPosition(160);
        targetAllocationModel.setSugFinalPos(new BigDecimal(91.43));
        targetAllocationModel.setSugTradeAlloc(new BigDecimal(41.43));

        targetAllocationModelList.add(targetAllocationModel);
        targetAllocMapList.put(account, targetAllocationModelList);
        tradesList.add(trades);
    }

    @Autowired
    TargetAllocation targetAllocation;

    @Test
    void setTarAlloc() {
        Map<String, List<TargetAllocationModel>> targetAlloc
                = targetAllocation.setTarAlloc(Collections.singletonList(capital),
                Collections.singletonList(holdings),
                Collections.singletonList(targets));

        assertEquals(targetAlloc.size(), 1);
        assertTrue(targetAlloc.containsKey(account));
        assertEquals(targetAlloc.get(account).size(), 1);
        assertEquals(targetAlloc.get(account).get(0).getAccount(), account);
        assertEquals(targetAlloc.get(account).get(0).getCapital(), capital.getCapital());
        assertEquals(targetAlloc.get(account).get(0).getStock(), targets.getStock());
        assertEquals(targetAlloc.get(account).get(0).getQtyHeld(), holdings.getQuantity());
        assertEquals(targetAlloc.get(account).get(0).getTarget(), targets.getTargetPercent());
        assertEquals(targetAlloc.get(account).get(0).getStockPrice(), holdings.getPrice());

    }

    @Test
    void getTarAlloc() {
        Map<String, List<TargetAllocationModel>> targetAlloc =
                targetAllocation.getTarAlloc(targetAllocMapList, tradesList);

        assertEquals(targetAlloc.size(), 1);
        assertTrue(targetAlloc.containsKey(account));
        assertEquals(targetAlloc.get(account).size(), 1);

        assertEquals(targetAlloc.get(account).get(0).getAccount(), account);
        assertEquals(targetAlloc.get(account).get(0).getCapital(), capital.getCapital());
        assertEquals(targetAlloc.get(account).get(0).getStock(), targets.getStock());
        assertEquals(targetAlloc.get(account).get(0).getQtyHeld(), holdings.getQuantity());
        assertEquals(targetAlloc.get(account).get(0).getTarget(), targets.getTargetPercent());
        assertEquals(targetAlloc.get(account).get(0).getStockPrice(), holdings.getPrice());

        assertEquals(targetAlloc.get(account).get(0).getTarMktValue(), targetAllocationModel.getTarMktValue());
        assertEquals(targetAlloc.get(account).get(0).getMaxShares(), targetAllocationModel.getMaxShares());
        assertEquals(targetAlloc.get(account).get(0).getAllPosition(), targetAllocationModel.getAllPosition());
        assertEquals(targetAlloc.get(account).get(0).getSugFinalPos(), targetAllocationModel.getSugFinalPos());
        assertEquals(targetAlloc.get(account).get(0).getSugTradeAlloc(), targetAllocationModel.getSugTradeAlloc());

    }


    @Test
    void getTarMktValue() {
        BigDecimal tarPer = targetAllocation.getTarMktValue(targets.getTargetPercent(), capital.getCapital());
        assertEquals(tarPer.setScale(1), BigDecimal.valueOf(2000.0));
    }

    @Test
    void getMaxShares() {
        BigDecimal divide = targetAllocation.getMaxShares(targetAllocationModel.getTarMktValue(), trades.getPrice());;
        assertEquals(divide, BigDecimal.valueOf(100));
    }

    @Test
    void getAllPositions() {
        int totalPostn = targetAllocation.getAllPositions(targetAllocationModelList, trades.getQuantity());
        assertEquals(totalPostn, 150);
    }

    @Test
    void getSugFinalPos() {
        List<TargetAllocationModel> tarAllocModelList =  targetAllocation.getSugFinalPos(targetAllocationModelList, allPos);
        assertEquals(tarAllocModelList.get(0).getSugFinalPos(), BigDecimal.valueOf(160));
    }

    @Test
    void setAllocations(){
        List<Allocations> allocations
                = targetAllocation.setAllocations(targetAllocMapList, tradesList);

        assertEquals(allocations.size(), 1);
        assertEquals(allocations.get(0).getAccount(), account);
        assertEquals(allocations.get(0).getStock(), stock);
        assertEquals(allocations.get(0).getQuantity(), "+41");

        boolean checkError = targetAllocation.checkErrorCondition(targetAllocationModel, tradesList);
        assertTrue(checkError);

    }


}