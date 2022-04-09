package com.skeqi.autotransmission.model.domian;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author CHUNAHO LIU
 * oa-svn提交记录表
 */
@Data
@TableName("t_svn_commit_log")
public class SvnCommitLog {

    /**
     * 主键编号
     */
    private String id;

    /**
     * 提交时间
     */
    private Date commitTime;

    /**
     * 提交状态
     */
    private String commitStatus;

    /**
     * 提交信息
     */
    private String commitMessage;

    /**
     * SVN提交日志说明
     */
    private String svnLog;


    public SvnCommitLog() {
    }


    public SvnCommitLog(String id, Date commitTime, String commitStatus, String commitMessage, String svnLog) {
        this.id = id;
        this.commitTime = commitTime;
        this.commitStatus = commitStatus;
        this.commitMessage = commitMessage;
        this.svnLog = svnLog;
    }
}
