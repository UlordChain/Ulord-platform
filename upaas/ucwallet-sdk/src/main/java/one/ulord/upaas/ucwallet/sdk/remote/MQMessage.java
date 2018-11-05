package one.ulord.upaas.ucwallet.sdk.remote;

public abstract class MQMessage {
    private String reqId;
    private MQMessageEnum type;

    public MQMessage(){}
    public MQMessage(String reqId, MQMessageEnum type){
        this.reqId = reqId;
        this.type = type;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public MQMessageEnum getType() {
        return type;
    }

    public void setType(MQMessageEnum type) {
        this.type = type;
    }
}

