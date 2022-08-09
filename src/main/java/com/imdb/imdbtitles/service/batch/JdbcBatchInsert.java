package com.imdb.imdbtitles.service.batch;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
public class JdbcBatchInsert {

    final JdbcTemplate jdbcTemplate;
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    Connection connection;

    @SneakyThrows
//    @PostConstruct
    public void init() {
        DataSource ds = jdbcTemplate.getDataSource();
        connection = ds.getConnection();
        connection.setAutoCommit(false);
    }

    public void batchInsert(List<String> lines) {
        StopWatch timer = new StopWatch();
        String sql = "INSERT INTO `Person` (id, primary_name, birth_year , death_year) VALUES(?,?,?,?)";

        timer.start();

        runBatch(lines, sql);

        timer.stop();
        log.info("Batch size {} finished, duration: {}", lines.size(), timer.getTotalTimeMillis());

    }

    @SneakyThrows
    public void parallelBatchInsert(List<String> lines) {
        StopWatch timer = new StopWatch();
        String sql = "INSERT INTO `Person` (id, primary_name, birth_year , death_year) VALUES(?,?,?,?)";
        int sizePerThread = lines.size() / 4;
        final AtomicInteger sublists = new AtomicInteger();
        CompletableFuture[] futures = lines.parallelStream()
                .collect(Collectors.groupingBy(t -> sublists.getAndIncrement() / sizePerThread))
                .values()
                .stream()
                .map(ul -> runBatchInsert(ul, sql))
                .toArray(CompletableFuture[]::new);
        CompletableFuture<Void> run = CompletableFuture.allOf(futures);
        timer.start();
        run.get();
        timer.stop();
        log.info("Batch size {} finished, duration: {}", lines.size(), timer.getTotalTimeMillis());
    }

    private CompletableFuture<Void> runBatchInsert(List<String> lines, String sql) {
        return CompletableFuture.runAsync(() -> {
            runBatch(lines, sql);
        }, executor);
    }

    private void runBatch(List<String> lines, String sql) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (String line : lines) {
                String[] fields = line.split("\t");
                try {
                    ps.setString(1, fields[0]);
                    ps.setString(2, fields[1]);
                    ps.setObject(3, getValue(fields[2]));
                    ps.setObject(4, getValue(fields[3]));

                    ps.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            ps.executeBatch();
            ps.clearBatch();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Integer getValue(String field) {
        return field.equals("\\N") ? null : Integer.parseInt(field);
    }

}
