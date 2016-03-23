package ivanandrianto.com.tubes1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera extends Activity {
    Button b1;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        b1=(Button)findViewById(R.id.button);
        iv=(ImageView)findViewById(R.id.imageView);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(bp);
            storeImage(bp);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private  File getOutputMediaFile(){
        File dir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        if (! dir.exists()){
            if (! dir.mkdirs()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String mImageName="IMAGE_"+ timeStamp +".jpg";
        File myFile;
        myFile = new File(dir.getPath() + File.separator + mImageName);
        return myFile;
    }

    private void storeImage(Bitmap image) {
        File imgFile = getOutputMediaFile();
        if (imgFile == null) {
            Log.d("z", "Error creating file: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(imgFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("z", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("z", "Error accessing file: " + e.getMessage());
        }
    }
}
