/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.sdk.test;

import one.ulord.upaas.ucwallet.sdk.listener.IReceiveMessage;
import one.ulord.upaas.ucwallet.sdk.remote.*;
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

    private final Logger logger = LoggerFactory.getLogger(TestReceiveMessageImpl.class);


    @Override
    public void handleResponse(MQMessage transactionResponse) {
        switch (transactionResponse.getType()){
            case RESPONSE:
                handleRawMessageResponse((SendRawTransactionResponse)(transactionResponse));
                break;
            case CONFIRM:
                handleRawMessageConfirm((SendRawTransactionConfirm)(transactionResponse));
                break;
            case DBLCONFIRM:
                handleRawMessageDblConfirm((SendRawTransactionDblConfirm)(transactionResponse));
                break;
            case ERROR:
                handleErrorResponse((MQErrorMessage)transactionResponse);
                break;
            default:
                logger.warn("Message cannot support by current program:" + transactionResponse);
        }
    }

    private void handleErrorResponse(MQErrorMessage transactionResponse) {
        logger.info("DEMO: reqId:{} has occured error.code:({}), Msg:{}",
                transactionResponse.getReqId(),
                transactionResponse.getCode(),
                transactionResponse.getError());
    }

    private void handleRawMessageConfirm(SendRawTransactionConfirm transactionResponse) {
        logger.info("DEMO: reqId:{} has confirmed, txHash is {}, status is {}.",
                transactionResponse.getReqId(),
                transactionResponse.getTxHash(),
                transactionResponse.isStatus());
    }

    private void handleRawMessageDblConfirm(SendRawTransactionDblConfirm transactionResponse) {
        logger.info("DEMO: reqId:{} has confirmed, txHash is {}, {} blocks has confirmed.",
                transactionResponse.getReqId(),
                transactionResponse.getTxHash(),
                transactionResponse.getConfirmBlocks());
    }

    private void handleRawMessageResponse(SendRawTransactionResponse transactionResponse) {
        logger.info("DEMO: reqId:{} has submit, txHash is {}.",
                transactionResponse.getReqId(),
                transactionResponse.getTxHash());
    }
}
