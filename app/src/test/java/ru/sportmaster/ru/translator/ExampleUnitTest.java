package ru.sportmaster.ru.translator;

import com.google.gson.Gson;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.sportmaster.ru.translator.model.LangDir;
import ru.sportmaster.ru.translator.model.TransaledText;
import ru.sportmaster.ru.translator.service.YandexApi;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testJsonTranslator() throws IOException {

        //Ретрофит
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.SECONDS)
                .connectTimeout(2, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YandexApi.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        YandexApi yaApi = retrofit.create(YandexApi.class);
        Response<TransaledText> hello = yaApi.translate("hello", "en-ru", YandexApi.key).execute();
        TransaledText transaledText= hello.body();
        System.out.println();


        Gson gson = new Gson();
        LangDir langDir = gson.fromJson(new BufferedReader(new FileReader("C:\\Temp\\android\\Translator2\\app\\src\\test\\java\\lang.json")), LangDir.class);
        String ruLang = langDir.getLangs().get("ru");
        String engLang = langDir.getLangs().get("en");
        System.out.println();
    }
}