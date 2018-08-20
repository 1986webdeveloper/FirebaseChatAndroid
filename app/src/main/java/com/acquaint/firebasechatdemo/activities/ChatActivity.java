package com.acquaint.firebasechatdemo.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.acquaint.firebasechatdemo.BuildConfig;
import com.acquaint.firebasechatdemo.Constants;
import com.acquaint.firebasechatdemo.GlobalData;
import com.acquaint.firebasechatdemo.R;
import com.acquaint.firebasechatdemo.helper.PhotoFullPopupWindow;
import com.acquaint.firebasechatdemo.model.ChatMessage;
import com.acquaint.firebasechatdemo.model.Upload;
import com.acquaint.firebasechatdemo.multiple_media_picker.Gallery;
import com.bumptech.glide.Glide;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.master.permissionhelper.PermissionHelper;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class ChatActivity extends AppCompatActivity {
    private static final int SIGN_IN_REQUEST_CODE = 3; //1 before
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_PIC = 2;
    private static final String TAG = ChatActivity.class.getSimpleName();
    String uname1,uname2;
    String room_type_1,room_type_2;
    int room_type;

    private FirebaseListAdapter<ChatMessage> adapter;
    ImageButton iv_attachment;
    private Uri filePath;
    EditText input;
    private StorageReference storageReference;
    private DatabaseReference mDatabase,tokenDb;
    ProgressDialog progressDialog;
    Upload upload;
    FloatingActionButton fab_image;
    Context context = this;
    RelativeLayout rl_main;
    ListView listOfMessages=null;
    PermissionHelper permissionHelper;
    /* int OPEN_MEDIA_PICKER = 2;
     int CAMERA_PIC_REQUEST = 1;*/
    String fileExtension=null;
    String regIdUname2;

    //Notification
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        input = (EditText) findViewById(R.id.input);
        listOfMessages = (ListView) findViewById(R.id.list_of_messages);
        getDatafromSharedPref();
        uname2=getIntent().getStringExtra("uname");
        getDeviceTokenforUname2();
        getSupportActionBar().setTitle(uname2);
        room_type_1 = uname1 + "_" + uname2;
        room_type_2 = uname2 + "_" + uname1;
        storageReference = FirebaseStorage.getInstance().getReference("chat");
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        fab_image = (FloatingActionButton) findViewById(R.id.fab_image);
        rl_main = (RelativeLayout) findViewById(R.id.activity_main);
        rl_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });
        fab_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                showDialogToSelectCameraOrGallery();




            }
        });
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            // Load chat room contents
            displayChatMessages();
        }
        FloatingActionButton fab =
                (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                if (input.getText().length() == 0) {


                    Toast.makeText(getApplicationContext(), "Please Enter Text", Toast.LENGTH_LONG).show();
                } else {
                    FirebaseDatabase.getInstance().getReference("chat").getRef()
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(room_type_1)){
                                        FirebaseDatabase.getInstance()
                                                .getReference("chat").child(room_type_1)
                                                .push()
                                                .setValue(new ChatMessage(input.getText().toString(),
                                                        uname1)
                                                );


                                        input.setText("");

                                    }
                                    else if(dataSnapshot.hasChild(room_type_2)){
                                        FirebaseDatabase.getInstance()
                                                .getReference("chat").child(room_type_2)
                                                .push()
                                                .setValue(new ChatMessage(input.getText().toString(),
                                                       uname1)
                                                );


                                        input.setText("");
                                    }
                                    else {
                                        FirebaseDatabase.getInstance()
                                                .getReference("chat").child(room_type_1)
                                                .push()
                                                .setValue(new ChatMessage(input.getText().toString(),
                                                        uname1)
                                                );

                                        input.setText("");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });






                }
                // Clear the input

            }
        });

        //Download file
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference("chat").child(Constants.DATABASE_PATH_UPLOADS);

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
                progressDialog.dismiss();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    upload = postSnapshot.getValue(Upload.class);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void getDeviceTokenforUname2() {
        tokenDb=FirebaseDatabase.getInstance().getReference("tokens").child(uname2);
        Log.i("Uname2",uname2);
    //    Query query=tokenDb.orderByChild("messageText").equalTo(uname2);
        tokenDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
                progressDialog.dismiss();
                ArrayList<String> token = new ArrayList<>();
                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    token.add(postSnapshot.getValue().toString());
                    /*ChatMessage chatMessage = postSnapshot.getValue(ChatMessage.class);
                    regIdUname2=snapshot.getValue("messageUser");
                    postSnapshot.getValue();
                    token*/


                }
                if(token.size()>0){
                    regIdUname2=token.get(2);
                }

        //        Log.i("tokenOfUname2",regIdUname2);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("DatabaseError",""+databaseError.toString());
                progressDialog.dismiss();
            }
        });

    }


    private void getDatafromSharedPref() {
        SharedPreferences sharedPreferences= getSharedPreferences("my prefs", Context.MODE_PRIVATE);
        String userId=sharedPreferences.getString("id","");

       String fname=sharedPreferences.getString("fname","");
       String lname=sharedPreferences.getString("lname","");
       uname1=fname+" "+lname;


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                displayChatMessages();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }
        if(requestCode== CAPTURE_PIC && resultCode==RESULT_OK && data!=null){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            filePath= GlobalData.getImageUri(ChatActivity.this, photo);
            uploadFile();
        }

        if(requestCode== PICK_IMAGE_REQUEST && resultCode==RESULT_OK){

            ArrayList<String> selectionResult1 = data.getStringArrayListExtra("result");
            Log.e("Data","from Open Picker"+selectionResult1.get(0));
//       //     filePath=Uri.parse(selectionResult1.get(0));
            if(selectionResult1.get(0).toLowerCase().contains("pdf")){
                File file = new File(selectionResult1.get(0));
                Log.e("File","file"+file.getPath());

          //     filePath = FileProvider.getUriForFile(ChatActivity.this,getApplicationContext().getPackageName(),file);
               filePath = FileProvider.getUriForFile(ChatActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);

            //   filePath=Uri.parse("http://"+selectionResult1.get(0));
                fileExtension="pdf";

            }
            else if(selectionResult1.get(0).toLowerCase().contains("pdf")){
                filePath= GlobalData.getImageContentUri(ChatActivity.this,selectionResult1.get(0));
            }
            else{
                File file = new File(selectionResult1.get(0));
                filePath = FileProvider.getUriForFile(ChatActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);
            }
          //


            uploadFile();
        }





    }



    //storage code
    //this method will upload the file
    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //added bilal khan code
            StorageReference sRef;
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            if(fileExtension!=null){
                sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + "pdf");
            }
            else {
                sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(filePath));
            }

            //getting the storage reference

            Log.e("storage", "reference" + sRef.toString());


            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();
                            //upload.setUrl(taskSnapshot.getDownloadUrl().toString());


                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded " + taskSnapshot.getDownloadUrl().toString(), Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                            Upload upload = new Upload(input.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString());

                            //adding an upload to firebase database
                            String uploadId = mDatabase.child("chat").push().getKey();
                            mDatabase.child(uploadId).setValue(upload);
                            //dwonload pic
                            try {
                                Log.e("Here", "URI" + taskSnapshot.getDownloadUrl().toString());
                                final URL url = new URL(taskSnapshot.getDownloadUrl().toString());
                                if (url != null) {
                                    FirebaseDatabase.getInstance().getReference("chat").getRef()
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.hasChild(room_type_1)){
                                                        FirebaseDatabase.getInstance()
                                                                .getReference("chat").child(room_type_1)
                                                                .push()
                                                                .setValue(new ChatMessage(url.toString(),
                                                                        uname1)
                                                                );
                                                        input.setText("");
                                                    }
                                                    else if(dataSnapshot.hasChild(room_type_2)){
                                                        FirebaseDatabase.getInstance()
                                                                .getReference("chat").child(room_type_2)
                                                                .push()
                                                                .setValue(new ChatMessage(url.toString(),
                                                                       uname1)
                                                                );
                                                        input.setText("");
                                                    }
                                                    else {
                                                        FirebaseDatabase.getInstance()
                                                                .getReference("chat").child(room_type_1)
                                                                .push()
                                                                .setValue(new ChatMessage(url.toString(),
                                                                        uname1)
                                                                );
                                                        input.setText("");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                }

                            } catch (Exception e) {

                            }


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }

    }

    //if there is not any file
    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void displayChatMessages() {


        final ListView listOfMessages=(ListView)findViewById(R.id.list_of_messages);

        FirebaseDatabase.getInstance().getReference("chat").getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(room_type_1)){
                            adapter = new FirebaseListAdapter<ChatMessage>(getApplicationContext(), ChatMessage.class,
                                    R.layout.message, FirebaseDatabase.getInstance().getReference("chat").child(room_type_1).orderByChild("messageTime")) {
                                @Override
                                protected void populateView(View v, ChatMessage model, int position) {
                                    // Get references to the views of message.xml

                                    populate(v, model, position);


                                }
                            };
                            listOfMessages.setAdapter(adapter);
                            listOfMessages.setSelection(listOfMessages.getCount() - 1);

                        }
                        else if(dataSnapshot.hasChild(room_type_2)){
                            adapter = new FirebaseListAdapter<ChatMessage>(getApplicationContext(), ChatMessage.class,
                                    R.layout.message, FirebaseDatabase.getInstance().getReference("chat").child(room_type_2).orderByChild("messageTime")) {
                                @Override
                                protected void populateView(View v, ChatMessage model, int position) {
                                    // Get references to the views of message.xml

                                    populate(v, model, position);



                                }
                            };
                            listOfMessages.setAdapter(adapter);
                            listOfMessages.setSelection(listOfMessages.getCount() - 1);

                        }
                        else {
                            adapter = new FirebaseListAdapter<ChatMessage>(getApplicationContext(), ChatMessage.class,
                                    R.layout.message, FirebaseDatabase.getInstance().getReference("chat").child(room_type_1).orderByChild("messageTime")) {
                                @Override
                                protected void populateView(View v, ChatMessage model, int position) {
                                    // Get references to the views of message.xml

                                    populate(v, model, position);

                                }
                            };

                            listOfMessages.setAdapter(adapter);
                            listOfMessages.setSelection(listOfMessages.getCount() - 1);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });








    }

    private void populate(View v, ChatMessage model, int position) {

        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        TextView messageUser = (TextView) v.findViewById(R.id.message_user);
        TextView messageTime = (TextView) v.findViewById(R.id.message_time);
        final ImageView attachment = (ImageView) v.findViewById(R.id.iv_file);
        VideoView video = (VideoView) v.findViewById(R.id.vv_file);
        PDFView pdfView = (PDFView) v.findViewById(R.id.pdfView);
        RelativeLayout rl_vv_file=v.findViewById(R.id.rl_vv_file);



        if (model.getMessageText() != null) {


            if (model.getMessageText().toLowerCase().contains(".jpg") || model.getMessageText().toLowerCase().contains(".png") || model.getMessageText().toLowerCase().contains(".jpeg")) {

                attachment.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.GONE);
                video.setVisibility(View.GONE);
                pdfView.setVisibility(View.GONE);
                rl_vv_file.setVisibility(View.GONE);

                final String url = model.getMessageText();

                Glide.with(ChatActivity.this)
                        .load(model.getMessageText())
                        .into(attachment);
                attachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Code to show image in full screen:
                        new PhotoFullPopupWindow(getApplicationContext(), R.layout.popup_photo_full, attachment, url, null);
                    }
                });


            } else if (model.getMessageText().toLowerCase().contains(".pdf")) {
                attachment.setVisibility(View.GONE);
                messageText.setVisibility(View.GONE);
                video.setVisibility(View.GONE);
                pdfView.setVisibility(View.VISIBLE);
                rl_vv_file.setVisibility(View.GONE);



                final String url = model.getMessageText().toString();
                downloadFile(model.getMessageText().toString(), pdfView);


                pdfView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, FullScreenActivity.class);
                        i.putExtra("url", url);
                        i.putExtra("view", 0); //1-video 0-pdf
                        startActivity(i);


                    }
                });


            } else if (model.getMessageText().toLowerCase().contains("mp4") || model.getMessageText().toLowerCase().contains("3gp")) {

                attachment.setVisibility(View.GONE);
                messageText.setVisibility(View.GONE);
                pdfView.setVisibility(View.GONE);
                video.setVisibility(View.VISIBLE);
                rl_vv_file.setVisibility(View.VISIBLE);

                video.setVideoURI(Uri.parse(model.getMessageText().toString()));
                video.start();
                final String url = model.getMessageText().toString();
                video.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Intent i = new Intent(context, FullScreenActivity.class);
                        i.putExtra("url", url);
                        i.putExtra("view", 1); //1-video 0-pdf
                        startActivity(i);

                        return false;
                    }
                });
                video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, FullScreenActivity.class);
                        i.putExtra("url", url);
                        i.putExtra("view", 1); //1-video 0-pdf
                        startActivity(i);


                    }
                });
            } else {
                pdfView.setVisibility(View.GONE);
                video.setVisibility(View.GONE);
                attachment.setVisibility(View.GONE);
                rl_vv_file.setVisibility(View.GONE);
                messageText.setVisibility(View.VISIBLE);
                messageText.setText(model.getMessageText());
            }

            // Set their text
            messageUser.setText(model.getMessageUser());
            String date = String.valueOf(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                    model.getMessageTime()));

            // Format the date before showing it
          String timeElapsed= GlobalData.getTimeElapsed(date);
           /* messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                    model.getMessageTime()));*/
           messageTime.setText(timeElapsed);

        }

    }

    public void downloadFile(String mURL, PDFView pdfView) {
        //  progressDialog.setVisibility(View.VISIBLE);
        DownloadFileTask task = new DownloadFileTask(
                ChatActivity.this,
                mURL, pdfView,
                "/download/pdf_file.pdf");
        task.startTask();
    }



    public class DownloadFileTask {
        public static final String TAG = "DownloadFileTask";

        private ChatActivity context;
        private GetTask contentTask;
        private String url;
        private String fileName;
        private PDFView pdfView;

        public DownloadFileTask(ChatActivity context, String url, PDFView pdfView, String fileName) {
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

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
    private void showDialogToSelectCameraOrGallery() {
        final CharSequence options[] = new CharSequence[]{getString(R.string.txt_capture_image), getString(R.string.txt_open_gallery)};


        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle(R.string.txt_select_mode);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                askPermission(which);
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent intent = new Intent(ChatActivity.this,Gallery.class);
        // Set the title
        intent.putExtra("title", "Select media");
        // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
        intent.putExtra("mode", 4);
        intent.putExtra("maxSelection", 1); // Optional
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, PICK_IMAGE_REQUEST);

        }

    }
    private void openCameraToCaptureImage() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG);
        cameraIntent.putExtra("return-data", true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAPTURE_PIC);
        }
    }
    /*ask for runtime permission*/
    void askPermission(final int cameraOrGallery) {
        permissionHelper = new PermissionHelper(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        if (permissionHelper.checkSelfPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            switch (cameraOrGallery) {
                case 0:
                    openCameraToCaptureImage();
                    break;
                case 1:
                    openGallery();
                    break;
            }
        } else {
            permissionHelper.request(new PermissionHelper.PermissionCallback() {
                @Override
                public void onPermissionGranted() {
                    Log.d(TAG, "onPermissionGranted() called");
                    if (permissionHelper.checkSelfPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                        if (cameraOrGallery == 0) {
                            openCameraToCaptureImage();
                        } else if (cameraOrGallery == 1) {
                            openGallery();
                        }
                    }
                }

                @Override
                public void onPermissionDenied() {
                    Log.d(TAG, "onPermissionDenied() called");
                }

                @Override
                public void onPermissionDeniedBySystem() {
                    Log.d(TAG, "onPermissionDeniedBySystem() called");
                    Toast.makeText(ChatActivity.this, getString(R.string.allow_permission), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionHelper != null) {
            permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Notification
    // Fetches reg id from shared preferences
    // and displays on the screen




}
