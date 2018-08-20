package com.acquaint.firebasechatdemo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.acquaint.firebasechatdemo.Constants;
import com.acquaint.firebasechatdemo.R;
import com.acquaint.firebasechatdemo.adapters.UserAdapter;
import com.acquaint.firebasechatdemo.listeners.onUserClickListener;
import com.acquaint.firebasechatdemo.model.Upload;
import com.acquaint.firebasechatdemo.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements onUserClickListener {
    RecyclerView rv_user;
    UserAdapter adapter;
    ArrayList<User> ar_userList= new ArrayList<>();
    ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    User user;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        getUsersfromDb();
        setUserAdapter();

    }

    private void getUsersfromDb() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference("user");
        ar_userList.clear();
        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
                progressDialog.dismiss();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    user = postSnapshot.getValue(User.class);
                    ar_userList.add(user);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }

    private void setUserAdapter() {
        adapter=new UserAdapter(MainActivity.this,ar_userList,this);
        rv_user.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        rv_user.setLayoutManager(mLayoutManager);
        rv_user.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        rv_user.setItemAnimator(new DefaultItemAnimator());
    }

    private void initWidgets() {
        rv_user=findViewById(R.id.rv_user);
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

    }



    @Override
    public void onUserClick(String name) {
        Intent i = new Intent(MainActivity.this,ChatActivity.class);
        i.putExtra("uname",name);
        startActivity(i);

    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.signout:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        auth= FirebaseAuth.getInstance();
        auth.signOut();
        Intent i = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(i);
        finish();

    }
}
