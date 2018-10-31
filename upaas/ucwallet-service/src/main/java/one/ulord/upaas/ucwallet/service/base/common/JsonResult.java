/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.common;

import java.util.Map;

/**
 * rest接口返回结果类
 *
 * @author chenxin
 * @since 2018-08-10
 */
public class JsonResult {

    private int resultCode;
    private String resultMsg;
    private Object result;
    private String jsessionid;

    public JsonResult() {
    }

    public JsonResult(int code, String message){
        this.resultCode = code;
        this.resultMsg = message;
    }

    public JsonResult(int code, String message, Object result){
        this.resultCode = code;
        this.resultMsg = message;
        this.result = result;
    }

    public int getResultCode() {
        return this.resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return this.resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getJsessionid() {
        return this.jsessionid;
    }

    public void setJsessionid(String jsessionid) {
        this.jsessionid = jsessionid;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
