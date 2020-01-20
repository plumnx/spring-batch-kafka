package com.plumnix.cloud.flow.testdb.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class Output {

    private List<? extends Map> items;

    private int size;

}