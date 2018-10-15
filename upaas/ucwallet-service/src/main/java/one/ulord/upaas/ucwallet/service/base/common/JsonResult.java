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
public class JsonResult<K, V> {

    private int resultCode;
    private String resultMsg;
    private String result;
    private String jsessionid;
//    private long totalCount;
//    private Map<K, V> dataMap;

    public JsonResult() {
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    //    public Map<K, V> getDataMap() {
//        return this.dataMap;
//    }
//
//    public void setDataMap(Map<K, V> dataMap) {
//        this.dataMap = dataMap;
//    }

//    public long getTotalCount() {
//        return this.totalCount;
//    }
//
//    public void setTotalCount(long totalCount) {
//        this.totalCount = totalCount;
//    }
}
