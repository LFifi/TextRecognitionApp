package com.example.textrecognitionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;

import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button captureImageBtn, detectTextBtn;
    private ImageView imageView;
    private TextView textView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    final int CROP_PIC = 2;
    Bitmap imageBitmap;
    String currentPhotoPath;
    File photoFile, photoFile2;
    Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureImageBtn = findViewById(R.id.capture_image);
        detectTextBtn = findViewById(R.id.detect_text_image);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        captureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dispatchTakePictureIntent();
                //longestCommonSubstring("tluszcz","thuszcz");
             /*   textView.setText("");
                String s1 = "thuwzcz";
                String s2 = "thuszcz";
int s=R.string.project_id;
                char[] X=s1.toCharArray();
                char[] Y=s2.toCharArray();
                LongestCommonSubstring obj=new LongestCommonSubstring();
                Toast.makeText(MainActivity.this,"Error: "+obj.lcsByString(s1,s2), Toast.LENGTH_LONG).show();

              */
            }
        });

        detectTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectTextFromImage();
            }
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile=null;
            try{
                photoFile=createImageFile();
            }catch (IOException ex)
            {

            }

            if(photoFile!=null)
            {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.textrecognitionapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
           // Bundle extras = data.getExtras();
           // imageBitmap = (Bitmap) extras.get("data");

           // imageView.setImageURI(photoURI);
         //  imageView.setImageURI(Uri.fromFile(photoFile));
            // BitmapFactory.Options bmOptions = new BitmapFactory.Options();
           // bmOptions.inSampleSize=4;
           // imageBitmap= BitmapFactory.decodeFile(String.valueOf(photoFile),bmOptions);
           // imageView.setImageBitmap(imageBitmap);
           // textView.setText(imageBitmap.getHeight()+"x"+imageBitmap.getWidth());
            performCrop();
            //imageView.setImageURI(photoURI);
        }
        if (requestCode == CROP_PIC && resultCode == RESULT_OK) {
             BitmapFactory.Options bmOptions = new BitmapFactory.Options();
             bmOptions.inSampleSize=1;
             imageBitmap= BitmapFactory.decodeFile(String.valueOf(photoFile),bmOptions);
             imageView.setImageBitmap(imageBitmap);
             textView.setText(imageBitmap.getHeight()+"x"+imageBitmap.getWidth());
            Log.e("save", "do it");

        }


    }

    private void detectTextFromImage() {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);

        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        firebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Error: "+ e.getMessage(), Toast.LENGTH_LONG).show();

                Log.d("Error: ",e.getMessage());
            }
        });
       /*
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();



          firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Error: "+ e.getMessage(), Toast.LENGTH_LONG).show();
            
                Log.d("Error: ",e.getMessage());
            }
        });*/
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {
        produkt prod = new produkt();
        Point p = null;
        double wartoscOdzywcza=0;
        boolean boolTrue=false;
        LongestCommonSubstring obj = new LongestCommonSubstring();
        List<FirebaseVisionText.TextBlock> blockList = firebaseVisionText.getTextBlocks();
        if (blockList.size() == 0) {String text = "ala";
            Toast.makeText(this,text.substring(0,0) , Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "No Text found in image", Toast.LENGTH_LONG).show();
        } else {

            for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {

                //   /*
                for (FirebaseVisionText.Line line : block.getLines())
                    for (FirebaseVisionText.Element elm : line.getElements()) {
Log.e("mojlog", elm.getText()+" pos:"+elm.getCornerPoints()[0]);


                        if (obj.lcsByString(elm.getText(), "tluszcz") > 4 ) {
                            Log.e("mojlog2", elm.getText()+" pos:"+elm.getCornerPoints()[0]);
                            p = new Point(elm.getCornerPoints()[0]);
                            Toast.makeText(this, "tlu: "+GetValueFromFirebaseVision(blockList,p), Toast.LENGTH_LONG).show();
                            double temp = GetValueFromFirebaseVision(blockList,p);
                            if(prod.getTluszcz()<temp)
                                prod.setTluszcz(temp);

                        }
                        else if (obj.lcsByString(elm.getText(), "nasycone") > 6) {
                            p = new Point(elm.getCornerPoints()[0]);
                            prod.setTluszczNasycony(GetValueFromFirebaseVision(blockList,p));
                        }
                        else if (obj.lcsByString(elm.getText(), "weglowodany") > 9) {
                            p = new Point(elm.getCornerPoints()[0]);
                            prod.setWeglowodany(GetValueFromFirebaseVision(blockList,p));
                        }
                        else if (obj.lcsByString(elm.getText(), "cukry") > 3) {
                            p = new Point(elm.getCornerPoints()[0]);
                            prod.setWeglowodanyWTymCukry(GetValueFromFirebaseVision(blockList,p));
                        }
                        else if (obj.lcsByString(elm.getText(), "bialko") > 4) {
                            p = new Point(elm.getCornerPoints()[0]);
                            prod.setBialko(GetValueFromFirebaseVision(blockList,p));
                        }
                        else if (obj.lcsByString(elm.getText(), "sól") >1) {
                            p = new Point(elm.getCornerPoints()[0]);
                            prod.setSol(GetValueFromFirebaseVision(blockList,p));
                        }
                        else if (obj.lcsByString(elm.getText(), "energetyczna") >10) {
                            p = new Point(elm.getCornerPoints()[0]);
                            prod.setKcal(GetValueFromFirebaseVisionKcl(blockList,p));
                        }
                        else if (obj.lcsByString(elm.getText(), "odzywcza") >5) {
                            p = new Point(elm.getCornerPoints()[0]);
                            wartoscOdzywcza=GetValueFromFirebaseVision(blockList,p);
                        }


                        // if(obj.lcsByString(elm.getText(),"16")==2)
                        // {
                        //      text = text+"@"+elm.getText();
                        //       text= text+" position: "+elm.getCornerPoints()[0];
                        //       p=new Point(elm.getCornerPoints()[0]);
                        //textView.setText(text);
                        //  }

                        //text = text+"@"+elm.getText();
                        //textView.setText(text);

                    }
            }

        }

        textView.setText("Wartosc odżywcza w "+wartoscOdzywcza+"g"+
                ", wartosc energetyczna: "+prod.getKcal()+
                ", tluszcze: "+prod.getTluszcz()+
                ", nas:"+prod.getTluszczNasycony()+
                ", wegl:"+prod.getWeglowodany()+
                ", cukry:"+prod.getWeglowodanyWTymCukry()+
                ", bialko:"+prod.getBialko()+
                ", sol:"+prod.getSol());
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private double GetValueFromFirebaseVision(List<FirebaseVisionText.TextBlock> firebaseVisionText, Point p) {
        LongestCommonSubstring obj = new LongestCommonSubstring();
        for (FirebaseVisionText.TextBlock block2 : firebaseVisionText) {
            for (FirebaseVisionText.Line line2 : block2.getLines()){
                for (FirebaseVisionText.Element elm2 : line2.getElements()) {
                    Log.i("mojlog",elm2.getText()+";"+elm2.getCornerPoints()[0].y+";"+p.y);
                    if (p != null && elm2.getCornerPoints()[0].y > (p.y - imageBitmap.getHeight()/20) && elm2.getCornerPoints()[0].y < (p.y + imageBitmap.getHeight()/20)) {
                        Log.i("mojlog4",elm2.getText()+";"+elm2.getCornerPoints()[0].y+";"+p.y);
                        if (obj.isDouble(elm2.getText()) ||
                                (elm2.getText().length()>1 && obj.isDouble(elm2.getText().substring(0, elm2.getText().length() - 1)))) {

                            return obj.returnDouble(elm2.getText());

                        }

                    }

                }
        }}
        return 99;
    }

    private double GetValueFromFirebaseVisionKcl(List<FirebaseVisionText.TextBlock> firebaseVisionText, Point p) {
        LongestCommonSubstring obj = new LongestCommonSubstring();
        double doubleToReturn=9999.0;
        for (FirebaseVisionText.TextBlock block2 : firebaseVisionText) {
            for (FirebaseVisionText.Line line2 : block2.getLines()){
                for (FirebaseVisionText.Element elm2 : line2.getElements()) {
                    Log.i("mojlog",elm2.getText()+";"+elm2.getCornerPoints()[0].y+";"+p.y);
                    if (p != null && elm2.getCornerPoints()[0].y > (p.y - 40) && elm2.getCornerPoints()[0].y < (p.y + 40)) {
                        Log.i("loggg",elm2.getText());
                        String temp=elm2.getText().replaceAll("[^0-9]+", "");
                        Log.i("loggg2",temp+"long:"+temp.length());
                        if(temp.length()>1) {
                            if (Double.parseDouble(temp) < doubleToReturn) ;
                            doubleToReturn = Double.parseDouble(temp);
                        }
                        }

                    }

                }
            }
        return doubleToReturn;
    }



    private void performCrop() {
        try {
            photoFile2=createImageFile();
            Intent cropIntent = new Intent("com.android.camera.action.CROP");

           // File f = new File(picUri);
            Uri contentUri  = FileProvider.getUriForFile(this,
                                    "com.example.textrecognitionapp.fileprovider",
                                   photoFile);
            getApplicationContext().grantUriPermission("com.android.camera",
                    contentUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


            cropIntent.setDataAndType(contentUri, "image/*");
            cropIntent.putExtra("crop", "true");
            //cropIntent.putExtra("aspectX", 2);
           // cropIntent.putExtra("aspectY", 1);
           // cropIntent.putExtra("outputX", 200); //512
           // cropIntent.putExtra("outputY", 200); //512
            cropIntent.putExtra("noFaceDetection", true);
            cropIntent.putExtra("return-data", false);
            cropIntent.putExtra ("outputFormat", Bitmap.CompressFormat.JPEG.name ());
            //cropIntent.putExtra (MediaStore.EXTRA_OUTPUT, photoURI);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(cropIntent, CROP_PIC);
        }
        catch (ActivityNotFoundException e) {
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}