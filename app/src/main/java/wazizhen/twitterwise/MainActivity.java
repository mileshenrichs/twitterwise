package wazizhen.twitterwise;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // declare view objects
    TextView titleTextView;
    EditText hashtagSearch;
    ImageButton searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titleTextView = (TextView) findViewById(R.id.title);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative.ttf");
        titleTextView.setTypeface(type);

        hashtagSearch = (EditText) findViewById(R.id.hashtagSearch);
        searchButton = (ImageButton) findViewById(R.id.searchButton);



        // click listener for "Get Wise" button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get value of #hashtag query
                String hashtagQuery = hashtagSearch.getText().toString();
                if(hashtagQuery.startsWith("#")) hashtagQuery = hashtagQuery.substring((1));

                Intent intent = new Intent(MainActivity.this, ViewTweetActivity.class);
                intent.putExtra("query", hashtagQuery);
                startActivity(intent);
            }
        });

    }
}
