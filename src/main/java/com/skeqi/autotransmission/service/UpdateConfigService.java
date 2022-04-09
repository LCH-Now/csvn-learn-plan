package com.skeqi.autotransmission.service;

import com.skeqi.autotransmission.model.vo.ResultVo;

import java.util.List;
import java.util.Map;

/**
 * @author CHUNHAO LIU
 * 修改配置文件
 */
public interface UpdateConfigService {

    /**
     * 更新SNV权限文件
     * @return
     */
    ResultVo<String> updateAccessFile(Map<String, Map<String, List<String>>> updateMap);


    /**
     * 获取SNV权限文件
     * @return
     */
    ResultVo<Map<String, Map<String, List<String>>>> getAccessFile();



    /**
     * 获取SNV用户信息
     * @return
     */
    ResultVo<List<String>> getAuthFile();

}
