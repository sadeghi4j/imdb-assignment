package com.imdb.imdbtitles.service.dataimport.common;

import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Setter
public class DefaultFileReader implements DataReader {

    private final String filePath;
    private ReadEvent readEvent;

    private Integer ROWS_TO_READ_COUNT = Integer.MAX_VALUE;//1_00_000;
    private Integer fetchSize = 10_000;
    private static final int skipRecords = 0;

    public DefaultFileReader(String filePath) {
        this.filePath = filePath;
    }

    public void setReadEventListener(ReadEvent readEvent) {
        this.readEvent = readEvent;
    }

    @Override
    public void read() throws IOException {
        List<String> listOfLines = new ArrayList<>(fetchSize);
        int readLines = 0;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
//            int i = 0;
            // just to skip the first line because it contains the column names
            String line = reader.readLine();
            while ((line = reader.readLine()) != null && readLines < ROWS_TO_READ_COUNT) {
                readLines++;
                if (readLines > skipRecords) {
                    listOfLines.add(line);
                    if (listOfLines.size() % fetchSize == 0) {
                        readEvent.notify(listOfLines);
                        listOfLines.clear();
                    }
                }
//                i++;
            }
            readEvent.notify(listOfLines);
        }
    }

}
