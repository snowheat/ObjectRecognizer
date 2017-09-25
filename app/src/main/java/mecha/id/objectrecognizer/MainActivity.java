package mecha.id.objectrecognizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.FileNotFoundException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.x;
import static android.R.attr.y;

public class MainActivity extends AppCompatActivity {

    ImageButton imageButton;
    ImageView imageView;
    SeekBar seekBar;
    TextView totalObjekTextView;
    String LOG = "LOG1";
    Bitmap tresholdBitmap,grayscaleBitmap;
    ToggleButton toggleButton;
    Button hitungTotalObjekButton;
    int totalTresholdObjects = 0;
    boolean invertTreshold = false;
    int tresholdImgArray[][];
    private List<String> tresholdObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG,"onClick()");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");

                startActivityForResult(intent,0);
            }
        });

        imageView = (ImageView) findViewById(R.id.imageView);
        totalObjekTextView = (TextView) findViewById(R.id.totalObjekTextView);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invertTreshold = !invertTreshold;
                Log.d(LOG,"invertTreshold : "+String.valueOf(invertTreshold));

            }
        });

        seekBar = (SeekBar) findViewById(R.id.tresholdSeekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(LOG,"onProgressChanged : "+String.valueOf(progress));
                imageView.setImageBitmap(toBinary(grayscaleBitmap,progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        hitungTotalObjekButton = (Button) findViewById(R.id.hitungTotalObjekButton);
        hitungTotalObjekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalTresholdObjects = getTotalTresholdObjects();
                totalObjekTextView.setText(String.valueOf(totalTresholdObjects));
            }
        });

        Log.i(LOG,"onCreate()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(LOG,"onActivityResult() "+requestCode);
        super.onActivityResult(requestCode, resultCode, data);

        //if(requestCode == RESULT_CANCELED) return;

        ParcelFileDescriptor fd;

        try {
            fd = getContentResolver().openFileDescriptor(data.getData(), "r");
            Log.i(LOG,"berhasil ambil gambar");
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            Log.e(LOG,e.getMessage());
            return;
        }
        Log.i(LOG,"onActivityResult() bmp");
        Bitmap bmp = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());

        Log.i(LOG,String.valueOf(imageButton.getWidth())+" "+String.valueOf(imageButton.getHeight()));
        Log.i(LOG,String.valueOf(bmp.getWidth())+" "+String.valueOf(bmp.getHeight()));

        float ratio = Math.min(
                (float) imageButton.getWidth() / bmp.getWidth(),
                (float) imageButton.getHeight() / bmp.getHeight());

        int width = Math.round((float) ratio * bmp.getWidth());
        int height = Math.round((float) ratio * bmp.getHeight());
        Log.i(LOG,String.valueOf(ratio)+" "+String.valueOf(width)+" "+String.valueOf(height));

        Bitmap newBitmap = Bitmap.createScaledBitmap(bmp, width,height, true);
        imageButton.setImageBitmap(newBitmap);


        Log.i(LOG,String.valueOf(imageView.getWidth())+" "+String.valueOf(imageView.getHeight()));
        Log.i(LOG,String.valueOf(bmp.getWidth())+" "+String.valueOf(bmp.getHeight()));

        ratio = Math.min(
                (float) imageView.getWidth() / bmp.getWidth(),
                (float) imageView.getHeight() / bmp.getHeight());

        width = Math.round((float) ratio * bmp.getWidth());
        height = Math.round((float) ratio * bmp.getHeight());
        Log.i(LOG,"B "+String.valueOf(ratio)+" "+String.valueOf(width)+" "+String.valueOf(height));

        newBitmap = Bitmap.createScaledBitmap(bmp, width,height, true);


        grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(grayscaleBitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(newBitmap, 0, 0, paint);

        grayscaleBitmap = transformasiLUT(grayscaleBitmap);



        tresholdBitmap = Bitmap.createBitmap(grayscaleBitmap);

        tresholdImgArray = new int[tresholdBitmap.getHeight()][tresholdBitmap.getWidth()];

        imageView.setImageBitmap(grayscaleBitmap);
    }

    private Bitmap transformasiLUT(Bitmap grayscaleBitmap) {

        return grayscaleBitmap;
    }


    private int getTotalObject(){
        return 0;
    }

    public Bitmap toBinary(Bitmap bmpOriginal,int tresholdParam) {
        int width, height, threshold, imgArrayColumn, imgArrayRow;

        height = grayscaleBitmap.getHeight();
        width = grayscaleBitmap.getWidth();
        threshold = tresholdParam;

        int a,r,g,b,intFromRGBA;
        int[] colorFromInt;

        int[] pixels = new int[(width*height)];
        grayscaleBitmap.getPixels(pixels, 0, width,0,0,width,height);

        //https://stackoverflow.com/questions/12100580/converting-pixel-color-bytes-to-bits-bits-to-bytes
        //https://stackoverflow.com/questions/2534116/how-to-convert-get-rgbx-y-integer-pixel-to-colorr-g-b-a-in-java

        Log.e(LOG,"Pixel array length : "+String.valueOf(pixels.length));


        try{
            for(int i=0;i<pixels.length;i++){
                imgArrayRow = i/width;
                imgArrayColumn = (i+width) % width;


                //Log.e(LOG,"tresholdImgArray["+String.valueOf(imgArrayColumn)+"]["+String.valueOf(imgArrayRow)+"]");

                colorFromInt = getColorFromInt(pixels[i]);

                a = colorFromInt[0];
                r = colorFromInt[1];
                g = colorFromInt[2];
                b = colorFromInt[3];


                if(!invertTreshold){
                    if(r<tresholdParam){
                        intFromRGBA = getIntFromARGB(a,0,0,0);
                        tresholdImgArray[imgArrayRow][imgArrayColumn] = 1;
                    }else{
                        intFromRGBA = getIntFromARGB(a,255,255,255);
                        tresholdImgArray[imgArrayRow][imgArrayColumn] = 0;
                    }
                }else{
                    if(r<tresholdParam){
                        intFromRGBA = getIntFromARGB(a,255,255,255);
                        tresholdImgArray[imgArrayRow][imgArrayColumn] = 0;
                    }else{
                        intFromRGBA = getIntFromARGB(a,0,0,0);
                        tresholdImgArray[imgArrayRow][imgArrayColumn] = 1;
                    }
                }

                //Clean the edge of picture
                if(imgArrayRow==0 || imgArrayColumn==0 || (imgArrayRow+1)==height || (imgArrayColumn+1)==width){
                    intFromRGBA = getIntFromARGB(a,255,255,255);
                    tresholdImgArray[imgArrayRow][imgArrayColumn] = 0;
                }



                pixels[i] = intFromRGBA;

                //Log.e(LOG,String.valueOf(pixels[i])+" "+String.valueOf(r)
                //        +" "+String.valueOf(g)+" "+String.valueOf(b)+" "+String.valueOf(a));

            }
        }catch(Exception e){
            Log.e(LOG,"ERROR: "+e.getMessage());
        }


        String imgArrayMatrixRow;

        /*
        for(int j=0;j<tresholdBitmap.getHeight();j++){
            imgArrayMatrixRow = "";
            for(int i=0;i<tresholdBitmap.getWidth();i++){
                imgArrayMatrixRow += String.valueOf(tresholdImgArray[i][j]);
            }

            Log.e(LOG,String.valueOf(j)+":"+imgArrayMatrixRow);
        }
        */

        Log.e(LOG,"j : "+String.valueOf(tresholdImgArray.length)+" i : "+String.valueOf(tresholdImgArray[0].length));

        tresholdBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        tresholdBitmap.copyPixelsFromBuffer(IntBuffer.wrap(pixels));

        return tresholdBitmap;
    }

    private int[] getColorFromInt(int pixels) {
        int[] colorFromInt = new int[4];
        colorFromInt[0] = (pixels >> 24) & 0xFF;
        colorFromInt[1] = (pixels >> 16) & 0xFF;
        colorFromInt[2] = (pixels >> 8) & 0xFF;
        colorFromInt[3] = pixels & 0xFF;

        return colorFromInt;
    }

    private int getIntFromARGB(int a, int r, int g, int b) {
        int intFromARGB;
        intFromARGB = (a << 24) | (r << 16) | (g << 8) | b;

        return intFromARGB;
    }

    private int getTotalTresholdObjects() {
        Log.e(LOG, "getTotalTresholdObjects() " + String.valueOf(tresholdBitmap.getWidth()) + " " + String.valueOf(tresholdBitmap.getHeight()));
        Log.e(LOG, "getTotalTresholdObjects() " + String.valueOf(tresholdImgArray[0].length) + " " + String.valueOf(tresholdImgArray.length));


        //tresholdImgArray = getCleanedImgArray(tresholdImgArray);

        TresholdObjectCounter tresholdObjectCounter = new TresholdObjectCounter(tresholdImgArray);
        totalTresholdObjects = tresholdObjectCounter.getTotalTresholdObjects();

        return totalTresholdObjects;
    }

    private int[][] getCleanedImgArray(int[][] tresholdImgArray) {

        return tresholdImgArray;
    }
    /*
    public void getTresholdObject() {

        int firstFoundX=0,firstFoundY=0,found = 0;
        List<Integer[][]> edgeCoordinates = new ArrayList<Integer[][]>();

        outerLoop:
        for(int j=1;j<tresholdImgArray.length-1;j++){
            for(int i=1;i<tresholdImgArray[0].length-1;i++){
                if(tresholdImgArray[j][i]==1){

                    firstFoundX = i;
                    firstFoundY = j;
                    found = 1;
                    break outerLoop;
                }
            }
        }

        if(found>0){
            Log.e(LOG,"Found x : "+String.valueOf(firstFoundX)+" y : "+String.valueOf(firstFoundY));

            getEdgeCoordinates(firstFoundX,firstFoundY,firstFoundX,firstFoundY,4);

            floodFill(x,y);
        }
    }
    */

    private void getEdgeCoordinates(int firstFoundX, int firstFoundY, int currentXParam, int currentYParam, int initNextDirection) {



    }

    private void floodFill(int x, int y) {

    }


}
