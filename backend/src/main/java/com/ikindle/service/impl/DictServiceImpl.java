package com.ikindle.service.impl;

import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.dto.DictDTO;
import com.ikindle.entity.Dict;
import com.ikindle.mapper.DictDtoMapper;
import com.ikindle.repository.DictRepository;
import com.ikindle.service.DictService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典 Service 实现
 */
@Service
@Transactional
public class DictServiceImpl extends BaseServiceImpl<Dict, Long> implements DictService {

    private final DictRepository dictRepository;
    private final DictDtoMapper dictDtoMapper;

    public DictServiceImpl(DictRepository dictRepository, DictDtoMapper dictDtoMapper) {
        super(dictRepository);
        this.dictRepository = dictRepository;
        this.dictDtoMapper = dictDtoMapper;
    }

    @Override
    public List<DictDTO> getByType(String type) {
        return dictRepository.findByTypeAndEnabledOrderBySortAsc(type, true).stream()
                .map(dictDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DictDTO getByTypeAndValue(String type, String value) {
        return dictRepository.findByTypeAndValue(type, value)
                .map(dictDtoMapper::toDto)
                .orElse(null);
    }

    @Override
    public DictDTO save(DictDTO dictDTO) {
        Dict dict = dictDtoMapper.toEntity(dictDTO);
        return dictDtoMapper.toDto(dictRepository.save(dict));
    }

    @Override
    public DictDTO update(Long id, DictDTO dictDTO) {
        Dict dict = dictRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "字典不存在"));
        dict.setType(dictDTO.getType());
        dict.setLabel(dictDTO.getLabel());
        dict.setValue(dictDTO.getValue());
        dict.setDescription(dictDTO.getDescription());
        dict.setSort(dictDTO.getSort());
        dict.setEnabled(dictDTO.getEnabled());
        return dictDtoMapper.toDto(dictRepository.save(dict));
    }

    @Override
    public void delete(Long id) {
        dictRepository.deleteById(id);
    }
}
