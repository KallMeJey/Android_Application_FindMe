package com.example.findme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

import static com.example.findme.R.string.Path_Objects;

public class StudentsActivity extends AppCompatActivity {

    Button logoutButton;
    Button addItemButton;

    DatabaseReference reference;
    LinearLayout linearLayout;
    LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        //setSupportActionBar(toolbar);

        linearLayout = findViewById(R.id.LinearViewImages);

        /* LayoutInflater permet de recopier un Layout existant en changent simplement les données des items
         */
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        /* J'écoute actuellement dans la base de données Firebase le chemin que point reference
         */
        reference = FirebaseDatabase.getInstance().getReference(getString(Path_Objects));

        /* Je parcour la Base de données FireBase
         * à chaque fois qu'il y a un objet je recopie un layout Vierge
         * Je modifie ses données et je l'ajoute au layout current
         */
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot child : dataSnapshot.getChildren()) {
                    final View view = layoutInflater.inflate(R.layout.my_scroll_layout_students, linearLayout, false);

                    reference.child("/" + child.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot childNext : dataSnapshot.getChildren()) {

                                TextView room = view.findViewById(R.id.Room);
                                TextView hour = view.findViewById(R.id.Hour);
                                TextView date = view.findViewById(R.id.Date);
                                TextView description = view.findViewById(R.id.Description);

                                if (Objects.equals(childNext.getKey(), getString(R.string.room))){
                                    room.setText(childNext.getValue(String.class));
                                }
                                if (Objects.equals(childNext.getKey(), getString(R.string.hour))){
                                    hour.setText(childNext.getValue(String.class));
                                }
                                if (Objects.equals(childNext.getKey(), getString(R.string.date))){
                                    date.setText(childNext.getValue(String.class));
                                }
                                if (Objects.equals(childNext.getKey(), getString(R.string.description))){
                                    description.setText(childNext.getValue(String.class));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(StudentsActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    });

                    linearLayout.addView(view);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudentsActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        });


        logoutButton = findViewById(R.id.Logout);

        /* Déconnecté l'utilisateur actuellement authentifié
         */
       logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(StudentsActivity.this, MainActivity.class));
            }
        });

        addItemButton = findViewById(R.id.AddItemButton);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentsActivity.this, NewObjectActivity.class));
            }
        });
    }
}