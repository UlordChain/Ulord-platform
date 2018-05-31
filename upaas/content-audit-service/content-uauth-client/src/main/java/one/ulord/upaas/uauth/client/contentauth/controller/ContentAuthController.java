/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.client.contentauth.controller;

import one.ulord.upaas.common.api.APIResult;
import one.ulord.upaas.uauth.client.contentauth.bo.ContentAuthBody;
import one.ulord.upaas.uauth.client.contentauth.service.ContentAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Content auth controller
 * @author haibo
 * @since 5/30/18
 */
@RestController
public class ContentAuthController {
    @Autowired
    ContentAuthService contentAuthService;

    /**
     * Execute content auth
     * @param content content
     * @return API result
     */
    @PostMapping("/contentauth")
    public APIResult executeContentAuth(@RequestBody ContentAuthBody content){
        return contentAuthService.contentAuth(content);
    }
}
