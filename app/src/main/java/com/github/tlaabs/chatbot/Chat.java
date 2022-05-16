package com.github.tlaabs.chatbot;

// 리사이클러뷰를 위한 데이터 클래스이다.
public class Chat {
    private String Who; // user와 bot의 타입을 구별할 수 있는 String이다.
    private String Messasge; // user 또는 bot의 메시지를 담는 String이다. URL또는 연락처 등을 담기도 한다.
    private String ImageUrl; // 이미지 Url 을 저장한다.

    // 생성자.
    public Chat(String who, String messasge) {
        Who = who;
        Messasge = messasge;
    }

    // Get, Set 메서드
    public String getWho() {
        return Who;
    }

    public void setWho(String who) {
        Who = who;
    }

    public String getMessasge() {
        return Messasge;
    }

    public void setMessasge(String messasge) {
        Messasge = messasge;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
