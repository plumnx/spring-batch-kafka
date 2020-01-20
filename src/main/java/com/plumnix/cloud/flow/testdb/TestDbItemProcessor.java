package com.plumnix.cloud.flow.testdb;

import org.springframework.batch.item.ItemProcessor;

import java.util.HashMap;
import java.util.Map;

public class TestDbItemProcessor implements ItemProcessor<String, Map> {

    /**
     * 1D,0,6,64,18,88,52
     *
     * @param line
     * @return
     * @throws Exception
     */
    @Override
    public Map process(String line) throws Exception {
        String[] lineArray = line.split(",", -1);

        Map<String, String> map = new HashMap<>(7);
        map.put("column1", lineArray[0]);
        map.put("column2", lineArray[1]);
        map.put("column3", lineArray[2]);
        map.put("column4", lineArray[3]);
        map.put("column5", lineArray[4]);
        map.put("column6", lineArray[5]);
        map.put("column7", lineArray[6]);
        return map;
    }

}
