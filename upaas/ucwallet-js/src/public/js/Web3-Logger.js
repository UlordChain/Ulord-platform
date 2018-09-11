/**
 * Copyright(c) 2018
 * Ulord core developers
 *
 * Logger
 * @author chenxin
 * @since 2018-08-29
 */
var Logger = {
    open:true,
    log:function(msg){
        if(this.open){
            console.log(msg);
        }
    },
    error:function(msg){
        if(this.open){
            console.error(msg);
        }
    },
    info:function(msg){
        if(this.open){
            console.info(msg);
        }
    },
    warn:function(msg){
        if(this.open){
            console.warn(msg);
        }
    },
    alert:function(msg){
        if(this.open){
            alert(msg);
        }
    }
};

// log control
Logger.open = true;