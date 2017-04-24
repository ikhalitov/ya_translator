package ru.sportmaster.ru.translator.dao;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.sportmaster.ru.translator.TranslatorApplication;


public class TranslatorSqlHelper extends SQLiteOpenHelper {

    public TranslatorSqlHelper() {
        super(TranslatorApplication.getContext(), "ya_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HistoryTable.getCreateTableQuery());
        db.execSQL(ParametrTable.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
