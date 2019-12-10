package com.plumnix.cloud.flow;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvItemReader implements ItemReader<CsvItemReader.HeaderBodyTemplate.HeaderBodyItem> {

    private Iterator<String> iterator;

    private HeaderBodyTemplate headerBodyTemplate;

    public CsvItemReader() {
        this.headerBodyTemplate = new HeaderBodyTemplate();
    }

    @Nullable
    @Override
    public HeaderBodyTemplate.HeaderBodyItem read() throws Exception {
        if(null == iterator) {
            List<String> lines = null;
            try {
                lines = Files.readAllLines(Paths.get(
                        new ClassPathResource("/test.csv").getURI()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.iterator = lines.iterator();
        }
        if(this.iterator.hasNext()) {
            String line = this.iterator.next();
            return new HeaderBodyTemplate.HeaderBodyItem(line, headerBodyTemplate).header().body().count();
        }
        return null;
    }

    @Data
    public static class HeaderBodyTemplate {

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

            public HeaderBodyItem header() {
                if(null != this.line) {
                    Matcher matcher = Pattern.compile("^H,(.*)").matcher(this.line);
                    if(matcher.find()) {
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
                if(null != this.line) {
                    Matcher matcher = Pattern.compile("^#,(.*)").matcher(this.line);
                    if(matcher.find()) {
                        Iterable<String> split = Splitter.on(",").trimResults().split(matcher.group(1));
                        Iterator<String> iterator = split.iterator();

                        String bodyKey = iterator.next();
                        List<String> bodyNames = Lists.newArrayList();
                        while(iterator.hasNext()) {
                            bodyNames.add(iterator.next());
                        }
                        this.headerBodyTemplate.addBodyName(bodyKey, bodyNames);
                        this.line = null;
                    }
                }
                return this;
            }

            public HeaderBodyItem count() {
                if(null != this.line) {
                    Matcher matcher = Pattern.compile("^T,(.*)").matcher(this.line);
                    if(matcher.find()) {
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

}
