package ru.sportmaster.ru.translator.dao;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import static ru.sportmaster.ru.translator.dao.ParametrTable.PARAM;
import static ru.sportmaster.ru.translator.dao.ParametrTable.TABLE;
import static ru.sportmaster.ru.translator.dao.ParametrTable.VALUE;

/**
 * Created by IKhalitov on 23.04.2017.
 */
@StorIOSQLiteType(table = TABLE)
public class Parametr {

    @StorIOSQLiteColumn(name = PARAM, key = true)
    String param;

    @StorIOSQLiteColumn(name = VALUE)
    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
