package com.example.matjeusz.opencv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Logowanie extends AppCompatActivity {

    Button ip;
    EditText edit;
    ListView listaPing;
    public String value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logowanie);
        ip = findViewById(R.id.potwierdz);
        edit = findViewById(R.id.IPadres);
        listaPing = findViewById(R.id.adapterLista);

    }

    public String fExecutarPing(){
        Editable host = edit.getText();
        value = edit.getText().toString();
        List<String> listaResponstaPing = new ArrayList<String>();


        ArrayAdapter<String> adapterLista = new  ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                listaResponstaPing);



        try {

            String cmdPing = "ping -c 4 "+host;
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(cmdPing);
            BufferedReader in = new BufferedReader(	new InputStreamReader(p.getInputStream()));
            String inputLinhe;

            while((inputLinhe = in.readLine())!= null){
                listaResponstaPing.add(inputLinhe);
                //adiciona para cada linha
                listaPing.setAdapter(adapterLista);
            }
            Toast.makeText(this, "Test PING wykonał się", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Toast.makeText(this, "Coś poszło nie tak"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        return value;
    }

    public void dalej (String value){
        Intent intent = new Intent(this, PloterMain.class);
        System.out.print(value);
        intent.putExtra("sprawdzenie",value);
        this.startActivity(intent);
        
    }


    public void przycisk1(View view)
    {
        fExecutarPing();
    }

    public void przycisk2(View view)
    {
        dalej(value);
    }


}
