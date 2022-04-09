package com.skeqi.autotransmission.service.impl;

import com.skeqi.autotransmission.constant.CommonLogConstant;
import com.skeqi.autotransmission.mapper.SvnCommitDetailLogMapper;
import com.skeqi.autotransmission.mapper.SvnCommitLogMapper;
import com.skeqi.autotransmission.model.domian.SvnCommitDetailLog;
import com.skeqi.autotransmission.model.domian.SvnCommitLog;
import com.skeqi.autotransmission.model.vo.ResultVo;
import com.skeqi.autotransmission.service.SvnUploadService;
import com.skeqi.autotransmission.util.FileUtil;
import com.skeqi.autotransmission.util.SpringContextUtil;
import com.skeqi.autotransmission.util.SvnUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @author CHUNHAO LIU
 * SVN文件处理实现类
 */
@Service
public class SvnUploadServiceImpl implements SvnUploadService {

    /**
     * 从配置文件之中读取
     */
    @Value("${workSpace}")
    private  String workSpaceFather;


    @Autowired
    private SvnCommitDetailLogMapper svnCommitDetailLogMapper;

    @Autowired
    private SvnCommitLogMapper svnCommitLogMapper;

    /**
     * @param fileArr       待上传的文件
     * @param filePath      上传的文件地址 以"/"开头的路径，用于在svn上新建对应文件夹
     * @param commitMessage 是否覆盖
     */
    @Override
    public ResultVo<String> upload(MultipartFile[] fileArr, String filePath, String commitMessage) {

        String svnLog = "";
        //提交状态默认成功,进入异常提示失败。
        String commitStatus = CommonLogConstant.COMMIT_STATUS_SUCCESS;
        //文件名称数组
        String[] fileNameArr = new String[fileArr.length];
        try {
            //需要对参数地址的分隔符"/" 参数做统一的处理 一律使用"\"
            if ("".equals(filePath) || null == filePath) {
                filePath = filePath.replace("/", "\\");
            }
            //拼接工作空间
            String workSpace = workSpaceFather + filePath;
            String fileName = "";
            //将文件保存在workSpace空间下
            for (int i = 0; i < fileArr.length; i++) {
                MultipartFile file = fileArr[i];
                fileName = file.getOriginalFilename();
                fileNameArr[i] = fileName;
                //保存文件
                FileUtil.savePic(file.getInputStream(), workSpace, fileName);
            }
            //开启SVN链接
            SvnUtil.SvnUpload();
            //更新文件夹
            SvnUtil.updateWorkSpace(new File(workSpaceFather), SVNRevision.HEAD, SVNDepth.INFINITY);
            //提交文件
            SvnUtil.upload(workSpace, filePath, fileNameArr, commitMessage);

        } catch (IOException e) {
            //进入这个异常表示的是文件还处于待提交状态
            svnLog = e.getMessage();
            commitStatus = CommonLogConstant.COMMIT_STATUS_WAIT;
        } catch (SVNException e) {
            //进入这个异常表示文件处于提交失败,具体就需要可查看具体的问题了
            svnLog = e.getMessage();
            commitStatus = CommonLogConstant.COMMIT_STATUS_FAIL;
        }
        //记录提交日志
        String commitId = UUID.randomUUID().toString();
        svnCommitLogMapper.insert(new SvnCommitLog(commitId, new Date(), commitStatus, commitMessage, svnLog));

        //记录提交详情
        for(int nameLength=0;nameLength<fileNameArr.length;nameLength++){
            String fileName=fileNameArr[nameLength];
            svnCommitDetailLogMapper.insert(new SvnCommitDetailLog(UUID.randomUUID().toString(),commitId,fileName,filePath));
        }

        return new ResultVo<String>().sucess();
    }







}
