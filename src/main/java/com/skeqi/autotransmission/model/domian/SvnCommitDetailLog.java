package com.skeqi.autotransmission.model.domian;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author CHUNHAO LIU
 * svn提交记录详情
 */
@Data
@TableName("t_svn_commit_detail_log")
public class SvnCommitDetailLog {


    /**
     * 主键编号
     */
    private String id;

    /**
     * 提交记录详情
     */
    private String commitId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 存放文件路径
     */
    private String filePath;



    public SvnCommitDetailLog() {
    }

    public SvnCommitDetailLog(String id, String commitId, String fileName, String filePath) {
        this.id = id;
        this.commitId = commitId;
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
