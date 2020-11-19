package com.example.rastreoqr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private Window window;
    private EditText etDia, etAnio;
    private Spinner spMeses, spGenero;
    private Button btRegistrarse;
    String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    String[] generos = {"Masculino", "Femenino"};
    String noCelular = "";
    String fechaNacimiento = "";
    String genero = "";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);
        this.window = getWindow();
        window.setStatusBarColor(Color.argb(255,165,39,63));
        window.setNavigationBarColor(Color.argb(255,165,39,63));
        spMeses = (Spinner)findViewById(R.id.spMes);
        spGenero = (Spinner)findViewById(R.id.spGenero);
        etDia = (EditText)findViewById(R.id.etDia);
        etAnio = (EditText)findViewById(R.id.etAnio);
        btRegistrarse = (Button)findViewById(R.id.btRegistrarse);
        ArrayAdapter adapter = new ArrayAdapter<String>(RegistroUsuarioActivity.this, android.R.layout.simple_list_item_1, meses);
        ArrayAdapter adapter2 = new ArrayAdapter<String>(RegistroUsuarioActivity.this, android.R.layout.simple_list_item_1, generos);
        spMeses.setAdapter(adapter);
        spGenero.setAdapter(adapter2);
        Intent intent = getIntent();
        noCelular = intent.getStringExtra("celular");
    }

    public void registrarse(View view){
        if(validacion()) {
            fechaNacimiento = etDia.getText().toString() + "/" + spMeses.getSelectedItem().toString() + "/" + etAnio.getText().toString();
            genero = spGenero.getSelectedItem().toString();
            SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("celular", noCelular);
            editor.putString("fechaNacimiento", fechaNacimiento);
            editor.putString("genero", genero);
            editor.commit();
            Intent intent = new Intent(RegistroUsuarioActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public boolean validacion(){
        String dia = etDia.getText().toString();
        String año = etAnio.getText().toString();
        if(dia.isEmpty() && año.isEmpty()){
            etDia.setError("Campo requerido");
            etAnio.setError("Campo requerido");
            return false;
        }else if(dia.isEmpty()) {
            etDia.setError("Campo requerido");
            return false;
        }else if(año.isEmpty()){
            etAnio.setError("Campo requerido");
            return false;
        }
        return true;
    }

    @Override public void onBackPressed() { moveTaskToBack(true); }
}
