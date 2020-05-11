package com.example.findme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    MaterialEditText email, password;
    Button LoginButton;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        LoginButton = findViewById(R.id.LoginButton);

        /*  j'utilise directement le service d'authentification de Firebase
         */
        auth = FirebaseAuth.getInstance();

        /* Je fais des test pour voir si les données saisies sont correctes avant de Login l'utilisateur
         */
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_email = Objects.requireNonNull(email.getText()).toString();
                String text_password = Objects.requireNonNull(password.getText()).toString();

                if (TextUtils.isEmpty(text_email) | TextUtils.isEmpty(text_password)){
                    Toast.makeText(LoginActivity.this, getString(R.string.Please_fill_in_all_fields), Toast.LENGTH_SHORT).show();
                } else if (text_password.length() < 8){
                    Toast.makeText(LoginActivity.this, getString(R.string.Password_8_characters), Toast.LENGTH_SHORT).show();
                } else {
                    login( text_email, text_password);
                }
            }
        });
    }

    /**
     * Je Login la personne qui a entrée sont Email et son Password
     * Si le service d'authentification de Firebase me renvoie une erreur
     * j'affiche que les données sont incorrectes
     * @param email
     * @param password
     */
    public void login(final String email, final String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(LoginActivity.this, NewObjectActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else{
                            Toast.makeText(LoginActivity.this, getString(R.string.incorrect_user_password), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
