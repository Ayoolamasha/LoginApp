package com.codics.loginapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText signUpUsername, signUpEmail, signUpPassword, signUpAge;
    private Button register;
    private TextView gotAnAccount;
    private ImageView signUpProfilePic;
    String userName, age, email, password;

    // FIREBASE
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private static final int PICK_IMAGE = 123;
    private Uri imagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        uiSetUp();
        validate();

        gotAnAccount.setOnClickListener(this);
        register.setOnClickListener(this);
        signUpProfilePic.setOnClickListener(this);

    }

    private void uiSetUp() {
        signUpUsername = findViewById(R.id.signUp_userName);
        signUpEmail = findViewById(R.id.signUp_email);
        signUpPassword = findViewById(R.id.signUp_password);
        signUpAge = findViewById(R.id.signUp_userAge);
        signUpProfilePic = findViewById(R.id.userProfilePic);
        gotAnAccount = findViewById(R.id.loginAlready);
        register = findViewById(R.id.signUp_button);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

    }

    private Boolean validate() {
        boolean result = false;
        userName = signUpUsername.getText().toString();
         email = signUpEmail.getText().toString();
         password = signUpPassword.getText().toString();
         age = signUpAge.getText().toString();

        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || age.isEmpty() || imagePath==null){
            Toast.makeText(getApplicationContext(), "Please fill all the necessary details", Toast.LENGTH_SHORT).show();
        }else{
            result = true;
        }
        return  result;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            imagePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                signUpProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.loginAlready:
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                break;

            case R.id.signUp_button:
                if (validate()){
                    String newUserEmail = signUpEmail.getText().toString().trim();
                    String newUserPassword = signUpPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(newUserEmail,newUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                sendUserData();
                                Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            }else{
                                Toast.makeText(getApplicationContext(), "Registration Unsuccessful", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            case R.id.userProfilePic:
                Intent intent = new Intent();
                //THE CODE BELOW INDICATES THAT THE FILE I AM EXPECTING IS AN IMAGE FILE AND IT ALSO SUPPORTS ALL KINDS OF IMAGE
                // IF I'D WANTED ONLY PNG I WOULD HAVE WROTE ("image/png"); THAT WOULD INDICATE ONLY PNG FILES
                //NOW IF YOU WANT ONLY DOCUMENT FILE CHANGE "image" TO "application/*" OR "application/pdf" IF YOU WANT ONLY PDF FILES
                //IF YOU WANT TO ADD AUDIO OR VIDEO "audio/*" or "audio/mp3" FOR VIDEO "video/mp4" or "video/*"
                intent.setType("image/*");
                //intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);

        }




    }

    private void sendUserData(){
        //THIS SENDS THE USER DATA WHICH INCLUDE NAME, AGE, EMAIL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(Objects.requireNonNull(firebaseAuth.getUid()));
        // THIS SENDS THE USER PROFILE IMAGE
        StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic"); // UserId/Images/ProfilePics
        UploadTask uploadTask = imageReference.putFile(imagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "File Not Uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "File Uploaded Successfully", Toast.LENGTH_SHORT).show();

            }
        });
        UserProfile userProfile = new UserProfile(userName, age, email);
        databaseReference.setValue(userProfile);
    }
}
