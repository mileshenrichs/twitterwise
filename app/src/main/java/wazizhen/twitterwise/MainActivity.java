package wazizhen.twitterwise;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    }
}
