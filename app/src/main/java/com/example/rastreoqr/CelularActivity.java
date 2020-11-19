package com.example.rastreoqr;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class CelularActivity extends AppCompatActivity {


    private Window window;
    private EditText etCelular;
    private Button btRegistrar;
    private static final String TAG = "CelularActivity";
    String codeBySystems;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_celular);
        etCelular = (EditText)findViewById(R.id.etCelular);
        btRegistrar = (Button)findViewById(R.id.btRegistrar);
        this.window = getWindow();
        window.setStatusBarColor(Color.argb(255,165,39,63));
        window.setNavigationBarColor(Color.argb(255,165,39,63));
    }

    public void registrarse(View view){
        String numeroCelular = etCelular.getText().toString().trim();
        if(numeroCelular.isEmpty() || numeroCelular.length() < 10){
            etCelular.setError("Campo requerido");
        }else{
            PhoneAuthProvider.getInstance().verifyPhoneNumber("+52"+numeroCelular,60, TimeUnit.SECONDS,CelularActivity.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            String code = phoneAuthCredential.getSmsCode();
                            if(code!=null){
                                Toast.makeText(CelularActivity.this, code, Toast.LENGTH_SHORT).show();
                            }
                            signInWithPhoneAuthCredential(phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(CelularActivity.this,  e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeSent(@NonNull final String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            codeBySystems = verificationId;
                            super.onCodeSent(verificationId, forceResendingToken);
                            Dialog dialog = new Dialog(CelularActivity.this);
                            dialog.setContentView(R.layout.verify_popup);

                            final EditText etVerifyCode = dialog.findViewById(R.id.etVerifyCode);
                            Button btVerifyOtp = dialog.findViewById(R.id.btVerifyOtp);
                            btVerifyOtp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String verificationCode = etVerifyCode.getText().toString();
                                    if(verificationId.isEmpty());
                                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,verificationCode);
                                    signInWithPhoneAuthCredential(credential);
                                }
                            });
                            dialog.show();
                        }
                    }
            );
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(CelularActivity.this, RegistroUsuarioActivity.class);
                            startActivity(intent);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(CelularActivity.this, "Verificación fallida", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void prueba(View view){
        String numeroCelular = etCelular.getText().toString().trim();
        if(numeroCelular.isEmpty() || numeroCelular.length() < 10) {
            etCelular.setError("Campo requerido");
        }else {
            Intent intent = new Intent(CelularActivity.this, RegistroUsuarioActivity.class);
            intent.putExtra("celular", etCelular.getText().toString());
            startActivity(intent);
        }
    }
}
