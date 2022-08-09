package com.imdb.imdbtitles.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PersonPreparedStatementSetter extends DefaultBatchPreparedStatementSetter {

    private String sql = "INSERT INTO `Person` (id, primary_name, birth_year , death_year) VALUES(?,?,?,?)";

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        String[] fields = lines.get(i).split("\t");
        ps.setObject(1, fields[0]);
        ps.setObject(2, fields[1]);
        ps.setObject(3, getValue(fields[2]));
        ps.setObject(4, getValue(fields[3]));
    }

}
