package com.imdb.imdbtitles.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(indexes = {@Index(columnList = "media_item_id"), @Index(columnList = "person_id"), @Index(columnList = "role_id"),
        @Index(columnList = "media_item_id, person_id, role_id", unique = true)})
public class Principal implements Persistable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    MediaItem mediaItem;

    Integer ordering;

    @ManyToOne(fetch = FetchType.LAZY)
    Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    Role role;

    @Override
    public boolean isNew() {
        return true;
    }
}
