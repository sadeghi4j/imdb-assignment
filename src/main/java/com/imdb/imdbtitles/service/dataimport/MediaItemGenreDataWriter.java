package com.imdb.imdbtitles.service.dataimport;

import com.imdb.imdbtitles.entity.Genre;
import com.imdb.imdbtitles.repository.GenreRepository;
import com.imdb.imdbtitles.service.dataimport.common.DefaultBatchInsert;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StopWatch;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.imdb.imdbtitles.util.ParseUtil.getBoolean;
import static com.imdb.imdbtitles.util.ParseUtil.getInteger;

@Setter
@Log4j2
public class MediaItemGenreDataWriter extends DefaultBatchInsert {

    private static final String SQL = "INSERT INTO `Media_Item_Genres` (Media_Item_id, genres_id) VALUES(?,?)";

    private List<Genre> genreList = new LinkedList<>();
    private GenreRepository genreRepository;

    public MediaItemGenreDataWriter(JdbcTemplate jdbcTemplate, GenreRepository genreRepository) {
        super(jdbcTemplate, SQL);
        this.genreRepository = genreRepository;
    }

    @SneakyThrows
    @Override
    public void batchInsert(List<String> lines) {
        List<Object[]> result = new LinkedList<>();
        /*for (String line : lines) {
            Object[] objects = parseLine(line);
            List<Genre> genreList = (List<Genre>) objects[1];
            for (Genre genre : genreList) {
                result.add(new Object[]{objects[0], genre.getId()});
            }
        }
*/
        lines.stream().map(this::parseLine).forEach(objects ->
                ((List<Genre>) objects[1]).stream()
                        .map(genre -> new Object[]{objects[0], genre.getId()})
                        .collect(Collectors.toList())
                        .forEach(result::add)
        );

        StopWatch timer = new StopWatch();
        int sizePerThread = lines.size() / numberOfThread;
        final AtomicInteger sublists = new AtomicInteger();
        CompletableFuture[] futures = result.parallelStream()
                .collect(Collectors.groupingBy(t -> sublists.getAndIncrement() / sizePerThread))
                .values()
                .stream()
//                .map(ul -> runBatchInsertByThread(ul, sql))
                .map(ul -> runBatchInsert(ul))
                .toArray(CompletableFuture[]::new);
        CompletableFuture<Void> run = CompletableFuture.allOf(futures);
        timer.start();
        run.get();
        timer.stop();
        log.info("Batch size {} finished, duration: {}", lines.size(), timer.getTotalTimeMillis());
    }

    private CompletableFuture<Void> runBatchInsert(List<Object[]> lines) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Object[] parsed = lines.get(i);
//                    Object[] parsed = parseLine(line);
                    for (int j = 0; j < parsed.length; j++) {
                        ps.setObject(j + 1, parsed[j]);
                    }
                }

                @Override
                public int getBatchSize() {
                    return lines.size();
                }
            });
        }, executor);

    }

    @Override
    public Object[] parseLine(String line) {
        String[] fields = line.split("\t");
        Object[] values = new Object[]{fields[0], getGenres(fields[8])};
        return values;
    }

    private List<Genre> getGenres(String genres) {
        return Arrays.stream(genres.split(","))
                .map(genreName -> genreList.stream()
                        .filter(role -> role.getName().equals(genreName)).findFirst().orElseGet(() -> {
                            Genre genre = Genre.builder().name(genreName).build();
                            genre = genreRepository.save(genre);
                            genreList.add(genre);
                            return genre;
                        })
                )
                .collect(Collectors.toList());
    }
}
