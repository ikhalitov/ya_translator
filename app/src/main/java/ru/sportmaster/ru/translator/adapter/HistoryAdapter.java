package ru.sportmaster.ru.translator.adapter;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.sportmaster.ru.translator.HistoryActivity;
import ru.sportmaster.ru.translator.R;
import ru.sportmaster.ru.translator.dao.History;


public class HistoryAdapter extends ArrayAdapter<History> {

    private List<History> historyList;

    private Activity context;

    public HistoryAdapter(@NonNull Activity context, @LayoutRes int resource, @IdRes int textViewResourceId, List<History> historyList) {
        super(context, resource, textViewResourceId, historyList);
        this.historyList = historyList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflator = context.getLayoutInflater();
        convertView = inflator.inflate(R.layout.history_layout, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.favoritesIcon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView1 = (ImageView) v;
                History history = historyList.get(position);
                boolean isFavorite = history.getFavorite();
                if (isFavorite) {
                    imageView1.setImageResource(R.drawable.unfavorites);
                    history.setFavorite(false);
                    HistoryActivity.updateHistory(history);
                } else {
                    imageView1.setImageResource(R.drawable.favorites);
                    history.setFavorite(true);
                    HistoryActivity.updateHistory(history);
                }
            }
        });
        History history = historyList.get(position);
        if (history.getFavorite()) {
            imageView.setImageResource(R.drawable.favorites);
        } else {
            imageView.setImageResource(R.drawable.unfavorites);
        }
        TextView originalTextView = (TextView) convertView.findViewById(R.id.originalText);
        originalTextView.setText(history.getTextOriginal());

        TextView translatedTextView = (TextView) convertView.findViewById(R.id.translatedText);
        translatedTextView.setText(history.getTextTranslated());

        TextView direction = (TextView) convertView.findViewById(R.id.directionTextView);
        direction.setText("\r\n" + history.getLangCode1() + "-" + history.getLangCode2());

        return convertView;
    }
}
