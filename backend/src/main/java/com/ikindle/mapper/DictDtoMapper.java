package com.ikindle.mapper;

import com.ikindle.entity.Dict;
import com.ikindle.dto.DictDTO;
import org.mapstruct.Mapper;

/**
 * 字典Mapper接口
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@Mapper(componentModel = "spring")
public interface DictDtoMapper extends BaseDtoMapper<Dict, DictDTO> {
}