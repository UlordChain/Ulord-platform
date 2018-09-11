/**
 * Copyright(c) 2018
 * Ulord core developers
 *
 * web3Test
 * @author chenxin
 * @since 2018-09-08
 */
var Web3Test = {

    web3: null,

    /**
     * init web3j
     */
    init:function(){
        Logger.log("init web3 test...");
        if (null == this.web3) {
            this.web3 = new Web3(window.web3.currentProvider);
        }
    },

    /**
     * 查询Gas余额
     * @param address
     */
    getGasBalance: function(){
        var address = document.getElementById('getGasBalance').value;
        Logger.log("getGasBalance address:"+address);
        this.web3.eth.getBalance(address, function(err, result) {
            if(err) {
                Logger.log("err:" + String(err));
                return;
            }
            Logger.log("balance:"+result);
            document.getElementById('result1').innerHTML = result;
        })
    },

    /**
     * 查询Token余额
     * @param address
     */
    getTokenBalance: function(address) {
        Logger.log("getTokenBalance address:"+address);

    },


    /**
     * 转账
     * @param fromAddress
     * @param toAddress
     * @param value
     */
    sendTransaction: function() {
        var currentAccount = this.web3._extend.utils.toChecksumAddress(this.web3.eth.defaultAccount);
        var toAddress = document.getElementById('sendTransaction_toAddress').value;
        var value = document.getElementById('sendTransaction_value').value;
        value = new BigNumber(value);
        Logger.log("sendTransaction toAddress:"+toAddress+",value:"+value+",defaultAccount:"+ currentAccount);
        this.web3.eth.sendTransaction({
            from: currentAccount,
            to: toAddress,
            value: value,
            //gas: "21000"
            //gasPrice: "0.4",
            //data: ""
        }, function(err, hash) {
            if(err) {
                Logger.log("err:" + String(err));
                return;
            }
            Logger.log("hash:"+hash);
            document.getElementById('result2').innerHTML = hash;
        })
    },

    /**
     * 发送原始交易
     * @param fromAddress
     * @param toAddress
     * @param value
     */
    sendRawTransaction: function() {
        var fromAddress = document.getElementById('sendRawTransaction_fromAddress').value;
        var toAddress = document.getElementById('sendRawTransaction_toAddress').value;
        var value = document.getElementById('sendRawTransaction_value').value;
        value = new BigNumber(value);
        Logger.log("sendRawTransaction fromAddress:"+fromAddress+",toAddress:"+toAddress+",value:"+value);


        //var privateKey = new Buffer('e331b6d69882b4cb4ea581d88e0b604039a3de5967688d3dcffdd2270c0fd109', 'hex');
        var rawTx = {
            nonce: '0x00',
            gasPrice: '0x09184e72a000',
            gasLimit: '0x2710',
            to: toAddress,
            value: value,
            data: '0x7f7465737432000000000000000000000000000000000000000000000000000000600057'
        };

        var tx = new Tx(rawTx);
        Logger.log(tx);
        //tx.sign(privateKey);

        // var serializedTx = tx.serialize();
        //
        // //Logger.log(serializedTx.toString('hex'));
        //
        // web3.eth.sendRawTransaction('0x' + serializedTx.toString('hex'), function(err, hash) {
        //     if (!err)
        //         Logger.log(hash);
        //         document.getElementById('result3').innerHTML = hash;
        // });
    },


    /**
     * 查询交易数
     * @param address
     */
    getTransactionCount: function() {
        var address = document.getElementById('getTransactionCount').value;
        Logger.log("getTransactionCount address:"+address);
        this.web3.eth.getTransactionCount(address, function(err, result) {
            if(err) {
                Logger.log("err:" + String(err));
                return;
            }
            Logger.log("number:"+result);
            document.getElementById('result5').innerHTML = result;
        })
    },


    /**
     * 查询交易
     * @param address
     */
    getTransaction: function() {
        var hash = document.getElementById('getTransaction').value;
        Logger.log("getTransaction hash:"+hash);
        this.web3.eth.getTransaction(hash, function(err, result) {
            if(err) {
                Logger.log("err:" + String(err));
                return;
            }
            Logger.log(result);
            var sbHtml = new StringBuffer();
            sbHtml.Append("<strong>hash:</strong> "+result.hash+"<br>");
            sbHtml.Append("<strong>nonce:</strong> "+result.nonce+"<br>");
            sbHtml.Append("<strong>blockHash:</strong> "+result.blockHash+"<br>");
            sbHtml.Append("<strong>blockNumber:</strong> "+result.blockNumber+"<br>");
            sbHtml.Append("<strong>from:</strong> "+result.from+"<br>");
            sbHtml.Append("<strong>to:</strong> "+result.to+"<br>");
            sbHtml.Append("<strong>gas:</strong> "+result.gas+"<br>");
            sbHtml.Append("<strong>gasPrice:</strong> "+result.gasPrice+"<br>");
            sbHtml.Append("<strong>transactionIndex:</strong> "+result.transactionIndex+"<br>");
            document.getElementById('result6').innerHTML = sbHtml.ToString();
        })
    },



    /**
     * 查询交易收据
     * @param hash
     */
    getTransactionReceipt: function() {
        var hash = document.getElementById('getTransactionReceipt').value;
        Logger.log("getTransactionReceipt hash:"+hash);
        this.web3.eth.getTransactionReceipt(hash, function(err, result) {
            if(err) {
                Logger.log("err:" + String(err));
                return;
            }
            Logger.log(result);
            // var sbHtml = new StringBuffer();
            // sbHtml.Append("<strong>hash:</strong> "+result.hash+"<br>");
            // sbHtml.Append("<strong>nonce:</strong> "+result.nonce+"<br>");
            // sbHtml.Append("<strong>blockHash:</strong> "+result.blockHash+"<br>");
            // sbHtml.Append("<strong>blockNumber:</strong> "+result.blockNumber+"<br>");
            // sbHtml.Append("<strong>from:</strong> "+result.from+"<br>");
            // sbHtml.Append("<strong>to:</strong> "+result.to+"<br>");
            // sbHtml.Append("<strong>gas:</strong> "+result.gas+"<br>");
            // sbHtml.Append("<strong>gasPrice:</strong> "+result.gasPrice+"<br>");
            // sbHtml.Append("<strong>transactionIndex:</strong> "+result.transactionIndex+"<br>");
            // document.getElementById('result6').innerHTML = sbHtml.ToString();
        })
    },


    /**
     * 监听账户状态变化
     */
    accountFilter: function() {
        Logger.log('filter');
        var currentAccount = this.web3._extend.utils.toChecksumAddress(currentAccount);
        var filter = this.web3.eth.filter({fromBlock:0, toBlock:'latest', address: currentAccount,topics:null});
        Logger.log(filter);
        filter.get(function (err, transactions) {
            Logger.log(transactions);
            transactions.forEach(function (tx) {
                var result = this.web3.eth.getTransaction(tx.transactionHash);
                console.log(result);
            });
        });

        //var filter = this.web3.eth.filter('pending');
        filter.watch(function (error, log) {
            Logger.log(log); //  {"address":"0x0000000000000000000000000000000000000000", "data":"0x0000000000000000000000000000000000000000000000000000000000000000", ...}
        });
        //filter.stopWatching();
    },


    /**
     * 授权登录
     */
    oauthLogin: function(){
        Logger.log("oauthLogin begin...");
        if(self.fetch) {
            var currentAccount = this.web3._extend.utils.toChecksumAddress(this.web3.eth.defaultAccount);
            var url = Config.SERVICE_URL + "/get/user/nonce/"+currentAccount;
            var param = {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json;charset=utf-8'
                },
                mode: 'cors',
                cache: 'default'
            };
            fetch(url,param).then(
                function (response) {
                    return response.json();
                })
                .then(this.handleSign).then(this.handleLogin)
                .catch(function (err) {
                        Logger.log(err);
                    }
                );
        } else {
            Logger.log("请更换浏览器后再试!");
        }
    },
    handleSign: function (nonce) {
        Logger.log("handleSign begin...");
        Logger.log(nonce);
        var currentAccount = this.web3._extend.utils.toChecksumAddress(this.web3.eth.defaultAccount);
        return new Promise(function (resolve, reject) {
            var message = 'logging blog, the nonce is :' + nonce;
            return this.web3.personal.sign(
                this.web3.fromUtf8(message),
                currentAccount,
                function (err, signature) {
                    if (err) {
                        Logger.log("您已经取消登陆!");
                        return reject(err);
                    }
                    return resolve({
                        currentAccount: currentAccount,
                        signature: signature,
                        message: message
                    });
                });
        });
    },
    handleLogin: function (jsonObj) {
        Logger.log("handleLogin begin...");
        Logger.log(jsonObj);
        var formData =  new FormData();
        var result = null;
        formData.append("signature",jsonObj.signature);
        formData.append("message",jsonObj.message);
        formData.append("address",jsonObj.currentAccount);
        $.ajax({
            type: "POST",
            dataType:"json",
            url: Config.SERVICE_URL + "/post/user/login",
            data:formData,
            async:false,
            processData : false,
            contentType : false,
            success:function(data){
                Logger.log(data);
                result = data;
            },
            error:function(e){
                Logger.log(e);
                return null;
            }
        });
        return result;
    }



};
Web3Test.init()