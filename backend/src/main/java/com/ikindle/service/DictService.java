package com.ikindle.service;

import com.ikindle.entity.Dict;
import com.ikindle.dto.DictDTO;

import java.util.List;

/**
 * 字典Service接口
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
public interface DictService extends BaseService<Dict, Long> {

    /**
     * 根据类型获取字典列表
     * 
     * @param type 字典类型
     * @return 字典DTO列表
     */
    List<DictDTO> getByType(String type);

    /**
     * 根据类型和值获取字典
     * 
     * @param type 字典类型
     * @param value 字典值
     * @return 字典DTO
     */
    DictDTO getByTypeAndValue(String type, String value);

    /**
     * 保存字典
     * 
     * @param dictDTO 字典DTO
     * @return 字典DTO
     */
    DictDTO save(DictDTO dictDTO);

    /**
     * 更新字典
     * 
     * @param id 字典ID
     * @param dictDTO 字典DTO
     * @return 字典DTO
     */
    DictDTO update(Long id, DictDTO dictDTO);

    /**
     * 删除字典
     * 
     * @param id 字典ID
     */
    void delete(Long id);
}