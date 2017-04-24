package ru.sportmaster.ru.translator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

public class LanguageChooseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        final Intent intent = getIntent();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, MainActivity.sortedLanguage.toArray(new String[]{}));
        ListView listView = (ListView) findViewById(R.id.languageList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentFinish = new Intent(LanguageChooseActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(MainActivity.buttonCode, (String) intent.getExtras().get(MainActivity.buttonCode));
                bundle.putString(MainActivity.langName, ((AppCompatTextView) view).getText().toString());
                bundle.putString(MainActivity.FROM_LANGUAGE_CHOOSE_ACTIVITY, "OK");
                intentFinish.putExtras(bundle);
                LanguageChooseActivity.this.setResult(RESULT_OK, intentFinish);
                finish();
                startActivity(intentFinish);
            }
        });

        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButtonInLanguageBack);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFinish = new Intent();
                LanguageChooseActivity.this.setResult(RESULT_CANCELED, intentFinish);
                finish();
            }
        });
    }
}
