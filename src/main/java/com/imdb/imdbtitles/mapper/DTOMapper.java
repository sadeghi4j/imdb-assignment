package com.imdb.imdbtitles.mapper;

import com.imdb.imdbtitles.dto.MediaItemDto;
import com.imdb.imdbtitles.entity.MediaItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper( DTOMapper.class );

    List<MediaItemDto> mapMediaItem(List<MediaItem> source);

}