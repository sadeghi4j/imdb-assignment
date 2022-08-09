package com.imdb.imdbtitles.service.dataimport;

import com.imdb.imdbtitles.service.dataimport.common.DefaultBatchInsert;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.imdb.imdbtitles.util.ParseUtil.getInteger;

@Setter
@Log4j2
public class PersonDataWriter extends DefaultBatchInsert {

    private static final String SQL = "INSERT INTO `Person` (id, primary_name, birth_year , death_year) VALUES(?,?,?,?)";

    public PersonDataWriter(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, SQL);
    }

    @Override
    public Object[] parseLine(String line) {
        String[] fields = line.split("\t");
        Object[] values = new Object[]{fields[0], fields[1], getInteger(fields[2]), getInteger(fields[3])};
        return values;
    }

}
