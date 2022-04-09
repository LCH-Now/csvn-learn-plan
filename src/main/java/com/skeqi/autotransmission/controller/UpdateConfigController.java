package com.skeqi.autotransmission.controller;

import com.skeqi.autotransmission.model.vo.ResultVo;
import com.skeqi.autotransmission.service.UpdateConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author CHUNHAO LIU
 * 修改配置
 */
@RestController
@RequestMapping("/config")
public class UpdateConfigController {

    @Autowired
    private UpdateConfigService updateConfigService;

    /**
     * @return
     */
    @RequestMapping(value = "/updateAccessFile", method = RequestMethod.POST)
    public ResultVo<String> updateAccessFile(@RequestBody  Map<String, Map<String, List<String>>> updateMap) {
        ResultVo<String> vo = new ResultVo<>();
        try {
            updateConfigService.updateAccessFile(updateMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

    /**
     * 解析权限文件
     * @return
     */
    @RequestMapping(value = "/getAccessFile", method = RequestMethod.POST)
    public ResultVo<Map<String, Map<String, List<String>>>> getAccessFile() {
        ResultVo<Map<String, Map<String, List<String>>>> vo = new ResultVo<>();
        try {
            vo=updateConfigService.getAccessFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

    /**
     * 获取当前服务器中的所有账号角色信息
     * @return
     */
    @RequestMapping(value = "/getAuthFile", method = RequestMethod.POST)
    public ResultVo<List<String>> getAuthFile() {
        ResultVo<List<String>> vo = new ResultVo<>();
        try {
            vo=updateConfigService.getAuthFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }

}
