package ru.sportmaster.ru.translator.dao;

import android.support.annotation.NonNull;

public class ParametrTable {

    @NonNull
    public static final String TABLE = "parametr";


    @NonNull
    public static final String PARAM = "PARAM";

    @NonNull
    public static final String VALUE = "VALUE";

    @NonNull
    public static final String MODIFY_DATE = "MODIFY_DATE";


    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + PARAM + " TEXT NOT NULL, "
                + VALUE + " TEXT NOT NULL "
                + ");";
    }
}
