package com.wyj.okhttp;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.Executors;

public class GetExample {
    private OkHttpClient client = new OkHttpClient();

    private String execute(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private void enqueue(String url) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });
    }

    public static void main(String[] args) throws IOException{
        GetExample example = new GetExample();
        String response = example.execute("https://raw.githubusercontent.com/square/okhttp/master/README.md");
        System.out.println(response);
//        Executors.newCachedThreadPool()
    }
}
