package com.skeqi.autotransmission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.skeqi.autotransmission.model.domian.SvnCommitLog;
import org.springframework.stereotype.Repository;

/**
 * @author CHUNAHO LIU
 * SVN提交记录表
 */
@Repository
public interface SvnCommitLogMapper extends BaseMapper<SvnCommitLog> {
}
