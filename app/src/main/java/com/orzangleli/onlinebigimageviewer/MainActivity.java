package com.orzangleli.onlinebigimageviewer;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.orzangleli.library.bean.DownloadImageEntity;
import com.orzangleli.library.callback.DownloadCallback;
import com.orzangleli.library.view.OnlineSubsamplingScaleImageView;

public class MainActivity extends AppCompatActivity {

    OnlineSubsamplingScaleImageView imageView;
    // online
    String sjdt = "http://map.ps123.net/world/UploadFile/201411/2014111807202791.jpg";
    String zgdt = "http://map.ps123.net/china/UploadFile/201407/2014070323542296.jpg";
    String thumbUrl = "http://img.hb.aicdn.com/28ffc3cf827fc819ab749a32a225780252e11b5289963f-oL3ks9_fw658";

    // local
    String bigImage1 = "file:///storage/emulated/0/DCIM/zgdt.jpg";
    String smallImage1 = "file:///storage/emulated/0/DCIM/Alipay/1493543430385.jpg";

    Button button;

    // todo 缓存判断后还是需要有进度和完成回调

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = this.findViewById(R.id.button);

        imageView = this.findViewById(R.id.imageview);
        imageView.setImageDownloadListener(new DownloadCallback(){
            @Override
            public void onProgress(DownloadImageEntity entity) {
                super.onProgress(entity);
                Log.i("lxc", "ImageDownloadListener ---> " + entity.getPercent());
            }

            @Override
            public void onComplete(DownloadImageEntity entity) {
                super.onComplete(entity);
                Log.i("lxc", "width ---> " + entity.getWidth()
                +"\nheight ---> " + entity.getHeight()
                +"\nfile_length ---> " + entity.getFileLength());
            }
        });
        imageView.setThumbnailImageDownloadListener(new DownloadCallback(){
            @Override
            public void onProgress(DownloadImageEntity entity) {
                super.onProgress(entity);
                Log.i("lxc", "ThumbnailImageDownloadListener ---> " + entity.getPercent());
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageUri(bigImage1, thumbUrl);
            }
        });

    }
}
