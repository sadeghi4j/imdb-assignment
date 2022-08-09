package com.imdb.imdbtitles.service.dataimport.common;

import java.util.List;

@FunctionalInterface
public interface ReadEvent {

    void notify(List<String> listOfLines);

}
