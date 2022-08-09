package com.imdb.imdbtitles.service.dataimport.common;

import java.io.IOException;

public interface DataReader {

    void read() throws IOException;

    void setReadEventListener(ReadEvent readEvent);

}
