package com.imdb.imdbtitles.service;

import com.imdb.imdbtitles.entity.MediaItem;
import com.imdb.imdbtitles.entity.Role;
import com.imdb.imdbtitles.repository.MediaItemRepository;
import com.imdb.imdbtitles.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class MediaItemService {

    final MediaItemRepository mediaItemRepository;
    final RoleRepository roleRepository;

    public List<MediaItem> findAllMediaItemsWithSameWriterAndDirectorWhoIsAlive() {
        Role director = roleRepository.findByNameEqualsIgnoreCase("director");
        Role writer = roleRepository.findByNameEqualsIgnoreCase("writer");
        Objects.requireNonNull(director);
        Objects.requireNonNull(writer);
        return mediaItemRepository.findAllMediaItemsWithSameWriterAndDirectorWhoIsAlive(director.getId(), writer.getId());
    }

    public List<MediaItem> findAllMediaItemsTheseTwoActorsPlayedAt(String actor1, String actor2) {
        return mediaItemRepository.findAllMediaItemsTheseTwoActorsPlayedAt(actor1, actor2);
    }

    public List<MediaItem> findBestMediaItemsOnEachYearByGenre(String genre) {
        return mediaItemRepository.findBestMediaItemsOnEachYearByGenre(genre);
    }

    /*public List<Object[]> findBestMediaItemsOnEachYearByGenre(String genre) {
        return mediaItemRepository.findBestMediaItemsOnEachYearByGenre(genre);
    }*/

    public void save(MediaItem mediaItem) {
        mediaItemRepository.save(mediaItem);
    }

    public List<MediaItem> findAll() {
        return mediaItemRepository.findAll();
    }
}
