package one.ulord.upaas.ucwallet.service.base.common;

public class MQErrorMessage extends MQMessage {
    private int code;
    private String error;
    public MQErrorMessage(String reqID, int code, String error){
        super(reqID, MQMessageEnum.ERROR);
        this.code = code;
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
