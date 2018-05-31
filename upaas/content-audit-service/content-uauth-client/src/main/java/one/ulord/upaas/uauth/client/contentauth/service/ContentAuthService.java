/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.client.contentauth.service;

import one.ulord.upaas.common.api.APIResult;
import one.ulord.upaas.uauth.client.contentauth.bo.ContentAuthBody;

/**
 * @author haibo
 * @since 5/30/18
 */
public interface ContentAuthService {
    APIResult contentAuth(ContentAuthBody content);
}
