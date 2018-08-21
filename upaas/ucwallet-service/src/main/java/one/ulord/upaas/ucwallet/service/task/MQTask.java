/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.task;

import one.ulord.upaas.ucwallet.service.base.common.Constants;
import one.ulord.upaas.ucwallet.service.base.common.RedisUtil;
import one.ulord.upaas.ucwallet.service.base.contract.ContentContract;
import one.ulord.upaas.ucwallet.service.base.contract.ContentContractHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import java.io.IOException;
import java.util.Set;

/**
 * Timed tasks, synchronous records of Redis
 *
 * @author chenxin
 * @since 2018-08-14
 */
@Component
@Configurable
@EnableScheduling
public class MQTask {

	private static final Logger logger = LoggerFactory.getLogger(MQTask.class);

	@Autowired
	RedisUtil redisUtil;

	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Autowired
	private ContentContractHelper contentContractHelper;

	@Scheduled(cron = "${task.mq.cron}")
	@Async("asyncServiceExecutor")
	public void query() {
//		logger.info("======================  job......is running.....");
		Set<String> keys = redisUtil.getKeys("*mq|*");
		if(null!=keys){
			for(String key : keys) {
                String values = (String)redisUtil.get(key);
                redisUtil.remove(key);

				// Each record starts with a new thread processing.
                if(null!=values){
                    String[] mArr = values.split("\\|",-1);
                    String type = mArr[0];
                    String reqId = mArr[1];
                    String routingKey = mArr[2];
                    String sleepTime = mArr[3];
                    String hash = mArr[4];
					String dappKey = mArr[5];
                    startNewThread(type,reqId,routingKey,sleepTime,hash,dappKey);
                }
			}
		}
	}


	/**
	 * Openning A new thread in the thread pool, and sent the result to MQ after processing.
	 * @param type
	 * @param reqId
	 * @param routingKey
	 * @param sleepTime
	 * @param hash
	 */
	@Async("asyncServiceExecutor")
	public void startNewThread(String type,String reqId,String routingKey,String sleepTime,String hash,String dappKey){
		logger.info("======================  job startNewThread......reqId="+reqId+",type="+type+",routingKey="+routingKey+",sleepTime="+sleepTime+",hash="+hash+",dappKey="+dappKey);
		ContentContract cc = contentContractHelper.getContentContract();
		try {
			Thread.sleep(Integer.parseInt(sleepTime) * 1000);
			TransactionReceipt r = cc.queryTransactionReceipt(hash);
			boolean status = false;
			if(null!=r){
				status = r.isStatusOK();
			}
//			logger.info("======================  job......status:" + status);
			String returnStr = type+"|"+reqId+"|"+status+"|"+dappKey;
			this.rabbitTemplate.convertAndSend(Constants.EXCHANGE_TOPIC,routingKey,returnStr);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
