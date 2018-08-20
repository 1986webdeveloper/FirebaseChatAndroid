package com.acquaint.firebasechatdemo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by acquaint on 16/8/18.
 */

public class GlobalData {
    private static final String TAG = "Global Data";

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static boolean isNetworkAvailable(Context context) {
        final Context appContext = context;
        ConnectivityManager cm =
                (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    public static boolean isFileOfRequiredSize(String filepath) {
        boolean temp = false;
        File file = new File(filepath);
        String type = GlobalData.isImageFile(filepath);
        // Get length of file in bytes
        long fileSizeInBytes = file.length();
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;
        int fileLength;
        if (type.equalsIgnoreCase("image/*")) {
            fileLength = 10; //10MB
        } else {
            fileLength = 20; //20MB
        }
        if (fileSizeInMB <= fileLength)
            temp = true;

        return temp;
    }
    public static String isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        Log.e(TAG, "isImageFile: " + mimeType);
        if (mimeType != null) {
            if (mimeType.startsWith("image")) {
                return "image/*";
            } else if (mimeType.startsWith("video")) {
                return "video/*";
            } else {
                return "image/*";
            }
        } else {
            return "image/*";
        }
    }
    public static Uri getImageContentUri(Context context, String filePath) {
        // String filePath = imageFile.getAbsolutePath();
        File file = new File(filePath);
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "title", null);
        return Uri.parse(path);
    }

    public static Bitmap getBitmapFromURL(final String urlS) {

        final Bitmap[] bitmapFrmUrlNew = new Bitmap[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL(urlS);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap bitmapFrmUrl = BitmapFactory.decodeStream(input);
                    bitmapFrmUrlNew[0] =bitmapFrmUrl;

                } catch (IOException e) {
                    e.printStackTrace();
                    bitmapFrmUrlNew[0] =null;
                }
            }
        }).start();


        return bitmapFrmUrlNew[0];
    }
    public static String getTimeElapsed(String givenDate) {
        //   String dateNew= getDate(givenDate);
        //   String dateNew = convertInLocalTime(givenDate);
        String dateNew =givenDate;
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy (HH:mm:ss)");
        String niceDateStr = "0 hours ago";
        try {
            Date date = inputFormat.parse(dateNew);
            Date now = new Date();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - date.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - date.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - date.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - date.getTime());
//
//          System.out.println(TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + " milliseconds ago");
//          System.out.println(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " minutes ago");
//          System.out.println(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " hours ago");
//          System.out.println(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago");

            if (seconds < 60) {
                niceDateStr = seconds + " seconds ago";
                //System.out.println(seconds+" seconds ago");
            } else if (minutes < 60) {
                niceDateStr = minutes + " minutes ago";
                //System.out.println(minutes+" minutes ago");
            } else if (hours < 24) {
                niceDateStr = hours + " hours ago";
                //System.out.println(hours+" hours ago");
            } else {
                niceDateStr = days + " days ago";
                //System.out.println(days+" days ago");
            }
            /*niceDateStr = DateUtils.getRelativeTimeSpanString(date.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS).toString();*/
        } catch (Exception e) {
            //  Toast.makeText(mContext,"Exception"+e,Toast.LENGTH_LONG).show();

        }
        return niceDateStr;

    }
    public static Bitmap generateImageFromPdf(Uri pdfUri, Context context, ImageView thumbnail) {
        int pageNumber = 0;

        Bitmap bitmap=null;
        PdfiumCore pdfiumCore = new PdfiumCore(context);
        try {

            ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);

            saveImage(bmp,context,thumbnail);


            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch(Exception e) {

            e.printStackTrace();

        }
        return bitmap;

    }
    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/PDF";
    public static void saveImage(Bitmap bmp,Context context,ImageView thumbnail) {
        FileOutputStream out = null;
        File file=null;
        try {
            File folder = new File(FOLDER);
            if(!folder.exists())
                folder.mkdirs();
            file=File.createTempFile("pdf","png",folder);

            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            Uri uri = Uri.fromFile(file);
            Glide.with(context).load(uri)
                    .apply(new RequestOptions().override(300, 300).centerCrop().skipMemoryCache(true).error(android.R.drawable.stat_notify_error))

                    .into(thumbnail);
        } catch (Exception e) {
            //todo with exception
            Log.i("Exception","Pdf"+e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                //todo with exception
            }
        }

    }

}
