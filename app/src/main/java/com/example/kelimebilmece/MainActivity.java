package com.example.kelimebilmece;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void mainBtnClick(View v){     // 4. butonlardaki onclick özelliğini çağırdım
        switch (v.getId()){
            case R.id.main_activity_btnPlay:
                Intent playIntent =new Intent(this, PlayActivity.class);
                finish();       //aktiviteyi bitirmesi için
                startActivity(playIntent);
                overridePendingTransition(R.anim.slide_out_up, R.anim.slide_in_down);  // 6  play butonuna basıldığında bulunduğumuz aktivitenin aşağı doğru
                break;                                                                 // açılan aktivitenin yukarı doğru çıkmasını sağladık

            case R.id.main_activity_btnShop:
                break;

            case R.id.main_activity_btnExit:
                break;
        }

    }
}