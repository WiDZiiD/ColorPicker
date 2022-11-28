package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // One Button
    Button BSelectImage;

    // One Preview Image
    DrawImageView IVPreviewImage;

    SeekBar seekBarX;
    SeekBar seekBarY;

    EditText coordX;
    EditText coordY;

    TextView red;
    TextView green;
    TextView blue;
    TextView hexValue;

    View colorExtracted;


    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        // register the UI widgets with their appropriate IDs
        BSelectImage = findViewById(R.id.button);
        IVPreviewImage = findViewById(R.id.imageGallery);
        seekBarX=findViewById(R.id.seekBarX);
        seekBarY=findViewById(R.id.seekBarY);
        coordX=findViewById(R.id.ediTextX);
        coordY=findViewById(R.id.ediTextY);
        red=findViewById(R.id.redText);
        green=findViewById(R.id.greenText);
        blue=findViewById(R.id.blueText);
        hexValue = findViewById(R.id.hexValue);
        colorExtracted=findViewById(R.id.colorPixel);

        // handle the Choose Image button to trigger
        // the image chooser function
        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        seekBarX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            // increment or decrement on process changed
            // increase the textsize
            // with the value of progress
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                coordX.setText(Integer.toString(progress));
                changeColor();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // This method will automatically
                // called when the user touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // This method will automatically
                // called when the user
                // stops touching the SeekBar
            }
        });

        IVPreviewImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int x =(int) motionEvent.getX();
                int y =(int) motionEvent.getY();

                seekBarX.setProgress(x);
                seekBarY.setProgress(y);

                return true;
            }
        });

        seekBarY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            // increment or decrement on process changed
            // increase the textsize
            // with the value of progress
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                coordY.setText(Integer.toString(progress));
                changeColor();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // This method will automatically
                // called when the user touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // This method will automatically
                // called when the user
                // stops touching the SeekBar
            }
        });

        coordX.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                seekBarX.setProgress(Integer.parseInt(coordX.getText().toString()));
            }
        });
        coordY.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                seekBarY.setProgress(Integer.parseInt(coordY.getText().toString()));
            }
        });


    }


    // this function is triggered when
    // the Select Image Button is clicked
    private void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }


    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        IVPreviewImage.setImageBitmap(
                                selectedImageBitmap);
                        int[] position = getBitmapPositionInsideImageView(IVPreviewImage);
                        seekBarX.setMax(position[2]-1);
                        seekBarY.setMax(position[3]-1);
                        //coordY.setText(Integer.toString(seekBarX.getMax()-10));
                        //coordX.setText(Integer.toString(seekBarX.getMax()-10));
                    }
                }
            });


    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    IVPreviewImage.setImageURI(selectedImageUri);
                }
            }
        }
    }



    public void changeColor(){

        Bitmap bitmap = ((BitmapDrawable)IVPreviewImage.getDrawable()).getBitmap();

        int[] position = getBitmapPositionInsideImageView(IVPreviewImage);

        float floorWidth = IVPreviewImage.getWidth();
        float floorHeight = IVPreviewImage.getHeight();

        float proportionateWidth = bitmap.getWidth() / floorWidth;
        float proportionateHeight = bitmap.getHeight() / floorHeight;

        float scale = Math.max(proportionateWidth, proportionateHeight);

        float x = (seekBarX.getProgress())*scale;
        float y = (seekBarY.getProgress())*scale;

        int pixel = bitmap.getPixel(Math.round(x),Math.round(y));
        int redValue = Color.red(pixel);
        int blueValue = Color.blue(pixel);
        int greenValue = Color.green(pixel);

        colorExtracted.setBackgroundColor(pixel);

        red.setText(Integer.toString(redValue));
        green.setText(Integer.toString(greenValue));
        blue.setText(Integer.toString(blueValue));

        IVPreviewImage.left = seekBarX.getProgress()+position[0]-10;
        IVPreviewImage.top = seekBarY.getProgress()+position[1]-10;
        IVPreviewImage.right = seekBarX.getProgress()+position[0]+10;
        IVPreviewImage.bottom = seekBarY.getProgress()+position[1]+10;

        IVPreviewImage.invalidate();
        IVPreviewImage.drawRect = true;

        String hex = String.format("#%02x%02x%02x", redValue, greenValue,blueValue);
        hexValue.setText(hex);


    }

    /**
     * Returns the bitmap position inside an imageView.
     * @param imageView source ImageView
     * @return 0: left, 1: top, 2: width, 3: height
     */
    public static int[] getBitmapPositionInsideImageView(ImageView imageView) {
        int[] ret = new int[4];

        if (imageView == null || imageView.getDrawable() == null)
            return ret;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - actH)/2;
        int left = (int) (imgViewW - actW)/2;

        ret[0] = left;
        ret[1] = top;

        return ret;
    }
}