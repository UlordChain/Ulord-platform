/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.api;

import one.ulord.upaas.common.UPaaSErrorCode;
import one.ulord.upaas.common.communication.UPaaSCommandCode;

/**
 * API Result
 * @author haibo
 * @since 5/26/18
 */
public class APIResult {
    private int errorCode;
    private String reason;
    private Object result;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * Build a error result
     * @param errorCode error code
     * @param reason error message
     * @return 消息对象
     */
    public static APIResult buildError(int errorCode, String reason){
        APIResult result = new APIResult();
        result.setErrorCode(errorCode);
        result.setReason(reason);

        return result;
    }

    /**
     * 通过异常构建一个错误对象（服务异常）
     * @param e 异常对象
     * @return
     */
    public static APIResult buildError(Exception e){
        return buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION,
                "Service exception， please retry. More detail is:" + e.getMessage());
    }

    /**
     * 构建一个正确返回值对象
     * @param result 消息结果对象
     * @param reason 消息提示信息
     * @return 消息对象
     */
    public static APIResult buildResult(Object result, String reason){
        APIResult r = new APIResult();
        r.setResult(result);
        r.setErrorCode(UPaaSErrorCode.SUCCESS);

        return r;
    }

    /**
     * 构建一个正确返回值对象
     * @param result 消息结果对象
     * @return 消息对象
     */
    public static APIResult buildResult(Object result){
        return buildResult(result, "");
    }
}
