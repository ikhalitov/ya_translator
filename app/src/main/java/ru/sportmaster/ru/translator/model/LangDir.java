package ru.sportmaster.ru.translator.model;

import java.util.HashMap;
import java.util.List;


public class LangDir {


    private HashMap<String, String> langs;
    private List<String> dirs;

    public LangDir() {
    }

    public LangDir(HashMap<String, String> langs, List<String> dirs) {
        this.langs = langs;
        this.dirs = dirs;
    }

    public HashMap<String, String> getLangs() {
        return langs;
    }

    public void setLangs(HashMap<String, String> langs) {
        this.langs = langs;
    }

    public List<String> getDirs() {
        return dirs;
    }

    public void setDirs(List<String> dirs) {
        this.dirs = dirs;
    }

    @Override
    public String toString() {
        return "LangDir{" +
                "langs=" + langs +
                ", dirs=" + dirs +
                '}';
    }
}
