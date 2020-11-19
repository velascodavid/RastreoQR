package com.example.rastreoqr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

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
import android.widget.Button;
import android.widget.TextView;


import com.google.android.material.snackbar.Snackbar;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity {

    CardView cvQR;

    private Window window;
    private Button btIniciar;

    TextView tvBienvenido;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBienvenido = (TextView)findViewById(R.id.tvBienvenido);

        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        final String celular = preferences.getString("celular", "");

        this.window = getWindow();
        window.setStatusBarColor(Color.argb(255,165,39,63));
        window.setNavigationBarColor(Color.argb(255,165,39,63));

        cvQR = (CardView)findViewById(R.id.cvQR);

        if(!celular.isEmpty()){
            tvBienvenido.setText("Bienvenido");
        }

        cvQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(celular.isEmpty()){
                    Intent intent = new Intent(MainActivity.this, CelularActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainActivity.this, EscanearActivity.class);
                    startActivity(intent);
                }
            }
        });


        if(validaPermisos()){

            cvQR.setEnabled(true);

        }else{
            Snackbar snackbar = Snackbar.make(cvQR, "Faltan los permisos", Snackbar.LENGTH_LONG);
            snackbar.show();
            cvQR.setEnabled(false);
        }


    }

    public boolean validaPermisos(){

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED)){
            return true;
        }
        if((shouldShowRequestPermissionRationale(CAMERA))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{CAMERA}, 100);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                cvQR.setEnabled(true);
            }else{
               solicitarPermisosManual();
            }
        }
    }

    private void solicitarPermisosManual(){
        final CharSequence[] opciones = {"Sí", "No"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(opciones[which].equals("Sí")){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    dialog.dismiss();
                    Snackbar snackbar = Snackbar.make(cvQR, "Los permisos no fueron aceptados", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        alertOpciones.show();
    }

    private void cargarDialogoRecomendacion(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Permiso desactivado");
        dialogo.setMessage("Debes aceptar el permiso de acceso a la cámara para escanear los QR");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{ CAMERA}, 100);
                }
            }
        });
        dialogo.show();
    }



    public void iniciar(View view){
        Intent intent = new Intent(this, CelularActivity.class);
        startActivity(intent);
    }




}
