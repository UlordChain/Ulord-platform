/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk.listener;


/**
 * Business processing interface
 *
 * @author chenxin
 * @since 14/8/18
 */
public interface IReceiveMessage {

    /**
     * Receive results of transfer gas , and add your handle
     * @param type Message type
     *             1：the first time return
     *             2：the second time return
     *             3：the third time return
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is 1, return hash value
     *             if type is 2, return the first confirm
     *             if type is 3, return the second confirm
     */
    public void handleTransferGas(String type,String reqId,String value);


    /**
     * Receive results of transfer token , and add your handle
     * @param type Message type
     *             1：the first time return
     *             2：the second time return
     *             3：the third time return
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is 1, return hash value
     *             if type is 2, return the first confirm
     *             if type is 3, return the second confirm
     */
    public void handleTransferToken(String type,String reqId,String value);


    /**
     * Receive results of publish resource , and add your handle
     * @param type Message type
     *             1：the first time return
     *             2：the second time return
     *             3：the third time return
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is 1, return hash value
     *             if type is 2, return the first confirm
     *             if type is 3, return the second confirm
     */
    public void handlePublishResource(String type,String reqId,String value);


}
