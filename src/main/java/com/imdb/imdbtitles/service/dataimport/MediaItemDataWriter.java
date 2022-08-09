package com.imdb.imdbtitles.service.dataimport;

import com.imdb.imdbtitles.service.dataimport.common.DefaultBatchInsert;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.imdb.imdbtitles.util.ParseUtil.getBoolean;
import static com.imdb.imdbtitles.util.ParseUtil.getInteger;

@Setter
@Log4j2
public class MediaItemDataWriter extends DefaultBatchInsert {

    private static final String SQL =
            "INSERT INTO `Media_Item` (id, title_type, primary_title , is_adult, start_year, end_year, runtime_minutes) VALUES(?,?,?,?,?,?,?)";
//    original_title,
    public MediaItemDataWriter(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, SQL);
//        this.genreRepository = genreRepository;
    }

    @Override
    public Object[] parseLine(String line) {
        String[] fields = line.split("\t");
        Object[] values = new Object[]{fields[0], fields[1], fields[2], /*fields[3],*/ getBoolean(fields[4]),
                getInteger(fields[5]), getInteger(fields[6]), getInteger(fields[7])};
        return values;
    }

}
