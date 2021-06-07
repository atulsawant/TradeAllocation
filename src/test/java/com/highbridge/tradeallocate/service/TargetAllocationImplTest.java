package com.highbridge.tradeallocate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TargetAllocationImplTest {

    @Autowired
    TargetAllocationImpl targetAllocation;

    @Test
    void setTarAlloc() {

    }

    @Test
    void getTarAlloc() {
    }

    @Test
    void getTarMktValue() {
    }

    @Test
    void getMaxShares() {
        BigDecimal divide = targetAllocation.getMaxShares(BigDecimal.TEN, BigDecimal.ONE);
        assertEquals(divide, BigDecimal.TEN.divide(BigDecimal.ONE));
    }

    @Test
    void getAllPositions() {
    }

    @Test
    void getSugFinalPos() {
    }
}