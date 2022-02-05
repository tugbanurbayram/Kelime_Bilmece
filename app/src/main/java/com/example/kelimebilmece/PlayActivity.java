package com.example.kelimebilmece;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    private TextView textViewQuestion, textViewQuest;
    private EditText editTextTahminDegeri;
    private SQLiteDatabase database;
    private Cursor cursor;
    private ArrayList<String> sorularList;
    private ArrayList<String> sorularKodList;
    private ArrayList<String> kelimelerList;
    private ArrayList<Character> kelimeHarfleri;

    private Random rndSoru, rndKelime, rndHarf;
    private int rndSoruNumber, rndKelimeNumber, rndHarfNumber;
    private String rastgeleSoru, getRastgeleSoruKodu, rastgeleKelime, kelimeBilgisi = "", textTahminDegeri;
    private int rastgeleBelirlenecekHarfSayisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        textViewQuestion = (TextView)findViewById(R.id.play_activity_textViewQuestion);
        textViewQuest = (TextView)findViewById(R.id.play_activity_textViewQuest);
        editTextTahminDegeri =(EditText)findViewById(R.id.play_activity_editTextGuess);
        sorularList = new ArrayList<>();
        sorularKodList = new ArrayList<>();
        kelimelerList= new ArrayList<>();
        rndSoru = new Random();
        rndKelime = new Random();
        rndHarf = new Random();

        for (Map.Entry soru : SplashScreenActivity.sorularHashmap.entrySet()){
            sorularList.add(String.valueOf(soru.getValue()));
            sorularKodList.add(String.valueOf(soru.getKey()));
        }

        rndSoruNumber =rndSoru.nextInt(sorularKodList.size());       // 19.  random olarak soruları getirdik
        rastgeleSoru = sorularList.get(rndSoruNumber);
        getRastgeleSoruKodu = sorularKodList.get(rndSoruNumber);

        textViewQuestion.setText(rastgeleSoru);

        //System.out.println("Soru Kodu = " + sorularKodList.get(rndSoruNumber));
        //System.out.println("Soru = " + sorularList.get(rndSoruNumber));

        try {
            database = this.openOrCreateDatabase("KelimeBilmece", MODE_PRIVATE, null);
            cursor=database.rawQuery("SELECT * FROM Kelimeler WHERE kKod = ?",new String[]{sorularKodList.get(rndSoruNumber)});   //20. random kod değeri soru işaretinin
            //yerine geçiyor . k kod a göre verileri aldırıyoruz
            int kelimeIndex = cursor.getColumnIndex("kelime");

            while (cursor.moveToNext())
                kelimelerList.add(cursor.getString(kelimeIndex));

            cursor.close();
        }catch (Exception e ){
            e.printStackTrace();
        }
        rndKelimeNumber = rndKelime.nextInt(kelimelerList.size());          // 21 kelimeyi aldım
        rastgeleKelime = kelimelerList.get(rndKelimeNumber);  //
        kelimelerList.remove(rndKelimeNumber);          //  22 içerisindeki rastgele kelimeyi aldıktan sonra sildik tekrar gelmemesi için

        //System.out.println(kelimelerList.get(rndKelimeNumber));

        for (int i=0; i<rastgeleKelime.length(); i++){         // 23 " _ " sayısını belirledim
            if (i<rastgeleKelime.length()-1)
                kelimeBilgisi +="_ ";    // aralarına boşluk koyduk
            else
                kelimeBilgisi +="_";    // son indekste boşluk bırakmadık
        }

        textViewQuest.setText(kelimeBilgisi);
        System.out.println("Gelen Kelime = " + rastgeleKelime);
        System.out.println("Gelen Kelime Harf Sayısı = "+rastgeleKelime.length());
        kelimeHarfleri = new ArrayList<>();

        for (char harf : rastgeleKelime.toCharArray())
            kelimeHarfleri.add(harf);
                                                                            // 24 rastgele kelime uzunluğuna göre rastgele harf belirledim
        if (rastgeleKelime.length() >= 5 && rastgeleKelime.length() <=7)
            rastgeleBelirlenecekHarfSayisi =1;
        else if (rastgeleKelime.length() >=8 && rastgeleKelime.length() <= 10)
            rastgeleBelirlenecekHarfSayisi = 2;
        else if (rastgeleKelime.length() >=11 && rastgeleKelime.length() <=14)
            rastgeleBelirlenecekHarfSayisi = 3;
        else if (rastgeleKelime.length() >= 15)
            rastgeleBelirlenecekHarfSayisi = 4;
        else
            rastgeleBelirlenecekHarfSayisi = 0;

        for (int i =0 ; i <rastgeleBelirlenecekHarfSayisi; i++  )
            setRastgeleHarfAl();
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_out_up,R.anim.slide_in_down);
    }

    public void btnHarfAl(View v){
        setRastgeleHarfAl();
    }
    public void btnTahminEt(View v){
        textTahminDegeri = editTextTahminDegeri.getText().toString();

        if (!TextUtils.isEmpty(textTahminDegeri)){
            if(textTahminDegeri.matches(rastgeleKelime))                                           // 25 eşleşiyorsa
                Toast.makeText(getApplicationContext(), "Doğru Tahmin.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Yanlış Tahmin.", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(getApplicationContext(), "Tahmin Değeri Boş Olamaz", Toast.LENGTH_SHORT).show();

    }
    private void setRastgeleHarfAl() {
        if (kelimeHarfleri.size() > 0){
            rndHarfNumber = rndHarf.nextInt(kelimeHarfleri.size());
            String[] txtHarfler = textViewQuest.getText().toString().split(" "); // string döndüğü için string dizinin içine attık. //textviewin içerisindeki değerleri aralarındaki boşlukkları sayarak almış olduk
            //System.out.println("Gelen Harf = " + kelimeHarfleri.get(rndHarfNumber));
            char[] gelenKelimeHarfler = rastgeleKelime.toCharArray();

            for (int i = 0; i< rastgeleKelime.length(); i++){       // uzunlugunu alıp her bir harfin içerisinde gezebileceğiz
                if (txtHarfler[i].equals("_") && gelenKelimeHarfler[i] == kelimeHarfleri.get(rndHarfNumber)){
                    txtHarfler[i] = String.valueOf(kelimeHarfleri.get(rndHarfNumber));
                    kelimeBilgisi = "";

                    for (int j= 0; j< txtHarfler.length; j++){
                        if (j< txtHarfler.length - 1 )
                            kelimeBilgisi += txtHarfler[j] + " ";
                        else
                            kelimeBilgisi += txtHarfler[j];
                    }
                    break;
                }
            }
            textViewQuest.setText(kelimeBilgisi);
            kelimeHarfleri.remove(rndHarfNumber);
        }
    }

}