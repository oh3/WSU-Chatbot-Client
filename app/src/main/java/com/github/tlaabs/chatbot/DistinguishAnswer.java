package com.github.tlaabs.chatbot;

import static android.telephony.PhoneNumberUtils.is12Key;

import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// 2022-01-07 황우진
// 챗봇으로부터 받은 답변을 일단 item_bottext ui으로 출력한뒤, 챗봇으로부터 받은 답변을 분석하여
// 답변에 따라 연락처 앱 연결, 카카오맵 연결, 웹 미리보기 등을 추가적으로 출력한다.
// 이 클래스는 챗봇으로부터 받은 답변을 분석하기 위해 존재하는 클래스이다.
public class DistinguishAnswer {

    // 이 함수로 String에 URL이 포함되어 있는지 확인
    public static boolean containsLink(String input) {
        boolean result = false;

        String[] parts = input.split("\\s+");
        Pattern pattern = Patterns.WEB_URL;

        for (String item : parts) {
            if (android.util.Patterns.WEB_URL.matcher(item).matches()) {
                result = true;
                break;
            }
        }

        if(input.contains("http://kko.to/")){
            result = true;
        }
        return result;
    }

    // String 에 있는 URL 추출
    public static List<String> extractUrls(String input) {
        // 새로운 List 객체 생성
        List<String> result = new ArrayList<String>();

        // 인자로 들어온 데이터에서 \\s 를 기준으로 하여 나눈다.
        String[] words = input.split("\\s+");

        // 일반적인 웹의 URL 패턴 정보이다.
        Pattern pattern = Patterns.WEB_URL;
        for(String word : words)
        {
            // 만약에 word에서 WEB_URL 패턴이 발견이 되었다면
            if(pattern.matcher(word).find())
            {
                // 만약 데이터에 http 또는 https가 포함되어 있을때에만 안에 있는 구문을 실행
                if(!word.toLowerCase().contains("http://") && !word.toLowerCase().contains("https://")) {
                    word = "http://" + word;
                }
                result.add(word);
            }
        }

        return result;
    }


    // String 에서 전화번호 추출
    public static String getPhoneNumber(String str){
        String phone = "";

        // String 형태의 데이터를 CharArray 형태로 바꾼다.
        for(char c : str.toCharArray()) {
            if(is12Key(c)) { // 만약 전화번호 정규식 패턴에 사용되는 12개의 문자가 있을때에만 아래의 구문을 실행.
                phone += c;
            }
        }

        if(isValidPhoneNumber(phone)){
            return phone;
        } else{
            return null;
        }
    }

    // 전화번호\전화번호 --> 서버대답.split("\");

    // 2022-01-10 황우진
    // 전화번호인지 확인 --> 9미만, 12 초과 시 전화번호라고 인식 안함 // 처음 3자리가 042가 아닐시 전화번호라고 인식안함..
    // 일단 이 코드로 대체.
    public final static boolean isValidPhoneNumber(CharSequence target) {
        // 만약 길이가 9미만 12초과이거나 null 값일때 false 값을 리턴한다.
        if (target == null || target.length() < 9 || target.length() > 12) {
            return false;
        } else {
            // 만약 0또는 4또는 2라는 숫자가 포함되어 있을때에만 true 를 반환한다.
            if(target.charAt(0)=='0' && target.charAt(1)=='4' && target.charAt(2)=='2'){
                return true;
            } else {
                return false;
            }
        }
    }
}