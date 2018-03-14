package com.sas_apps.reddit.comments;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sas_apps.reddit.login.LoginActivity;
import com.sas_apps.reddit.R;
import com.sas_apps.reddit.WebViewActivity;
import com.sas_apps.reddit.adaptor.CommentAdaptor;
import com.sas_apps.reddit.login.reddit_login.CheckLogin;
import com.sas_apps.reddit.model.comment.CheckComment;
import com.sas_apps.reddit.model.comment.Comment;
import com.sas_apps.reddit.model.Feed;
import com.sas_apps.reddit.model.post.entry.Entry;
import com.sas_apps.reddit.retrofit.LogInApi;
import com.sas_apps.reddit.retrofit.PostCommentApi;
import com.sas_apps.reddit.retrofit.RedditApi;
import com.sas_apps.reddit.utils.Url;
import com.sas_apps.reddit.xml.ExtractXmlByTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CommentsActivity extends AppCompatActivity {

    private static final String TAG = "CommentsActivity";

    private String postUrl;
    private String postThumbnailUrl;
    private String postTitle;
    private String postAuthor;
    private String postUpdated;
    private String postId;

    //  Session parameters
    private String userName;
    private String cookie;
    private String modhash;

    private int defaultImage;
    private String currentFeed;
    private ListView listViewComments;
    Toolbar toolbar;
    private ArrayList<Comment> entriesList;
    private ProgressBar progressBar;
    private TextView textProgressBar;
    ArrayList<Comment> commentList;
    Dialog dialog;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        progressBar = findViewById(R.id.progressBar_comments);
        textProgressBar = findViewById(R.id.text_progressBar);
        toolbar = findViewById(R.id.toolbar_comments);
        progressBar.setVisibility(View.VISIBLE);
        listViewComments = findViewById(R.id.list_comments);
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerView = inflater.inflate(R.layout.layout_header_view, null, false);
        listViewComments.addHeaderView(headerView);
        getSessionParameters();
        setUpToolbar();
        setupImageLoader();
        setCommentLayout();
        loadComments(postId);
    }

    private void loadComments(String Id) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Url.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        RedditApi redditApi = retrofit.create(RedditApi.class);
        Call<Feed> call = redditApi.getFeed(currentFeed);
        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                Log.d(TAG, "onResponse: Server response " + response.toString());
                List<Entry> entriesList = response.body().getEntrys();
                commentList = new ArrayList<>();
                // Log comment entries
//                for (int i = 0; i < entriesList.size(); i++) {
//                    Log.d(TAG, "/n onResponse: Comment # "+i+"/n "+entriesList.get(i).toString()+"/n/n");
//                }

                for (int i = 0; i < entriesList.size(); i++) {
                    ExtractXmlByTag extractXml = new ExtractXmlByTag(entriesList.get(i).getContent(),
                            "<div class=\"md\"><p>", "</p>");
                    List<String> mComments = extractXml.extract();
                    try {
                        commentList.add(new Comment(mComments.get(0),
                                entriesList.get(i).getAuthor().getName().substring(3),
                                entriesList.get(i).getUpdated().substring(0, 10),
                                entriesList.get(i).getId()));
                    } catch (IndexOutOfBoundsException e) {
                        Log.e(TAG, "onResponse: IndexOutOfBoundsException" + e.getMessage());

//                        commentList.add(new Comment("ERROR ",
//                                "",
//                                "",
//                                ""));
                    } catch (NullPointerException e) {
                        Log.e(TAG, "onResponse: NullPointerException No Author " + e.getMessage());
                        commentList.add(new Comment(mComments.get(0),
                                "Anonymous",
                                entriesList.get(i).getUpdated(),
                                entriesList.get(i).getId()));
                    }
                }


                CommentAdaptor commentAdaptor = new CommentAdaptor(CommentsActivity.this,
                        R.layout.layout_comment, commentList);
                listViewComments.setAdapter(commentAdaptor);
                progressBar.setVisibility(View.GONE);
                textProgressBar.setVisibility(View.GONE);
                listViewComments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        getComment(commentList.get(position).getId());
                    }
                });
            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
//                Toast.makeText(CommentsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                textProgressBar.setVisibility(View.GONE);
            }
        });
    }


    private void setCommentLayout() {

        Intent incomingIntent = getIntent();
        postUrl = incomingIntent.getStringExtra(getString(R.string.postUrl));
        postThumbnailUrl = incomingIntent.getStringExtra(getString(R.string.postThumbnail));
        postTitle = incomingIntent.getStringExtra(getString(R.string.postTitle));
        postAuthor = incomingIntent.getStringExtra(getString(R.string.postAuthor));
        postUpdated = incomingIntent.getStringExtra(getString(R.string.postUpdated));
        postId = incomingIntent.getStringExtra(getString(R.string.postId));
        toolbar.setTitle(incomingIntent.getStringExtra("subReddit"));

        Log.d(TAG, "setCommentLayout: " + postId);

        TextView title = headerView.findViewById(R.id.text_postTitle);
        TextView author = headerView.findViewById(R.id.text_postAuthor);
        TextView updated = headerView.findViewById(R.id.text_postUpdated);
        ImageView thumbnail = headerView.findViewById(R.id.image_postThumbnail);
        FloatingActionButton btnReply = findViewById(R.id.button_reply);
        ProgressBar progressBar = findViewById(R.id.progressBar_post);

        title.setText(postTitle);
        author.setText("Submitted by: " + postAuthor);
        updated.setText(postUpdated);
        displayImage(postThumbnailUrl, thumbnail, progressBar);
        try {
            String[] splitUrl = postUrl.split(Url.BASE_URL);
            currentFeed = splitUrl[1];
            Log.d(TAG, "initPost: current feed: " + currentFeed);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "initPost: ArrayIndexOutOfBoundsException: " + e.getMessage());

        }
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: opening " + postUrl);
                Intent intent = new Intent(CommentsActivity.this, WebViewActivity.class);
                intent.putExtra("url", postUrl);
                startActivity(intent);
            }
        });

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getComment(postId);
            }
        });
    }


    //  Open comment dialog and call post comment method
    private void getComment(final String id) {
        dialog = new Dialog(this);
        //   dialog.setTitle("Comment");
        dialog.setContentView(R.layout.layout_comment_dialog);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.60);
        dialog.getWindow().setLayout(width, height);
        Button buttonPostComment = dialog.findViewById(R.id.button_postComment);
        Button buttonCancelPost = dialog.findViewById(R.id.button_cancelComment);
        final EditText editComment = dialog.findViewById(R.id.edit_comment);
        dialog.show();
        buttonCancelPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editComment.setText("");
                dialog.dismiss();
            }
        });

        buttonPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment(id, editComment.getText().toString());
            }
        });
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume: called");
        getSessionParameters();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comments, menu);
        return true;
    }


    private void postComment(String id, String comment) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Url.COMMENT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostCommentApi postCommentApi = retrofit.create(PostCommentApi.class);

        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("User-Agent", userName);
        headerMap.put("X-Modhash", modhash);
        headerMap.put("cookie", "reddit_session=" + cookie);

        Call<CheckComment> call = postCommentApi.postComment(headerMap, "comment", id, comment);

        call.enqueue(new Callback<CheckComment>() {
            @Override
            public void onResponse(Call<CheckComment> call, Response<CheckComment> response) {
                Log.d(TAG, "onResponse: Response message " + response.toString());
                Log.d(TAG, "onResponse: Response body " + response.body());
                try {
                    String result = response.body().getIsSuccess();
                    if (result.equals("true")) {
                        dialog.dismiss();
                        Toast.makeText(CommentsActivity.this, "Comment posted", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(CommentsActivity.this, "Error. Have you logged in?", Toast.LENGTH_SHORT).show();
                    }

                } catch (NullPointerException e) {
                    Toast.makeText(CommentsActivity.this, "Error. Have you logged in?", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<CheckComment> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(CommentsActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSessionParameters() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CommentsActivity.this);
        userName = sharedPreferences.getString(getResources().getString(R.string.userName), "");
        modhash = sharedPreferences.getString(getResources().getString(R.string.modHash), "");
        cookie = sharedPreferences.getString(getResources().getString(R.string.cookie), "");
//        Log.d(TAG, "getSessionParameters: modhash: " + modhash);
//        Log.d(TAG, "getSessionParameters: cookie: " + cookie);
//        Log.d(TAG, "getSessionParameters: username " + userName);

    }


    private void displayImage(String imageURL, ImageView imageView, final ProgressBar progressBar) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();
        imageLoader.displayImage(imageURL, imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
            }

        });
    }


    private void setUpToolbar() {

        setSupportActionBar(toolbar);
        toolbar.setTitle("Reddit");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_login:
                        Intent intent = new Intent(CommentsActivity.this, LoginActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.menu_copyUrl:
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", postUrl);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(CommentsActivity.this, "Post URL copied to clipboard", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });
    }


    private void setupImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                CommentsActivity.this)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);
        defaultImage = CommentsActivity.this.getResources().getIdentifier("@drawable/reddit",
                null, CommentsActivity.this.getPackageName());
    }
}
