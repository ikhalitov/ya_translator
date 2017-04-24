package ru.sportmaster.ru.translator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;
import com.pushtorefresh.storio.sqlite.queries.RawQuery.Builder;

import java.util.List;

import ru.sportmaster.ru.translator.adapter.HistoryAdapter;
import ru.sportmaster.ru.translator.dao.History;
import ru.sportmaster.ru.translator.dao.HistoryTable;
import ru.sportmaster.ru.translator.dao.Parametr;
import ru.sportmaster.ru.translator.dao.ParametrTable;

public class HistoryActivity extends AppCompatActivity {

    private boolean isFavorite = false;
    private TextView label;

    private ImageButton back;
    private ImageButton favorites;
    private ImageButton history;
    private ImageButton delete;
    private ListView listView;
    private List<History> historyList;

    private Long modifyDate = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new HistoryTable();
        modifyDate = getModifyDate();
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        isFavorite = bundle.getBoolean(HistoryTable.IS_FAVORITE);
        historyList = isFavorite ? loadFavorites() : loadHistory();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);
        label = (TextView) findViewById(R.id.historyOrFavoriteLabel);
        label.setText(isFavorite ? R.string.favorites : R.string.history);
        label.setBackgroundResource(isFavorite ? R.drawable.edittextstyle : R.drawable.historystyle);
        listView = (ListView) findViewById(R.id.historylist);
        listView.setAdapter(new HistoryAdapter(this, R.layout.history_layout, R.id.originalText, historyList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mainIntent = new Intent(HistoryActivity.this, MainActivity.class);
                History history = historyList.get(position);
                Bundle bundleToMain = new Bundle();
                bundleToMain.putString(HistoryTable.TEXT_ORIGINAL, history.getTextOriginal());
                bundleToMain.putString(HistoryTable.TEXT_TRANSLATED, history.getTextTranslated());
                bundleToMain.putString(HistoryTable.LANG_CODE_1, history.getLangCode1());
                bundleToMain.putString(HistoryTable.LANG_CODE_2, history.getLangCode2());
                bundleToMain.putString(MainActivity.FROM_FAVORITES_HISTORY, MainActivity.FROM_FAVORITES_HISTORY);
                mainIntent.putExtras(bundleToMain);
                HistoryActivity.this.setResult(RESULT_OK, mainIntent);
                finish();
                startActivity(mainIntent);
            }
        });
        back = (ImageButton) findViewById(R.id.homeButton);
        favorites = (ImageButton) findViewById(R.id.favoritesButton);
        history = (ImageButton) findViewById(R.id.historyButton);
        delete = (ImageButton) findViewById(R.id.deleteHistoryButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFinish = new Intent(HistoryActivity.this, MainActivity.class);
                HistoryActivity.this.setResult(RESULT_CANCELED, intentFinish);
                finish();
                startActivity(intentFinish);
            }
        });

        favorites.setOnClickListener(new HistoryListener(true, this, HistoryActivity.class));
        history.setOnClickListener(new HistoryListener(false, this, HistoryActivity.class));


        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (isFavorite) {
                            deleteFavorites();
                        } else {
                            deleteHistory();
                        }
                        listView.setAdapter(null);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setMessage(isFavorite ? R.string.delete_favorites : R.string.delete_history).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
            }
        });

    }

    private void deleteFavorites() {
        Builder builder = RawQuery.builder();
        RawQuery.CompleteBuilder query = builder.query("update history set IS_FAVORITE=0");
        TranslatorApplication.getStorIOSQLite().executeSQL().withQuery(query.build()).prepare().executeAsBlocking();
    }


    private void deleteHistory() {
        TranslatorApplication.getStorIOSQLite().delete().byQuery(DeleteQuery.builder().table(HistoryTable.TABLE)
                .where("IS_FAVORITE = 0")
                .build()).prepare().executeAsBlocking();
        modifyDate = System.currentTimeMillis();
        Parametr parametr = new Parametr();
        parametr.setParam(ParametrTable.MODIFY_DATE);
        parametr.setValue(modifyDate.toString());
        TranslatorApplication.getStorIOSQLite().put().object(parametr).prepare().executeAsBlocking();
    }

    private List<History> loadFavorites() {
        return TranslatorApplication.getStorIOSQLite().get().listOfObjects(History.class).withQuery(Query.builder()
                .table(HistoryTable.TABLE)
                .where("IS_FAVORITE = ?")
                .whereArgs(1)
                .orderBy(HistoryTable.TIME+ " desc")
                .build()).prepare().executeAsBlocking();
    }

    private List<History> loadHistory() {
        return TranslatorApplication.getStorIOSQLite().get().listOfObjects(History.class).withQuery(Query.builder()
                .table(HistoryTable.TABLE)
                .where(HistoryTable.TIME + " > ?")
                .whereArgs(modifyDate)
                .orderBy(HistoryTable.TIME+ " desc")
                .build()).prepare().executeAsBlocking();
    }

    private Long getModifyDate() {
        Parametr parametr = TranslatorApplication.getStorIOSQLite().get().object(Parametr.class).withQuery(Query.builder()
                .table(ParametrTable.TABLE)
                .where(ParametrTable.PARAM + " = ?")
                .whereArgs(ParametrTable.MODIFY_DATE)
                .build()).prepare().executeAsBlocking();
        return Long.parseLong(parametr.getValue());

    }

    public static void updateHistory(History history) {
        TranslatorApplication.getStorIOSQLite().put().object(history).prepare().executeAsBlocking();
    }

}
