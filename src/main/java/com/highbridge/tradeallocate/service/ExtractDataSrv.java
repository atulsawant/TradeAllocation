package com.highbridge.tradeallocate.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.highbridge.tradeallocate.model.Model;
import com.highbridge.tradeallocate.model.TargetAllocationModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExtractDataSrv implements ExtractData {
    @Override
    public <T extends Model> List<T> readCsv(Class<? extends Model> type, String fileName) {
        try {
            CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
            CsvMapper mapper = new CsvMapper();
            File file = new ClassPathResource(fileName).getFile();
            MappingIterator<T> readValues =
                    mapper.readerFor(type).with(bootstrapSchema).readValues(file);
            return readValues.readAll();
        } catch (IOException e) {
            log.error("Error occurred while loading object list from file " + fileName, e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Unknown exception occurred" + fileName, e);
            throw new RuntimeException("Unknown exception occurred" + e);
        }
    }

    @Override
    public void writeCsv(Map<String, List<TargetAllocationModel>> targetAllocationMap){
        List<TargetAllocationModel> finalTargetAllocList = new ArrayList<>();

        targetAllocationMap.forEach(
                (account, targetAllocationModel) -> {
                    for (TargetAllocationModel model : targetAllocationModel) {
                        finalTargetAllocList.add(model);
                        log.info("final list for targetAllocation is" + model.toString());
                    }
                }
        );

        // create mapper and schema
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(TargetAllocationModel.class);
        schema = schema.withColumnSeparator('\t');

        // output writer
        try {
            ObjectWriter myObjectWriter = mapper.writer(schema);
            File tempDir = new File("tmp/TargetAllocation");
            tempDir.mkdirs();
            File tempFile = new File(tempDir, "TargetAllocation.csv");
            FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(tempFileOutputStream, 1024);
            OutputStreamWriter writerOutputStream = new OutputStreamWriter(bufferedOutputStream, "UTF-8");
            myObjectWriter.writeValue(writerOutputStream, finalTargetAllocList);
        }
            catch (IOException e) {
            e.printStackTrace();
        }

    }
}
