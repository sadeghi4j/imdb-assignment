package com.imdb.imdbtitles.repository;

import com.imdb.imdbtitles.entity.MediaItem;
import com.imdb.imdbtitles.entity.Principal;
import com.imdb.imdbtitles.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrincipalRepository extends JpaRepository<Principal, String> {

}
