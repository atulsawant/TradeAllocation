package com.highbridge.tradeallocate.service;

import com.highbridge.tradeallocate.model.Capital;
import com.highbridge.tradeallocate.model.Holdings;
import com.highbridge.tradeallocate.model.Targets;
import com.highbridge.tradeallocate.model.Trades;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Collections;

@SpringBootTest
class ApplicationRunnerTest {

    @Autowired
    ApplicationRunner applicationRunner;

    static Capital capital = new Capital();
    static Targets targets = new Targets();
    static Trades trades = new Trades();
    static Holdings holdings = new Holdings();

    @BeforeAll
    static void setUp(){
        capital.setAccount("Test");
        capital.setCapital(BigDecimal.ONE);
    }

    @MockBean
    ExtractData extractData;

    @MockBean
    TargetAllocation targetAllocation;

    @Test
    void run() throws Exception {

        //Mocking all method calls, no logic resides in this method
        Mockito.when(extractData.readCsv(Capital.class, "static/capital.csv"))
                .thenReturn(Collections.singletonList(capital));
        Mockito.when(extractData.readCsv(Holdings.class, "static/holdings.csv"))
                .thenReturn(Collections.singletonList(holdings));
        Mockito.when(extractData.readCsv(Targets.class, "static/targets.csv"))
                .thenReturn(Collections.singletonList(targets));
        Mockito.when(extractData.readCsv(Trades.class, "static/trades.csv"))
                .thenReturn(Collections.singletonList(trades));

        /*Mockito.doNothing().when(targetAllocation)
                .getTarAlloc(Collections.emptyMap(), Collections.emptyList());
        Mockito.doNothing().when(targetAllocation)
                .setTarAlloc(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());*/
        Mockito.doNothing().when(extractData).writeCsv(Collections.emptyMap());

        applicationRunner.run();
    }
}