package wazizhen.twitterwise;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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

    TextView tweetContent;
    TextView userDisplayName;
    TextView tweetDate;
    ImageButton favoriteButton;
    TextView favoriteTweetText;

    boolean tweetFavorited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tweet);

        // init or retrieve favorites database
        final SQLiteDatabase db = this.openOrCreateDatabase("Tweets", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS favorites (id BIGINT, content VARCHAR(255), userDisplayName VARCHAR(255), userName VARCHAR(255), date TEXT)");

        // establish view elements, set font
        Typeface signika = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative.ttf");
        tweetContent = (TextView) findViewById(R.id.tweetContent);
        tweetContent.setTypeface(signika);
        userDisplayName = (TextView) findViewById(R.id.userDisplayName);
        userDisplayName.setTypeface(signika);
        tweetDate = (TextView) findViewById(R.id.tweetDate);
        tweetDate.setTypeface(signika);
        favoriteButton = (ImageButton) findViewById(R.id.favoriteButton);
        favoriteTweetText = (TextView) findViewById(R.id.favoriteTweetText);
        favoriteTweetText.setTypeface(signika);

        Bundle bundle = getIntent().getExtras();
        String searchQuery = bundle.getString("query"); // hashtag user is has searched for


        final Tweet tweet = findTweet(searchQuery);
        tweetContent.setText("\"" + tweet.getContent().replaceAll("\\<.*?>","") + "\"");
        userDisplayName.setText(getResources().getString(R.string.user_display_name, tweet.getUserDisplayName()));
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        tweetDate.setText(format.format(tweet.getDate()));
        // check db for this Tweet in favorites, set favoriteButton image & text accordingly
        tweetFavorited = tweetIsFavorited(db, tweet.getId());
        if(tweetFavorited) {
            favoriteButton.setImageResource(R.drawable.favorited_icon_full);
            favoriteTweetText.setText(R.string.favorited);
        } else {
            favoriteButton.setImageResource(R.drawable.favorited_icon_empty);
            favoriteTweetText.setText(R.string.favorite_this);
        }

        // TEST
//        Cursor c = db.rawQuery("SELECT * FROM favorites", null);
//        int idIndex = c.getColumnIndex("id");
//        int contentIndex = c.getColumnIndex("content");
//        int userDisplayNameIndex = c.getColumnIndex("userDisplayName");
//        int userNameIndex = c.getColumnIndex("userName");
//        int dateIndex = c.getColumnIndex("date");
//        c.moveToFirst();
//        while (c != null) {
//            Log.i("id", String.valueOf(c.getLong(idIndex)));
//            Log.i("content", c.getString(contentIndex));
//            Log.i("userDisplayName", c.getString(userDisplayNameIndex));
//            Log.i("userName", c.getString(userNameIndex));
//            Log.i("date", c.getString(dateIndex));
//            c.moveToNext();
//        }
//        c.close();

        // Toggle favoriteButton src, change favoriteTweetText
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tweetFavorited) {
                    favoriteButton.setImageResource(R.drawable.favorited_icon_full);
                    favoriteTweetText.setText(R.string.favorited);
                    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
                    // add Tweet to favorites data table
                    db.execSQL("INSERT INTO favorites (id, content, userDisplayName, userName, date) VALUES " +
                        "(" + tweet.getId() + ", " +
                        "'" + tweet.getContent() + "', " +
                        "'" + tweet.getUserDisplayName() + "', " +
                        "'" + tweet.getUserName() + "', " +
                        "'" + format.format(tweet.getDate()) + "')");
                    tweetFavorited = true;
                } else {
                    favoriteButton.setImageResource(R.drawable.favorited_icon_empty);
                    favoriteTweetText.setText(R.string.tweet_removed);
                    // delete Tweet from favorites data table
                    db.execSQL("DELETE FROM favorites WHERE id = " + tweet.getId());
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
            "MyPath101", "_MyPath101", date);
        return t;
    }

    /**
     * Queries favorites database to check if current Tweet is favorited by user
     */
    public static boolean tweetIsFavorited(SQLiteDatabase db, long id) {
        Cursor c = db.rawQuery("SELECT * FROM favorites WHERE id = " + String.valueOf(id), null);
        if(c.getCount() <= 0){
            c.close();
            return false;
        }
        c.close();
        return true;
    }
}
