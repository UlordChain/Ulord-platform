/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.communication.bo;

import lombok.Data;

/**
 * @author haibo
 * @since 5/23/18
 */
@Data
public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(){}
    public LoginRequest(String username, String password){
        this.username = username;
        this.password = password;
    }
}
