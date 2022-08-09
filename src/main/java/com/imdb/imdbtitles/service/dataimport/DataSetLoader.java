package com.imdb.imdbtitles.service.dataimport;

import com.imdb.imdbtitles.entity.Genre;
import com.imdb.imdbtitles.entity.Person;
import com.imdb.imdbtitles.entity.Role;
import com.imdb.imdbtitles.repository.*;
import com.imdb.imdbtitles.service.dataimport.common.DataImporter;
import com.imdb.imdbtitles.service.dataimport.common.DataReader;
import com.imdb.imdbtitles.service.dataimport.common.DataWriter;
import com.imdb.imdbtitles.service.dataimport.common.DefaultFileReader;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//@NoArgsConstructor
//@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class DataSetLoader {

    @Value("${app.title-dataset-path}")
    String TITLE_DATASET_PATH;
    @Value("${app.name-dataset-path}")
    String NAME_DATASET_PATH;
    @Value("${app.title-principals-dataset-path}")
    String TITLE_PRINCIPALS_DATASET_PATH;
    @Value("${app.title-ratings-dataset-path}")
    String TITLE_RATING_DATASET_PATH;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    Integer BATCH_SIZE;


    final RoleRepository roleRepository;
    final GenreRepository genreRepository;
//    final NameDatasetBatchInsertService nameDatasetBatchInsertService;
    //    final PersonInsertBatchService personInsertBatchService;
//    final JdbcBatchInsert jdbcBatchInsert;
    final JdbcTemplate jdbcTemplate;


    @SneakyThrows
    @Async
    public void initDB() {
        long start = System.currentTimeMillis();

        loadNameDataSet_UsingJdbcTemplateParallelBatchInsert();
        loadTitleDataSet_UsingJdbcTemplateParallelBatchInsert();
        loadMediaItemGenreDataSet_UsingJdbcTemplateParallelBatchInsert();
        loadPrincipalsDataSet_UsingJdbcTemplateParallelBatchInsert();
        loadRatingDataSet_UsingJdbcTemplateParallelBatchInsert();

//        loadNameDataSetUsingJdbcTemplateBatchInsert();
//        loadNameDataSetUsingJdbcParallelBatchInsert();

//        loadNameDataSetUsingJdbcBatchInsert();
//        loadNameDataSetAndConvertIntoPerson();
//        loadTitleDataSet();
//        loadPrincipalsDataSet();
//        loadRatingDataSet();
        long end = System.currentTimeMillis();
        System.out.println("end - start (s) = " + ((end - start)));
    }

    @SneakyThrows
    public void loadNameDataSet_UsingJdbcTemplateParallelBatchInsert() {
        DataReader dataReader = new DefaultFileReader(NAME_DATASET_PATH);
        DataWriter dataWriter = new PersonDataWriter(jdbcTemplate);
        DataImporter dataImporter = new DataImporter(dataReader, dataWriter);
        dataImporter.start();
    }

    @SneakyThrows
    public void loadTitleDataSet_UsingJdbcTemplateParallelBatchInsert() {
        DataReader dataReader = new DefaultFileReader(TITLE_DATASET_PATH);
        DataWriter dataWriter = new MediaItemDataWriter(jdbcTemplate);
        DataImporter dataImporter = new DataImporter(dataReader, dataWriter);
        dataImporter.start();
    }

    @SneakyThrows
    public void loadMediaItemGenreDataSet_UsingJdbcTemplateParallelBatchInsert() {
        DataReader dataReader = new DefaultFileReader(TITLE_DATASET_PATH);
        DataWriter dataWriter = new MediaItemGenreDataWriter(jdbcTemplate, genreRepository);
        DataImporter dataImporter = new DataImporter(dataReader, dataWriter);
        dataImporter.start();
    }

    @SneakyThrows
    public void loadPrincipalsDataSet_UsingJdbcTemplateParallelBatchInsert() {
        DataReader dataReader = new DefaultFileReader(TITLE_PRINCIPALS_DATASET_PATH);
        DataWriter dataWriter = new PrincipalsDataWriter(jdbcTemplate, roleRepository);
        DataImporter dataImporter = new DataImporter(dataReader, dataWriter);
        dataImporter.start();
    }

    @SneakyThrows
    public void loadRatingDataSet_UsingJdbcTemplateParallelBatchInsert() {
        DataReader dataReader = new DefaultFileReader(TITLE_RATING_DATASET_PATH);
        DataWriter dataWriter = new RatingDataWriter(jdbcTemplate);
        DataImporter dataImporter = new DataImporter(dataReader, dataWriter);
        dataImporter.start();
    }

/*
    private void loadTitleDataSet() throws IOException {
        readFile(TITLE_DATASET_PATH, 10, line -> {
//            System.out.println(line);
            String[] fields = line.split("\t");
            String id = fields[0];
            MediaItem mediaItem = MediaItem.builder()
                    .id(id)
                    .titleType(fields[1])
                    .primaryTitle(fields[2])
                    .originalTitle(fields[3])
                    .isAdult(Boolean.parseBoolean(fields[4]))
                    .startYear(Integer.parseInt(fields[5]))
                    .endYear(getValue(fields[6]))
                    .runtimeMinutes(getValue(fields[7]))
                    .genres(getGenres(fields[8]))
                    .build();
//            System.out.println(mediaItem);

            mediaItemRepository.save(mediaItem);
            return line;
        });
    }

    private void loadPrincipalsDataSet() throws IOException {
        *//*Principal principal = new Principal(null, new MediaItem("tt0000001"), 1, new Person("nm0000001"), getRole("director"));
        principalRepository.save(principal);
        principal = new Principal(null, new MediaItem("tt0000001"), 2, new Person("nm0000001"), getRole("writer"));
        principalRepository.save(principal);
        principal = new Principal(null, new MediaItem("tt0000002"), 2, new Person("nm0000002"), getRole("writer"));
        principalRepository.save(principal);
        principal = new Principal(null, new MediaItem("tt0000002"), 2, new Person("nm0000002"), getRole("director"));
        principalRepository.save(principal);*//*
        readFile(TITLE_PRINCIPALS_DATASET_PATH, 100, line -> {
//            System.out.println(line);
            String[] fields = line.split("\t");
            Principal principal = new Principal(null, new MediaItem(fields[0]), Integer.parseInt(fields[1]), new Person(fields[2]), getRole(fields[3]));
            try {
                principalRepository.save(principal);
            } catch (Exception e) {
                System.err.println(principal.getPerson().getId() + " id of person not found");
            }
            return line;
        });
    }*/


}
