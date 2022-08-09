package com.imdb.imdbtitles.test;

import com.imdb.imdbtitles.dto.MediaItemDto;
import com.imdb.imdbtitles.entity.MediaItem;
import com.imdb.imdbtitles.mapper.DTOMapper;
import com.imdb.imdbtitles.service.MediaItemService;
import com.imdb.imdbtitles.service.PrincipalService;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
public class AppController {

    final MediaItemService mediaItemService;
    final PrincipalService principalService;
    final MeterRegistry meterRegistry;


    @GetMapping("/assignment1")
    public List<MediaItemDto> findAllMediaItemsWithSameWriterAndDirectorWhoIsAlive() {
        return DTOMapper.INSTANCE.mapMediaItem(mediaItemService.findAllMediaItemsWithSameWriterAndDirectorWhoIsAlive());
    }

    @GetMapping("/assignment2/{actor1}/{actor2}")
    public List<MediaItemDto> findAllMediaItemsTheseTwoActorsPlayedAt(@PathVariable String actor1, @PathVariable String actor2) {
//        Map<String, String> actorsIds   actorsIds.get("actor1"), actorsIds.get("actor2")
        return DTOMapper.INSTANCE.mapMediaItem(mediaItemService.findAllMediaItemsTheseTwoActorsPlayedAt(actor1, actor2));
    }

    @GetMapping("/assignment3/{genre}")
    public List<MediaItemDto> findAllMediaItemsTheseTwoActorsPlayedAt(@PathVariable String genre) {
        return DTOMapper.INSTANCE.mapMediaItem(mediaItemService.findBestMediaItemsOnEachYearByGenre(genre));
    }

    @GetMapping("/assignment4")
    public double countOfHttpRequests() {
        meterRegistry.get("http.server.requests").timers()
                .forEach(t -> {
                    System.out.println("Request uri:" + t.getId().getTag("uri"));
                    System.out.println("Request type:" + t.getId().getTag("method"));
                    System.out.println("Request status:" + t.getId().getTag("status"));
                    System.out.println("Request count: " + t.count());
                    System.out.println("--------------------------------------------------");
                });
        return meterRegistry.get("http.server.requests").timers().stream().mapToLong(Timer::count).sum();
        /*Iterator<Measurement> iterator = meterRegistry.counter("http.server.requests").measure().iterator();
        while (iterator.hasNext()) {
            Measurement measurement = iterator.next();
            if (measurement.getStatistic().name().equals("COUNT")) {
                return measurement.getValue();
            }
        }*/
//        return 0;
    }

    @GetMapping("/all")
    public List<MediaItem> findAll() {
        return mediaItemService.findAll();
    }


    @GetMapping(value = "/healthcheck")
    public ResponseEntity healthcheck() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping(value = "/healthcheck")
    public ResponseEntity healthcheckDelete() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
}
