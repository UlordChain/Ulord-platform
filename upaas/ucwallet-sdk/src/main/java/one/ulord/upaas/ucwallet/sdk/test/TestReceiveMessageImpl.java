/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk.test;

import one.ulord.upaas.ucwallet.sdk.listener.IReceiveMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Test Business implementation
 *
 * @author chenxin
 * @since 14/8/18
 */
@Component
public class TestReceiveMessageImpl implements IReceiveMessage{

    final Logger logger = LoggerFactory.getLogger(TestReceiveMessageImpl.class);

    /**
     * Receive results of message by transfer gas , and handle
     * @param type Message type
     *             1：the first time return
     *             2：the second time return
     *             3：the third time return
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is 1, return hash value
     *             if type is 2, return the first confirm
     */
    public void handleTransferGas(String type,String reqId,String value){
        logger.info("======================  TestReceiveMessageImpl.handleTransferGas......type:"+type+",reqId:"+reqId+",value:"+value);
    }

    /**
     * Receive results of message by transfer token , and handle
     * @param type Message type
     *             1：the first time return
     *             2：the second time return
     *             3：the third time return
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is 1, return hash value
     *             if type is 2, return the first confirm
     */
    public void handleTransferToken(String type,String reqId,String value){
        logger.info("======================  TestReceiveMessageImpl.handleTransferToken......type:"+type+",reqId:"+reqId+",value:"+value);
    }

    /**
     * Receive results of message by publish resource , and handle
     * @param type Message type
     *             1：the first time return
     *             2：the second time return
     *             3：the third time return
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is 1, return hash value
     *             if type is 2, return the first confirm
     */
    public void handlePublishResource(String type,String reqId,String value){
        logger.info("======================  TestReceiveMessageImpl.handlePublishResource......type:"+type+",reqId:"+reqId+",value:"+value);
    }


}
