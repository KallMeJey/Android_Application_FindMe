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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText username, email, password;
    Button RegisterButton;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        RegisterButton = findViewById(R.id.RegisterButton);

        /* Appel au service d'authentification de firebase
         */
        auth = FirebaseAuth.getInstance();

        /* Au click je fais les verification avant d'enregistrer les données
         */
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_username = Objects.requireNonNull(username.getText()).toString();
                String text_email = Objects.requireNonNull(email.getText()).toString();
                String text_password = Objects.requireNonNull(password.getText()).toString();

                if (TextUtils.isEmpty(text_username) | TextUtils.isEmpty(text_email) | TextUtils.isEmpty(text_password)){
                    Toast.makeText(RegisterActivity.this, getString(R.string.Please_fill_in_all_fields), Toast.LENGTH_SHORT).show();
                } else if (text_password.length() < 8){
                    Toast.makeText(RegisterActivity.this, getString(R.string.Password_8_characters), Toast.LENGTH_SHORT).show();
                } else {
                    register(text_username, text_email, text_password);
                }
            }
        });
    }

    /**
     * J'enregistre les données dans la base de données firebase si elles sont correcte
     * @param username
     * @param email
     * @param password
     */
    private void register(final String username, final String email, final String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            final String userId = Objects.requireNonNull(firebaseUser).getUid();

                            reference = FirebaseDatabase.getInstance().getReference(getString(R.string.Path_User)).child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(getString(R.string.email), email);
                            hashMap.put(getString(R.string.password), password);
                            hashMap.put(getString(R.string.username), username);

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(RegisterActivity.this, NewObjectActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                                        );
                                        finish();
                                    }
                                }
                            });
                        }

                        else {
                            Toast.makeText(RegisterActivity.this, getString(R.string.You_cant_be_registered), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
