package com.plumnix.cloud;

import com.google.common.base.Splitter;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileTest {

    @Test
    public void test_1() throws IOException {
        List<String> lines = Files.readAllLines(
                Paths.get(
                        new ClassPathResource("/test.csv").getURI()),
                StandardCharsets.UTF_8);
        lines.forEach(System.out::println);
    }

    @Test
    public void test_2() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(new ClassPathResource("/test.csv").getURI()));

        lines.forEach(line -> {
            Matcher matcher = Pattern.compile("^H,(.*)").matcher(line);
            if(matcher.find()) {
                System.out.println("H:");
                Iterable<String> split = Splitter.on("=").trimResults().split(matcher.group(1));
                split.forEach(System.out::println);
            }
            matcher = Pattern.compile("^#,(.*)").matcher(line);
            if(matcher.find()) {
                System.out.println("#:");
                Iterable<String> split = Splitter.on(",").trimResults().split(matcher.group(1));
                split.forEach(System.out::println);
            }
            matcher = Pattern.compile("^D,(.*)").matcher(line);
            if(matcher.find()) {
                System.out.println("D:");
                Iterable<String> split = Splitter.on(",").trimResults().split(matcher.group(1));
                split.forEach(System.out::println);
            }
            matcher = Pattern.compile("^T,(.*)").matcher(line);
            if(matcher.find()) {
                System.out.println("T:");
                Iterable<String> split = Splitter.on("=").trimResults().split(matcher.group(1));
                split.forEach(System.out::println);
            }
        });
    }

}
