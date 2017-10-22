package models;

import java.util.Date;

/**
 * Created by Henrichs on 10/21/2017.
 * Represents a single Twitter post.
 */

public class Tweet {
    private long id;
    private String content;
    private String userDisplayName;
    private String userName;
    private String profilePicUrl;
    private Date date;

    public Tweet(long id, String content, String userDisplayName, String userName, String profilePicUrl, Date date) {
        this.id = id;
        this.content = content;
        this.userDisplayName = userDisplayName;
        this.userName = userName;
        this.profilePicUrl = profilePicUrl;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public Date getDate() {
        return date;
    }
}
