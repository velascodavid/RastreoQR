package com.example.rastreoqr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

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
    int mes;


    RequestQueue rq;
    JsonRequest jrq;

    ProgressDialog progreso;
    StringRequest stringRequest;

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

        rq = Volley.newRequestQueue(getApplicationContext());
    }

    public void registrarse(View view){

        final String gen;

        //Año-mes-día
        if(validacion()) {

            if(spGenero.getSelectedItem().toString() == "Masculino"){
                gen ="M";
            }else{
                gen = "F";
            }

            switch(spMeses.getSelectedItem().toString()){
                case "Enero":
                {
                    mes=1;
                    break;
                }
                case "Febrero":
                {
                    mes=2;
                    break;
                }
                case "Marzo":
                {
                    mes=3;
                    break;
                }
                case "Abril":
                {
                    mes=4;
                    break;
                }
                case "Mayo":
                {
                    mes=5;
                    break;
                }
                case "Junio":
                {
                    mes=6;
                    break;
                }
                case "Julio":
                {
                    mes=7;
                    break;
                }
                case "Agosto":
                {
                    mes=8;
                    break;
                }
                case "Septiembre":
                {
                    mes=9;
                    break;
                }
                case "Octubre":
                {
                    mes=10;
                    break;
                }
                case "Noviembre":
                {
                    mes=11;
                    break;
                }
                case "Diciembre":
                {
                    mes=12;
                    break;
                }
            }



            progreso=new ProgressDialog(RegistroUsuarioActivity.this);
            progreso.setMessage("Cargando...");
            progreso.setCancelable(false);
            progreso.show();

            String url = "http://davidvelasco.com.mx/AppCovid/RegistraUsuario.php?";

            stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progreso.hide();

                    if (response.trim().equalsIgnoreCase("registra")) {
                        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("celular", noCelular);
                        editor.putString("fechaNacimiento", fechaNacimiento);
                        editor.putString("genero", genero);
                        editor.commit();
                        Intent intent = new Intent(RegistroUsuarioActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(RegistroUsuarioActivity.this, "¡Registrado con éxito!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegistroUsuarioActivity.this, "Ocurrió un error. Verifica tus datos e inténtalo de nuevo.", Toast.LENGTH_LONG).show();
                        btRegistrarse.setEnabled(true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progreso.hide();

                    btRegistrarse.setEnabled(true);

                    Toast.makeText(RegistroUsuarioActivity.this, "Hubo un error al conectarse al servidor. Revisa tu conexión e inténtalo de nuevo", Toast.LENGTH_LONG).show();

                    /*Snackbar snackbar = Snackbar.make(btGuardar, "Hubo un error al conectarse al servidor. Revisa tu conexión e inténtalo de nuevo", Snackbar.LENGTH_LONG);
                    snackbar.show();*/

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    fechaNacimiento = etAnio.getText().toString() + "/" + mes + "/" + etDia.getText().toString();

                    System.out.println("fechaNac:" + fechaNacimiento);

                    String FechaNac = fechaNacimiento;
                    String Cel = noCelular;

                    Map<String, String> parametros = new HashMap<>();

                    parametros.put("FechaNacimiento", fechaNacimiento);
                    parametros.put("Celular", Cel);
                    parametros.put("Genero", gen);

                    return parametros;

                }
            };

            rq.add(stringRequest);




            //fechaNacimiento = etDia.getText().toString() + "/" + spMeses.getSelectedItem().toString() + "/" + etAnio.getText().toString();

            //genero = spGenero.getSelectedItem().toString();

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
