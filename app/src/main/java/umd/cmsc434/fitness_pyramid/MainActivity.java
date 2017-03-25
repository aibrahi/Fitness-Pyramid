package umd.cmsc434.fitness_pyramid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText mealName;
    private EditText mealCal;
    private Button addMealButton;
    private Button takePictureButton;
    private ImageView imageView;
    private ImageView imageView2;

    DatabaseReference databaseMeal;

    private ListView listViewMeal;
    private List<Meal> mealList;

    private final int MY_PERMISSIONS_REQUEST_READ_CAMERA = 1;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private StorageReference mStorageRef;

    private ProgressDialog progressDialog;
    private String mCurrentPhotoPath;
    private Uri picUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseMeal = FirebaseDatabase.getInstance().getReference("meal");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mealName = (EditText) findViewById(R.id.mealName);
        mealCal = (EditText) findViewById(R.id.mealCalories);
        addMealButton= (Button) findViewById(R.id.addMealButton);
        takePictureButton = (Button) findViewById(R.id.cameriaButton);
        imageView = (ImageView) findViewById(R.id.imageView);


        // Reference to an image file in Firebase Storage
        StorageReference imageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://fitness-pyramid.appspot.com/Photos1490312907691");

// ImageView in your Activity
        imageView2 = (ImageView) findViewById(R.id.imageView2);

// Load the image using Glide
        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(imageStorageReference)
                .into(imageView2);

        listViewMeal = (ListView) findViewById(R.id.listViewMeal);
        mealList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);

        addMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mealStr = mealName.getText().toString().trim();
                String mealInt = mealCal.getText().toString().trim();

                if(!TextUtils.isEmpty(mealStr) && !TextUtils.isEmpty(mealInt) ){
                    String id = databaseMeal.push().getKey();
                    Meal meal = new Meal(id, mealStr, Integer.parseInt(mealInt));
                    databaseMeal.child(id).setValue(meal);


                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Make sure all fields are comeplete", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {



                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_CAMERA);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.

                }else{
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                }

            }
        });
    }


    /** Create a File for saving an image */
    private  File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyApplication");

        /**Create the storage directory if it does not exist*/
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }

        /**Create a media file name*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".png");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){

            progressDialog.setMessage("Uploading Image .... ");
            progressDialog.show();

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] databaos = baos.toByteArray();

            //set the image into imageview
            imageView.setImageBitmap(bitmap);

            StorageReference filepath = mStorageRef.child("Photos"+ new Date().getTime());

            filepath.putBytes(databaos).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Uploading Picture ....", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseMeal.addValueEventListener( new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mealList.clear();

                for(DataSnapshot mealSnapshot : dataSnapshot.getChildren()){
                    Meal meal = mealSnapshot.getValue(Meal.class);

                    mealList.add(meal);
                }

                Meallist adapter = new Meallist(MainActivity.this, mealList);
                listViewMeal.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
