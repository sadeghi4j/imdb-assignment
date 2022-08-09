package com.imdb.imdbtitles.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(indexes = {@Index(columnList = "deathYear")})
public class Person implements Persistable<String> {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, length = 10)
    @NonNull
    String id;
//    @Column(length = 120)
    String primaryName;
    Integer birthYear;
    Integer deathYear;
    /*@ManyToMany
    @JoinTable(name = "PERSON_ROLE",
            joinColumns = {@JoinColumn(name = "person_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    List<Role> primaryProfession;*/
//    knownForTitles


    public Person(String id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
