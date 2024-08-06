package com.anniezhang.textfromimage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // UI components
    ImageView imageView, imageView2;
    TextView textView, resultTv;
    ConstraintLayout btnLogOut, chose, showUploads;

    // Firebase components
    FirebaseAuth mAuth;
    FirebaseVisionImageLabeler labeler;
    StorageReference mStorageRef;
    DatabaseReference mDatabaseRef;
    ArrayList<String> pathArray;
    Uri imageUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        chose = findViewById(R.id.button);
        resultTv = findViewById(R.id.textView2);
        imageView2 = findViewById(R.id.imageView2);
        showUploads = findViewById(R.id.history);
        btnLogOut = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();

        // Check for camera permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }

        // Set up AutoML model
        setupAutoMLModel();

        // Set up log out button click listener
        btnLogOut.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        // Set up image selection button click listener
        chose.setOnClickListener(v -> openImageSelector());

        // Set up show uploads button click listener
        showUploads.setOnClickListener(v -> openImagesActivity());
    }

    private void setupAutoMLModel() {
        FirebaseAutoMLLocalModel localModel = new FirebaseAutoMLLocalModel.Builder()
                .setAssetFilePath("Files/manifest.json")
                .build();

        try {
            FirebaseVisionOnDeviceAutoMLImageLabelerOptions options = new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel)
                    .setConfidenceThreshold(0.0f)
                    .build();
            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options);
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }
    }

    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(Intent.createChooser(intent, "Choose an image"));
    }

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            imageUpload = data.getData();
                            imageView2.setImageURI(imageUpload);
                            processImage(imageUpload);
                        }
                    }
                }
            }
    );

    private void processImage(Uri imageUri) {
        try {
            FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(getApplicationContext(), imageUri);
            labeler.processImage(image)
                    .addOnSuccessListener(labels -> handleImageLabelingSuccess(labels))
                    .addOnFailureListener(e -> resultTv.setText("Error"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleImageLabelingSuccess(List<FirebaseVisionImageLabel> labels) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
        String date = sdf.format(new Date());

        for (FirebaseVisionImageLabel label : labels) {
            String text = label.getText();
            float confidence = label.getConfidence();
            DecimalFormat df2 = new DecimalFormat("##.##");
            String confidence2 = df2.format(confidence * 100) + "%";

            String description = getDescription(text);
            resultTv.setText("Fruit/Vegetable: " + text + "\nConfidence: " + confidence2 + "\nDescription: " + description);

            uploadImageToFirebase(text, confidence2, date);
            break;
        }
    }

    private String getDescription(String label) {
        switch (label) {
            case "Corn__Northern_Leaf_Blight":
                return "Northern corn leaf blight or Turcicum leaf blight has characteristic cigar-shaped lesions and can cause significant yield loss in crops. It can be treated using foliar fungicides to contain the virus in the corn seedling's early growing stages.";
            case "Apple__Black_Rot":
                return "Black rot is a disease of apples that infect the fruit, bark, and leaves of the plant. It is caused by the fungus Botryosphaeria obtusa and can jump from healthy tissue of other fruit trees. Black rot can be treated by pruning out diseased/dead branches, picking up all dried and shriveled fruits on trees, and removing infected plant material.";
            case "Corn__Cercospora_Leaf_Spot":
                return "Cercospora leaf spot is a common fungal disease in corn and can be managed by avoiding overwatering or watering too late in the day to reduce free moisture. In addition, spacing the crops can encourage air movement and reduce high humidity levels, thereby helping to control the fungus.";
            case "Grape__Black_Rot":
                return "Grape Black Rot is caused by the fungus Guignardia bidwellii, a severe disease that impacts both wild and farm-raised grapes. Some methods to prevent the disease are to use Mancozeb or Ziram which are highly effective protectants of the plants.";
            case "Tomato__Leaf_Mold":
                return "Tomato leaf mold is caused by a fungus and can be managed by controlling the humidity, using fungicides, as well as changing the location in which the tomatoes are grown.";
            case "Tomato__Bacterial_Spot":
                return "Unfortunately, tomatoes with bacterial spots cannot be cured and can only be further prevented by removing the symptomatic tomatoes from the healthy plants.";
            case "Grape__Esca":
                return "Grape esca is a disease of mature grapevines and can be treated by cutting off the affected leaves, using fungicide resistance, and managing the humidity in its growing environment.";
            case "Orange__Haunglongbing":
                return "Orange Huanglongbing or citrus greening occurs when there is uneven yellow discoloration on leaves caused by restricted nutrients. Unfortunately, citrus greening cannot be cured and will ultimately destroy the tree. It is very crucial to remove the trees that have citrus greening.";
            case "Tomato__Tomato_Mosaic_Virus":
                return "Tomato mosaic virus is a plant pathogenic virus and can be treated by removing all infected plants and disinfecting gardening tools after every use.";
            case "Grape__Leaf_Blight":
                return "Grape leaf blight produces dark brown patches on the surface of grape leaves and can be controlled by using fungicide resistance and cutting the infected leaves.";
            case "Peach__Bacterial_Spot":
                return "Peach bacterial spot can be treated with plant nutrients including copper, oxytetracycline, and mycoshield and generic equivalents.";
            case "Corn__Healthy":
                return "Congratulations! You have a healthy corn!";
            case "Cherry__Healthy":
                return "Congratulations! You have a healthy cherry!";
            case "Blueberry__Healthy":
                return "Congratulations! You have a healthy blueberry!";
            case "Tomato__Early_Blight":
                return "Tomato early blight can be treated by rotating crops, allowing space between plants, using mulch on the growing fields, and watering
