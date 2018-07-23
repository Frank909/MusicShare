package com.sms.musicshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.sms.musicshare.adapters.InfoAdapter;
import com.sms.musicshare.helper.Info;

import java.util.ArrayList;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoActivity extends AppCompatActivity {

    @BindView(R.id.info_toolbar) Toolbar toolbar;
    @BindView(R.id.list_view_in_info_activity) ListView listview;

    @BindString(R.string.info_play_store) String info_play_store;
    @BindString(R.string.infotitle) String info_play_store_title;
    @BindString(R.string.send_feedback) String info_send_feedback;
    @BindString(R.string.send_feeback_msg) String info_feedback_msg;
    @BindString(R.string.info_developers) String info_developers;
    @BindString(R.string.info_feedback) String info_name;

    @BindDrawable(R.drawable.ic_star) Drawable drawable_star;
    @BindDrawable(R.drawable.ic_mail) Drawable drawable_mail;
    @BindDrawable(R.drawable.ic_developers) Drawable drawable_developers;

    private InfoAdapter infoAdapter;
    private ArrayList<Info> data;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(info_name);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = new ArrayList<>();
        data.add(new Info(Info.ID_REVIEW, info_play_store_title, info_play_store, drawable_star));
        data.add(new Info(Info.ID_FEEDBACK, info_send_feedback, info_feedback_msg, drawable_mail));
        data.add(new Info(Info.ID_DEVELOPERS, info_developers, "Ventura Francesco\nZito Andrea\nFornelli Luca", drawable_developers));

        infoAdapter = new InfoAdapter(this, data);
        listview.setAdapter(new InfoAdapter(this, data));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
