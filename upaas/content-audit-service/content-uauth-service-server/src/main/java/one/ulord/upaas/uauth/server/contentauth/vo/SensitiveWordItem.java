/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.contentauth.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import one.ulord.upaas.uauth.common.vo.SensitiveWord;

/**
 * @author haibo
 * @since 5/26/18
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SensitiveWordItem extends SensitiveWord {
    private int uid;
    private int disabled;

    public SensitiveWordItem(){}
    public SensitiveWordItem(SensitiveWord word) {
        this.setKeyword(word.getKeyword());
        this.setLevel(word.getLevel());
        this.disabled = 0;
    }

    public SensitiveWord of(){
        return new SensitiveWord(this.getKeyword(), this.getLevel());
    }
}
