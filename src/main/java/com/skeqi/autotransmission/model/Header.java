package com.skeqi.autotransmission.model;

import com.skeqi.autotransmission.constant.CommonReturnConstant;
import lombok.Data;

/**
 * @author CHUNHAO LIU
 * @version V1.0
 * @Title: Controller
 * @Description: 头文件
 * @date 2022-03-19 10:34:19
 */
@Data
public class Header {
    /**
     * 状态码，0代表成功 -1失败 1异常提示信息(微服务调用之间使用)
     */
    private String ret;

    /**
     * 状态结果说明
     */
    private String msg;


    public Header() {
    }

    public Header(String ret) {
        this.ret = ret;
    }

    public Header(String ret, String msg) {
        this.ret = ret;
        this.msg = msg;
    }

    public Header success() {

        return new Header(CommonReturnConstant.SUCCESS_CODE, CommonReturnConstant.SUCCESS_MSG);
    }

    public Header fail() {

        return new Header(CommonReturnConstant.FAIL_CODE, CommonReturnConstant.FAIL_MSG);
    }


}
