package com.imdb.imdbtitles.dto;

import lombok.Data;

@Data
public class MediaItemDto {

    String id;
    String titleType;
    String primaryTitle;
    String originalTitle;
    boolean isAdult;
    Integer startYear;
    Integer endYear;
    Integer runtimeMinutes;

    float averageRating;
    int numVotes;
}
