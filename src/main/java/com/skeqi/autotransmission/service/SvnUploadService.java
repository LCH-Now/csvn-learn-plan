package com.skeqi.autotransmission.service;

import com.skeqi.autotransmission.model.vo.ResultVo;
import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNException;

import java.io.IOException;

/**
 * @author CHUNHAO LIU
 * SVN文件处理接口类
 */
public interface SvnUploadService {

    /**
     *
     * SVN获取文件上传到指定目录下
     * @param fileArr  待上传的文件
     * @param filePath 需要存放的文件路径
     * @param commitMessage 是否覆盖
     * @return
     * @throws SVNException
     * @throws IOException
     */
    ResultVo<String> upload(MultipartFile[] fileArr,String filePath, String commitMessage) throws SVNException, IOException;
}
