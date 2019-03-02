package com.app.hamzaworld.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.app.hamzaworld.R;
import com.app.hamzaworld.other.Helper;
import com.app.hamzaworld.other.ImageCache;
import java.util.ArrayList;

public class DetailAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> imageList;
    ImageLoader imageLoader;

    public DetailAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.detail_adapter, container, false);

        NetworkImageView imageView = view.findViewById(R.id.detail_niv);

        imageLoader = ImageCache.getInstance(context).getImageLoader();
        imageLoader.get(Helper.IMAGE_URL + imageList.get(position), ImageLoader.getImageListener(imageView, R.drawable.image_preview, R.drawable.image_alert));
        imageView.setImageUrl(Helper.IMAGE_URL + imageList.get(position), imageLoader);
        imageView.setScaleType(NetworkImageView.ScaleType.FIT_CENTER);

        container.addView(view);
        return  view;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
