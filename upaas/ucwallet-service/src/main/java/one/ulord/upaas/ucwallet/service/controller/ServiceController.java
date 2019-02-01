/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import one.ulord.upaas.ucwallet.service.base.common.Constants;
import one.ulord.upaas.ucwallet.service.base.common.JsonResult;
import one.ulord.upaas.ucwallet.service.base.common.ResultUtil;
import one.ulord.upaas.ucwallet.service.base.contract.Provider;
import one.ulord.upaas.ucwallet.service.service.SUTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.SignedRawTransaction;
import org.web3j.crypto.TransactionDecoder;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

import static one.ulord.upaas.ucwallet.service.base.common.Constants.INVALID_NONCE_VALUE;
import static one.ulord.upaas.ucwallet.service.base.common.Constants.NO_ENOUGH_SUT;

/**
 * ucwallet-service API Service
 * 
 * @author chenxin, yinhaibo
 * @since 2018-08-10
 */
@Api(value = "Service Center")
@RestController
@RequestMapping(value = "api")
public class ServiceController{
	private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);
    private static final BigDecimal POW18 = BigDecimal.TEN.pow(18);
    private static final Pattern addressPattern = Pattern.compile("^(0[xX]){0,1}[0-9a-zA-Z]{40}$");
	@Autowired
    private SUTService sutService;

    @Autowired
    private Provider provider;

	/**
	 * Get balance by address
	 * @param address
	 * @return
	 */
	@ApiOperation(value = "Get balance by address", notes = "Get balance by address")
	@RequestMapping(value = "getBalance/{address}", method = RequestMethod.GET)
	public ResponseEntity<String> getSutBalance(@PathVariable(value="address") String address) {
		logger.debug("getSutBalance address:"+address);

		try {
            // For compatible
            String gasBalance = sutService.getBalance(address).multiply(POW18).toBigInteger().toString();
			logger.debug("address{}, balance:{}", address, gasBalance);
            return ResultUtil.GoResponseSuccess(gasBalance);
		} catch (Exception e) {
			logger.error("get balance error:", e);
			return ResultUtil.GoResponseFailure(e.getMessage());
		}
	}

    @ApiOperation(value = "Get balance by address", notes = "Get balance by address")
    @RequestMapping(value = "balance/{address}", method = RequestMethod.GET)
    public ResponseEntity<String> getBalance(@PathVariable(value="address") String address) {
        logger.debug("getSutBalance address:"+address);

		if (address == null ||
				!addressPattern.matcher(address).matches()){
			return ResultUtil.GoResponseFailure(Constants.INVALID_PARAMETER,
					"Invalid parameter: address:" + address);
		}
        try {
            String gasBalance = sutService.getBalance(address).toString();
            logger.debug("address{}, balance:{}", address, gasBalance);
            return ResultUtil.GoResponseSuccess(gasBalance);
        } catch (Exception e) {
            logger.error("get balance error:", e);
            return ResultUtil.GoResponseFailure(e.getMessage());
        }
    }

	/**
	 * Get token balance by address
	 * @param address
	 * @return
	 */
	@ApiOperation(value = "Get token balance by address", notes = "Get token balance by address")
	@RequestMapping(value = "getTokenBalance/{address}", method = RequestMethod.GET)
	public ResponseEntity<String> getTokenBalance(@PathVariable(value="address") String address, String token) {
		if (token == null || !addressPattern.matcher(token).matches()){
		    // using default contract address
            token = provider.getContractAddress();
		}
		logger.info("address:{}, token:{}", address, token);
		if (address == null || token == null ||
				!addressPattern.matcher(address).matches() ||
				!addressPattern.matcher(token).matches()){
			return ResultUtil.GoResponseFailure(Constants.INVALID_PARAMETER,
					"Invalid parameter: address:" + address + ", token:" + token);
		}
		try {
		    // For compatible
            String tokenBalance = sutService.getTokenBalance(token, address).multiply(POW18).toBigInteger().toString();
			logger.info("Token balance:" + tokenBalance);
            return ResultUtil.GoResponseSuccess(tokenBalance);
		} catch (Exception e) {
            logger.error("get token balance error:", e);
			return ResultUtil.GoResponseFailure(e.getMessage());
		}
	}

    @ApiOperation(value = "Get token balance by address", notes = "Get token balance by address")
    @RequestMapping(value = "tokenBalance/{address}", method = RequestMethod.GET)
    public ResponseEntity<String> getTokenBalance2(@PathVariable(value="address") String address, String token) {
        logger.info("address:{}, token:{}", address, token);

        if (token == null || !addressPattern.matcher(token).matches()){
            // using default contract address
            token = provider.getContractAddress();
        }
		logger.info("address:{}, token:{}", address, token);
		if (address == null || token == null ||
				!addressPattern.matcher(address).matches() ||
				!addressPattern.matcher(token).matches()){
			return ResultUtil.GoResponseFailure(Constants.INVALID_PARAMETER,
					"Invalid parameter: address:" + address + ", token:" + token);
		}
        try {
            String tokenBalance = sutService.getTokenBalance(token, address).toString();
            logger.info("Token balance:" + tokenBalance);
            return ResultUtil.GoResponseSuccess(tokenBalance);
        } catch (Exception e) {
            logger.error("get token balance error:", e);
            return ResultUtil.GoResponseFailure(e.getMessage());
        }
    }


	/**
	 * Get transaction count by address
	 * @param address
	 * @return
	 */
	@ApiOperation(value = "Get transaction count by address", notes = "Get transaction count by address")
	@RequestMapping(value = "getTransactionCount/{address}", method = RequestMethod.GET)
	public ResponseEntity<String> getTransactionCountOld(@PathVariable(value="address") String address) {
		return getTransactionCount(address);
	}

    @ApiOperation(value = "Get transaction count by address", notes = "Get transaction count by address")
    @RequestMapping(value = "transactionCount/{address}", method = RequestMethod.GET)
    public ResponseEntity<String> getTransactionCount(@PathVariable(value="address") String address) {
        try {
            String nonce = sutService.getTransactionCount(address).toString();
            logger.debug("address:{} nonce:{}", address, nonce);
            return ResultUtil.GoResponseSuccess(nonce);
        } catch (Exception e) {
            return ResultUtil.GoResponseFailure(e.getMessage());
        }
    }

    /**
     * Send raw transaction
     * @param hexValue
     * @return
     */
    @ApiOperation(value = "Send raw transaction", notes = "Send raw transaction")
    @RequestMapping(value = "sendRawTransaction", method = RequestMethod.POST)
    public ResponseEntity<String> sendRawTransactionOld(@RequestParam String hexValue) {
        return sendRawTransaction(hexValue);
    }
	/**
	 * Send raw transaction
	 * @param hexValue
	 * @return
	 */
	@ApiOperation(value = "Send raw transaction", notes = "Send raw transaction")
	@RequestMapping(value = "rawTransaction", method = RequestMethod.POST)
	public ResponseEntity<String> sendRawTransaction(@RequestParam String hexValue) {
		logger.debug("hexValue:{}", hexValue);

		String hash;
		try {
            RawTransaction tx;
            tx = TransactionDecoder.decode(hexValue);
            if (tx instanceof SignedRawTransaction) {
                String from = ((SignedRawTransaction) tx).getFrom().toLowerCase();
                BigInteger nonce = tx.getNonce();
                BigDecimal gasFee = sutService.fromWei(tx.getGasLimit().multiply(tx.getGasPrice()));
                BigDecimal value = sutService.fromWei(tx.getValue());

                BigInteger txCount = sutService.getTransactionCount(from);
                BigDecimal balance = sutService.getBalance(from);


                // check balance
                if (balance.subtract(gasFee).subtract(value).signum() < 0) {
                    // No more sUT
                    return ResultUtil.GoResponseFailure(NO_ENOUGH_SUT, "No enough sUT.");
                }

                // check nonce value
                if (nonce.compareTo(txCount) < 0) {
                    // Invalid nonce value
                    return ResultUtil.GoResponseFailure(INVALID_NONCE_VALUE, "Invalid nonce value");
                }
            }
			hash = sutService.sendRawTransaction(hexValue);
            return ResultUtil.GoResponseSuccess(hash);
		} catch (Exception e) {
		    logger.error("send raw transaction err:{}", hexValue, e);
			return ResultUtil.GoResponseFailure(e.getMessage());
		}
	}


	/**
	 * query transaction
	 * @param txhash transaction hash
	 * @return
	 */
	@ApiOperation(value = "query transaction by transaction hash", notes = "query transaction")
	@RequestMapping(value = "queryTransaction", method = RequestMethod.GET)
	public ResponseEntity<String> queryTransactionOld(@RequestParam String txhash) {
	    return queryTransaction(txhash);
    }

    @ApiOperation(value = "query transaction by transaction hash", notes = "query transaction")
    @RequestMapping(value = "transaction", method = RequestMethod.GET)
    public ResponseEntity<String> queryTransaction(@RequestParam String txhash) {
		JsonResult resultJson = new JsonResult();
		logger.info("getTransaction:{}", txhash);

		try {
            Transaction tx = sutService.getTransaction(txhash);
            return ResultUtil.GoResponseSuccess(tx);
		} catch (Exception e) {
            logger.error("query transaction for {}:", txhash, e);
            return ResultUtil.GoResponseFailure(resultJson);
		}
	}


    @ApiOperation(value = "query transaction receipt by transaction hash", notes = "query transaction receipt")
    @RequestMapping(value = "queryTransactionReceipt", method = RequestMethod.GET)
    public ResponseEntity<String> queryTransactionReceiptOld(@RequestParam String txhash) {
	    return queryTransactionReceipt(txhash);
    }
	/**
	 * query transaction
	 * @param txhash transaction hash
	 * @return
	 */
	@ApiOperation(value = "query transaction receipt by transaction hash", notes = "query transaction receipt")
	@RequestMapping(value = "transactionReceipt", method = RequestMethod.GET)
	public ResponseEntity<String> queryTransactionReceipt(@RequestParam String txhash) {
		logger.info("getTransactionReceipt" + txhash);

		try {
            TransactionReceipt receipt = sutService.getTransactionReceipt(txhash);
            return ResultUtil.GoResponseSuccess(receipt);
		} catch (Exception e) {
			logger.error("query transaction for transaction:{}", txhash, e);
			return ResultUtil.GoResponseFailure(e.getMessage());
		}
	}

	/**
	 * query transaction
	 * @return
	 */
	@ApiOperation(value = "query UT Fed Address", notes = "query UT Fed Address")
	@RequestMapping(value = "UTFedAddress", method = RequestMethod.GET)
	public ResponseEntity<String> queryUTFedAddress() {
		try {
			String fedAddress = sutService.queryUTFedAddress();
			return ResultUtil.GoResponseSuccess(fedAddress);
		} catch (Exception e) {
			logger.error("query UTFedAddress:{}",  e);
			return ResultUtil.GoResponseFailure(e.getMessage());
		}
	}
}
