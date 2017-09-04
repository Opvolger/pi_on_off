package net.opvolger.piaanuit.api;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;

public abstract class BaseCallback implements Callback {
    protected Context context;
    protected Handler mainHandler;

    protected abstract class RunUI implements Runnable
    {
        protected String data;

        public RunUI(String data)
        {
            this.data = data;
        }
    }

    public BaseCallback(Context context)
    {
        this.context = context;
        this.mainHandler = new Handler(context.getMainLooper());
    }

    @Override
    public void onFailure(final Call call, IOException e) {
        Log.d("CALLAPI", "Er is iets misgegaan" + e);
        this.mainHandler.post(new RunUI(null) {
            @Override
            public void run() {
                if (!call.isCanceled()) {
                    Toast.makeText(context, "Er is iets misgegaan!", Toast.LENGTH_LONG).show();
                }

            }
        });

        this.onFailure();
    }

    @Override
    public void onResponse(Call call, okhttp3.Response response) throws IOException {
        String rawJsonData = response.body().string();
        Log.d("CALLAPI", "Ik heb antwoord " + rawJsonData);
        try {
            this.onSuccess(rawJsonData);
        }
        catch (Exception e)
        {
            this.onFailure();
        }
    }

    public abstract void onSuccess(String result);

    public abstract void onFailure();
}
