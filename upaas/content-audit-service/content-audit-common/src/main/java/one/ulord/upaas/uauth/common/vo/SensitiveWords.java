/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.common.vo;

import lombok.Data;

/**
 * @author haibo
 * @since 5/22/18
 */
@Data
public class SensitiveWords {
    private String keyword;
    private int    level;
}
