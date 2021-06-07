package com.highbridge.tradeallocate.service;

import com.highbridge.tradeallocate.model.Capital;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExtractDataSrvTest {

    @Autowired
    private ExtractDataSrv extractDataSrv;

    @Test
    void loadObjectList() {

        List<Capital> capData = extractDataSrv.readCsv(Capital.class, "static/capital.csv");
        assertEquals(capData.get(0).getAccount(), "John");
        assertEquals(capData.get(0).getCapital(), BigDecimal.valueOf(50_000));

    }
}