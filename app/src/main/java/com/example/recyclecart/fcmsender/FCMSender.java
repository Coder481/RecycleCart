package com.example.recyclecart.fcmsender;

import com.example.recyclecart.R;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FCMSender {

    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send",
            KEY_STRING = "key=YOUR_SERVER_KEY_HERE";
    

    OkHttpClient client = new OkHttpClient();
    public void send(String message, Callback callback){
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(FCM_URL)
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization",KEY_STRING)
                .post(requestBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

}
