package com.imdb.imdbtitles.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(indexes = {@Index(columnList = "startYear")})
public class MediaItem {

    @Id
    @Column(name = "id", length = 10, nullable = false, updatable = false)
    @NonNull
    String id;
    String titleType;
    @Column(length = 500)
    String primaryTitle;
    //    String originalTitle;
    Boolean isAdult;
    Integer startYear;
    Integer endYear;
    Integer runtimeMinutes;

    @ManyToMany
    List<Genre> genres;

    @Column(columnDefinition = "decimal(3,1)")
    Float averageRating;
    Integer numVotes;

    //    Rating rating;
    @ToString.Exclude
    @OneToMany(mappedBy = "mediaItem")
    List<Principal> principals;


}
