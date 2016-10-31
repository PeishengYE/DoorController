package com.radioyps.doorcontroller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yep on 30/10/16.
 */
public class ImageViewActivity  extends AppCompatActivity {

    private TextView mTextView;
    private ImageView mImageView;

    public final static String EXTRA_IMAGE_BYTE_ARRAY = "com.radioyps.doorcontroller..ImageViewActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_for_remote);
        mTextView = (TextView)findViewById(R.id.Remote_image_status_info);
        mImageView = (ImageView)findViewById(R.id.Remote_image_contents);

        Intent intent = getIntent();
        byte[] imageBytes = intent.getByteArrayExtra(ImageViewActivity.EXTRA_IMAGE_BYTE_ARRAY);

        /*FIXME this code should be in a independant thread.
        * and redecoded the bitmap, when screen rotate */
        Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        mImageView.setMinimumHeight(dm.heightPixels);
        mImageView.setMinimumWidth(dm.widthPixels);
        mImageView.setImageBitmap(bm);

    }
}
