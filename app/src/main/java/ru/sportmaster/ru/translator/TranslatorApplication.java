package ru.sportmaster.ru.translator;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import ru.sportmaster.ru.translator.dao.History;
import ru.sportmaster.ru.translator.dao.HistoryStorIOSQLiteDeleteResolver;
import ru.sportmaster.ru.translator.dao.HistoryStorIOSQLiteGetResolver;
import ru.sportmaster.ru.translator.dao.HistoryStorIOSQLitePutResolver;
import ru.sportmaster.ru.translator.dao.Parametr;
import ru.sportmaster.ru.translator.dao.ParametrStorIOSQLiteDeleteResolver;
import ru.sportmaster.ru.translator.dao.ParametrStorIOSQLiteGetResolver;
import ru.sportmaster.ru.translator.dao.ParametrStorIOSQLitePutResolver;
import ru.sportmaster.ru.translator.dao.ParametrTable;
import ru.sportmaster.ru.translator.dao.TranslatorSqlHelper;


public class TranslatorApplication extends Application {
    private static TranslatorApplication instance;
    private static StorIOSQLite storIOSQLite;

    public TranslatorApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TranslatorSqlHelper sqliteOpenHelper = new TranslatorSqlHelper();
        storIOSQLite = DefaultStorIOSQLite.builder().sqliteOpenHelper(sqliteOpenHelper)
                .addTypeMapping(History.class, SQLiteTypeMapping.<History>builder()
                        .putResolver(new HistoryStorIOSQLitePutResolver())
                        .getResolver(new HistoryStorIOSQLiteGetResolver())
                        .deleteResolver(new HistoryStorIOSQLiteDeleteResolver())
                        .build())
                .addTypeMapping(Parametr.class, SQLiteTypeMapping.<Parametr>builder().putResolver(new ParametrStorIOSQLitePutResolver())
                        .getResolver(new ParametrStorIOSQLiteGetResolver()).
                                deleteResolver(new ParametrStorIOSQLiteDeleteResolver()).build()).build();
        sqliteOpenHelper.getWritableDatabase();
        Parametr parametr = new Parametr();
        parametr.setParam(ParametrTable.MODIFY_DATE);
        parametr.setValue("0");
        storIOSQLite.put().object(parametr).prepare().executeAsBlocking();
        Stetho.initializeWithDefaults(this);
    }

    public static StorIOSQLite getStorIOSQLite() {
        return storIOSQLite;
    }


}
