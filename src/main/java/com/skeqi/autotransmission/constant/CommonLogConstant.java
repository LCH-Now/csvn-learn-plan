package com.skeqi.autotransmission.constant;

/**
 * @author CHUNHAO LIU
 */
public interface CommonLogConstant {

    /**
     * SVN操作日志记录
     */
     String CREAT_DIRECTORY="提交文件前的文件夹初始化操作";
     String SVN_SERVICE_CONNECT_EXCEPTION="SVN服务器链接异常";


    /**
     * SVN提交状态
     */
    String COMMIT_STATUS_WAIT="00";
    String COMMIT_STATUS_SUCCESS="01";
    String COMMIT_STATUS_FAIL="02";


    /**
     * 接口返回提示字符
     */
    String UPDATE_SUCCESS="修改成功";
    String UPDATE_FAIL="修改失败,请联系管理员";

    /**
     * 特殊符号
     */
    String LEFT_BRACKETS="[";
    String RIGHT_BRACKETS="]";
    String EQUIP="=";
    String COMMA=",";
    String NEW_LINE="\n";
    String COLON=":";

}
