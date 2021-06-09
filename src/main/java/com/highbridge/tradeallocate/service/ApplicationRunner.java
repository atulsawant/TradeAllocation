package com.highbridge.tradeallocate.service;

import com.highbridge.tradeallocate.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Application runner, will be called after spring context load
 */
@Service
public class ApplicationRunner implements CommandLineRunner {

    private final ExtractData extractData;
    private final TargetAllocation targetAllocation;

    public ApplicationRunner(ExtractData extractData, TargetAllocation targetAllocation) {
        this.extractData = extractData;
        this.targetAllocation = targetAllocation;
    }

    @Override
    public void run(String... args) throws Exception {

        //extracting data from the csv files
        List<Capital> capData = extractData.readCsv(Capital.class, "static/capital.csv");
        List<Holdings> holdData = extractData.readCsv(Holdings.class, "static/holdings.csv");
        List<Targets> targetData = extractData.readCsv(Targets.class, "static/targets.csv");
        List<Trades> tradesData = extractData.readCsv(Trades.class, "static/trades.csv");

        //loading data from the csv file to a Map
        //todo: test for empty argument
        Map<String, List<TargetAllocationModel>> preTargetAllocationMap
                = targetAllocation.setTarAlloc(capData, holdData, targetData);
        //calculating data and loading to a Map
        Map<String, List<TargetAllocationModel>> postTargetAllocationMap
                = targetAllocation.getTarAlloc(preTargetAllocationMap, tradesData);
        //writing all the data from the map to csv
        extractData.writeCsv(postTargetAllocationMap);

        //For checks and mapping allocations
        List<Allocations> allocations
                = targetAllocation.setAllocations(postTargetAllocationMap, tradesData);
        //writing all the data for allocations.csv
        extractData.writeCsvAlloc(allocations);
    }
}
