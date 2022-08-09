package com.imdb.imdbtitles.service.batch;

import com.imdb.imdbtitles.entity.Person;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class NameDatasetBatchInsertService {

    final JdbcTemplate jdbcTemplate;

    private ExecutorService executor = null;//Executors.newFixedThreadPool(4);

    @SneakyThrows
    public void batchInsert(DefaultBatchPreparedStatementSetter batchPreparedStatementSetter) {
        StopWatch timer = new StopWatch();
        List<String> lines = batchPreparedStatementSetter.getLines();
//        String sql = "INSERT INTO `Person` (id, primary_name, birth_year , death_year) VALUES(?,?,?,?)";
        int  sizePerThread = lines.size() / 4;
        final AtomicInteger sublists = new AtomicInteger();
        timer.start();
        lines.parallelStream()
                .collect(Collectors.groupingBy(t -> sublists.getAndIncrement() / sizePerThread))
                .values()
                .stream()
                .forEach(ul -> runBatchInsert(batchPreparedStatementSetter));
        timer.stop();
        log.info("Batch size {} finished, duration: {}", lines.size(), timer.getTotalTimeMillis());
//        log.info("batchInsertAsync -> Total time in seconds: " + timer.getTotalTimeSeconds());
    }

    @SneakyThrows
    public void parallelBatchInsert(DefaultBatchPreparedStatementSetter batchPreparedStatementSetter) {
        StopWatch timer = new StopWatch();
        List<String> lines = batchPreparedStatementSetter.getLines();
//        String sql = "INSERT INTO `Person` (id, primary_name, birth_year , death_year) VALUES(?,?,?,?)";
        int  sizePerThread = lines.size() / 4;
        final AtomicInteger sublists = new AtomicInteger();
        CompletableFuture[] futures = lines.parallelStream()
                .collect(Collectors.groupingBy(t -> sublists.getAndIncrement() / sizePerThread))
                .values()
                .stream()
//                .map(ul -> runBatchInsertByThread(ul, sql))
                .map(ul -> runBatchInsertByThread(batchPreparedStatementSetter))
                .toArray(CompletableFuture[]::new);
        CompletableFuture<Void> run = CompletableFuture.allOf(futures);
        timer.start();
        run.get();
        timer.stop();
        log.info("Batch size {} finished, duration: {}", lines.size(), timer.getTotalTimeMillis());
//        log.info("batchInsertAsync -> Total time in seconds: " + timer.getTotalTimeSeconds());
    }

    private int[] runBatchInsert(DefaultBatchPreparedStatementSetter batchPreparedStatementSetter) {
        return jdbcTemplate.batchUpdate(batchPreparedStatementSetter.getSql(), batchPreparedStatementSetter);
    }

    private CompletableFuture<Void> runBatchInsertByThread(DefaultBatchPreparedStatementSetter batchPreparedStatementSetter) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.batchUpdate(batchPreparedStatementSetter.getSql(), batchPreparedStatementSetter);
        }, executor);

    }
/*    private CompletableFuture<Void> runBatchInsertByThread(List<String> users, String sql) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.batchUpdate(sql, new PersonBatchPreparedStatementSetter(users));
        }, executor);
    }*/

    /*class PersonBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

        private List<String> persons;

        public PersonBatchPreparedStatementSetter(List<String> persons) {
            this.persons = persons;
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            String[] fields = persons.get(i).split("\t");
            ps.setString(1, fields[0]);
            ps.setString(2, fields[1]);
            ps.setObject(3, getValue(fields[2]));
            ps.setObject(4, getValue(fields[3]));
        }

        @Override
        public int getBatchSize() {
            return persons.size();
        }

        private String getStringValue(String field) {
            return field.equals("\\N") ? null : field;
        }

        private Integer getValue(String field) {
            return field.equals("\\N") ? null : Integer.parseInt(field);
        }
    }*/
}
