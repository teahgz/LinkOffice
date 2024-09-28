package com.fiveLink.linkOffice.mapper;

import com.fiveLink.linkOffice.nofication.domain.NoficationDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoficationMapper {

    int bellCount(Long memberNo);

    List<NoficationDto> selectUnreadList(Long memberNo);
}
