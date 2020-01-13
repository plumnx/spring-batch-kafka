package com.plumnix.cloud;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.common.value.qual.IntRange;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class FileTest {

    @Test
    public void test_1() throws IOException {
        List<String> lines = Files.readAllLines(
                Paths.get(
                        new ClassPathResource("/test.csv").getURI()),
                StandardCharsets.UTF_8);
        lines.forEach(log::info);
    }

    @Test
    public void test_2() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(new ClassPathResource("/test.csv").getURI()));

        lines.forEach(line -> {
            Matcher matcher = Pattern.compile("^H,(.*)").matcher(line);
            if (matcher.find()) {
                log.info("H:");
                Iterable<String> split = Splitter.on("=").trimResults().split(matcher.group(1));
                split.forEach(System.out::println);
            }
            matcher = Pattern.compile("^#,(.*)").matcher(line);
            if (matcher.find()) {
                log.info("#:");
                Iterable<String> split = Splitter.on(",").trimResults().split(matcher.group(1));
                split.forEach(System.out::println);
            }
            matcher = Pattern.compile("^D,(.*)").matcher(line);
            if (matcher.find()) {
                log.info("D:");
                Iterable<String> split = Splitter.on(",").trimResults().split(matcher.group(1));
                split.forEach(System.out::println);
            }
            matcher = Pattern.compile("^T,(.*)").matcher(line);
            if (matcher.find()) {
                log.info("T:");
                Iterable<String> split = Splitter.on("=").trimResults().split(matcher.group(1));
                split.forEach(System.out::println);
            }
        });
    }

    @Test
    public void test_build_millions_csv() throws IOException {
        final int DATA_SIZE = 1_000_000;
//         D,0,1,2,3,4,5
        String lines = IntStream.range(0, DATA_SIZE).boxed().map(num -> {
            StringBuilder lineBuilder = new StringBuilder(num + "D,");
            int randomCsvType = new Random().nextInt(2);
            lineBuilder.append(randomCsvType).append(",");
            lineBuilder.append(IntStream.range(0, CSV_TYPE_MAP.get(randomCsvType)).boxed().
                    map(value -> String.valueOf(new Random().nextInt(100))).collect(Collectors.joining(",")));
            return lineBuilder.toString();
        }).collect(Collectors.joining("\r\n"));
//        log.info(String.format(CSV_CONTENT, lines + "\r\n"));

        File folder = new File(this.getClass().getResource("/").getFile().replaceAll("target/test-classes", "src/main/resources"));
        File file = new File(folder, "test_" + String.valueOf(new Random().nextInt(1000)) + ".csv");
        Files.write(file.toPath(), String.format(CSV_CONTENT, lines + "\r\n", String.valueOf(DATA_SIZE)).getBytes());
    }

    @Test
    public void test_buffer_reader_read_by_line() throws IOException {
        String beforeMemory = logMemory();
        Instant beforeNow = Instant.now();
        StringBuilder sb = new StringBuilder();

        File file = new ClassPathResource("test_458.csv").getFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            String thisLine = null;
            while ((thisLine = reader.readLine()) != null) {
                sb.append(thisLine);
//                log.info(thisLine);
            }
        }
        log.info("============= job duration: " + Duration.between(beforeNow, Instant.now()).getNano());
        log.info(beforeMemory);
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        log.info(logMemory());
        logMemory();
    }

    @Test
    public void test_random_access_file_read_by_line_first() throws IOException {
        String beforeMemory = logMemory();
        Instant beforeNow = Instant.now();
        StringBuilder sb = new StringBuilder();

        File file = new ClassPathResource("test_458.csv").getFile();

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(0);

            String thisLine = null;
            while ((thisLine = randomAccessFile.readLine()) != null) {
//                sb.append(thisLine);
                log.info(thisLine);
            }
        }
        log.info("============= job duration: " + Duration.between(beforeNow, Instant.now()).getNano());
        log.info(beforeMemory);
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        log.info(logMemory());
        logMemory();
    }


    @Test
    public void test_random_access_file_read_by_line_second() throws IOException {


        String beforeMemory = logMemory();
        Instant beforeNow = Instant.now();
        StringBuilder sb = new StringBuilder();

        File file = new ClassPathResource("test.csv").getFile();

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(17_000_000);

            try (FileInputStream fileInputStream = new FileInputStream(randomAccessFile.getFD());
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));) {
                String thisLine = null;
                while ((thisLine = bufferedReader.readLine()) != null) {
                    // sb.append(thisLine);
                    log.info(thisLine);
                }
            }
        }
        log.info("============= job duration: " + Duration.between(beforeNow, Instant.now()).getNano());
        log.info(beforeMemory);
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        log.info(logMemory());
        logMemory();
    }

    @Test
    public void test_random_access_file_read_by_line_third() throws IOException {
        String beforeMemory = logMemory();
        Instant beforeNow = Instant.now();
        StringBuilder sb = new StringBuilder();

        File file = new ClassPathResource("test.csv").getFile();
        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));) {
            lineNumberReader.setLineNumber(5);

            String thisLine = null;
            while ((thisLine = lineNumberReader.readLine()) != null) {
                // sb.append(thisLine);
                log.info(thisLine);
            }
        }

        log.info("============= job duration: " + Duration.between(beforeNow, Instant.now()).getNano());
        log.info(beforeMemory);
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        log.info(logMemory());
        logMemory();
    }

    @Test
    public void test_random_access_file_read_by_line_five() throws IOException {
        int lineNo = 0;
        Long length = 0L;
        Map<Integer, Long> lineMap = Maps.newHashMap();
        File file = new ClassPathResource("test.csv").getFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            String thisLine = null;
            while ((thisLine = reader.readLine()) != null) {
                lineNo++;
                length += thisLine.length();
                lineMap.put(lineNo, length);
                log.info(thisLine);
            }
        }

        lineMap.keySet().stream().forEach(value -> {
            System.out.println("line no: " + value + ", length: " + lineMap.get(value));
        });
    }

    @Test
    public void test_random_access_file_read_by_line_six() throws IOException {
        String beforeMemory = logMemory();
        Instant beforeNow = Instant.now();
        StringBuilder sb = new StringBuilder();

        File file = new ClassPathResource("test.csv").getFile();

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(113);

            String thisLine = null;
            while ((thisLine = randomAccessFile.readLine()) != null) {
//                sb.append(thisLine);
                log.info(thisLine);
            }
        }
        log.info("============= job duration: " + Duration.between(beforeNow, Instant.now()).getNano());
        log.info(beforeMemory);
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        log.info(logMemory());
        logMemory();
    }

    @Test
    public void test_random_access_file_read_by_line_six_second() throws IOException {
        String beforeMemory = logMemory();
        Instant beforeNow = Instant.now();
        StringBuilder sb = new StringBuilder();

        File file = new ClassPathResource("test.csv").getFile();

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(113);

            try (FileInputStream fileInputStream = new FileInputStream(randomAccessFile.getFD());
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));) {
                String thisLine = null;
                while ((thisLine = bufferedReader.readLine()) != null) {
                    // sb.append(thisLine);
                    log.info(thisLine);
                }
            }
        }
        log.info("============= job duration: " + Duration.between(beforeNow, Instant.now()).getNano());
        log.info(beforeMemory);
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        log.info(logMemory());
        logMemory();

//        Files.write(Paths.get("C://data//new_test.csv"), sb.toString().getBytes());
    }

    /**
     * ===============================================================================================================
     * ===============================================================================================================
     * ===============================================================================================================
     */

    @Test
    public void test_final_one() throws IOException {
        int lineNo = 0;
        Long length = 0L;
        Map<Integer, Long> lineMap = new LinkedHashMap<>();
//        File file = new ClassPathResource("test_460.csv").getFile();
        File file = new ClassPathResource("test_458.csv").getFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            String thisLine = null;
            while ((thisLine = reader.readLine()) != null) {
                lineNo++;
                length += thisLine.length();

//                if (lineNo % 10 == 0) {
                if (lineNo % 100_000 == 0) {
                    lineMap.put(lineNo, length);
                    log.info(thisLine);
                }
            }
        }

        lineMap.keySet().stream().forEach(value -> {
            System.out.println("line no: " + value + ", length: " + lineMap.get(value));
        });
    }

    /**
     * line no: 10, length: 166 + 10 * 2
     * line no: 20, length: 344 + 20 * 2
     * line no: 30, length: 519 + 30 * 2
     * line no: 40, length: 698 + 40 * 2
     * line no: 50, length: 870 + 50 * 2
     * line no: 60, length: 1038 + 60 * 2
     * line no: 70, length: 1219 + 70 * 2
     * line no: 80, length: 1400 + 80 * 2
     * line no: 90, length: 1581 + 90 * 2
     * line no: 100, length: 1758 + 100 * 2
     *
     * @throws IOException
     */
    @Test
    public void test_final_two() throws IOException {
        String beforeMemory = logMemory();
        Instant beforeNow = Instant.now();
        StringBuilder sb = new StringBuilder();

        File file = new ClassPathResource("test_460.csv").getFile();

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(1581 + 90 * 2);

            try (FileInputStream fileInputStream = new FileInputStream(randomAccessFile.getFD());
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));) {
                String thisLine = null;
                while ((thisLine = bufferedReader.readLine()) != null) {
                    // sb.append(thisLine);
                    log.info(thisLine);
                }
            }
        }
        Instant now = Instant.now();
        log.info("before time:" + beforeNow);
        log.info("now time:" + now);

        log.info(beforeMemory);
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        log.info(logMemory());
        logMemory();

//        Files.write(Paths.get("C://data//new_test.csv"), sb.toString().getBytes());
    }

    /**
     * line no: 100000, length: 2047902 + 100000 * 2
     * line no: 200000, length: 4208058 + 300000 * 2
     * line no: 300000, length: 6368357 + 400000 * 2
     * line no: 400000, length: 8528949 + 500000 * 2
     * line no: 500000, length: 10687518 + 500000 * 2
     * line no: 600000, length: 12847181 + 600000 * 2
     * line no: 700000, length: 15007402 + 700000 * 2
     * line no: 800000, length: 17166865 + 800000 * 2
     * line no: 900000, length: 19326754 + 900000 * 2
     * line no: 1000000, length: 21487835 + 1000000 * 2
     *
     * @throws IOException
     */
    @Test
    public void test_final_three() throws IOException {
        String beforeMemory = logMemory();
        Instant beforeNow = Instant.now();
        StringBuilder sb = new StringBuilder();

        String lineSeparator = System.getProperty("line.separator", "\n");
        File file = new ClassPathResource("test_458.csv").getFile();

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(19326754 + 900_000 * lineSeparator.length());

            try (FileInputStream fileInputStream = new FileInputStream(randomAccessFile.getFD());
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));) {
                String thisLine = null;
                while ((thisLine = bufferedReader.readLine()) != null) {
                    sb.append(thisLine + lineSeparator);
                    // log.info(thisLine);
                }
            }
        }

        Instant now = Instant.now();
        log.info("before time:" + beforeNow);
        log.info("now time:" + now);

        log.info(beforeMemory);
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        log.info(logMemory());
        logMemory();

        Files.write(Paths.get("C://data//new_test.csv"), sb.toString().getBytes());
    }

    @Test
    public void test_final_four() throws IOException {
        int lineNo = 0;
        String beforeMemory = logMemory();
        Instant beforeNow = Instant.now();
        StringBuilder sb = new StringBuilder();

        String lineSeparator = System.getProperty("line.separator", "\n");
        File file = new ClassPathResource("test_458.csv").getFile();
        try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            String thisLine = null;
            while ((thisLine = reader.readLine()) != null) {
                if (++lineNo > 900000) {
                    sb.append(thisLine + lineSeparator);
                    // log.info(thisLine);
                }
            }
        }

        Instant now = Instant.now();
        log.info("before time:" + beforeNow);
        log.info("now time:" + now);

        log.info(beforeMemory);
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        System.out.println("--------------------------------------");
        log.info(logMemory());
        logMemory();

        Files.write(Paths.get("C://data//new_test_1.csv"), sb.toString().getBytes());
    }

    private static final String logMemory() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Max Memory: %s MB", Runtime.getRuntime().maxMemory() / 1048576) + "/r/n");
        sb.append(String.format("Total Memory: %s MB", Runtime.getRuntime().totalMemory() / 1048576) + "/r/n");
        sb.append(String.format("Free Memory: %s MB", Runtime.getRuntime().freeMemory() / 1048576) + "/r/n");

        return sb.toString();
    }

    private static final String CSV_CONTENT = "H,data=20191210\n" +
            "#,0,one,two,three,four,five\n" +
            "#,1,six,seven,eight\n" +
            "%s" +
            "T,count=%s\n";

    private static final Map<Integer, Integer> CSV_TYPE_MAP;

    static {
        CSV_TYPE_MAP = Maps.newHashMap();
        CSV_TYPE_MAP.put(0, 5);
        CSV_TYPE_MAP.put(1, 3);
    }
}
