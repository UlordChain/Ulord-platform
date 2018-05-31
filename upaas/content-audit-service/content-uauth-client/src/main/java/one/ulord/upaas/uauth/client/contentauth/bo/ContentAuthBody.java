/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.client.contentauth.bo;

import lombok.Data;
import one.ulord.upaas.common.ContentFormatEnum;

/**
 * @author haibo
 * @since 5/30/18
 */
@Data
public class ContentAuthBody {
    ContentFormatEnum format;
    String content;
}
