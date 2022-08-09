package com.imdb.imdbtitles.repository;

import com.imdb.imdbtitles.entity.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaItemRepository extends JpaRepository<MediaItem, String> {

    @Query("SELECT p1.mediaItem FROM Principal p1 JOIN Principal p2 on p1.mediaItem.id = p2.mediaItem.id " +
            "where p1.person.deathYear is null and p1.person.id = p2.person.id and p1.role.name = 'director' and p2.role.name = 'writer'")
    List<MediaItem> findAllMediaItemsWithSameWriterAndDirectorWhoIsAlive();

    @Query("SELECT p1.mediaItem FROM Principal p1 JOIN Principal p2 on p1.mediaItem.id = p2.mediaItem.id " +
            "where p1.person.id = p2.person.id and p1.role.name = 'actor' and p1.person.id = ?1 and p2.role.name = 'actor' and p2.person.id = ?2")
    List<MediaItem> findAllMediaItemsTheseTwoActorsPlayedAt(String actor1, String actor2);

    /*@Query(value = "select MEDIA_ITEM.ID, PRIMARY_TITLE, start_year, AVERAGE_RATING from MEDIA_ITEM inner join MEDIA_ITEM_GENRES mig on MEDIA_ITEM.id = mig.media_item_id inner join genre g on g.id = mig.genres_id  " +
            "where g.name = 'Animation' and  exists (   " +
            "select 1 from ( select start_year, max(average_rating) as average_rating from MEDIA_ITEM group by start_Year) as cond " +
            "                where MEDIA_ITEM.start_year=cond.start_year " +
            "                and MEDIA_ITEM.average_rating =cond.average_rating " +
            "             ) " +
            "order by start_year"
            , nativeQuery = true)*/
//    @Query("select mi from MediaItem mi where mi.id SELECT m.startYear, max(m.averageRating) as movie_id FROM MediaItem m join m.genres g where g.name = ?1 " +
//            "group by m.startYear")
//            "having m.averageRating = max(m.averageRating)"
//    )
    /*@Query(value = "SELECT * FROM MEDIA_ITEM inner join MEDIA_ITEM_GENRES mig on MEDIA_ITEM.id = mig.media_item_id inner join genre g on g.id = mig.genres_id " +
            "WHERE g.name = 'Animation' AND (START_YEAR, AVERAGE_RATING) IN " +
            "( SELECT START_YEAR, MAX(AVERAGE_RATING) " +
            "  FROM MEDIA_ITEM " +
            "  GROUP BY START_YEAR " +
            ")", nativeQuery = true)
    List<Object[]> findBestMediaItemsOnEachYearByGenre(String genre);
    */

    @Query("select mi from MediaItem mi join mi.genres g where g.name = ?1 and (mi.startYear, mi.averageRating) IN " +
            "(SELECT m.startYear, MAX(m.averageRating) " +
            "  FROM MediaItem m" +
            "  GROUP BY m.startYear " +
            ")")
    List<MediaItem> findBestMediaItemsOnEachYearByGenre(String genre);

}
