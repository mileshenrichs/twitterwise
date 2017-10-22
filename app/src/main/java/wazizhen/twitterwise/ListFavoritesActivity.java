package wazizhen.twitterwise;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import models.Tweet;

public class ListFavoritesActivity extends ListActivity {

    private ArrayList<Tweet> favorites;
    private TweetAdapter tweetAdapter;

    ImageView profilePic;
    TextView content;
    TextView userDisplayName;

    ListView favoritesView;

    DatabaseHelper dbHelper;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_favorites);
        dbHelper = DatabaseHelper.getInstance(this);
        favorites = dbHelper.getAllFavorites();

        this.tweetAdapter = new TweetAdapter(this, R.layout.favorite_row, favorites);
        setListAdapter(this.tweetAdapter);

        favoritesView = (ListView) findViewById(android.R.id.list);
        favoritesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListFavoritesActivity.this, ViewTweetActivity.class);
                intent.putExtra("tweetId", String.valueOf(favorites.get(position).getId()));
                startActivity(intent);
            }
        });

    }

    public void goHome(View v) {
        startActivity(new Intent(ListFavoritesActivity.this, MainActivity.class));
    }

    private class TweetAdapter extends ArrayAdapter<Tweet> {

        private ArrayList<Tweet> favorites;

        public TweetAdapter(Context context, int textViewResourceId, ArrayList<Tweet> items) {
            super(context, textViewResourceId, items);
            this.favorites = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.favorite_row, null);
            }
            Tweet t = favorites.get(position);
            if (t != null) {
                profilePic = (ImageView) v.findViewById(R.id.profilePic);
                content = (TextView) v.findViewById(R.id.content);
                userDisplayName = (TextView) v.findViewById(R.id.userDisplayName);
                if(profilePic != null) Picasso.with(getApplicationContext()).load(t.getProfilePicUrl()).into(profilePic);
                if(content != null) content.setText(t.getContent().replaceAll("\\<.*?>",""));
                if(userDisplayName != null) userDisplayName.setText(getResources().getString(R.string.user_display_name, t.getUserDisplayName()));
            }
            return v;
        }
    }
}
