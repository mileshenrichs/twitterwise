package wazizhen.twitterwise;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tweet);

        Typeface helvetica = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative.ttf");

        tweetContent = (TextView) findViewById(R.id.tweetContent);
        tweetContent.setTypeface(helvetica);
        userDisplayName = (TextView) findViewById(R.id.userDisplayName);
        userDisplayName.setTypeface(helvetica);
        tweetDate = (TextView) findViewById(R.id.tweetDate);
        tweetDate.setTypeface(helvetica);
        favoriteButton = (ImageButton) findViewById(R.id.favoriteButton);
        favoriteTweetText = (TextView) findViewById(R.id.favoriteTweetText);
        favoriteTweetText.setTypeface(helvetica);

        Bundle bundle = getIntent().getExtras();
        String searchQuery = bundle.getString("query");

        System.out.println("########## searchQuery: " + searchQuery);

        Tweet tweet = findTweet(searchQuery);
        tweetContent.setText("\"" + tweet.getContent().replaceAll("\\<.*?>","") + "\"");
        userDisplayName.setText(getResources().getString(R.string.user_display_name, tweet.getUserDisplayName()));
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        tweetDate.setText(format.format(tweet.getDate()));
        // TODO: display different favoriteButton depending on whether this Tweet is on user's favorites storage
        // TODO: display different favoriteTweetText depending on if Tweet is favorited

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: toggle favoriteButton src, change favoriteTweetText
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
}
