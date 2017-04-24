package ru.sportmaster.ru.translator;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.sportmaster.ru.translator.dao.History;
import ru.sportmaster.ru.translator.dao.HistoryTable;
import ru.sportmaster.ru.translator.dao.Parametr;
import ru.sportmaster.ru.translator.dao.ParametrTable;
import ru.sportmaster.ru.translator.model.LangDir;
import ru.sportmaster.ru.translator.model.TransaledText;
import ru.sportmaster.ru.translator.service.YandexApi;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    //объект для доступа к АПИ переводчика
    public static YandexApi yaApi;

    public static HashMap<String, String> langToCode = new HashMap<>();

    public static HashMap<String, String> codeToLang = new HashMap<>();

    public static List<String> sortedLanguage = new ArrayList<>();

    HashMap<String, String> buttonCodesMap = new HashMap<>();
    HashMap<String, Button> buttonMap = new HashMap<>();
    public static final String FROM_LANGUAGE_CHOOSE_ACTIVITY = "FROM_LANGUAGE_CHOOSE_ACTIVITY";
    public static final String FROM_FAVORITES_HISTORY = "FROM_FAVORITES_HISTORY";


    private String buttonCode1 = "1";
    private String buttonCode2 = "2";

    private Button button1;
    private Button button2;
    private Button offInetButton;
    private ImageButton history;
    private ImageButton favorites;
    private ImageButton addFavoritesButton;
    private ImageButton changeButton;
    private LinearLayout linearLayout;
    private EditText editText;
    private TextView textView;

    public static final String buttonCode = "buttonCode";
    public static final String langName = "langName";

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        new HistoryTable();
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            if (extras.getString(FROM_LANGUAGE_CHOOSE_ACTIVITY) != null) {
                //Если перешли из выбора языка
                //смотрим по коду кнопки 1 или 2, какая кнопка была использована для выбора языка
                String buttonCode = extras.getString(MainActivity.buttonCode);
                String langName = extras.getString(MainActivity.langName);
                //Определяем на какой код языка меняется кнопка
                String langCode = langToCode.get(langName);
                if (langCode == null) {
                    return;
                }
                if (isEqualsLanguage(buttonCode, langCode)) {
                    //если такой язык уже установлен то меняем их местами
                    changeLanguage();
                } else {
                    //Получаем кнопку где меняется язык
                    Button button = buttonMap.get(buttonCode);
                    button.setText(langName);
                    //устанавливаем выбранный код языка
                    buttonCodesMap.put(buttonCode, langCode);
                }
                //переводим текст
                translateText(true);
            } else if (extras.get(FROM_FAVORITES_HISTORY) != null) {
                Bundle bundle = extras;

                String code1 = bundle.getString(HistoryTable.LANG_CODE_1);
                button1.setText(codeToLang.get(code1));
                buttonCodesMap.put(buttonCode1, code1);

                String code2 = bundle.getString(HistoryTable.LANG_CODE_2);
                button2.setText(codeToLang.get(code2));
                buttonCodesMap.put(buttonCode2, code2);

                Log.e("OPPs4= ", HistoryTable.TEXT_ORIGINAL);
                Log.e("OPPs5= ", bundle.getString(HistoryTable.TEXT_ORIGINAL));

                editText.setText(bundle.getString(HistoryTable.TEXT_ORIGINAL));
                textView.setText(bundle.getString(HistoryTable.TEXT_TRANSLATED));
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private boolean isEqualsLanguage(String buttonCode, String langCode) {
        //выбираем код кнопки, в зависимости от того на какой кнопке мы меняли язык
        String anotherButtonCode = buttonCode.equals(buttonCode1) ? buttonCode2 : buttonCode1;
        //Проверяем если все-таки язык на кнопках совпал
        return langCode.equals(buttonCodesMap.get(anotherButtonCode));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //инициализируем Retrofit, языковые настройки и контент
        init();
    }

    private void init() {
        //Настройки для ретрофита
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5500, TimeUnit.MILLISECONDS)
                .connectTimeout(5500, TimeUnit.MILLISECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YandexApi.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        yaApi = retrofit.create(YandexApi.class);
        //Языковые настройки
        yaApi.getLangs(Locale.getDefault().getLanguage(), YandexApi.key).enqueue(new Callback<LangDir>() {
            @Override
            public void onResponse(Call<LangDir> call, Response<LangDir> response) {
                LangDir langDir = response.body();
                initLangAndCode(langDir);
                //Инициализируем контент
                initContent();
            }

            @Override
            public void onFailure(Call<LangDir> call, Throwable t) {
                //при отсутствии сети загружаем из файла
                Gson gson = new Gson();
                try {
                    LangDir langDir = gson.fromJson(new BufferedReader(new InputStreamReader(getBaseContext().getAssets().open("lang.json"))), LangDir.class);
                    initLangAndCode(langDir);
                    //Инициализируем контент
                    initContent();
                } catch (IOException e) {
                    //не обрабатываемая ситуация
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (editText != null) {
            editText.clearFocus();
        }
    }

    private void initContent() {
        setContentView(R.layout.activity_main);
        //инициализация кнопок
        initComponents();

        //устанавливаем обработчик на изменение текста
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                translateText(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void translateText(final boolean isSaveInHistory) {
        //получаем текст для перевода
        final String text = editText.getText().toString();
        //получаем направление перевода
        String lang = buttonCodesMap.get(buttonCode1) + "-" + buttonCodesMap.get(buttonCode2);
        if (!text.trim().isEmpty()) {

            yaApi.translate(text, lang, YandexApi.key).enqueue(new Callback<TransaledText>() {
                @Override
                public void onResponse(Call<TransaledText> call, Response<TransaledText> response) {
                    //Заполняем поле с переведенным текстом
                    setConversionVisibility(true);
                    textView.setText(response.body().getText().get(0));
                    if (isSaveInHistory) {
                        insertOrUpdateHistory(false, getHistoryObject());
                    }
                }

                @Override
                public void onFailure(Call<TransaledText> call, Throwable t) {
                    setConversionVisibility(false);
                }
            });
        } else {
            textView.setText("");
        }
    }

    private void setConversionVisibility(boolean isVisible) {
        if (isVisible) {
            textView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            addFavoritesButton.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            addFavoritesButton.setVisibility(View.GONE);
        }
    }

    private void initComponents() {
        editText = (EditText) findViewById(R.id.textfortranslate);
        textView = (TextView) findViewById(R.id.conversionTextView);
        linearLayout = (LinearLayout) findViewById(R.id.conversionOffInet);

        //инилизируем кнопки русским и английским языком
        buttonCodesMap = new HashMap<>();
        buttonCodesMap.put(buttonCode1, "ru");
        buttonCodesMap.put(buttonCode2, "en");

        favorites = (ImageButton) findViewById(R.id.favorites);
        history = (ImageButton) findViewById(R.id.history);

        button1 = (Button) findViewById(R.id.lang1);
        button1.setText(codeToLang.get(buttonCodesMap.get(buttonCode1)));
        buttonMap.put(buttonCode1, button1);

        button2 = (Button) findViewById(R.id.lang2);
        button2.setText(codeToLang.get(buttonCodesMap.get(buttonCode2)));
        buttonMap.put(buttonCode2, button2);

        offInetButton = (Button) findViewById(R.id.offInetButton);
        addFavoritesButton = (ImageButton) findViewById(R.id.addfavorites);
        changeButton = (ImageButton) findViewById(R.id.change);
        //ставим видимым окно перевода
        setConversionVisibility(true);
        offInetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateText(true);
            }
        });
        //Устанавливаем обработчики событий на кнопках
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LanguageChooseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(buttonCode, buttonCode1);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LanguageChooseActivity.class);
                Bundle extras = new Bundle();
                extras.putString(buttonCode, buttonCode2);
                intent.putExtras(extras);
                startActivityForResult(intent, 0);
            }
        });

        favorites.setOnClickListener(new HistoryListener(true, this, HistoryActivity.class) {
            @Override
            public void onClick(View v) {
                insertOrUpdateHistory(false, getHistoryObject());
                super.onClick(v);
            }
        });

        history.setOnClickListener(new HistoryListener(false, this, HistoryActivity.class) {
            @Override
            public void onClick(View v) {
                insertOrUpdateHistory(false, getHistoryObject());
                super.onClick(v);
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                changeLanguage();
            }
        });


        addFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertOrUpdateHistory(true, getHistoryObject());
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    insertOrUpdateHistory(false, getHistoryObject());
                }
            }
        });

    }


    private void changeLanguage() {
        String code1temp = buttonCodesMap.get(buttonCode1);
        String code2temp = buttonCodesMap.get(buttonCode2);
        buttonCodesMap.put(buttonCode1, code2temp);
        buttonCodesMap.put(buttonCode2, code1temp);
        button1.setText(codeToLang.get(buttonCodesMap.get(buttonCode1)));
        button2.setText(codeToLang.get(buttonCodesMap.get(buttonCode2)));
        translateText(true);
    }

    public void insertOrUpdateHistory(final boolean isFavorite, History historyObject) {
        final History history = historyObject;
        history.setFavorite(isFavorite);
        if (history.getTextOriginal().trim().isEmpty()) {
            return;
        }
        History previousHistory = TranslatorApplication.getStorIOSQLite().get().object(History.class).withQuery(Query.builder().table(HistoryTable.TABLE).where("id = ?").whereArgs(history.getId()).build()).prepare().executeAsBlocking();
        if (previousHistory != null && previousHistory.getFavorite().equals(Boolean.TRUE) && isFavorite) {
            //Объект уже есть в избранном, поэтому сообщим пользователю об этом
            showToast(getResources().getString(R.string.yet_in_save_in_favorites));
        } else {
            if (previousHistory != null && previousHistory.getFavorite().equals(Boolean.TRUE) && !isFavorite) {
                //Объект уже есть в избранном поэтому не перезаписываем его
                Long lastModifyDate = getModifyDate();
                if (previousHistory.getTime() < lastModifyDate) {
                    history.setTime(System.currentTimeMillis());
                    history.setFavorite(previousHistory.getFavorite());
                    TranslatorApplication.getStorIOSQLite().put().object(history).prepare().executeAsBlocking();
                }else{
                    previousHistory.setTime(System.currentTimeMillis());
                    TranslatorApplication.getStorIOSQLite().put().object(previousHistory).prepare().executeAsBlocking();
                }
            } else {
                //Требуется сохранить объект
                TranslatorApplication.getStorIOSQLite().put().object(history).prepare().executeAsBlocking();
                if (isFavorite) {
                    showToast(getResources().getString(R.string.save_in_favorites));
                }
            }
        }


    }

    private Long getModifyDate() {
        Parametr parametr = TranslatorApplication.getStorIOSQLite().get().object(Parametr.class).withQuery(Query.builder()
                .table(ParametrTable.TABLE)
                .where(ParametrTable.PARAM + " = ?")
                .whereArgs(ParametrTable.MODIFY_DATE)
                .build()).prepare().executeAsBlocking();
        return Long.parseLong(parametr.getValue());

    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(),
                        text,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    private History getHistoryObject() {
        History history = new History();
        history.setTextOriginal(editText.getText().toString().trim());
        history.setTextTranslated(textView.getText().toString().trim());
        history.setLangCode1(buttonCodesMap.get(buttonCode1));
        history.setLangCode2(buttonCodesMap.get(buttonCode2));
        history.setTime(System.currentTimeMillis());
        history.calculateAndSetId();
        return history;
    }

    private void initLangAndCode(LangDir langDir) {
        //Карта "код языка"- "название языка"
        MainActivity.codeToLang = langDir.getLangs();
        //Карта "название языка"- "код языка"
        HashMap<String, String> langToCode = new HashMap<>();
        for (Map.Entry<String, String> stringStringEntry : langDir.getLangs().entrySet()) {
            langToCode.put(stringStringEntry.getValue(), stringStringEntry.getKey());
        }
        MainActivity.langToCode = langToCode;
        ArrayList<String> list = new ArrayList<>(MainActivity.langToCode.keySet());
        Collections.sort(list);
        //сортированный список языков
        sortedLanguage = list;
    }

}
