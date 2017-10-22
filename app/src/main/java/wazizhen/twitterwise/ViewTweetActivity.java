package wazizhen.twitterwise;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Tweet;

public class ViewTweetActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    TextView tweetContent;
    TextView userDisplayName;
    TextView tweetDate;
    ImageButton favoriteButton;
    ImageButton viewMyFavorites;
    TextView favoriteTweetText;
    ImageView profilePic;

    boolean tweetFavorited;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tweet);

        profilePic = (ImageView) findViewById(R.id.profilePic);

        Bundle bundle = getIntent().getExtras();
        String searchQuery = bundle.getString("query"); // hashtag user is has searched for
        final Tweet tweet = findTweet(searchQuery); // get Tweet to display

        // Picasso.with(getApplicationContext()).load(profileImgURL).into(profilePic);

        // retrieve favorites database through helper
        dbHelper = DatabaseHelper.getInstance(this);

        // establish view elements, set font
        Typeface signika = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative.ttf");
        tweetContent = (TextView) findViewById(R.id.tweetContent);
        tweetContent.setTypeface(signika);
        userDisplayName = (TextView) findViewById(R.id.userDisplayName);
        userDisplayName.setTypeface(signika);
        tweetDate = (TextView) findViewById(R.id.tweetDate);
        tweetDate.setTypeface(signika);
        favoriteButton = (ImageButton) findViewById(R.id.favoriteButton);
        viewMyFavorites = (ImageButton) findViewById(R.id.viewMyFavorites);
        favoriteTweetText = (TextView) findViewById(R.id.favoriteTweetText);
        favoriteTweetText.setTypeface(signika);

        tweetContent.setText("\"" + tweet.getContent().replaceAll("\\<.*?>","") + "\"");
        userDisplayName.setText(getResources().getString(R.string.user_display_name, tweet.getUserDisplayName()));
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        tweetDate.setText(format.format(tweet.getDate()));

        // check db for this Tweet in favorites, set favoriteButton image & text accordingly
        tweetFavorited = dbHelper.hasTweet(tweet.getId());
        if(tweetFavorited) {
            favoriteButton.setImageResource(R.drawable.favorited_icon_full);
            favoriteTweetText.setText(R.string.favorited);
        } else {
            favoriteButton.setImageResource(R.drawable.favorited_icon_empty);
            favoriteTweetText.setText(R.string.favorite_this);
        }

        // TEST
        List<Tweet> tweets = dbHelper.getAllFavorites();

        // click listener for "View my Favorites" button
        viewMyFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewTweetActivity.this, ListFavoritesActivity.class);
                startActivity(intent);
            }
        });

        // Toggle favoriteButton src, change favoriteTweetText
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tweetFavorited) {
                    favoriteButton.setImageResource(R.drawable.favorited_icon_full);
                    favoriteButton.animate().scaleY(1.12f).scaleX(1.12f).setDuration(300);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            favoriteButton.animate().scaleY(0.90f).scaleX(0.90f).setDuration(500);
                        }
                    }, 300);
                    favoriteTweetText.animate().translationYBy(20f).alpha(0).setDuration(500);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            favoriteTweetText.setText(R.string.favorited); // don't change text until fully disappeared
                            favoriteTweetText.animate().translationYBy(-20f).alpha(1f).setDuration(500);
                        }
                    }, 500);
                    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
                    // add Tweet to favorites data table
                    dbHelper.addFavorite(tweet);
                    tweetFavorited = true;
                } else {
                    favoriteButton.setImageResource(R.drawable.favorited_icon_empty);
                    favoriteTweetText.animate().translationYBy(20f).alpha(0).setDuration(500);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            favoriteTweetText.setText(R.string.tweet_removed);
                            favoriteTweetText.animate().translationYBy(-20f).alpha(1f).setDuration(500);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    favoriteTweetText.animate().alpha(0).setDuration(1000);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            favoriteTweetText.setText(R.string.favorite_this);
                                            favoriteTweetText.animate().alpha(1f).setDuration(1500);
                                        }
                                    }, 2000);
                                }
                            }, 2000);
                        }
                    }, 500);
                    // delete Tweet from favorites data table
                    dbHelper.removeFavorite(tweet);
                    tweetFavorited = false;
                }
            }
        });

        // TODO: utilize this later for an "Explore" tab underneath Tweet info
        List<String> includedHashtags = new ArrayList<>();
        String content = tweetContent.getText().toString();
        Pattern hashtagPattern = Pattern.compile("#...");
        Matcher hashtagMatcher = hashtagPattern.matcher(content);
        while(hashtagMatcher.find()) {
            int tagIndex = content.indexOf(hashtagMatcher.group());
            int nextSpaceIndex = content.indexOf(" ", tagIndex);
            includedHashtags.add(content.substring(tagIndex + 1, nextSpaceIndex).toLowerCase().replaceAll("\\?|!|\\.", ""));
        }

        System.out.println(includedHashtags.toString());

    }

    public static Tweet findTweet(String query) {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        Date date = new Date();
        try {
            date = format.parse("September 25, 2017");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Tweet t = new Tweet(912422761318043650L,
            "<a href=\"https://twitter.com/hashtag/College?src=hash&amp;ref_src=twsrc%5Etfw\">#College</a> <a href=\"https://twitter.com/hashtag/Freshman?src=hash&amp;ref_src=twsrc%5Etfw\">#Freshman</a> Have you considered <a href=\"https://twitter.com/hashtag/studyabroad?src=hash&amp;ref_src=twsrc%5Etfw\">#studyabroad</a>? Here are 10 Reasons Everyone Should Study Abroad- <a href=\"https://t.co/n9B3drXg7Q\">https://t.co/n9B3drXg7Q</a>",
            "MyPath101", "_MyPath101", "https://pbs.twimg.com/profile_images/505400940862513152/Bk-Fi5fo_200x200.jpeg", date);
        return t;
    }
}
