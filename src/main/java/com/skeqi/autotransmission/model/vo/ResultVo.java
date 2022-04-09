package com.skeqi.autotransmission.model.vo;

import com.skeqi.autotransmission.constant.CommonReturnConstant;
import com.skeqi.autotransmission.model.Header;
import lombok.Data;

/**
 * @author CHUNHAO LIU
 * 封装出统一的返回参数
 */
@Data
public class ResultVo<T> {

    /**
     * 响应头
     */
    private Header header = new Header();

    /**
     * 泛型
     */
    private T data;

    public ResultVo() {
    }

    public ResultVo(Header header) {
        this.header = header;
    }

    public ResultVo(Header header, T data) {
        this.header = header;
        this.data = data;
    }

    public ResultVo<T> sucess() {
        return new ResultVo<T>(new Header(CommonReturnConstant.SUCCESS_CODE,CommonReturnConstant.SUCCESS_MSG));
    }

    public ResultVo<T> sucess(T t) {
        return new ResultVo<T>(new Header(CommonReturnConstant.SUCCESS_CODE,CommonReturnConstant.SUCCESS_MSG), t);
    }

    public ResultVo<T> fail() {
        return new ResultVo<T>(new Header(CommonReturnConstant.FAIL_CODE, CommonReturnConstant.FAIL_MSG));
    }

    public ResultVo<T> fail(T t) {
        return new ResultVo<T>(new Header(CommonReturnConstant.FAIL_CODE, CommonReturnConstant.FAIL_MSG),t);
    }

}
