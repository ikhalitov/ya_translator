package ru.sportmaster.ru.translator.dao;

import android.support.annotation.NonNull;

public class HistoryTable {

    @NonNull
    public static final String TABLE = "history";

    @NonNull
    public static final String COLUMN_ID = "id";

    @NonNull
    public static final String TEXT_ORIGINAL = "TEXT_ORIGINAL";

    @NonNull
    public static final String TEXT_TRANSLATED = "TEXT_TRANSLATED";

    @NonNull
    public static final String LANG_CODE_1 = "LANG_CODE_1";

    @NonNull
    public static final String LANG_CODE_2 = "LANG_CODE_2";

    @NonNull
    public static final String IS_FAVORITE = "IS_FAVORITE";

    @NonNull
    public static final String TIME = "TIME";

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " STRING NOT NULL PRIMARY KEY, "
                + TEXT_ORIGINAL + " TEXT NOT NULL, "
                + TEXT_TRANSLATED + " TEXT NOT NULL ,"
                + LANG_CODE_1 + " TEXT NOT NULL ,"
                + LANG_CODE_2 + " TEXT NOT NULL ,"
                + TIME + " INTEGER NOT NULL ,"
                + IS_FAVORITE + " INTEGER NOT NULL DEFAULT 0"
                + ");";
    }


}
