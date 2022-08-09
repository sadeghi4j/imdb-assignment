package com.imdb.imdbtitles;

import com.imdb.imdbtitles.service.dataimport.DataSetLoader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ImdbTitlesApplication {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    public static void main(String[] args) {
        SpringApplication.run(ImdbTitlesApplication.class, args);
    }

    @Autowired
    private DataSetLoader dataSetLoader;

    /*@Bean
    public CommandLineRunner commandLineRunner() {
        return (args) -> {
            System.out.println("Hello World");

        };
    }*/

    @EventListener(ApplicationReadyEvent.class)
    public void init() throws Exception {
        try {
            dataSetLoader.initDB();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", UUID.randomUUID().toString())
                .addLong("JobId", System.currentTimeMillis())
                .addLong("time", System.currentTimeMillis()).toJobParameters();

        JobExecution execution = jobLauncher.run(job, jobParameters);
        System.out.println("STATUS :: " + execution.getStatus());*/
    }
}
