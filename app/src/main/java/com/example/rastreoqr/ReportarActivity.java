package com.example.rastreoqr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReportarActivity extends AppCompatActivity {

    EditText etFechaSintomas;
    Button btFecha, btReportar;

    String celular;

    Calendar c;
    DatePickerDialog dpd;

    RequestQueue rq;
    JsonRequest jrq;

    ProgressDialog progreso;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportar);

        etFechaSintomas = (EditText)findViewById(R.id.etFechaSintomas);
        btFecha = (Button)findViewById(R.id.btFecha);
        btReportar= (Button)findViewById(R.id.btReportar);

        rq = Volley.newRequestQueue(getApplicationContext());


        btFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                final int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);


                dpd = new DatePickerDialog(ReportarActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                        etFechaSintomas.setText(mYear + "/" + (mMonth+1) + "/" + mDay);

                    }
                }, year, month, day);

                dpd.show();

            }
        });

        btReportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarReporte();
            }
        });


    }

    public void guardarReporte(){
        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        celular = preferences.getString("celular", "");

        if(!celular.isEmpty()){

            //Itcg-2020


            progreso=new ProgressDialog(ReportarActivity.this);
            progreso.setMessage("Cargando...");
            progreso.setCancelable(false);
            progreso.show();

            String url = "http://davidvelasco.com.mx/AppCovid/RegistrarPositivo.php?";

            stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progreso.hide();
                    if (response.trim().equalsIgnoreCase("registra")) {
                        Toast.makeText(ReportarActivity.this, "Registro correcto", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ReportarActivity.this, "Ocurrió un error. Inténtalo de nuevo.", Toast.LENGTH_LONG).show();
                        btReportar.setEnabled(true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progreso.hide();

                    btReportar.setEnabled(true);

                    Toast.makeText(ReportarActivity.this, "Hubo un error al conectarse al servidor. Revisa tu conexión e inténtalo de nuevo", Toast.LENGTH_LONG).show();

                    /*Snackbar snackbar = Snackbar.make(btGuardar, "Hubo un error al conectarse al servidor. Revisa tu conexión e inténtalo de nuevo", Snackbar.LENGTH_LONG);
                    snackbar.show();*/

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    String Cel = celular;
                    String FechaSintomas = etFechaSintomas.getText().toString();

                    Map<String, String> parametros = new HashMap<>();

                    parametros.put("FechaInicial", FechaSintomas);
                    parametros.put("Celular", Cel);

                    return parametros;

                }
            };

            rq.add(stringRequest);

        }
    }


}
