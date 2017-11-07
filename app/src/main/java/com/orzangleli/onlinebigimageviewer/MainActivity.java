package com.orzangleli.onlinebigimageviewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.orzangleli.library.bean.DownloadImageEntity;
import com.orzangleli.library.callback.DownloadCallback;
import com.orzangleli.library.view.OnlineSubsamplingScaleImageView;

public class MainActivity extends AppCompatActivity {

    OnlineSubsamplingScaleImageView imageView;
    String sjdt = "http://map.ps123.net/world/UploadFile/201411/2014111807202791.jpg";
    String zgdt = "http://map.ps123.net/china/UploadFile/201407/2014070323542296.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (OnlineSubsamplingScaleImageView)this.findViewById(R.id.imageview);
        imageView.setImageUri(sjdt);
        imageView.setImageDownloadListener(new DownloadCallback(){
            @Override
            public void onProgress(DownloadImageEntity entity) {
                super.onProgress(entity);
                Log.i("lxc", " ---> " + entity.getPercent());
            }
        });
    }
}
