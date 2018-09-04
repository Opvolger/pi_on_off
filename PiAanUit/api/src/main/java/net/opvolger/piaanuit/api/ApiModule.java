package net.opvolger.piaanuit.api;

import android.util.Log;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ApiModule {

    public String url = "";
    private OkHttpClient okHttpClient;
    private static ApiModule instance;

    private ApiModule() {
        this.okHttpClient = new OkHttpClient.Builder().build();
        try {
            run();
        } catch (Exception e) {

        }
    }

    public void run() throws Exception {
        Request request = new Request.Builder()
                .url("https://www.opvolger.net/online.php")
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (ApiModule.this.url.equals("")) {
                    ApiModule.this.url = "http://" + response.body().string() + ":8080/api/Light/";
                }
            }
        });
    }


    public static void resetInstance()
    {
        instance = null;
    }

    public static ApiModule getInstance() throws Exception {
        if (instance == null)
        {
            instance = new ApiModule();
            if (Objects.equals(instance.url, ""))
            {
                instance.run();
            }
        }
        return instance;
    }

    public Call post(String action, Callback callback) {

        String endpoint = this.url + action;

        Request request = new Request.Builder()
                .url(this.url + action)
                .get()
                .header("User-Agent", "OKCCNL Android")
                .header("Source", "App.Android")
                .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
                .build();
        Log.d("PIAANUIT", "Ik doe een callback call " + endpoint);
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
        return call;
    }
}
