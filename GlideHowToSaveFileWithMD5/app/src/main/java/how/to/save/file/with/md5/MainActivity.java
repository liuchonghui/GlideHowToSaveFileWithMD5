package how.to.save.file.with.md5;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.EmagInternalCacheUtil;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;

/**
 * Created by liuchonghui on 2017/3/2.
 */
public class MainActivity extends Activity {

    String url = "https://gist.github.com/liuchonghui/277cd9fac31b8cff8c9ccbc3600b55fd/raw/49b167791912cb98c0e34bda4e44e20c5fd129a1/IMOIPIWtEU5sWyv.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView onlineImage = (ImageView) findViewById(R.id.online_image);
        final TextView onlineUrl = (TextView) findViewById(R.id.online_url);
        onlineUrl.setText(url);
        final ImageView localImage = (ImageView) findViewById(R.id.local_image);
        final TextView cachePath = (TextView) findViewById(R.id.cachedPath);
        final Button loadBtn = (Button) findViewById(R.id.btn);
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File cachedFile = new File(
                        EmagInternalCacheUtil.getCachePathByUrl(v.getContext(), url));
                Uri uri = Uri.fromFile(cachedFile);
                Glide.with(v.getContext()).load(uri).into(localImage);
                cachePath.setText(cachedFile.getAbsolutePath());
            }
        });

        Uri uri = Uri.parse(url);
        Glide.with(this).load(uri)
                .asBitmap()
                .into(new BitmapImageViewTarget(onlineImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        int viewHeight = onlineImage.getMeasuredHeight();
                        int bitmapWidth = resource.getWidth();
                        int bitmapHeight = resource.getHeight();
                        int viewWidth = bitmapWidth * viewHeight / bitmapHeight;
                        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) onlineImage.getLayoutParams();
                        lp.width = viewWidth;
                        lp = (ViewGroup.LayoutParams) localImage.getLayoutParams();
                        lp.width = viewWidth;
                        super.setResource(resource);
                        loadBtn.setEnabled(true);
                    }
                });
    }
}