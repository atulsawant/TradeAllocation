package com.highbridge.tradeallocate.service;

import com.highbridge.tradeallocate.model.Allocations;
import com.highbridge.tradeallocate.model.Capital;
import com.highbridge.tradeallocate.model.TargetAllocationModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExtractDataSrvTest {

    @Autowired
    private ExtractDataSrv extractDataSrv;
    static TargetAllocationModel targetAllocationModel = new TargetAllocationModel();
    static Allocations allocations = new Allocations();
    static final String account = "John";

    @BeforeAll
    static void setUp(){
        //setting up list of target allocation model
        targetAllocationModel.setAccount(account);
        targetAllocationModel.setCapital(BigDecimal.valueOf(50000));
        targetAllocationModel.setStock("GOOGLE");
        targetAllocationModel.setQtyHeld(50);
        targetAllocationModel.setTarget(4.0f);
        targetAllocationModel.setStockPrice(BigDecimal.valueOf(20));
        targetAllocationModel.setTarMktValue(BigDecimal.valueOf(2000));
        targetAllocationModel.setMaxShares(BigDecimal.valueOf(100));
        targetAllocationModel.setAllPosition(160);
        targetAllocationModel.setSugFinalPos(new BigDecimal(91.43));
        targetAllocationModel.setSugTradeAlloc(new BigDecimal(41.43));

        //John	GOOGLE	+41
        allocations.setQuantity("+41");
        allocations.setAccount("John");
        allocations.setStock("GOOGLE");
    }
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

    @Test
    void writeCsv(){

        extractDataSrv.writeCsv(Collections.singletonMap(account,Collections.singletonList(targetAllocationModel)));

        File tempDir = new File("tmp/TargetAllocation");
        tempDir.mkdirs();
        File tempFile = new File(tempDir, "TargetAllocation.csv");

        //test files exists and is not not empty
        assertTrue(tempDir.exists());
        assertTrue((tempFile.exists()));
        assertTrue(tempFile.length()!=0);

    }

    @Test
    void writeCsvAlloc(){

        extractDataSrv.writeCsvAlloc(Collections.singletonList(allocations));

        File tempDir = new File("tmp/Allocations");
        tempDir.mkdirs();
        File tempFile = new File(tempDir, "allocations.csv");

        //test files exists and is not not empty
        assertTrue(tempDir.exists());
        assertTrue((tempFile.exists()));
        assertTrue(tempFile.length()!=0);

    }
}