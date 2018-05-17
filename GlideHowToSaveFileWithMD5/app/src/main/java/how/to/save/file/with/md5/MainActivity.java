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

    // 图片可能会过期
    String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526549080872&di=4fe0f2dda0c76f7957faaab4b54b8dcf&imgtype=jpg&src=http%3A%2F%2Fimg3.imgtn.bdimg.com%2Fit%2Fu%3D1569598378%2C1791208052%26fm%3D214%26gp%3D0.jpg";

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
                // 获取到缓存路径
                String cachedFilePath = EmagInternalCacheUtil.getCachePathByUrl(v.getContext(), url);
                File cachedFile = new File(cachedFilePath);
                Uri uri = Uri.fromFile(cachedFile);
                Glide.with(v.getContext()).load(uri).into(localImage);
                cachePath.setText(cachedFile.getAbsolutePath());
            }
        });
        Uri uri = Uri.parse(url);
        Glide.with(getApplicationContext())
                .load(uri)
                .asBitmap()
                .dontAnimate()
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