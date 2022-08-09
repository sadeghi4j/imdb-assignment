package com.imdb.imdbtitles.service.dataimport.common;

import lombok.SneakyThrows;

public class DataImporter {

    private final DataReader dataReader;
    private final DataWriter dataWriter;

    public DataImporter(DataReader dataReader, DataWriter dataWriter) {
        this.dataReader = dataReader;
        this.dataWriter = dataWriter;
        dataReader.setReadEventListener(dataWriter::batchInsert);
    }

    @SneakyThrows
    public void start() {
        dataReader.read();
    }

}
