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
        assertNotNull(capData);
        assertEquals(capData.get(0).getAccount(), "John");
        assertEquals(capData.get(0).getCapital(), BigDecimal.valueOf(50_000));
    }

    @Test
    void loadObjectListBlankTest() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            extractDataSrv.readCsv(Capital.class, "static/blank.csv");
        });
        String expectedMessage = "File not found or is empty or unrecognized field";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

    }

    //todo: test id number of variables are different then expected
    @Test
    void loadObjectCheckVariables() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            List<Capital> capData = extractDataSrv.readCsv(Capital.class, "static/wrongcapital.csv");
        });

        String expectedMessage = "File not found or is empty or unrecognized field";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void loadObjectNotExist() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            extractDataSrv.readCsv(Capital.class, "notExist.csv");
        });

        String expectedMessage = "File not found or is empty or unrecognized field";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}