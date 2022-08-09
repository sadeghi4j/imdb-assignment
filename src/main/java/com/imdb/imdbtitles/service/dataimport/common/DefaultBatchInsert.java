package com.imdb.imdbtitles.service.dataimport.common;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
public abstract class DefaultBatchInsert implements DataWriter {

    protected int numberOfThread = 8;
    protected ExecutorService executor = Executors.newFixedThreadPool(numberOfThread);

    protected JdbcTemplate jdbcTemplate;
    protected String sql;

    protected DefaultBatchInsert(JdbcTemplate jdbcTemplate, String sql) {
        this.jdbcTemplate = jdbcTemplate;
        this.sql = sql;
    }

    @SneakyThrows
    @Override
    public void batchInsert(List<String> lines) {
        StopWatch timer = new StopWatch();
        int sizePerThread = lines.size() / numberOfThread;
        final AtomicInteger sublists = new AtomicInteger();
        CompletableFuture[] futures = lines.stream()
                .map(this::parseLine)
                .collect(Collectors.groupingBy(t -> sublists.getAndIncrement() / sizePerThread))
                .values()
                .stream()
//                .map(ul -> runBatchInsertByThread(ul, sql))
                .map(this::runBatchInsertByThread)
                .toArray(CompletableFuture[]::new);
        CompletableFuture<Void> run = CompletableFuture.allOf(futures);
        timer.start();
        run.get();
        timer.stop();
        log.info("Batch size {} finished, duration: {}", lines.size(), timer.getTotalTimeMillis());
    }

    protected CompletableFuture<Void> runBatchInsertByThread(List<Object[]> lines) {
        return CompletableFuture.runAsync(() -> {
            try {
//                jdbcTemplate.batchUpdate(sql, lines);
                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Object[] parsed = lines.get(i);
                        for (int j = 0; j < parsed.length; j++) {
                            ps.setObject(j + 1, parsed[j]);
                        }
                    }

                    @Override
                    public int getBatchSize() {
                        return lines.size();
                    }
                });
            } catch (Exception e) {
                for (Object[] line : lines) {
                    try {
                        jdbcTemplate.update(sql, line);
                    } catch (Exception ex) {
                        log.error(ex.getMessage() + " | "+ Arrays.toString(line));
                    }
                }
                /*if (e.getCause() instanceof BatchUpdateException) {
                    BatchUpdateException be = (BatchUpdateException) e.getCause();
                    int[] batchRes = be.getUpdateCounts();
                    if (batchRes != null && batchRes.length > 0) {
                        for (int index = 0; index < batchRes.length; index++) {
                            if (batchRes[index] == Statement.EXECUTE_FAILED) {
                                log.error("Error execution >>>>>>>>>>>"
                                        + index + " --- , codeFail : " + batchRes[index]
                                        + "---, line " + lines.get(index));
                            }
                        }
                    }
                }*/
            }
        }, executor);

    }

}
