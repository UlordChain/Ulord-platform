/**
 * Copyright(c) 2018
 * Ulord core developers
 *
 * web3Helper
 * @author chenxin
 * @since 2018-08-28
 */
var Web3Helper = {

    web3: null,

    // contract abi
    ABI_AUTHOR_MODULE: null,
    ABI_USER_MODULE: null,

    /**
     * init web3j
     */
    init:function(){
        Logger.log("init web3 helper...");
        if (null == this.web3) {
            if (!window.web3) {
                alert("没有检查到MetaMask插件，请先安装MetaMask");
            }else{
                this.web3 = new Web3(window.web3.currentProvider);
            }
        }
        // load contract abi
        this.ABI_AUTHOR_MODULE = this.getAbi(Config.CONTRACT_AUTHOR_MODULE);
        this.ABI_USER_MODULE = this.getAbi(Config.CONTRACT_USER_MODULE);
    },


    /**
     * 检查网络（前端在拦截器中调用并提示用户）
     * @return {boolean}
     */
    isNetworkConnected: function() {
        Logger.log("isNetworkConnected...");
        var netId = this.web3.version.network;
        Logger.log("isNetworkConnected netId:"+netId);
        if (netId != Config.NET_ID) {
            switch (netId) {
                case "1":
                    return "您当前连接的是以太坊主网，请先切换至"+Config.NET_NAME;
                case "2":
                    return "您当前连接的是Morden测试网，请先切换至"+Config.NET_NAME;
                case "3":
                    return "您当前连接的是ropsten测试网，请先切换至"+Config.NET_NAME;
                case "4":
                    return "您当前连接的是Rinkeby测试网，请先切换至"+Config.NET_NAME;
                case "42":
                    return "您当前连接的是Kovan测试网，请先切换至"+Config.NET_NAME;
                default:
                    return "未知网络，请先切换至"+Config.NET_NAME;
            }
        }else{
            Logger.log("isNetworkConnected connect success");
            return "success";
        }
    },


    /**
     * 判断MetaMask是否已登录（前端在拦截器中调用并提示用户）
     * @return {boolean}
     */
    isMetaMaskLogin: function() {
        var msg = "success";
        if (!window.web3) {
            msg = "没有检查到MetaMask插件，请先安装MetaMask";
        }
        // if (!this.web3.eth.coinbase) {
        //     Logger.log('请先登陆或者激活MetaMask');
        //     return;
        // }
        if(null == this.web3.eth.defaultAccount || undefined == this.web3.eth.defaultAccount){
            msg = "请登录MetaMask";
        }
        Logger.log("isMetaMaskLogin msg:"+msg);
        return msg;
    },


    /**
     * get abi jsonObject by contract name
     * @param contractName
     * @return
     */
    getAbi: function(contractName) {
        Logger.log("getAbi contractName: "+contractName);
        var abiObj = null;
        $.ajax({
            type:"get",
            dataType:"json",
            url: Config.SERVICE_URL + "/get/abi/"+contractName,
            async:false,
            success:function(data){
                abiObj = data.msg;
            },
            error:function(e){
                Logger.log(e);
            }
        });
        return abiObj;
    },


    /**
     * register
     * @param username
     * @param password
     * @param address
     * @return
     */
    register: function(username,password) {
        var currentAccount = this.web3._extend.utils.toChecksumAddress(this.web3.eth.defaultAccount);
        Logger.log("register username:"+username+",password:"+password+",defaultAccount:"+ currentAccount);
        var result = null;
        var formData =  new FormData();
        formData.append("username",username);
        formData.append("password",password);
        formData.append("address",currentAccount);
        $.ajax({
            type:"POST",
            dataType:"json",
            url: Config.SERVICE_URL + "/post/user/register",
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
            }
        });
        return result;
    },



    /**
     * Login
     * @param username
     * @param password
     * @param address
     * @return token
     */
    login: function(username,password) {
        var currentAccount = this.web3._extend.utils.toChecksumAddress(this.web3.eth.defaultAccount);
        Logger.log("login username:"+username+",password:"+password+",defaultAccount:"+currentAccount);
        var formData =  new FormData();
        var result = null;
        formData.append("username",username);
        formData.append("password",password);
        formData.append("address",currentAccount);
        $.ajax({
            type:"POST",
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
    },



    /**
     * logout
     * @param username
     * @param password
     * @param address
     * @param token
     * @return
     */
    logout: function(token,username,password) {
        var currentAccount = this.web3._extend.utils.toChecksumAddress(this.web3.eth.defaultAccount);
        Logger.log("logout username:"+username+",password:"+password+",token:"+token+",defaultAccount:"+currentAccount);
        var formData =  new FormData();
        var result = null;
        formData.append("username",username);
        formData.append("password",password);
        formData.append("address",currentAccount);
        formData.append("token",token);
        $.ajax({
            type:"POST",
            dataType:"json",
            url: Config.SERVICE_URL + "/post/user/logout",
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
    },



    /**
     * get user info
     * @param username
     * @param address
     * @param token
     * @return
     */
    getUserInfo: function(token,username) {
        var currentAccount = this.web3._extend.utils.toChecksumAddress(this.web3.eth.defaultAccount);
        Logger.log("getUserInfo username:"+username+",token:"+token+",defaultAccount:"+currentAccount);
        var formData =  new FormData();
        var result = null;
        formData.append("username",username);
        formData.append("address",currentAccount);
        formData.append("token",token);
        $.ajax({
            type:"POST",
            dataType:"json",
            url: Config.SERVICE_URL + "/post/user/info",
            data:formData,
            async:false,
            processData : false,
            contentType : false,
            //crossDomain: true,
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
    },



    /**
     * upload content to UDFS
     * @param token : 登录token
     * @param title : 资源标题
     * @param description : 资源描述
     * @param body : 资源内容
     * @return udfs hash
     */
    uploadUdfs: function(token,title,description,body) {
        Logger.log("getUdfsHash token:"+token+",title:"+title+",description:"+description+",body:"+body);
        var result = null;
        var formData =  new FormData();
        formData.append("token",token);
        formData.append("title",title);
        formData.append("description",description);
        formData.append("body",body);
        $.ajax({
            type:"POST",
            dataType:"json",
            url: Config.SERVICE_URL + "/post/blog/content",
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
    },


    /**
     * 发布一个新的内容上链
     * @param token     : 登录token
     * @param udfsHash  : 内容的UDFS Hash值
     * @param title     : 内容标题
     * @param price     : 内容的定价，0表示免费
     * @param type      : 内容的类型
     * @return
     */
    publishResource: function(token,udfsHash,title,price,type) {
        var currentAccount = this.web3._extend.utils.toChecksumAddress(this.web3.eth.defaultAccount);
        Logger.log("publish udfsHash:"+udfsHash+",title:"+title+",price:"+price+",type:"+type+",defaultAccount:"+currentAccount+",token:"+token);

        // 通过ABI和合约地址获取已部署的合约对象
        var myContractInstance = this.web3.eth.contract(this.ABI_AUTHOR_MODULE).at(Config.CONTRACT_ADDRESS_AUTHOR_MODULE);

        // 发布资源
        Logger.log("publish begin..." );
        return new Promise(function(resolve, reject) {
            myContractInstance.publish(udfsHash,price,type,function(err, hash) {
                if(err){
                    Logger.log("err:" + String(err));
                    return;
                }
                Logger.log("publish hash:"+hash);

                // 查询交易收据
                // _this.web3.eth.getTransactionReceipt(hash, function(err, result) {
                //     console.log(result);
                // });

                // 将信息传给后台
                var formData =  new FormData();
                formData.append("token",token);
                formData.append("title",title);
                formData.append("address",currentAccount);
                formData.append("tx_hash",hash);
                $.ajax({
                    type:"POST",
                    dataType:"json",
                    url: Config.SERVICE_URL + "/post/blog/chain",
                    data:formData,
                    async:false,
                    processData : false,
                    contentType : false,
                    success:function(data){
                        Logger.log(data);
                        resolve(data);
                    },
                    error:function(e){
                        Logger.log(e);
                        reject(e);
                    }
                });

                // 返回处理
                // _this.renderReceipt(_this,token,title,_address,hash);
            })
        })
    },
    // renderReceipt: function(_this,token,title,address,hash) {
    //     Logger.log("publish renderReceipt hash:"+hash+",token:"+token+",title:"+title+",address:"+address);
    //     // 通过一个交易哈希，返回一个交易的收据
    //     _this.web3.eth.getTransactionReceipt("0x62cadd67f4441189f075f089968a14cad8496d5883dbccefeb2efe48f33ad0ef",function(err, result) {
    //         if(err){
    //             Logger.log("err:" + String(err));
    //             return;
    //         }
    //         Logger.log("publish receipt.........");
    //         Logger.log(result);
    //         Logger.log("publish receipt.........");
    //     })
    // },


    /**
     * 读者购买资源
     * @param token      : 登录token
     * @param claimId    : 资源ID
     * @param toAddress  : 收款地址
     * @param value      : 付款金额
     * @param gas        : gas费用
     * return hash
     */
    purchaseResource: function(token,claimId,toAddress,value,gas) {
        var currentAccount = this.web3._extend.utils.toChecksumAddress(this.web3.eth.defaultAccount);
        Logger.log("purchaseResource token:"+token+",claimId:"+claimId+",toAddress:"+toAddress+",value:"+value+",gas:"+gas+",defaultAccount:"+ currentAccount);
        value = new BigNumber(value);

        // 通过ABI和合约地址获取已部署的合约对象
        var myContractInstance = this.web3.eth.contract(this.ABI_USER_MODULE).at(Config.CONTRACT_ADDRESS_USER_MODULE);
        Logger.log(this.ABI_USER_MODULE);

        // 开始购买
        myContractInstance.buy.sendTransaction(claimId, {from: currentAccount,value:value,gas:300000}, function(err, hash) {
            if (err) {
                Logger.log("err:" + String(err));
                return;
            }
            Logger.log("purchaseResource hash:" + hash);

            // 查询交易收据
            // _this.web3.eth.getTransactionReceipt(hash, function(err, result) {
            //     console.log(result);
            // });

            // 将信息传给后台
            var formData =  new FormData();
            formData.append("token",token);
            formData.append("claim_id",claimId);
            formData.append("address",currentAccount);
            $.ajax({
                type:"POST",
                dataType:"json",
                url: Config.SERVICE_URL + "/post/blog/payment",
                data:formData,
                async:false,
                processData : false,
                contentType : false,
                success:function(data){
                    Logger.log(data);
                    return data;
                },
                error:function(e){
                    Logger.log(e);
                    return null;
                }
            });

        });
    },


    /**
     * 查询资源列表
     * @param token  : 登录token
     * @param page   : 页码
     * return json
     */
    queryResourceList: function(token,page) {
        var currentAccount = this.web3._extend.utils.toChecksumAddress(this.web3.eth.defaultAccount);
        Logger.log("queryResourceList page:"+page+",token:"+token+",defaultAccount:"+currentAccount);
        var result = null;
        var formData =  new FormData();
        formData.append("token",token);
        formData.append("page",page);
        formData.append("address",currentAccount);
        $.ajax({
            type:"POST",
            dataType:"json",
            url: Config.SERVICE_URL + "/post/blog/list",
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
            }
        });
        return result;
    },


    /**
     * 查询资源详情
     * @param token  : 登录token
     * @param blog_id  : 资源id
     * @return
     */
    queryResourceDetail: function(token,blogId) {
        var currentAccount = this.web3._extend.utils.toChecksumAddress(this.web3.eth.defaultAccount);
        Logger.log("queryByAddress blogId:"+blogId+",defaultAccount:"+currentAccount);
        var result = null;
        var formData =  new FormData();
        formData.append("token",token);
        formData.append("blog_id",blogId);
        formData.append("address",currentAccount);
        $.ajax({
            type:"POST",
            dataType:"json",
            url: Config.SERVICE_URL + "/post/blog/info",
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
            }
        });
        return result;

        // 通过ABI和合约地址获取已部署的合约对象
        // var myContractInstance = _web3.eth.contract(this.ABI_AUTHOR_MODULE).at(Config.CONTRACT_ADDRESS);
        // myContractInstance.findClaimInfo(claimId,function(err, result) {
        //     if(err) {
        //         Logger.log("err:" + String(err));
        //         return ;
        //     }
        //     Logger.log("result:"+result);
        // })

    },




    /**
     * 广告续费
     * @param claimId  : 资源ID
     * @param value  : 续费金额
     * @return
     */
    renewAD: function(_claimId,_value) {
        var currentAccount = this.web3._extend.utils.toChecksumAddress(this.web3.eth.defaultAccount);
        Logger.log("publish renewAD:"+_claimId+",_value:"+_value+",defaultAccount:"+currentAccount);

        // 通过ABI和合约地址获取已部署的合约对象
        var myContractInstance = this.web3.eth.contract(this.ABI_AUTHOR_MODULE).at(Config.CONTRACT_ADDRESS_AUTHOR_MODULE);

        // 发布资源
        Logger.log("renewAD begin..." );
        myContractInstance.renewAD.sendTransaction(_claimId, {from: currentAccount,value: _value,gas:300000}, function(err, hash) {
            if(err){
                Logger.log("err:" + String(err));
                return null;
            }
            Logger.log("renewAD hash:"+hash);
            return hash;
        })
    }

};
Web3Helper.init()