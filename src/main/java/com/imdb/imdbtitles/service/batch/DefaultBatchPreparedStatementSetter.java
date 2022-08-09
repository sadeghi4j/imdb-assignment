package com.imdb.imdbtitles.service.batch;

import lombok.Data;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.util.List;

@Data
public abstract class DefaultBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

    protected List<String> lines;
    private String sql;
//    private String sql = "INSERT INTO `Person` (id, primary_name, birth_year , death_year) VALUES(?,?,?,?)";

    /*private DefaultBatchPreparedStatementSetter() {

    }

    public DefaultBatchPreparedStatementSetter(List<String> lines, String sql) {
        this.lines = lines;
        this.sql = sql;
    }*/

    public abstract String getSql();

    @Override
    public int getBatchSize() {
        return lines.size();
    }

    protected Integer getValue(String field) {
        return field.equals("\\N") ? null : Integer.parseInt(field);
    }
}
