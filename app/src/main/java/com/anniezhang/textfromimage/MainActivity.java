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
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    ConstraintLayout btnLogOut;
    FirebaseAuth mAuth;

    // Android Firebase Auto ML vision Edge
    ConstraintLayout chose;
    ImageView imageView2;
    TextView resultTv;
    FirebaseVisionImageLabeler labeler;

    StorageReference mStorageRef;
    DatabaseReference mDatabaseRef;
    FirebaseAuth auth;
    ArrayList<String> pathArray;
    Uri imageUpload;

    ConstraintLayout showUploads;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //
        chose = findViewById(R.id.button);
        resultTv = findViewById(R.id.textView2);
        imageView2 = findViewById(R.id.imageView2);
        // Label images with an AutoML-trained model on Android
        // Subtask 4 of Step 1 (Load the model) on https://firebase.google.com/docs/ml/android/label-images-with-automl?authuser=0#java

        showUploads = findViewById(R.id.history);

        FirebaseAutoMLLocalModel localModel =
                new FirebaseAutoMLLocalModel.Builder()
                        //.setAssetFilePath("manifest.json")
                        // or .setAbsoluteFilePath(absolute file path to manifest file)
                        //.setAbsoluteFilePath("/Users/anniezhang/Downloads/TextFromImage/app/src/main/assets/Files/metadata.json")
                        .setAssetFilePath("Files/manifest.json")
                        .build();

        //FirebaseVisionImageLabeler labeler;
        try {
            FirebaseVisionOnDeviceAutoMLImageLabelerOptions options =
                    new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.0f) // Evaluate your model in the Firebase console
                            // to determine an appropriate value
                            .build();
            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options);

        } catch (FirebaseMLException e){

        }

        //
//        imageView = findViewById(R.id.imageId);
//        textView = findViewById(R.id.textId);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }

        btnLogOut = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();

        btnLogOut.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });


        chose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                someActivityResultLauncher.launch(Intent.createChooser(i, "Choose an image"));
                //startActivity(121, 121, Intent.createChooser(i, "Choose an image"));
                //onActivityResult(121,121,i);

            }
            ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                // Here, no request code
                                Intent data = result.getData();
                                //doSomeOperations();
                                imageView2.setImageURI(data.getData());
                                imageUpload = (data.getData());
                                resultTv.setText("TextView");
                                FirebaseVisionImage image;
                                try {
                                    image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                                    labeler.processImage(image)
                                            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                                                @Override
                                                public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                                                    // Task completed successfully
                                                    // ...
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
                                                    String date = sdf.format(new Date());
                                                    for (FirebaseVisionImageLabel label: labels){
                                                        String text = label.getText();
                                                        float confidence = label.getConfidence();
                                                        DecimalFormat df2 = new DecimalFormat("##.##");
                                                        String confidence2 = df2.format(confidence * 100) + "%";
                                                        //resultTv.setText(requestCode);
                                                        //resultTv.setText(text + " " + confidence);
                                                        String text2 = "";
                                                        String description = "";
                                                        if (text.equals("Corn__Northern_Leaf_Blight"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Corn\nCondition: Northern Leaf Blight";
                                                            description = "Northern corn leaf blight or Turcicum leaf blight has characteristic cigar-shaped lesions and can cause significant yield loss in crops. It can be treated using folicar fungicides to contain the virus in the corn seedling's early growing stages.";
                                                        }
                                                        if (text.equals("Apple__Black_Rot"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Apple\nCondition:  Black Rot";
                                                            description = "Black rot is a disease of apples that infect the fruit, bark, and leaves of the plant. It is caused by the fungus Botryosphaeria obtusa and can jump from healthy tissue of other fruit trees. Black rot can be treated by pruning out diseased/dead branches, picking up all dried and shriveled fruits on trees, and removing infected plant material";
                                                        }
                                                        if (text.equals("Corn__Cercospora_Leaf_Spot"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Corn\nCondition: Cercospora Leaf Spot";
                                                            description = "Cercospora leaf spot is a common fungal disease in corn and can be managed by avoiding overwatering or watering too late in the day to reduce free moisture. In addition, spacing the crops can encourage air movement and reduce high humidity levels, thereby helping to control the fungus.";
                                                        }
                                                        if (text.equals("Grape__Black_Rot"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Grape\nCondition: Black Rot";
                                                            description = "Grape Black Rot is caused by the fungus Guignardia bidwellii, a severe disease that impacts both wild and farm-raised grapes. Some methods to prevent the disease are to use Mancozeb or Ziram which are highly effective protectants of the plants. ";
                                                        }
                                                        if (text.equals("Tomato__Leaf_Mold"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Tomato\nCondition: Leaf Mold";
                                                            description = "Tomato leaf mold is caused by a fungus and can be managed by controlling the humidity, using fungicides, as well as changing the location in which the tomatoes are grown.";
                                                        }
                                                        if (text.equals("Tomato__Bacterial_Spot"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Tomato\nCondition: Bacterial Spot";
                                                            description = "Unfortunately, tomatoes with bacterial spots cannot be cured and can only be further prevented by removing the symptomatic tomatoes from the healthy plants.";
                                                        }
                                                        if (text.equals("Grape__Esca"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Grape\nCondition: Esca";
                                                            description = "Grape esca is a disease of mature grapevines and can be treated by cutting off the affected leaves, using fungicide resistance, and managing the humidity in its growing environment.";
                                                        }
                                                        if (text.equals("Orange__Haunglongbing"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Orange\nCondition: HuangLongBing";
                                                            description = "Orange Huanglongbing or citrus greening occurs when there is uneven yellow discoloration on leaves caused by restricted nutrients. Unfortunately, citris greening cannot be cured and will ultimately destroy the tree. It is very crucial to remove the trees that have citrus greening.";
                                                        }
                                                        if (text.equals("Tomato__Tomato_Mosaic_Virus"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Tomato\nCondition: Mosaic Virus";
                                                            description = "Tomato mosaic virus is a plant pathogenic virus and can be treated by removing all infected plants and disinfecting gardening tools after every use.";
                                                        }
                                                        if (text.equals("Grape__Leaf_Blight"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Grape\nCondition: Leaf Blight";
                                                            description = "Grape leaf blight produces dark brown patches on the surface of grape leaves and can be controlled by using fungicide resistance and cutting the infected leaves.";
                                                        }
                                                        if (text.equals("Peach__Bacterial_Spot"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Peach\nCondition: Bacterial Spot";
                                                            description = "Peach bacterial spot can be treated with plant nutrients including copper, oxytetracycline, and mycoshield and generic equivalents.";
                                                        }
                                                        if (text.equals("Corn__Healthy"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Corn\nCondition: Healthy";
                                                            description = "Congratulations! You have a healthy corn!";
                                                        }
                                                        if (text.equals("Cherry__Healthy"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Cherry\nCondition: Healthy";
                                                            description = "Congratulations! You have a healthy cherry!";
                                                        }
                                                        if (text.equals("Blueberry__Healthy"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Blueberry\nCondition: Healthy";
                                                            description = "Congratulations! You have a healthy blueberry!";
                                                        }
                                                        if (text.equals("Tomato__Early_Blight"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Tomato\nCondition: Early Blight";
                                                            description = "Tomato early blight can be treated by rotating crops, allowing space between plants, using mulch on the growing fields, and watering from below.";
                                                        }
                                                        if (text.equals("Corn__Common_Rust"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Corn\nCondition Common Rust";
                                                            description = "Corn common rust produces rust-colored pustules on both sides of the corn leaf. It can be treated by immediately spraying with fungicide.";
                                                        }
                                                        if (text.equals("Tomato__Tomato_Leaf_Curl_Virus"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Tomato\nCondition: Leaf Curl Virus";
                                                            description = "The tomato leaf curl virus can be controlled by spraying the entire plant and below the leaves with imidacloprid.\n" +
                                                                    "Apple__Healthy";
                                                        }
                                                        if (text.equals("Apple__Healthy"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Apple\nCondition: Healthy";
                                                            description = "Congratulations! You have a healthy apple!";
                                                        }
                                                        if (text.equals("Apple__Apple_Scab"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Apple\nCondition: Apple Scab";
                                                            description = "Apple scabs can be treated by removing and destroying the fallen leaf litter as well as not overcrowding the plants.";
                                                        }
                                                        if (text.equals("Tomato__Healthy"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Tomato\nCondition: Healthy";
                                                            description = "Congratulations! You have a healthy tomato!";
                                                        }
                                                        if (text.equals("Bell_Pepper__Healthy"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Bell Pepper\nCondition: Healthy";
                                                            description = "Congratulations! You have a healthy bell pepper!";
                                                        }
                                                        if (text.equals("Raspberry__Healthy"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Raspberry\nCondition: Healthy";
                                                            description = "Congratulations! You have a healthy raspberry!";
                                                        }
                                                        if (text.equals("Cherry__Powdery_Mildew"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Cherry\nCondition: Powdery Mildew";
                                                            description = "Cherry powdery mildew can be treated by spraying the plants with potassium bicarbonate (similar to baking soda) once every one or two weeks. Potassium bicarbonate is a contact fungicide that kills the powdery mildew spores rapidly.";
                                                        }
                                                        if (text.equals("Peach__Healthy"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Peach\nCondition: Healthy";
                                                            description = "Congratulations! You have a healthy peach!";
                                                        }
                                                        if (text.equals("Soybean__Healthy"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Soybean\nCondition: Healthy";
                                                            description = "Congratulations! You have a healthy soybean!";
                                                        }
                                                        if (text.equals("Tomato__Spider_Mites"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Tomato\nCondition: Spider Mites";
                                                            description = "Tomato spider mites are pale-orange to red spots on the underside of the leaves and can be treated with a mixture of alcohol and water to remove and kill visible spider mites. Dilute 1 cup of alcohol in 30oz of water and pour this solution into a spray bottle. Next, spray both sides of the leaves and wipe them off with a paper towel.";
                                                        }
                                                        if (text.equals("Tomato__Target_Spot"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Tomato\nCondition: Target Spot";
                                                            description = "Tomato target spot, also known as early blight, is a fungal disease and can be treated by removing the infected plants to prevent the spread to healthy plants.";
                                                        }
                                                        if (text.equals("Potato__Healthy"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Potato\nCondition: Healthy";
                                                            description = "Congratulations! You have a healthy potato!";
                                                        }
                                                        if (text.equals("Strawberry__Leaf_Scorch"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Strawberry\nCondition: Leaf Scorch";
                                                            description = "Strawberry leaf scorch can be treated by applying fungicide to the plants.";
                                                        }
                                                        if (text.equals("Tomato__Septoria_Leaf_Spot"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Tomato\nCondition: Septoria Leaf Spot";
                                                            description = "Tomato septoria leaf spots can be treated by removing the infected leaves immediately and using chemical or organic fungicides.";
                                                        }
                                                        if (text.equals("Strawberry__Healthy"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Strawberry\nCondition: Healthy";
                                                            description = "Congratulations! You have a healthy strawberry!";
                                                        }
                                                        if (text.equals("Apple__Cedar_Apple_Rust"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Apple\nCondition: Cedar Apple Rust";
                                                            description = "Apple cedar rust can be treated by spraying the apple trees with copper to prevent the fungal infections.";
                                                        }
                                                        if (text.equals("Grape__Healthy"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Grape\nCondition: Healthy";
                                                            description = "Congratulations! You have a healthy grape!";
                                                        }
                                                        if (text.equals("Potato__Late_Blight"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Potato\nCondition: Late Blight";
                                                            description = "Potato late blight can be managed by spraying the plant with prophylactic spraw of mancozeb at 0.25%.";
                                                        }
                                                        if (text.equals("Squash__Powdery_Mildew"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Squash\nCondition: Powdery Mildew";
                                                            description = "Powdery mildew on squash can be treated with sulfur-containing organic fungicides as both prevention and treatment for existing infections. Trimming and pruning the affected leaves also aids the treatment.";
                                                        }
                                                        if (text.equals("Bell_Pepper__Bacterial_Spot"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Bell Pepper\nCondition: Bacterial Spot";
                                                            description = "Bacterial leaf spots in bell peppers can be treated by often spraying the affected plants with copper and Mancozeb spray every three to five days.\n" +
                                                                    "Tomato__Late_Blight";
                                                        }
                                                        if (text.equals("Tomato__Late_Blight"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Tomato\nCondition: Late Blight";
                                                            description = "Tomato late blight can be treated by pruning or staking plants to improve air circulation.";
                                                        }
                                                        if (text.equals("Potato__Early_Blight"))
                                                        {
                                                            text2 = "Fruit/Vegetable: Potato\nCondition: Early Blight";
                                                            description = "Potato leaf blight can be treated by cutting off all infected leaves and spraying the plant with a solution of baking soda and water (1/2 teaspoon per gallon of water).";
                                                        }
                                                        resultTv.setText(text2 + "\nConfidence: "+ confidence2 + "\nDescription: " + description);

                                                        pathArray = new ArrayList<>();
                                                        auth = FirebaseAuth.getInstance();
                                                        mStorageRef = FirebaseStorage.getInstance().getReference();

                                                        //checkFilePermissions();
                                                        FirebaseUser user = auth.getCurrentUser();
                                                        String userID = user.getUid();

                                                        String name = text + " " + confidence;
                                                        String name2 = text;
                                                        String location = "images/users/" + userID + "/" + name;
                                                        String location2 = "images/users/" + userID;
                                                        mDatabaseRef = FirebaseDatabase.getInstance().getReference(location2);

                                                        Uri uri = imageUpload;
                                                        StorageReference storageReference = mStorageRef.child(location);
                                                        //DatabaseReference databaseReference = mDatabaseRef.child(location);
                                                        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                // Get a URL to the uploaded content
                                                                Toast.makeText(getBaseContext(),"Upload Success",Toast.LENGTH_SHORT).show();
                                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        //String imageUrl = uri.toString();
                                                                        Upload upload = new Upload(text, confidence2, date, uri.toString());
                                                                        String uploadId = mDatabaseRef.push().getKey();
                                                                        assert uploadId != null;
                                                                        mDatabaseRef.child(uploadId).setValue(upload);
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getBaseContext(),"Upload Failed",Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

//                                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                                            @Override
//                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                                                if (taskSnapshot.getMetadata() != null) {
//                                                                    if (taskSnapshot.getMetadata().getReference() != null) {
//                                                                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
//                                                                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                                                            @Override
//                                                                            public void onSuccess(Uri uri) {
//                                                                                String imageUrl = uri.toString();
//                                                                                //createNewPost(imageUrl);
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//                                                            }});
//                                                        //StorageReference storageReference2 = FirebaseStorage.getInstance().getReference(location);
//                                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                                            @Override
//                                                            public void onSuccess(Uri uri) {
//                                                                String uploadId = mDatabaseRef.push().getKey();
//                                                                mDatabaseRef.child(uploadId).setValue(uri);
//                                                            }
//                                                        }).addOnFailureListener(new OnFailureListener() {
//                                                            @Override
//                                                            public void onFailure(@NonNull Exception e) {
//                                                                Toast.makeText(MainActivity.this,"Upload Failed",Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        });
                                                        break;
//                                                        resultTv.invalidate();
//                                                        resultTv.requestLayout();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Task failed with an exception
                                                    // ...
                                                    resultTv.setText("Error");
                                                }
                                            });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        });
        showUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();
            }
        });
    }

    private void openImagesActivity() {
        Intent intent = new Intent(this, ScanHistory.class);
        startActivity(intent);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = MainActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += MainActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }
//        else{
//            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
//        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    public void doProcess(View view) {
        //open the camera => create an Intent object
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        Bundle bundle = data.getExtras();
//        //from bundle, extract the image
//        Bitmap bitmap = (Bitmap) bundle.get("data");
//        //set image in imageview
//        imageView.setImageBitmap(bitmap);
//        //process the image
//        //1. create a FirebaseVersionObject from a Bitmap Object
//        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
//        //2. Get an instance of FirebaseVision
//        FirebaseVision firebaseVision = FirebaseVision.getInstance();
//        //3. Create an instance of FirebaseVision Text Recognizer
//        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
//        //4. Create a task to process the image
//        Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
//        //5. If task is success
//        task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//            @Override
//            public void onSuccess(FirebaseVisionText firebaseVisionText) {
//                String s = firebaseVisionText.getText();
//                textView.setText(s);
//            }
//        });
//        //6. If task is failure
//        task.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//
//        //
//        if (requestCode == 121)
//        {
//            imageView2.setImageURI(data.getData());
//
//            FirebaseVisionImage image;
//            try {
//                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
//                labeler.processImage(image)
//                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
//                            @Override
//                            public void onSuccess(List<FirebaseVisionImageLabel> labels) {
//                                // Task completed successfully
//                                // ...
//                                for (FirebaseVisionImageLabel label: labels){
//                                    String text = label.getText();
//                                    float confidence = label.getConfidence();
//                                    resultTv.setText(text + " " + confidence);
//                                }
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                // Task failed with an exception
//                                // ...
//                            }
//                        });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    //


//    }
}