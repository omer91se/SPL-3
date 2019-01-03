package bgu.spl.net.impl.bidi;

import bgu.spl.net.impl.bidi.MessageTypes.PMrequestMsg;
import bgu.spl.net.impl.bidi.MessageTypes.PostMsg;

import java.util.List;

public class UserInfo {
    private String username;
    private List<String> followingList;
    private List<PMrequestMsg> mailBox;
    private List<PostMsg> feed;

     UserInfo(String username,List<String> followingList, List<PostMsg> feed, List<PMrequestMsg> mailBox){
         this.username = username;
         this.followingList = followingList;
         this.feed = feed;
         this.mailBox = mailBox;

    }

    public String getUsername() {
        return username;
    }
    public List<String> getFollowingList(){
         return this.followingList;
    }

    public List<PMrequestMsg> getMailBox() {
        return mailBox;
    }

    public List<PostMsg> getFeed() {
        return feed;
    }
}
