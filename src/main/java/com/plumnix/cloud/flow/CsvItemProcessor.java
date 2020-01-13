package com.plumnix.cloud.flow;

import com.google.common.base.Splitter;
import com.plumnix.cloud.entity.HeaderBodyTemplate;
import lombok.Data;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.Nullable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvItemProcessor implements ChunkListener, ItemProcessor<HeaderBodyTemplate.HeaderBodyItem, CsvItemProcessor.HeaderBody> {

    private ChunkContext chunkContext;

    @Nullable
    @Override
    public HeaderBody process(HeaderBodyTemplate.HeaderBodyItem item) throws Exception {
//        System.out.println(Thread.currentThread().getId() + " " + Thread.currentThread().getName());
        if(item.getLine() == null) {
            return null;
        }
        HeaderBody headerBody = new HeaderBody().accept(item);

        Matcher matcher = Pattern.compile("^D,(.*)").matcher(item.getLine());
        if(matcher.find()) {
            Iterable<String> split = Splitter.on(",").trimResults().split(matcher.group(1));
            Iterator<String> iterator = split.iterator();

            String headKey = iterator.next();
            List<String> headers = item.getHeaderBodyTemplate().getBodyNames().get(headKey);
            for(int i = 0; i < headers.size(); i++) {
                headerBody.addBody(headers.get(i), iterator.next());
            }
        }
//        System.out.println(Thread.currentThread().getId() + " " + Thread.currentThread().getName() + " ItemProcessor: " + headerBody.toString());
        return headerBody;
    }

    @Override
    public void beforeChunk(ChunkContext context) {
        this.chunkContext = context;
    }

    @Override
    public void afterChunk(ChunkContext context) {

    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }

    @Data
    public static class HeaderBody {

        private Map<String, String> headers;

        private Map<String, String> body;

        public HeaderBody() {
            headers = new LinkedHashMap();
            body = new LinkedHashMap();
        }

        public void addBody(String key, String value) {
            body.put(key, value);
        }

        public HeaderBody accept(HeaderBodyTemplate.HeaderBodyItem item) {
            this.headers.putAll(item.getHeaderBodyTemplate().getHeaders());
            return this;
        }

    }

}
