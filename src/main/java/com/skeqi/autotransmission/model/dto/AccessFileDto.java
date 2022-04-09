package com.skeqi.autotransmission.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author CHUNHAO LIU
 * 权限文件修改Dto
 */
@Data
public class AccessFileDto {

    private Map<String, List<String>> groupMap;
}
