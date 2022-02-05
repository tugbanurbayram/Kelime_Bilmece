package com.example.kelimebilmece;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class SplashScreenActivity extends AppCompatActivity {
    // 17  sorular için listeler oluşturdum
    private String[] sorularList = {"Mutfakta iş yaparken veya yemek yerken kullanılan aletler nelerdir?", "İç Anadolu bölgesindeki iller?"};
    private String[] sorularKodList = {"m1","s1"};

    //kelimeler için listeler
    private String[] kelimelerList = {"çatal", "bıçak", "kaşık", "tabak", "bulaşık süngeri", "tencere", "tava", "çaydanlık", "mutfak robotu", "kesme tahtası", "süzgeç", "fırın", "mixer", "mikrodalga", "ketıl",
            "Aksaray", "Ankara", "Çankırı", "Eskişehir", "Karaman", "Kayseri", "Kırıkkale", "Kırşehir", "Konya", "Nevşehir", "Niğde", "Sivas", "Yozgat"};

    private String[] kelimelerKodList = {"m1", "m1", "m1", "m1", "m1", "m1", "m1", "m1", "m1", "m1", "m1", "m1", "m1", "m1", "m1"
            , "s1", "s1", "s1", "s1", "s1", "s1", "s1", "s1", "s1", "s1", "s1", "s1", "s1"};//her bir kelimeye eşleşecek kod değeri belli olması için kelime sayısı kadar yazdık

    private ProgressBar mProgress;
    private TextView mTextView;
    private SQLiteDatabase database;
    private Cursor cursor;
    private float maksimumProgres = 100f, artacakProgress, progresMiktarı = 0;
    static public HashMap<String, String> sorularHashmap;
    private SQLiteStatement statement;
    private String sqlSorgusu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mProgress = (ProgressBar) findViewById(R.id.splash_screen_activity_progressBar);
        mTextView = (TextView) findViewById(R.id.splash_screen_activity_textViewState);
        sorularHashmap = new HashMap<>();

        try {
            database = this.openOrCreateDatabase("KelimeBilmece", MODE_PRIVATE, null);   // 9.  kelime bilmece adında veritabanı oluşturduk

            database.execSQL("CREATE TABLE IF NOT EXISTS Sorular (id INTEGER PRIMARY KEY, sKod VARCHAR UNIQUE, soru VARCHAR)"); //  10. sorular tablosunu oluşturduk. ünique yani benzersiz id atadık. içerisine eklenen bir değer bir daha eklenemiyor
            database.execSQL("DELETE FROM Sorular");                                // 11   sürekli sürekli eklenmemesi için sorular tablosundaki tüm değerleri sildi
            sqlSorulariEkle();

            database.execSQL("CREATE TABLE IF NOT EXISTS Kelimeler(kKod VARCHAR, kelime VARCHAR, FOREIGN KEY (kKod) REFERENCES Sorular (sKod))"); //  19  kKod u , sorular tablosundaki sKod a bağladık .
            database.execSQL("DELETE FROM Kelimeler");                                                                                              // bu sayede skod ile kKodu eşleşecek değerleri alabilmiş olduk
            sqlKelimeleriEkle();

            cursor = database.rawQuery("SELECT * FROM Sorular", null);              // 12 tüm kayıtları aldım
            artacakProgress = maksimumProgres / cursor.getCount(); // 13. veritabanındaki veri miktarını getcount ile aldık. maksimum progrese böldük. artacak miktarı belirledik.

            int sKodIndex = cursor.getColumnIndex("sKod"); //columnındexleri belirledik
            int soruIndex = cursor.getColumnIndex("soru");

            mTextView.setText("Sorular Yükleniyor...");   // 14. textwievin içerisine sorular yükleniyor diye mesaj yazdırdım

            while (cursor.moveToNext()) {
                sorularHashmap.put(cursor.getString(sKodIndex), cursor.getString(soruIndex));  //hasmap'te de benzersiz olacağı için hasmap haline getirdik
                progresMiktarı += artacakProgress;
                mProgress.setProgress((int)progresMiktarı);  // 15  while döngüsü içinde verileri alırken progress miktarını arttırdık
            }

            mTextView.setText("Sorular Alındı, Uygulama Başlatılıyor...");
            cursor.close();

            new CountDownTimer(1100, 1000) {


                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    finish();
                    startActivity(mainIntent);
                }
            }.start();                                  // 16  uygulamayı 1 sn içerisinde başlattık

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sqlSorulariEkle() {
        try {                                                               // 18  kullanıcıdan veri alırken veri tabanına veri eklemek için
            for (int s = 0; s < sorularList.length; s++) {
                sqlSorgusu = "INSERT INTO Sorular (sKod, soru) VALUES(?,?)"; // kaç kolum varsa o kadar soru işareti bıraktık
                statement = database.compileStatement(sqlSorgusu); // sql sorgusunu kullanılabilir hale getirdik
                statement.bindString(1, sorularKodList[s]);
                statement.bindString(2, sorularList[s]); //verileri içerisine eklemiş olduk
                statement.execute();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sqlKelimeleriEkle() {
        try {
            for (int k = 0; k < kelimelerList.length; k++) {
                sqlSorgusu = "INSERT INTO Kelimeler (kKod, kelime) VALUES(?,?)";
                statement = database.compileStatement(sqlSorgusu);
                statement.bindString(1, kelimelerKodList[k]);
                statement.bindString(2, kelimelerList[k]);
                statement.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}