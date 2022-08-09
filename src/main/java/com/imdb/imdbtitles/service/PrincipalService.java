package com.imdb.imdbtitles.service;

import com.imdb.imdbtitles.entity.MediaItem;
import com.imdb.imdbtitles.entity.Principal;
import com.imdb.imdbtitles.repository.PrincipalRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class PrincipalService {

    final PrincipalRepository principalRepository;


}
