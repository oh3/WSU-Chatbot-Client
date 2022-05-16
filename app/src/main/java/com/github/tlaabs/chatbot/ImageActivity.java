package com.github.tlaabs.chatbot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

// 이미지 확대 액티비티 클래스
public class ImageActivity extends AppCompatActivity {
    // 이미지를 확대할 수 있는 이미지뷰 이다.
    private SubsamplingScaleImageView zoomImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        // 비트맵으로 전달할 시...
        byte[] arr = getIntent().getByteArrayExtra("image");
        final Bitmap image = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        zoomImage = (SubsamplingScaleImageView)findViewById(R.id.zoomImage);
        zoomImage.setImage(ImageSource.bitmap(image));
    }
}