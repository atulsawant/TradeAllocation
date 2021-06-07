package com.highbridge.tradeallocate;

import com.highbridge.tradeallocate.model.*;
import com.highbridge.tradeallocate.service.ExtractData;
import com.highbridge.tradeallocate.service.TargetAllocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;


@SpringBootApplication
@Slf4j
public class TradeAllocateApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(TradeAllocateApplication.class, args);

        ExtractData extractData = applicationContext.getBean(ExtractData.class);
        TargetAllocation targetAllocation = applicationContext.getBean(TargetAllocation.class);

        //extracting data from the csv files
        List<Capital> capData = extractData.readCsv(Capital.class, "static/capital.csv");
        List<Holdings> holdData = extractData.readCsv(Holdings.class, "static/holdings.csv");
        List<Targets> targetData = extractData.readCsv(Targets.class, "static/targets.csv");
        List<Trades> tradesData = extractData.readCsv(Trades.class, "static/trades.csv");

        //loading data from the csv file to a Map
        Map<String, List<TargetAllocationModel>> preTargetAllocationMap = targetAllocation.setTarAlloc(capData, holdData, targetData);
        //calculating data and loading to a Map
        Map<String, List<TargetAllocationModel>> postTargetAllocationMap = targetAllocation.getTarAlloc(preTargetAllocationMap, tradesData);
        //writing all the data from the map to csv
        extractData.writeCsv(postTargetAllocationMap);


    }


}
