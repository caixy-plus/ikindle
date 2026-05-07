package com.ikindle.controller;

import com.ikindle.common.ApiResponse;
import com.ikindle.dto.DictDTO;
import com.ikindle.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典Controller
 * 
 * @author iKindle Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/dicts")
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 根据类型获取字典列表
     * 
     * @param type 字典类型
     * @return 字典列表
     */
    @GetMapping("/type/{type}")
    public ApiResponse<List<DictDTO>> getByType(@PathVariable String type) {
        List<DictDTO> dicts = dictService.getByType(type);
        return ApiResponse.success(dicts);
    }

    /**
     * 根据类型和值获取字典
     * 
     * @param type 字典类型
     * @param value 字典值
     * @return 字典
     */
    @GetMapping("/type/{type}/value/{value}")
    public ApiResponse<DictDTO> getByTypeAndValue(@PathVariable String type, @PathVariable String value) {
        DictDTO dict = dictService.getByTypeAndValue(type, value);
        if (dict == null) {
            return ApiResponse.notFound("字典项未找到");
        }
        return ApiResponse.success(dict);
    }

    /**
     * 创建字典
     * 
     * @param dictDTO 字典DTO
     * @return 字典DTO
     */
    @PostMapping
    public ApiResponse<DictDTO> create(@RequestBody DictDTO dictDTO) {
        DictDTO savedDict = dictService.save(dictDTO);
        return ApiResponse.success(savedDict);
    }

    /**
     * 更新字典
     * 
     * @param id 字典ID
     * @param dictDTO 字典DTO
     * @return 字典DTO
     */
    @PutMapping("/{id}")
    public ApiResponse<DictDTO> update(@PathVariable Long id, @RequestBody DictDTO dictDTO) {
        DictDTO updatedDict = dictService.update(id, dictDTO);
        if (updatedDict == null) {
            return ApiResponse.notFound("字典项未找到");
        }
        return ApiResponse.success(updatedDict);
    }

    /**
     * 删除字典
     * 
     * @param id 字典ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        dictService.delete(id);
        return ApiResponse.success();
    }
}