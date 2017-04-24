package ru.sportmaster.ru.translator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ru.sportmaster.ru.translator.dao.HistoryTable;


public class HistoryListener implements View.OnClickListener {

    private Boolean isFavorite;

    private Activity from;

    private Class to;

    public HistoryListener(Boolean isFavorite, Activity from, Class to) {
        this.isFavorite = isFavorite;
        this.from = from;
        this.to = to;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(from, to);
        Bundle extras = new Bundle();
        extras.putBoolean(HistoryTable.IS_FAVORITE, isFavorite);
        intent.putExtras(extras);
        from.startActivityForResult(intent, 0);
    }


}
