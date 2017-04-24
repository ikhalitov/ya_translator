package ru.sportmaster.ru.translator.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.sportmaster.ru.translator.model.LangDir;
import ru.sportmaster.ru.translator.model.TransaledText;

public interface YandexApi {

    String key = "trnsl.1.1.20170330T133155Z.b0f4ee3fa6973043.f0b587934859a7bdbe76a8a572548956d6ae180e";

    String baseUrl = "https://translate.yandex.net/";

    @GET("/api/v1.5/tr.json/getLangs")
    Call<LangDir> getLangs(@Query("ui") String lang, @Query("key") String key);

    @GET("/api/v1.5/tr.json/translate")
    Call<TransaledText> translate(@Query("text") String text, @Query("lang") String lang, @Query("key") String key);


}
