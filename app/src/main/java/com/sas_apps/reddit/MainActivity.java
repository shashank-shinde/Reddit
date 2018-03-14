package com.sas_apps.reddit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sas_apps.reddit.adaptor.PostAdaptor;
import com.sas_apps.reddit.comments.CommentsActivity;
import com.sas_apps.reddit.login.LoginActivity;
import com.sas_apps.reddit.model.post.HomeFeed;
import com.sas_apps.reddit.model.post.Post;
import com.sas_apps.reddit.model.Feed;
import com.sas_apps.reddit.model.post.entry.Entry;
import com.sas_apps.reddit.retrofit.RedditApi;
import com.sas_apps.reddit.retrofit.RedditHomeApi;
import com.sas_apps.reddit.utils.Url;
import com.sas_apps.reddit.xml.ExtractXmlByTag;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ArrayList<Post> posts;
    EditText editQuery;
    Button btnSearch;
    List<Entry> entries;
    Retrofit retrofit;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editQuery = findViewById(R.id.edit_query);
        btnSearch = findViewById(R.id.button_search);
        toolbar = findViewById(R.id.toolbar_main);
        setUpToolbar();
        hideSoftKeyboard();
        loadHomePage("");
        retrofit = new Retrofit.Builder()
                .baseUrl(Url.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reddit(editQuery.getText().toString().trim());
                hideSoftKeyboard();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setTitle("Reddit");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_loginMain:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                }
                return true;
            }
        });
    }


    void reddit(final String query) {
        RedditApi feedAPI = retrofit.create(RedditApi.class);
        Call<Feed> call = feedAPI.getFeed(query);
        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                hideSoftKeyboard();
                // Log.d(TAG, "onResponse: feed: " + response.body().toString());
                Log.d(TAG, "onResponse: Server Response: " + response.toString());
                try {
                    entries = response.body().getEntrys();
                    posts = new ArrayList<>();
                    for (int i = 0; i < entries.size(); i++) {
                        ExtractXmlByTag extractContent = new ExtractXmlByTag(entries.get(i).getContent(), "<a href=");
                        List<String> postContent = extractContent.extract();

                        ExtractXmlByTag extractImage = new ExtractXmlByTag(entries.get(i).getContent(), "<img src=");
                        try {
                            postContent.add(extractImage.extract().get(0));
                        } catch (NullPointerException e) {
                            postContent.add(null);
                            Log.e(TAG, "onResponse: NullPointerException (No image)" + e.getMessage());
                        } catch (IndexOutOfBoundsException e) {
                            postContent.add(null);
                            Log.e(TAG, "onResponse: IndexOutOfBoundsException " + e.getMessage());
                        }

                        try {
                            posts.add(new Post(
                                    entries.get(i).getTitle(),
                                    entries.get(i).getAuthor().getName().substring(3),
                                    entries.get(i).getUpdated().substring(0,10),
                                    postContent.get(0),
                                    postContent.get(postContent.size() - 1),
                                    entries.get(i).getId()
                            ));
                        } catch (NullPointerException e) {
                            Log.e(TAG, "onResponse: NullPointerException (No Author)" + e.getMessage());
                            posts.add(new Post(
                                    entries.get(i).getTitle(),
                                    "Anonymous",
                                    entries.get(i).getUpdated(),
                                    postContent.get(0),
                                    postContent.get(postContent.size() - 1),
                                    entries.get(i).getId()
                            ));
                        }
                    }



                    ListView listView = findViewById(R.id.list_homePosts);
                    PostAdaptor customListAdapter = new PostAdaptor(MainActivity.this, R.layout.layout_post, posts);
                    listView.setAdapter(customListAdapter);

                    //List onClick
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent commentsIntent = new Intent(MainActivity.this, CommentsActivity.class);
                            commentsIntent.putExtra(getString(R.string.postUrl), posts.get(position).getPostUrl());
                            commentsIntent.putExtra(getString(R.string.postAuthor), posts.get(position).getAuthor());
                            commentsIntent.putExtra(getString(R.string.postThumbnail), posts.get(position).getThumbnailUrl());
                            commentsIntent.putExtra(getString(R.string.postTitle), posts.get(position).getTitle());
                            commentsIntent.putExtra(getString(R.string.postUpdated), posts.get(position).getDateUpdated());
                            commentsIntent.putExtra(getString(R.string.postId), posts.get(position).getId());
                            commentsIntent.putExtra("subReddit",query);
                            startActivity(commentsIntent);
                        }
                    });
                } catch (NullPointerException e) {
                    Log.e(TAG, "onResponse: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "No sub-reddit found", Toast.LENGTH_SHORT).show();
                }

//                for (int j = 0; j < posts.size(); j++) {
//                    Log.d(TAG, "POST #  " + j +
//                            "\nPostURL: " + posts.get(j).getPostUrl() + "\n " +
//                            "ThumbnailURL: " + posts.get(j).getThumbnailUrl() + "\n " +
//                            "Title: " + posts.get(j).getTitle() + "\n " +
//                            "Author: " + posts.get(j).getAuthor().substring(3) + "\n " +
//                            "updated: " + posts.get(j).getDateUpdated() + "\n " +
//                            " PostID: " + posts.get(j).getId()
//                    );
//                }


            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS feed " + t.getMessage());
                Toast.makeText(MainActivity.this, "An Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    ------------------------------    Load Home Page      -------------------------------


    void loadHomePage(final String query) {
        Retrofit retrofit1;
        retrofit1 = new Retrofit.Builder()
                .baseUrl("https://www.reddit.com")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        RedditHomeApi feedAPI = retrofit1.create(RedditHomeApi.class);
        Call<HomeFeed> call = feedAPI.getFeed();
        call.enqueue(new Callback<HomeFeed>() {
            @Override
            public void onResponse(Call<HomeFeed> call, Response<HomeFeed> response) {
//                 Log.d(TAG, "onResponse: feed: " + response.body().toString());
                Log.d(TAG, "onResponse: Server Response: " + response.toString());
                try {
                    entries = response.body().getEntrys();
                    posts = new ArrayList<>();
                    for (int i = 0; i < entries.size(); i++) {
                        ExtractXmlByTag extractContent = new ExtractXmlByTag(entries.get(i).getContent(), "<a href=");
                        List<String> postContent = extractContent.extract();

                        ExtractXmlByTag extractImage = new ExtractXmlByTag(entries.get(i).getContent(), "<img src=");
                        try {
                            postContent.add(extractImage.extract().get(0));
                        } catch (NullPointerException e) {
                            postContent.add(null);
                            Log.e(TAG, "onResponse: NullPointerException (No image)" + e.getMessage());
                        } catch (IndexOutOfBoundsException e) {
                            postContent.add(null);
                            Log.e(TAG, "onResponse: IndexOutOfBoundsException " + e.getMessage());
                        }

                        try {
                            posts.add(new Post(
                                    entries.get(i).getTitle(),
                                    entries.get(i).getAuthor().getName().substring(3),
                                    entries.get(i).getUpdated().substring(0,10),
                                    postContent.get(0),
                                    postContent.get(postContent.size() - 1),
                                    entries.get(i).getId()
                            ));
                        } catch (NullPointerException e) {
                            Log.e(TAG, "onResponse: NullPointerException (No Author)" + e.getMessage());
                            posts.add(new Post(
                                    entries.get(i).getTitle(),
                                    "Anonymous",
                                    entries.get(i).getUpdated(),
                                    postContent.get(0),
                                    postContent.get(postContent.size() - 1),
                                    entries.get(i).getId()
                            ));
                        }
                    }



                    ListView listView = findViewById(R.id.list_homePosts);
                    PostAdaptor customListAdapter = new PostAdaptor(MainActivity.this, R.layout.layout_post, posts);
                    listView.setAdapter(customListAdapter);

                    //List onClick
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent commentsIntent = new Intent(MainActivity.this, CommentsActivity.class);
                            commentsIntent.putExtra(getString(R.string.postUrl), posts.get(position).getPostUrl());
                            commentsIntent.putExtra(getString(R.string.postAuthor), posts.get(position).getAuthor());
                            commentsIntent.putExtra(getString(R.string.postThumbnail), posts.get(position).getThumbnailUrl());
                            commentsIntent.putExtra(getString(R.string.postTitle), posts.get(position).getTitle());
                            commentsIntent.putExtra(getString(R.string.postUpdated), posts.get(position).getDateUpdated());
                            commentsIntent.putExtra(getString(R.string.postId), posts.get(position).getId());
                            commentsIntent.putExtra("subReddit","Reddit");
                            startActivity(commentsIntent);
                        }
                    });



                } catch (NullPointerException e) {
                    Log.e(TAG, "onResponse: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "No sub-reddit found", Toast.LENGTH_SHORT).show();
                }

//                for (int j = 0; j < posts.size(); j++) {
//                    Log.d(TAG, "POST #  " + j +
//                            "\nPostURL: " + posts.get(j).getPostUrl() + "\n " +
//                            "ThumbnailURL: " + posts.get(j).getThumbnailUrl() + "\n " +
//                            "Title: " + posts.get(j).getTitle() + "\n " +
//                            "Author: " + posts.get(j).getAuthor().substring(3) + "\n " +
//                            "updated: " + posts.get(j).getDateUpdated() + "\n " +
//                            " PostID: " + posts.get(j).getId()
//                    );
//                }
//

            }

            @Override
            public void onFailure(Call<HomeFeed> call, Throwable t) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.getMessage());
                Toast.makeText(MainActivity.this, "An Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
