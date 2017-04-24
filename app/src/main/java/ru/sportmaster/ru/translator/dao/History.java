package ru.sportmaster.ru.translator.dao;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static ru.sportmaster.ru.translator.dao.HistoryTable.COLUMN_ID;
import static ru.sportmaster.ru.translator.dao.HistoryTable.IS_FAVORITE;
import static ru.sportmaster.ru.translator.dao.HistoryTable.LANG_CODE_1;
import static ru.sportmaster.ru.translator.dao.HistoryTable.LANG_CODE_2;
import static ru.sportmaster.ru.translator.dao.HistoryTable.TABLE;
import static ru.sportmaster.ru.translator.dao.HistoryTable.TEXT_ORIGINAL;
import static ru.sportmaster.ru.translator.dao.HistoryTable.TEXT_TRANSLATED;
import static ru.sportmaster.ru.translator.dao.HistoryTable.TIME;


@StorIOSQLiteType(table = TABLE)
public class History {

    @StorIOSQLiteColumn(name = COLUMN_ID, key = true)
    String id;

    @StorIOSQLiteColumn(name = TEXT_ORIGINAL)
    String textOriginal;

    @StorIOSQLiteColumn(name = TEXT_TRANSLATED)
    String textTranslated;

    @StorIOSQLiteColumn(name = LANG_CODE_1)
    String langCode1;

    @StorIOSQLiteColumn(name = LANG_CODE_2)
    String langCode2;

    @StorIOSQLiteColumn(name = IS_FAVORITE)
    Boolean isFavorite;

    @StorIOSQLiteColumn(name = TIME)
    Long time;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void calculateAndSetId() {
        String s = textOriginal + textTranslated + langCode1 + langCode2;
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(s.getBytes(), 0, s.length());
        setId(new BigInteger(1, m.digest()).toString(16));
    }

    public String getTextOriginal() {
        return textOriginal;
    }

    public void setTextOriginal(String textOriginal) {
        this.textOriginal = textOriginal;
    }

    public String getTextTranslated() {
        return textTranslated;
    }

    public void setTextTranslated(String textTranslated) {
        this.textTranslated = textTranslated;
    }

    public String getLangCode1() {
        return langCode1;
    }

    public void setLangCode1(String langCode1) {
        this.langCode1 = langCode1;
    }

    public String getLangCode2() {
        return langCode2;
    }

    public void setLangCode2(String langCode2) {
        this.langCode2 = langCode2;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }


    @Override
    public String toString() {
        return "History";
    }
}
