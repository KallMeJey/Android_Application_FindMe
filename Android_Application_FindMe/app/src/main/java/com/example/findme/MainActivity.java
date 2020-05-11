package com.example.findme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Je regarde s'il y a un utilisateur actuellement authentifié dans la BD
         */
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        /* s'il y a un utilisateur actuellement authentifié
         * il n'a pas besoin de s'authentifier
         */

        if (firebaseUser != null && Objects.equals(firebaseUser.getEmail(), "parisdescartes@parisdescartes.fr")){
            startActivity(new Intent(MainActivity.this, LostObjectsActivity.class));
        } else if (firebaseUser != null){
            startActivity(new Intent(MainActivity.this, StudentsActivity.class));
        } else {

        }

        Button loginButton = findViewById(R.id.LoginButton);
        Button registerButton = findViewById(R.id.RegisterButton);

        /* Lancement de l'activité Login
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        /* Lancement de l'activité Register
         */
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

    }
}
