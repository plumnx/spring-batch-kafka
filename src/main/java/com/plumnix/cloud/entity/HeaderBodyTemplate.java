package com.plumnix.cloud.entity;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class HeaderBodyTemplate {

    private Map<String, String> headers;

    private Map<String, List<String>> bodyNames;

    private int count = 0;

    public HeaderBodyTemplate() {
        this.headers = new LinkedHashMap();
        this.bodyNames = Maps.newHashMap();
    }

    public void addHeaders(String key, String value) {
        this.headers.put(key, value);
    }

    public void addHeaders(Map<String, String> map) {
        this.headers.putAll(map);
    }

    public void addBodyName(String key, List<String> list) {
        this.bodyNames.put(key, list);
    }

    @Data
    public static class HeaderBodyItem {

        private String line;

        private HeaderBodyTemplate headerBodyTemplate;

        public HeaderBodyItem(String line, HeaderBodyTemplate headerBodyTemplate) {
            this.line = line;
            this.headerBodyTemplate = headerBodyTemplate;
        }

        public static boolean checkHeader(String line) {
            if (null != line) {
                Matcher matcher = Pattern.compile("^H,(.*)").matcher(line);
                if (matcher.find()) {
                    return true;
                }
            }
            return false;
        }

        public static Map<String, String> extractHeader(String line) {
            if (checkHeader(line)) {
                Matcher matcher = Pattern.compile("^H,(.*)").matcher(line);
                if (matcher.find()) {
                    Iterable<String> split = Splitter.on("=").trimResults().split(matcher.group(1));
                    Iterator<String> iterator = split.iterator();

                    String headKey = iterator.next();
                    String headValue = iterator.next();
                    Map<String, String> map = Maps.newHashMap();
                    map.put(headKey, headValue);

                    return map;
                }
            }
            return null;
        }

        public HeaderBodyItem header() {
            if (null != this.line) {
                Matcher matcher = Pattern.compile("^H,(.*)").matcher(this.line);
                if (matcher.find()) {
                    Iterable<String> split = Splitter.on("=").trimResults().split(matcher.group(1));
                    Iterator<String> iterator = split.iterator();

                    String headKey = iterator.next();
                    String headValue = iterator.next();
                    this.headerBodyTemplate.addHeaders(headKey, headValue);
                    this.line = null;
                }
            }
            return this;
        }

        public HeaderBodyItem body() {
            if (null != this.line) {
                Matcher matcher = Pattern.compile("^#,(.*)").matcher(this.line);
                if (matcher.find()) {
                    Iterable<String> split = Splitter.on(",").trimResults().split(matcher.group(1));
                    Iterator<String> iterator = split.iterator();

                    String bodyKey = iterator.next();
                    List<String> bodyNames = Lists.newArrayList();
                    while (iterator.hasNext()) {
                        bodyNames.add(iterator.next());
                    }
                    this.headerBodyTemplate.addBodyName(bodyKey, bodyNames);
                    this.line = null;
                }
            }
            return this;
        }

        public HeaderBodyItem count() {
            if (null != this.line) {
                Matcher matcher = Pattern.compile("^T,(.*)").matcher(this.line);
                if (matcher.find()) {
                    Iterable<String> split = Splitter.on("=").trimResults().split(matcher.group(1));
                    Iterator<String> iterator = split.iterator();
                    iterator.next();
                    String value = iterator.next();
                    this.headerBodyTemplate.setCount(Integer.valueOf(value));
                    this.line = null;
                }
            }
            return this;
        }

    }

}
