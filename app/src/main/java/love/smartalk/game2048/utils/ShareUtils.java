package love.smartalk.game2048.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import love.smartalk.game2048.R;


public class ShareUtils {

    private ShareUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static Bitmap captureScreen(Activity activity) {
        activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
        Bitmap bitmap = activity.getWindow().getDecorView().getDrawingCache();
        return bitmap;
    }



    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    public static String getLove2048Dir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "love2048";
    }

    public static String saveLove2048Capture(Activity activity) throws FileNotFoundException {
        if (!isSDCardEnable()){
            Toast.makeText(activity,R.string.image_save_failed,Toast.LENGTH_SHORT).show();
            return "";
        }
        File love2048Dir = new File(getLove2048Dir());
        if (!love2048Dir.exists())
            love2048Dir.mkdir();
        File file = new File(getLove2048Dir(),"love2048.jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {

        }
        FileOutputStream outputStream = new FileOutputStream(file);
        captureScreen(activity).compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static String saveWeChatPay(Activity activity) {
        if (!isSDCardEnable()){
            Toast.makeText(activity, R.string.image_save_failed,Toast.LENGTH_SHORT).show();
            return "";
        }
        File love2048Dir = new File(getLove2048Dir());
        if (!love2048Dir.exists())
            love2048Dir.mkdir();
        File file = new File(getLove2048Dir(),"wechat.jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {

        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),R.drawable.weixin);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
