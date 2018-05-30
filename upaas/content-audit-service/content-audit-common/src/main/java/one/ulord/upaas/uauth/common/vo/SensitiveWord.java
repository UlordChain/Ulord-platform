/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.common.vo;


/**
 * @author haibo
 * @since 5/22/18
 */
public class SensitiveWord {
    private String keyword;
    private int    level;

    public SensitiveWord(){

    }

    public SensitiveWord(String keyword, int level){
        this.keyword = keyword;
        this.level = level;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
