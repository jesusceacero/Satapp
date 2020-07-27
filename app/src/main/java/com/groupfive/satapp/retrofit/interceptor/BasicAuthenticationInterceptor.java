package com.groupfive.satapp.retrofit.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BasicAuthenticationInterceptor implements Interceptor {

    private String authToken;

    public BasicAuthenticationInterceptor(String token) {
        this.authToken = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("Authorization", authToken);

        Request request = builder.build();
        return chain.proceed(request);
    }
}