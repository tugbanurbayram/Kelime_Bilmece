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

        rndSoruNumber =rndSoru.nextInt(sorularKodList.size());       // 19.  random olarak sorular?? getirdik
        rastgeleSoru = sorularList.get(rndSoruNumber);
        getRastgeleSoruKodu = sorularKodList.get(rndSoruNumber);

        textViewQuestion.setText(rastgeleSoru);

        //System.out.println("Soru Kodu = " + sorularKodList.get(rndSoruNumber));
        //System.out.println("Soru = " + sorularList.get(rndSoruNumber));

        try {
            database = this.openOrCreateDatabase("KelimeBilmece", MODE_PRIVATE, null);
            cursor=database.rawQuery("SELECT * FROM Kelimeler WHERE kKod = ?",new String[]{sorularKodList.get(rndSoruNumber)});   //20. random kod de??eri soru i??aretinin
            //yerine ge??iyor . k kod a g??re verileri ald??r??yoruz
            int kelimeIndex = cursor.getColumnIndex("kelime");

            while (cursor.moveToNext())
                kelimelerList.add(cursor.getString(kelimeIndex));

            cursor.close();
        }catch (Exception e ){
            e.printStackTrace();
        }
        rndKelimeNumber = rndKelime.nextInt(kelimelerList.size());          // 21 kelimeyi ald??m
        rastgeleKelime = kelimelerList.get(rndKelimeNumber);  //
        kelimelerList.remove(rndKelimeNumber);          //  22 i??erisindeki rastgele kelimeyi ald??ktan sonra sildik tekrar gelmemesi i??in

        //System.out.println(kelimelerList.get(rndKelimeNumber));

        for (int i=0; i<rastgeleKelime.length(); i++){         // 23 " _ " say??s??n?? belirledim
            if (i<rastgeleKelime.length()-1)
                kelimeBilgisi +="_ ";    // aralar??na bo??luk koyduk
            else
                kelimeBilgisi +="_";    // son indekste bo??luk b??rakmad??k
        }

        textViewQuest.setText(kelimeBilgisi);
        System.out.println("Gelen Kelime = " + rastgeleKelime);
        System.out.println("Gelen Kelime Harf Say??s?? = "+rastgeleKelime.length());
        kelimeHarfleri = new ArrayList<>();

        for (char harf : rastgeleKelime.toCharArray())
            kelimeHarfleri.add(harf);
                                                                            // 24 rastgele kelime uzunlu??una g??re rastgele harf belirledim
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
            if(textTahminDegeri.matches(rastgeleKelime))                                           // 25 e??le??iyorsa
                Toast.makeText(getApplicationContext(), "Do??ru Tahmin.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Yanl???? Tahmin.", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(getApplicationContext(), "Tahmin De??eri Bo?? Olamaz", Toast.LENGTH_SHORT).show();

    }
    private void setRastgeleHarfAl() {
        if (kelimeHarfleri.size() > 0){
            rndHarfNumber = rndHarf.nextInt(kelimeHarfleri.size());
            String[] txtHarfler = textViewQuest.getText().toString().split(" "); // string d??nd?????? i??in string dizinin i??ine att??k. //textviewin i??erisindeki de??erleri aralar??ndaki bo??lukklar?? sayarak alm???? olduk
            //System.out.println("Gelen Harf = " + kelimeHarfleri.get(rndHarfNumber));
            char[] gelenKelimeHarfler = rastgeleKelime.toCharArray();

            for (int i = 0; i< rastgeleKelime.length(); i++){       // uzunlugunu al??p her bir harfin i??erisinde gezebilece??iz
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