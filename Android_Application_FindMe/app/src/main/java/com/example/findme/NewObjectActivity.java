package com.example.findme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class NewObjectActivity extends AppCompatActivity {

    DatabaseReference reference;

    Button ButtonHour, dateButton, takePictureButton, shareButton, logoutButton, showItemButton;
    MaterialEditText room;
    TextView date, hour, description;
    ImageView image;
    Bitmap bitmap;

    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_object);



        ButtonHour = findViewById(R.id.ButtonHeure);
        dateButton = findViewById(R.id.ButtonDate);
        takePictureButton = findViewById(R.id.ButtonTakePicture);
        shareButton = findViewById(R.id.ButtonShare);
        room = findViewById(R.id.Room);
        date = findViewById(R.id.Date);
        hour = findViewById(R.id.Hour);
        image = findViewById(R.id.ImageTaked);
        description = findViewById(R.id.Description);

        date.setText(Calendar.getInstance().get(Calendar.YEAR) + "/" + Calendar.getInstance().get(Calendar.MONTH) + "/" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));


        /* Service de selection d'heure
         * au click une popup de selection d'heure
         */
        ButtonHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(NewObjectActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hour.setText(hourOfDay + ":" + minute);
                    }
                }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        /* Service de selection de date
         * au click une popup de selection de date
         */
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(NewObjectActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        /* Service de prise de photo
         * au click on peut prendre un photo avec l'appareil photo
         */
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0);
            }
        });

        /* J'effectue des vérification si toutes les données sont entrée avant de les enregistrer dans la base de données Firebase
         */
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_room = Objects.requireNonNull(room.getText()).toString();
                String text_hour = Objects.requireNonNull(hour.getText()).toString();
                String text_date = Objects.requireNonNull(date.getText()).toString();
                String text_description = Objects.requireNonNull(description.getText()).toString();

                if (TextUtils.isEmpty(text_room) | TextUtils.isEmpty(text_hour) | TextUtils.isEmpty(text_date) | TextUtils.isEmpty(text_description) | bitmap == null){
                    Toast.makeText(NewObjectActivity.this, getString(R.string.Please_fill_in_all_fields), Toast.LENGTH_SHORT).show();
                } else if (description.length() > 200){
                    Toast.makeText(NewObjectActivity.this, getString(R.string.description_too_long), Toast.LENGTH_SHORT).show();
                } else {
                    save(text_room, text_hour, text_date, text_description, bitmap);
                }
            }
        });


        logoutButton = findViewById(R.id.Logout);

        /* Déconnecté l'utilisateur actuellement authentifié
         */
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(NewObjectActivity.this, MainActivity.class));
            }
        });

        showItemButton = findViewById(R.id.ShowItemButton);

        showItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null && Objects.equals(firebaseUser.getEmail(), "parisdescartes@parisdescartes.fr")){
                    startActivity(new Intent(NewObjectActivity.this, LostObjectsActivity.class));
                } else {
                    startActivity(new Intent(NewObjectActivity.this, StudentsActivity.class));
                }
            }
        });
    }

    /**
     * Conversion d'une Image de type Bitmap en Base64
     * @param image
     * @return String -> Image en Base64
     */
    private String getImageInBase64(Bitmap image){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    /**
     * J'affiche l'image qui vient d'être prise en photo actuellement
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
        image.setImageBitmap((Bitmap) Objects.requireNonNull(data.getExtras()).get("data"));
    }

    /**
     * J'enregistre les données dans la base de données Firebase
     * @param room
     * @param hour
     * @param date
     * @param description
     * @param bitmap
     */
    private void save(final String room, final String hour, String date, String description, Bitmap bitmap){

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(getString(R.string.date), date);
        hashMap.put(getString(R.string.description), description);
        hashMap.put(getString(R.string.hour), hour);
        hashMap.put(getString(R.string.image), getImageInBase64(bitmap));
        hashMap.put(getString(R.string.room),room);

        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.Path_Objects)).child(getRandomAlphaNumeric(new Random().nextInt(20 + 1) + 5));

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser != null && Objects.equals(firebaseUser.getEmail(), "parisdescartes@parisdescartes.fr")){
                        startActivity(new Intent(NewObjectActivity.this, LostObjectsActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        startActivity(new Intent(NewObjectActivity.this, StudentsActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(NewObjectActivity.this, getString(R.string.data_sending_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Générer un numér d'identification unique pour l'objet dans la base de dnnnées firebase
     * @param len
     * @return
     */
    public String getRandomAlphaNumeric(int len) {
        char[] ch = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                'w', 'x', 'y', 'z' };

        char[] c=new char[len];
        Random random=new Random();
        for (int i = 0; i < len; i++) {
            c[i]=ch[random.nextInt(ch.length)];
        }

        return new String(c);
    }

}
