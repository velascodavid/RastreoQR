package com.example.rastreoqr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class EscanearActivity extends AppCompatActivity{

    String empresa="", fechahora="", celular;
    private ZXingScannerView mScannerView;
    Context context;
    Button btGuardar;

    EditText etNombre, etFecha, etIdEmpresa, etDireccion;

    RequestQueue rq;
    JsonRequest jrq;

    ProgressDialog progreso;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear);

        rq = Volley.newRequestQueue(getApplicationContext());

        etIdEmpresa = (EditText)findViewById(R.id.etIdEmpresa);
        etDireccion = (EditText)findViewById(R.id.etDomicilio);
        etNombre = (EditText)findViewById(R.id.etNombreEmpresa);
        etFecha = (EditText)findViewById(R.id.etFechaVisita);
        btGuardar = (Button)findViewById(R.id.btGuardar);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null){
                //etNombre.setText(result.getContents());
                Date cal = (Date) Calendar.getInstance().getTime();
                fechahora=cal.toString();
                etFecha.setText(fechahora);
                empresa = result.getContents();

                String [] datos = empresa.split("/");
                etIdEmpresa.setText(datos[0]);
                etNombre.setText(datos[1]);
                etDireccion.setText(datos[2]);

                btGuardar.setEnabled(true);


            }else{
                Toast.makeText(this, "Ocurrió un error al escanear. Inténtalo de nuevo", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void btnEscanear (View view){



        new IntentIntegrator(EscanearActivity.this).initiateScan();

        /*mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();*/
    }


    /*@Override
    public void handleResult(com.google.zxing.Result result) {
        etNombre = (EditText)findViewById(R.id.etNombreEmpresa);
        etFecha = (EditText)findViewById(R.id.etFechaVisita);

        Log.v("HandleResult", result.getText());
        Toast toast1=Toast.makeText(this,"Resultado del escaner", Toast.LENGTH_LONG);
        toast1.show();
        Toast toast2=Toast.makeText(this,result.getText(), Toast.LENGTH_SHORT);
        //etNombre.setText(result.getText());
        toast2.show();
        //Toast toast3=Toast.makeText(this,getPhoneNumber(), Toast.LENGTH_SHORT);
        //toast3.show();

        String dt;
        Date cal = (Date) Calendar.getInstance().getTime();
        Toast toast4=Toast.makeText(this,cal.toString(), Toast.LENGTH_SHORT);
        toast4.show();
        Log.e("handler", result.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        empresa=result.getText();

        //etNombre.setText(empresa);
        etFecha.setText(fechahora);

        fechahora=cal.toString();

        mScannerView.resumeCameraPreview(this);



        //Una opción
        mScannerView.resumeCameraPreview(this);

        mScannerView.stopCameraPreview();

        mScannerView.stopCamera();

        //this.finish();
       // etNombre.setText(result.getText());
        etFecha.setText(fechahora);

        //setContentView(R.layout.activity_escanear);
        etNombre.setText(result.getText());

    }

    @Override
    protected void onResume() {
        //etNombre.setText("empresa" + empresa);
        //etFecha.setText(fechahora);
        super.onResume();


    }*/

    public void guardarBitacora(){

        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        celular = preferences.getString("celular", "");

        if(!celular.isEmpty()){

            //Itcg-2020


            progreso=new ProgressDialog(EscanearActivity.this);
            progreso.setMessage("Cargando...");
            progreso.setCancelable(false);
            progreso.show();

            String url = "http://davidvelasco.com.mx/AppCovid/RegistraBitacora.php?";

            stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progreso.hide();

                    if (response.trim().equalsIgnoreCase("registra")) {
                        Toast.makeText(EscanearActivity.this, "Registro correcto", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(EscanearActivity.this, "La empresa no se encuentra activa.", Toast.LENGTH_LONG).show();
                        btGuardar.setEnabled(true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progreso.hide();

                    btGuardar.setEnabled(true);

                    Toast.makeText(EscanearActivity.this, "Hubo un error al conectarse al servidor. Revisa tu conexión e inténtalo de nuevo", Toast.LENGTH_LONG).show();

                    /*Snackbar snackbar = Snackbar.make(btGuardar, "Hubo un error al conectarse al servidor. Revisa tu conexión e inténtalo de nuevo", Snackbar.LENGTH_LONG);
                    snackbar.show();*/

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError{
                    String IdEmpresa = etIdEmpresa.getText().toString();
                    String Cel = celular;
                    String FechaVisita = etFecha.getText().toString();

                    Map<String, String> parametros = new HashMap<>();

                    parametros.put("IdEmpresa", IdEmpresa);
                    parametros.put("Celular", Cel);
                    parametros.put("Fecha", FechaVisita);

                    return parametros;

                }
            };

            rq.add(stringRequest);

        }

    }

    public void btGuardar(View view){

        btGuardar.setEnabled(false);
        guardarBitacora();
        //etNombre.setText(empresa);
        //etFecha.setText(fechahora);
        String x="ZAPOTLÁN EL GRANDE  ITCG-Conciencia-COVID\n"+"Estás visitando a la empresa: "+empresa+"\n"+" BIENVENIDO, programa de rastreo COVID \n"+"Se registra el celular: "+341+" GRACIAS POR ATENDER el programa de rastreo COVID";
        Toast toast4=Toast.makeText(this, x,Toast.LENGTH_SHORT);
        //toast4.show();
    }
}
