package com.github.tlaabs.chatbot;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

// 어플리케이션 내부 폴더 안에 데이터를 파일로 저장하게 하는 클래스
public class PreferenceManager {
    // 해당하는 SharedPreferences의 이름이다.
    public static final String PREFERENCES_NAME = "rebuild_preference";
    private static final boolean DEFAULT_VALUE_BOOLEAN = false;

    // SharedPreferences 객체를 얻어온다.
    private static SharedPreferences getPreferences(Context context) {
        // 첫번째 인자 : 해당하는 SharedPreferences의 이름
        // 두번째 인자 : 자신의 앱 내부에서만 사용 가능
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }


    /*
       boolean 값 저장
     * @param context
     * @param key
     * @param value
     */
    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /*
    boolean 값 로드
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        boolean value = prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN);
        return value;
    }


    public static void removeKey(Context context, String key) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(key);
        edit.commit();
    }


    public static void clear(Context context) {
        SharedPreferences prefs = getPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
}
