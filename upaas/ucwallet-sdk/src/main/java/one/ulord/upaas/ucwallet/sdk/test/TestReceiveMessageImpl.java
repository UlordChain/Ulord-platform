/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk.test;

import one.ulord.upaas.ucwallet.sdk.listener.IReceiveMessage;
import one.ulord.upaas.ucwallet.sdk.utils.MessageType;
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
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is FIRST, return hash value
     *             if type is SECOND, return the first confirm (true or false)
     *             if type is THIRD, return the second confirm (true or false)
     */
    public void handleTransferGas(MessageType type,String reqId,String value){
        logger.info("======================  TestReceiveMessageImpl.handleTransferGas......type="+type+",reqId="+reqId+"，value="+value);
        switch (type) {
            case FIRST:
                logger.info("the first time return hash, value: "+value);
                break;
            case SECOND:
                logger.info("the second time return the first confirm, value: "+value);
                break;
            case THIRD:
                logger.info("the third time return the second confirm, value: "+value);
                break;
        }
    }

    /**
     * Receive results of message by transfer token , and handle
     * @param type Message type
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is FIRST, return hash value
     *             if type is SECOND, return the first confirm (true or false)
     *             if type is THIRD, return the second confirm (true or false)
     */
    public void handleTransferToken(MessageType type,String reqId,String value){
        logger.info("======================  TestReceiveMessageImpl.handleTransferToken......type="+type+",reqId="+reqId+"，value="+value);
        switch (type) {
            case FIRST:
                logger.info("the first time return hash, value: "+value);
                break;
            case SECOND:
                logger.info("the second time return the first confirm, value: "+value);
                break;
            case THIRD:
                logger.info("the third time return the second confirm, value: "+value);
                break;
        }
    }

    /**
     * Receive results of message by transfer token list , and handle
     * @param type Message type
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is FIRST, return hash value
     *             if type is SECOND, return the first confirm (true or false)
     *             if type is THIRD, return the second confirm (true or false)
     */
    public void handleTransferTokenList(MessageType type, String reqId, String value){
        logger.info("======================  TestReceiveMessageImpl.handleTransferTokenList......type="+type+",reqId="+reqId+"，value="+value);
        switch (type) {
            case FIRST:
                logger.info("the first time return hash, value: "+value);
                break;
            case SECOND:
                logger.info("the second time return the first confirm, value: "+value);
                break;
            case THIRD:
                logger.info("the third time return the second confirm, value: "+value);
                break;
        }
    }

    /**
     * Receive results of message by publish resource , and handle
     * @param type Message type
     * @param reqId Uniquely identified business ID
     * @param value The result value of returned
     *             if type is FIRST, return hash value
     *             if type is SECOND, return the first confirm (true or false)
     *             if type is THIRD, return the second confirm (true or false)
     */
    public void handlePublishResource(MessageType type,String reqId,String value){
        logger.info("======================  TestReceiveMessageImpl.handlePublishResource......type="+type+",reqId="+reqId+"，value="+value);
        switch (type) {
            case FIRST:
                logger.info("the first time return hash, value: "+value);
                break;
            case SECOND:
                logger.info("the second time return the first confirm, value: "+value);
                break;
            case THIRD:
                logger.info("the third time return the second confirm, value: "+value);
                break;
        }
    }


}
