package pl.edu.uwr.pum.footballapp.util;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.widget.ImageView;
import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.bumptech.glide.request.RequestOptions;
import com.caverock.androidsvg.SVG;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


import pl.edu.uwr.pum.footballapp.R;
import pl.edu.uwr.pum.footballapp.util.SVG.SvgSoftwareLayerSetter;

public class Util {

    public static long refreshTime = 20 * 1000 * 1000 * 1000L;

    public static final int NUM_OF_THREADS = 4;


    public static void loadImage(ImageView imageView, String url,
                                 CircularProgressDrawable progressDrawable) {
        RequestOptions options = new RequestOptions()
                .placeholder(progressDrawable); //Tutaj placeholder

        Glide.with(imageView.getContext())
                .setDefaultRequestOptions(options)
                .as(PictureDrawable.class)
                .transition(withCrossFade())
                .listener(new SvgSoftwareLayerSetter())
                .load(url)
                .into(imageView);
    }

    public static CircularProgressDrawable getProgressDrawable(Context context) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(15f);
        circularProgressDrawable.setCenterRadius(20f);
        circularProgressDrawable.start();
        return circularProgressDrawable;
    }

    @BindingAdapter("image_url")
    public static void loadImage(ImageView imageView, String url){
        loadImage(imageView, url, getProgressDrawable(imageView.getContext()));
    }
}
