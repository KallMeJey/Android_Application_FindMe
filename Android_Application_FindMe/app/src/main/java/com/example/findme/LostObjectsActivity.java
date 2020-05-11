package com.example.findme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.example.findme.R.*;
import static com.example.findme.R.string.*;


public class LostObjectsActivity extends AppCompatActivity {

    DatabaseReference reference;
    LinearLayout linearLayout;
    LayoutInflater layoutInflater;

    Button addItemButton;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_lost_objects);



        linearLayout = findViewById(id.LinearViewImages);

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
                    final View view = layoutInflater.inflate(layout.my_scroll_layout, linearLayout, false);

                    reference.child("/" + child.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot childNext : dataSnapshot.getChildren()) {

                                TextView room = view.findViewById(id.Room);
                                TextView hour = view.findViewById(id.Hour);
                                TextView date = view.findViewById(id.Date);
                                ImageView image = view.findViewById(id.image);
                                TextView description = view.findViewById(id.Description);

                                if (Objects.equals(childNext.getKey(), getString(string.room))){
                                    room.setText(childNext.getValue(String.class));
                                }
                                if (Objects.equals(childNext.getKey(), getString(string.hour))){
                                    hour.setText(childNext.getValue(String.class));
                                }
                                if (Objects.equals(childNext.getKey(), getString(string.date))){
                                    date.setText(childNext.getValue(String.class));
                                }
                                if (Objects.equals(childNext.getKey(), getString(string.description))){
                                    description.setText(childNext.getValue(String.class));
                                }
                                if (Objects.equals(childNext.getKey(), getString(string.image))){
                                    byte[] image64 = Base64.decode(Objects.requireNonNull(childNext.getValue(String.class)).replace(getString(string.replace_base_64_string), ""), Base64.DEFAULT);
                                    image.setImageBitmap(BitmapFactory.decodeByteArray( image64, 0, image64.length));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(LostObjectsActivity.this, getString(string.error), Toast.LENGTH_SHORT).show();
                        }
                    });

                    linearLayout.addView(view);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LostObjectsActivity.this, getString(string.error), Toast.LENGTH_SHORT).show();
            }
        });


        logoutButton = findViewById(R.id.Logout);

        /* Déconnecté l'utilisateur actuellement authentifié
         */
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(LostObjectsActivity.this, MainActivity.class));
            }
        });

        addItemButton = findViewById(R.id.AddItemButton);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LostObjectsActivity.this, NewObjectActivity.class));
            }
        });
    }

}
