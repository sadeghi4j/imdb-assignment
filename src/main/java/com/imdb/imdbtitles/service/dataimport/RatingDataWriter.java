package com.imdb.imdbtitles.service.dataimport;

import com.imdb.imdbtitles.service.dataimport.common.DefaultBatchInsert;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.imdb.imdbtitles.util.ParseUtil.getInteger;

@Setter
@Log4j2
public class RatingDataWriter extends DefaultBatchInsert {

    private static final String SQL = "UPDATE `Media_Item` SET average_rating = ? , num_votes = ? where id = ?";

    public RatingDataWriter(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, SQL);
    }

    @Override
    public Object[] parseLine(String line) {
        String[] fields = line.split("\t");
        Object[] values = new Object[]{getFloat(fields[1]), getInteger(fields[2]), fields[0]};
        return values;
    }

    public static float getFloat(String field) {
        return field.equals("\\N") ? null : Float.parseFloat(field);
    }

    public static void main(String[] args) {
        String num = "5.7";
        System.out.println("Float.parseFloat(num) = " + Float.parseFloat(num));
    }
}
