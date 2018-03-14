package com.sas_apps.reddit.adaptor;
/*
 * Created by Shashank Shinde.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sas_apps.reddit.R;
import com.sas_apps.reddit.model.post.Post;

import java.util.ArrayList;

public class PostAdaptor extends ArrayAdapter<Post> {

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private static final String TAG = "PostAdaptor";

    public PostAdaptor(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public PostAdaptor(Context context, int resource, ArrayList<Post> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        setupImageLoader();
    }

    private static class ViewHolder {
        TextView title;
        TextView author;
        TextView date;
        ProgressBar progressBar;
        ImageView thumbnailUrl;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String title = getItem(position).getTitle();
        String imgUrl = getItem(position).getThumbnailUrl();
        String author = getItem(position).getAuthor();
        String dateUpdated = getItem(position).getDateUpdated();


        try {
            View view;
            final ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(mResource, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.text_postTitle);
                viewHolder.thumbnailUrl = convertView.findViewById(R.id.image_post);
                viewHolder.author = convertView.findViewById(R.id.text_postAuthor);
                viewHolder.date = convertView.findViewById(R.id.text_postTime);
                viewHolder.progressBar = convertView.findViewById(R.id.progress_post);
                view = convertView;
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                view = convertView;
            }
            lastPosition = position;
            viewHolder.title.setText(title);
            viewHolder.author.setText(author);
            viewHolder.date.setText(dateUpdated);
            ImageLoader imageLoader = ImageLoader.getInstance();
            int defaultImage = mContext.getResources().getIdentifier("@drawable/reddit", null, mContext.getPackageName());
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisc(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            imageLoader.displayImage(imgUrl, viewHolder.thumbnailUrl, options, new
                    ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            viewHolder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            viewHolder.progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            viewHolder.progressBar.setVisibility(View.GONE);

                        }
                    });
            return convertView;
        }
        catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return convertView;
        }
    }

    private void setupImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);
    }
}
