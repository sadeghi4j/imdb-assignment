package com.imdb.imdbtitles.service.dataimport.common;

import java.util.List;

public interface DataWriter {

    Object[] parseLine(String line);

    void batchInsert(List<String> lines);

}
