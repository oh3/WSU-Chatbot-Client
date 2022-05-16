package com.github.tlaabs.chatbot;


import org.json.JSONException;
import org.json.JSONObject;


import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

// 서버에 연결하여 답변을 받는 클래스이다.
public class ServerRequest {

    // 서버 IP주소 목록
    // 192.168.0.14
    // 192.168.0.7
    // 112.150.84.136
    // search

    // 서버에 연결하기 위한 URL 주소를 저장한다.
    private static String URL = "http://" + "192.168.0.14" + ":" + 5000 + "/query/";

    // 함수 내부적으로 사용하는 함수.
    // 통신하는데에 필요한 RequestBody를 만드는데에 필요한 함수이다.
    private static RequestBody buildRequestBody(String msg) {
        // JSON 객체 생성
        JSONObject jsonInput = new JSONObject();
        try {
            // query 라는 이름의 인자로 들어온 메세지 데이터를 JSON 객체에 저장한다.
            jsonInput.put("query", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 위에서 만든 JSON 객체 데이터를 바탕으로 RequestBody 객체를 만든다.
        RequestBody reqBody = RequestBody.create(
                // json 형태의 데이터를 utf-8 의 형식으로
                MediaType.parse("application/json; charset=utf-8"),
                jsonInput.toString()
        );

        return reqBody;
    }

    // 서버에 연결하는함수. 첫번째 함수는 어떤 데이터를 원하냐에 따라서 조금씩 달라진다.
    public static void postRequest(String urlType, String message, Callback callback) {
        // RequestBody 객체 생성
        RequestBody requestBody = buildRequestBody(message);

        // 요청을 전송할 OkHttpClient 객체 생성
        OkHttpClient okHttpClient = new OkHttpClient();

        // Timeout 시간을 셋팅하고 빌드한 객체를 저장한다.
        okHttpClient = okHttpClient.newBuilder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .build();

        // Request 객체를 생성하여 저장한다.
        Request request = new Request
                .Builder()
                .post(requestBody) // 위에서 만든 RequestBody 객체를 인자로 보낸다. 이때, post() 메서드를 통하여 post 요청임을 명시한다.
                .url(URL + urlType) // 어떤 데이터를 원하냐에 따라서 URL 이 조금씩 달라진다.
                .build();

        // 비동기식으로 실행하여 서버의 연결을 기다린다. 콜백 함수는 이 함수의 세번째 인자를 사용한다.
        okHttpClient.newCall(request).enqueue(callback);
    }

}