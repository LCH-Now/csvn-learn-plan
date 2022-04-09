package com.skeqi.autotransmission.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.skeqi.autotransmission.model.vo.ResultVo;
import com.skeqi.autotransmission.service.SvnUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author CHUNHAO LIU
 * SVN文件处理控制类
 */
@RestController
@RequestMapping("/svn")
public class SvnUploadController {


    @Autowired
    private SvnUploadService svnUploadService;

    /**
     * @param fileArr 需要存放的文件集合
     * @param filePath 需要存放的文件路径
     * @param commitMessage 是否覆盖
     * @return
     */
    @RequestMapping(value = "/filesCommit", method = RequestMethod.POST)
    public ResultVo<String> upload(@RequestParam("fileArr") MultipartFile[] fileArr, String filePath, String commitMessage) {
        ResultVo<String> vo=new ResultVo<>();
        try{
            vo=svnUploadService.upload(fileArr,filePath,commitMessage);
        }catch(Exception e){
            e.printStackTrace();
        }
        return vo;
    }

}
