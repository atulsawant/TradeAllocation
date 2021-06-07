package com.highbridge.tradeallocate.service;

import com.highbridge.tradeallocate.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service
public class TargetAllocationImpl implements TargetAllocation {

    @Override
    public Map<String, List<TargetAllocationModel>> setTarAlloc(List<Capital> capData, List<Holdings> holdData,
                                                                List<Targets> targetData) {

        Map<String, List<TargetAllocationModel>> tarAllocMap = new HashMap<>();
        TargetAllocationModel targetAllocation;

        String cpAccount, hAccount, tAccount, hStock, tStock;
        BigDecimal cpCap, hStockPrice;
        BigInteger hQty;
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

    @Override
    public Map<String, List<TargetAllocationModel>> getTarAlloc(Map<String, List<TargetAllocationModel>> preTargetAllocationMap, List<Trades> tradesData) {
        Map<String, List<TargetAllocationModel>> targetAllocMap = preTargetAllocationMap;
        Map<String, List<TargetAllocationModel>> targetAllocMap2 = preTargetAllocationMap;
        //set Target market Value and Max Share
        targetAllocMap.forEach(
                (account, targetModel) -> {
                    for (TargetAllocationModel model : targetModel) {
                        model.setTarMktValue(getTarMktValue(model.getTarget(), model.getCapital()));
                        model.setMaxShares(getMaxShares(model.getTarMktValue(), model.getStockPrice()));
                    }
                }
        );

        // set all_in_position
        targetAllocMap.forEach(
                (account, targetModel) -> {
                    for (TargetAllocationModel model : targetModel) {
                        targetAllocMap2.forEach(
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
        targetAllocMap.forEach(
                (account, targetModel) -> {
                    for (TargetAllocationModel model : targetModel) {
                        targetAllocMap2.forEach(
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
                                                        model.setSugFinalPos(modelInv.getSugFinalPos());
                                                        model.setSugTradeAlloc(modelInv.getSugFinalPos().
                                                                subtract(new BigDecimal(modelInv.getQtyHeld())));
                                                    }

                                            }
                                        }
                                    }
                                }
                        );
                    }
                }
        );

        return targetAllocMap;
    }

    @Override
    public BigDecimal getTarMktValue(Float tarPer, BigDecimal capital) {
        BigDecimal tarPer1 = BigDecimal.valueOf(tarPer);
        BigDecimal capital1 = capital;

        return tarPer1.multiply(capital1).multiply(BigDecimal.valueOf(0.01));
    }

    @Override
    public BigDecimal getMaxShares(BigDecimal tarMktValue, BigDecimal curStockPrice) {
        BigDecimal tarMktValue1 = tarMktValue;
        BigDecimal curStockPrice1 = curStockPrice;

        return tarMktValue1.divide(curStockPrice1);
    }

    @Override
    public Integer getAllPositions(List<TargetAllocationModel> investors, BigInteger newTradeQty) {

        Integer totalPostn = newTradeQty.intValue();


        for (TargetAllocationModel model : investors)
            totalPostn = totalPostn + model.getQtyHeld().intValue();

        return totalPostn;

    }

    @Override
    public List<TargetAllocationModel> getSugFinalPos(List<TargetAllocationModel> investors, Integer allPos) {
        BigDecimal sumTarMktValue = null;
        BigDecimal tempTarMktValue = null;
        BigDecimal tmpFinalPos = null, sugFinalPos = null;
        List<TargetAllocationModel> investors1 = new ArrayList<>();

        for (TargetAllocationModel model : investors) {
            if (tempTarMktValue == null)
                tempTarMktValue = model.getTarMktValue();
            else
                sumTarMktValue = tempTarMktValue.add(model.getTarMktValue());
        }


        for (TargetAllocationModel model : investors) {
            tmpFinalPos = model.getTarMktValue().multiply(BigDecimal.valueOf(model.getAllPosition()));
            sugFinalPos = tmpFinalPos.divide(sumTarMktValue, 2, RoundingMode.HALF_UP);
            model.setSugFinalPos(sugFinalPos);
            investors1.add(model);
        }

        return investors1;
    }


}
