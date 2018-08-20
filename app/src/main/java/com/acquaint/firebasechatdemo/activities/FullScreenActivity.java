package com.acquaint.firebasechatdemo.activities;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;


import com.acquaint.firebasechatdemo.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by acquaint on 12/5/18.
 */

public class FullScreenActivity extends AppCompatActivity {
    String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_pdf);

        PDFView pdfView1 = (PDFView) findViewById(R.id.pdfViewOnclick);
        VideoView vv_file_onclick = (VideoView) findViewById(R.id.vv_file_onclick);

        url = getIntent().getStringExtra("url");
        int flag = getIntent().getIntExtra("view", 0);
        if (flag == 1) {
            pdfView1.setVisibility(View.GONE);
            vv_file_onclick.setVisibility(View.VISIBLE);
            vv_file_onclick.setVideoURI(Uri.parse(url));
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(vv_file_onclick);
            vv_file_onclick.setMediaController(mediaController);
            vv_file_onclick.start();
        } else {
            pdfView1.setVisibility(View.VISIBLE);
            vv_file_onclick.setVisibility(View.GONE);
                downloadFile(url,pdfView1);

        }

    }

    public void downloadFile(String mURL, PDFView pdfView) {
        //  progressDialog.setVisibility(View.VISIBLE);
        DownloadFileTask task = new DownloadFileTask(
                this,
                mURL, pdfView,
                "/download/pdf_file.pdf");
        task.startTask();
    }

    public class DownloadFileTask {
        public static final String TAG = "DownloadFileTask";

        private FullScreenActivity context;
        private GetTask contentTask;
        private String url;
        private String fileName;
        private PDFView pdfView;

        public DownloadFileTask(FullScreenActivity context, String url, PDFView pdfView, String fileName) {
            this.context = context;
            this.url = url;
            this.fileName = fileName;
            this.pdfView = pdfView;
        }

        public void startTask() {
            doRequest();
        }

        private void doRequest() {
            contentTask = new GetTask();
            contentTask.execute();
        }

        private class GetTask extends AsyncTask<String, Integer, String> {

            @Override
            protected String doInBackground(String... arg0) {
                int count;
                try {
                    Log.d(TAG, "url = " + url);
                    URL _url = new URL(url);
                    URLConnection conection = _url.openConnection();
                    conection.connect();
                    InputStream input = new BufferedInputStream(_url.openStream(),
                            8192);
                    OutputStream output = new FileOutputStream(
                            Environment.getExternalStorageDirectory() + fileName);
                    byte data[] = new byte[1024];
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }
                return null;
            }

            protected void onPostExecute(String data) {
                onFileDownloaded(pdfView);
            }
        }

    }

    public void onFileDownloaded(final PDFView pdfView) {

        // progressBar.setVisibility(View.GONE);
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + "/download/pdf_file.pdf");
        if (file.exists()) {
            pdfView.fromFile(file)
                    //.pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                    .enableSwipe(true)
                    .swipeHorizontal(true)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .enableAnnotationRendering(true)
                    .password(null)
                    .scrollHandle(null)
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            pdfView.setMinZoom(1f);
                            pdfView.setMidZoom(5f);
                            pdfView.setMaxZoom(10f);
                            pdfView.zoomTo(2f);
                            pdfView.scrollTo(100, 0);
                            pdfView.moveTo(0f, 0f);
                        }
                    })
                    .load();

        }
    }
}
