package com.highbridge.tradeallocate.service;

import com.highbridge.tradeallocate.model.Model;
import com.highbridge.tradeallocate.model.TargetAllocationModel;

import java.util.List;
import java.util.Map;

public interface ExtractData {
    public <T extends Model> List<T> readCsv(Class<? extends Model> type, String fileName);
    public void writeCsv(Map<String, List<TargetAllocationModel>> targetAllocationMap);
}
