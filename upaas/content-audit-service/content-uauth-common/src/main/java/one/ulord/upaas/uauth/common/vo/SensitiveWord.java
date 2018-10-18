/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.common.vo;


import java.util.Arrays;
import java.util.TreeSet;

/**
 * @author haibo
 * @since 5/22/18
 */
public class SensitiveWord {
    private String keyword;
    private int    level;
    private String scene;
    private TreeSet<String> scenes = new TreeSet<>();

    public SensitiveWord(){

    }

    public SensitiveWord(String keyword, int level, String scene){
        this.keyword = keyword;
        this.level = level;
        this.setScene(scene);

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

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
        String[] a = this.scene.split(",");
        if (a != null && a.length > 0){
            scenes.addAll(Arrays.asList(a));
        }
    }

    public boolean hitScene(String scene){
        if ("*".equals(this.scene)){
            return true;
        }
        if (scene == null){ return false; }
        return scenes.contains(scene);
    }
}
