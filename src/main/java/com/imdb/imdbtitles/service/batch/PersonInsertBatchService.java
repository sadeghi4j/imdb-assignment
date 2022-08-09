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
public class PersonInsertBatchService {

    final JdbcTemplate jdbcTemplate;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    Integer BATCH_SIZE;

    private static final ExecutorService executor = null;//Executors.newFixedThreadPool(10);

    //    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void batchInsert(List<Person> persons) {
        CompletableFuture.runAsync(() -> {

        });
//        StopWatch timer = new StopWatch();
        String sql = "INSERT INTO Person (id, primary_name, birth_year , death_year) VALUES(?,?,?,?)";
//        timer.start();

        jdbcTemplate.batchUpdate(sql, new PersonBatchPreparedStatementSetter(persons));

//        timer.stop();
//        log.info("batchInsert -> Total time in seconds: " + timer.getTotalTimeSeconds());
    }

    @SneakyThrows
    public void parallelBatchInsert(List<Person> persons) {
        StopWatch timer = new StopWatch();
        String sql = "INSERT INTO `Person` (id, primary_name, birth_year , death_year) VALUES(?,?,?,?)";
        final AtomicInteger sublists = new AtomicInteger();
        log.info("Batch Started");
        CompletableFuture[] futures = persons.parallelStream()
                .collect(Collectors.groupingBy(t -> sublists.getAndIncrement() / 10_000))
                .values()
                .stream()
                .map(ul -> runBatchInsert(ul, sql))
                .toArray(CompletableFuture[]::new);
        CompletableFuture<Void> run = CompletableFuture.allOf(futures);
        timer.start();
        run.get();
        timer.stop();
        log.info("Batch finished, duration: {}", timer.getTotalTimeMillis());
//        log.info("batchInsertAsync -> Total time in seconds: " + timer.getTotalTimeSeconds());
    }

    public CompletableFuture<Void> runBatchInsert(List<Person> users, String sql) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.batchUpdate(sql, new PersonBatchPreparedStatementSetter(users));
        }, executor);
    }

    class PersonBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

        private List<Person> persons;

        public PersonBatchPreparedStatementSetter(List<Person> persons) {
            this.persons = persons;
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            Person person = persons.get(i);
            ps.setString(1, person.getId());
            ps.setString(2, person.getPrimaryName());
            ps.setObject(3, person.getBirthYear());
            ps.setObject(4, person.getDeathYear());
        }

        @Override
        public int getBatchSize() {
            return persons.size();
        }
    }
}
