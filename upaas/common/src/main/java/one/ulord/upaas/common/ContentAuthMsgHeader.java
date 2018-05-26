/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common;

/**
 * Content Auth Message Header
 * @author haibo
 * @since 5/16/18
 */
public class ContentAuthMsgHeader {
    private String clientId;
    private ContentFormatEnum format;
    private long contentLength;


    private ContentAuthMsgHeader(String clientId, ContentFormatEnum format, long contentLength){
        this.clientId = clientId;
        this.format = format;
        this.contentLength = contentLength;
    }


    public static class Builder{
        String clientId;
        ContentFormatEnum format;
        long contentLength;

        public Builder clientId(String clientId){
            this.clientId = clientId;
            return this;
        }

        public Builder fromat(ContentFormatEnum format){
            this.format = format;
            return this;
        }

        public Builder contentLength(long contentLength){
            this.contentLength = contentLength;
            return this;
        }


        public ContentAuthMsgHeader build(){
            if (clientId == null) throw new RuntimeException("Invalid client id.");
            return new ContentAuthMsgHeader(clientId, format, contentLength);
        }
    }
}
