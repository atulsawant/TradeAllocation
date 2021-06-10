package com.highbridge.tradeallocate.service;

import com.highbridge.tradeallocate.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TargetAllocationImpl implements TargetAllocation {

    /**
     * @param capData    Capital and account info
     * @param holdData   Holdings data for a given account with information like Stock, quantity, price and market value.
     * @param targetData Targets data has account, stock and target percentage allocated for the account
     * @return Map of accounts which has list of records which is filled with information extracted from
     * csv files like Trades, Capital, Holdings and Targets
     */
    @Override
    public Map<String, List<TargetAllocationModel>> setTarAlloc(List<Capital> capData, List<Holdings> holdData,
                                                                List<Targets> targetData) {

        // Map of Account as Key and Allocations as Value
        Map<String, List<TargetAllocationModel>> tarAllocMap = new HashMap<>();
        TargetAllocationModel targetAllocation;

        String cpAccount, hAccount, tAccount, hStock, tStock;
        BigDecimal cpCap, hStockPrice;
        Integer hQty;
        Float targetP;

        // setting holdings data into tradeallocation model
        if (!holdData.isEmpty()) {
            for (Holdings hData : holdData) {

                hAccount = hData.getAccount();
                hStock = hData.getStock();
                hQty = hData.getQuantity();
                hStockPrice = hData.getPrice();

                targetAllocation = new TargetAllocationModel();

                if (!hAccount.isEmpty())
                    targetAllocation.setAccount(hAccount);

                if ((hAccount.equals(targetAllocation.getAccount()) &&
                        (targetAllocation.getStock() == null))) {
                    if (!hStock.isEmpty())
                        targetAllocation.setStock(hStock);
                    if (hQty != null)
                        targetAllocation.setQtyHeld(hQty);
                    if (hStockPrice != null)
                        targetAllocation.setStockPrice(hStockPrice);

                    //setting targets data into tradeallocation model
                    if (!targetData.isEmpty()) {
                        for (Targets tData : targetData) {

                            targetP = tData.getTargetPercent();
                            tAccount = tData.getAccount();
                            tStock = tData.getStock();

                            //unique record can be combination of account and stock
                            if ((tAccount.equals(targetAllocation.getAccount())) &&
                                    (tStock.equals(targetAllocation.getStock()))) {
                                if (targetP != null)
                                    targetAllocation.setTarget(targetP);

                                //setting capital data into TargetAllocation model
                                if (!capData.isEmpty()) {
                                    for (Capital cData : capData) {

                                        cpAccount = cData.getAccount();
                                        cpCap = cData.getCapital();

                                        if (cpAccount.equals(targetAllocation.getAccount())) {
                                            targetAllocation.setCapital(cpCap);
                                        }
                                    } // end of setting capital data
                                }
                                tarAllocMap.computeIfAbsent(hAccount, k -> new ArrayList<>()).add(targetAllocation);
                                break;
                            }
                            //end of setting targets data
                        }
                    }
                }
                //end of setting holdings data
            }
            // end of setting targetallocation data
        }

        log.info("Hashmap produced is", tarAllocMap);
        return tarAllocMap;
    }

    /**
     * @param preTargetAllocationMap Map of accounts which has list of records which is filled with information
     *                               extracted from csv files like Trades, Capital, Holdings and Targets
     * @param tradesData             List of Trades data which has info like Stock, Account and target percentage.
     * @return Map of accounts which has list of records which is filled with information extracted from
     * csv files like Trades, Capital, Holdings and Targets as well as calculated allocations
     */
    @Override
    public Map<String, List<TargetAllocationModel>> getTarAlloc(Map<String, List<TargetAllocationModel>> preTargetAllocationMap, List<Trades> tradesData) {
        //set Target market Value and Max Share
        preTargetAllocationMap.forEach(
                (account, targetModel) -> {
                    for (TargetAllocationModel model : targetModel) {
                        model.setTarMktValue(getTarMktValue(model.getTarget(), model.getCapital()));
                        model.setMaxShares(getMaxShares(model.getTarMktValue(), model.getStockPrice()));
                    }
                }
        );

        // set all_in_position
        preTargetAllocationMap.forEach(
                (account, targetModel) -> {
                    for (TargetAllocationModel model : targetModel) {
                        preTargetAllocationMap.forEach(
                                (account2, targetModel2) -> {
                                    if (!account.equals(account2)) {
                                        for (TargetAllocationModel model2 : targetModel2) {
                                            if (model2.getStock().equals(model.getStock())) {
                                                List<TargetAllocationModel> investors = new ArrayList<>();
                                                List<TargetAllocationModel> updatedInv = new ArrayList<>();
                                                investors.add(model);
                                                investors.add(model2);

                                                for (Trades trade : tradesData) {
                                                    if (trade.getStock().equals(model.getStock())) {
                                                        model.setAllPosition(getAllPositions(investors, trade.getQuantity()));
                                                        break;
                                                    }
                                                }


                                            }
                                        }
                                    }
                                }
                        );
                    }
                }
        );

        // setting values for suggested_final_position and suggest_trade_allocation
        preTargetAllocationMap.forEach(
                (account, targetModel) -> {
                    for (TargetAllocationModel model : targetModel) {
                        preTargetAllocationMap.forEach(
                                (account2, targetModel2) -> {
                                    if (!account.equals(account2)) {
                                        for (TargetAllocationModel model2 : targetModel2) {
                                            if (model2.getStock().equals(model.getStock())) {

                                                List<TargetAllocationModel> investors = new ArrayList<>();
                                                List<TargetAllocationModel> updatedInv = new ArrayList<>();
                                                investors.add(model);
                                                investors.add(model2);

                                                updatedInv = getSugFinalPos(investors, model.getAllPosition());
                                                for (TargetAllocationModel modelInv : updatedInv)
                                                    if ((modelInv.getAccount().equals(model.getAccount())) &&
                                                            (modelInv.getStock().equals(model.getStock()))) {
                                                        model.setSugFinalPos(modelInv.getSugFinalPos().setScale(0, RoundingMode.HALF_UP));
                                                        model.setSugTradeAlloc(modelInv.getSugFinalPos().
                                                                subtract(new BigDecimal(modelInv.getQtyHeld())).setScale(0, RoundingMode.HALF_UP));
                                                    }

                                            }
                                        }
                                    }
                                }
                        );
                    }
                }
        );

        return preTargetAllocationMap;
    }

    /**
     * @param tarPer  Target Percentage for a given account for a given stock
     * @param capital Capital amount for a given account
     * @return Calculated Target Market Value for the given account and stock
     */
    @Override
    public BigDecimal getTarMktValue(Float tarPer, BigDecimal capital) {
        BigDecimal tarPer1 = BigDecimal.valueOf(tarPer);
        return tarPer1.multiply(capital).multiply(BigDecimal.valueOf(0.01));
    }


    /**
     * @param tarMktValue   calculated target market value for a given account
     * @param curStockPrice current stock price for a given stock
     * @return Calculated Maxixum Share for a given account and stock
     */
    @Override
    public BigDecimal getMaxShares(BigDecimal tarMktValue, BigDecimal curStockPrice) {
        return tarMktValue.divide(curStockPrice);
    }

    /**
     * @param investors   list of calculated allocation table for each account
     * @param newTradeQty a trade quality allocated to be bought/sold for a given account
     * @return calculated all in position for a given account and stock.
     */
    @Override
    public Integer getAllPositions(List<TargetAllocationModel> investors, Integer newTradeQty) {

        int totalPostn = newTradeQty;
        for (TargetAllocationModel model : investors)
            totalPostn = totalPostn + model.getQtyHeld().intValue();
        return totalPostn;

    }

    /**
     * @param investors list of calculated allocation table for each account
     * @param allPos    calculated all in position for a given account and stock
     * @return calculated suggestion final position for a given account and stock
     */

    @Override
    public List<TargetAllocationModel> getSugFinalPos(List<TargetAllocationModel> investors, Integer allPos) {
        BigDecimal sumTarMktValue = null;
        BigDecimal tempTarMktValue = null;
        BigDecimal tmpFinalPos, sugFinalPos;
        List<TargetAllocationModel> investors1 = new ArrayList<>();

        for (TargetAllocationModel model : investors) {
            if (tempTarMktValue == null)
                tempTarMktValue = model.getTarMktValue();
            else
                sumTarMktValue = tempTarMktValue.add(model.getTarMktValue());
        }

        if (sumTarMktValue == null)
            sumTarMktValue = tempTarMktValue;

        for (TargetAllocationModel model : investors) {
            tmpFinalPos = model.getTarMktValue().multiply(BigDecimal.valueOf(model.getAllPosition()));
            if (tmpFinalPos != null && sumTarMktValue != null) {
                sugFinalPos = tmpFinalPos.divide(sumTarMktValue, 2, RoundingMode.HALF_UP);
                model.setSugFinalPos(sugFinalPos.setScale(0, RoundingMode.HALF_UP));
                investors1.add(model);
            }
        }

        return investors1;
    }

    /**
     * @param targetAllocMap list of calculated allocation table for each account
     * @param tradesDataList List of Trades retrieved from Trades csv
     * @return Calculated for Allocation for a given account and the stock
     */
    @Override
    public List<Allocations> setAllocations(Map<String, List<TargetAllocationModel>> targetAllocMap, List<Trades> tradesDataList) {
        final List<Allocations> allocations = new ArrayList<>();

        targetAllocMap.forEach(
                (account, targetModel) -> {
                    for (TargetAllocationModel model : targetModel) {
                        Allocations allocation = new Allocations();
                        allocation.setAccount(model.getAccount());
                        allocation.setStock(model.getStock());
                        if (checkErrorCondition(model, tradesDataList))
                            allocation.setQuantity("+" + model.getSugTradeAlloc().setScale(0, RoundingMode.HALF_UP).toString());
                        else
                            allocation.setQuantity("0");

                        allocations.add(allocation);
                    }
                }
        );
        return allocations;
    }

    /**
     * @param model          Calculated target allocation for a given account and stock
     * @param tradesDataList List of Trades retrieved from Trades csv
     * @return true or false based on error checks condition over the calculated values.
     */
    @Override
    public boolean checkErrorCondition(TargetAllocationModel model, List<Trades> tradesDataList) {

        //ERROR Condition: SUGGESTED_FINAL_POSITION < 0
        if (model.getSugFinalPos().intValue() < 0)
            return false;
        //ERROR Condition: SUGGESTED_FINAL_POSITION > MAX_SHARES
        if (model.getSugFinalPos().compareTo(model.getMaxShares()) > 0)
            return false;

        //ERROR Condition: SUGGESTED_FINAL_POSITION < Currently Held Quantity when trade is a BUY.
        for (Trades tradesData : tradesDataList) {
            if ((model.getStock().equals(tradesData.getStock())) && (tradesData.getType().equals("BUY")))
                if (model.getSugFinalPos().compareTo(model.getStockPrice()) < 0)
                    return false;
        }
        //ERROR Condition: SUGGESTED_FINAL_POSITION > Currently Held Quantity when trade is a SELL.
        for (Trades tradesData : tradesDataList) {
            if ((model.getStock().equals(tradesData.getStock())) && (tradesData.getType().equals("SELL")))
                if (model.getSugFinalPos().compareTo(model.getStockPrice()) > 0)
                    return false;
        }

        return true;

    }

}
