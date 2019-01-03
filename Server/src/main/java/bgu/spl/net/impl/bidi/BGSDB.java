package bgu.spl.net.impl.bidi;

import bgu.spl.net.impl.bidi.MessageTypes.Msg;
import bgu.spl.net.impl.bidi.MessageTypes.MsgType;
import bgu.spl.net.impl.bidi.MessageTypes.PMrequestMsg;
import bgu.spl.net.impl.bidi.MessageTypes.PostMsg;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class BGSDB {

    //-----------Fields--------------------
    private ConcurrentHashMap<String, String> users;
    private ConcurrentMap<String, Integer> connectedUsers;
    private ConcurrentHashMap<String, List<String>> followingMap;
    private ConcurrentHashMap<String, List<PostMsg>> feeds;
    private ConcurrentHashMap<String, List<PMrequestMsg>> mailBoxes;
    private ConcurrentMap<String, Integer> postsCounter;

    //----------Constructors---------------
    public BGSDB() {
        this.users = new ConcurrentHashMap<>();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.followingMap = new ConcurrentHashMap<>();
        this.feeds = new ConcurrentHashMap<>();
        this.mailBoxes = new ConcurrentHashMap<>();
        this.postsCounter = new ConcurrentHashMap<>();
    }

    //----------Methods-------------------
    /**
     *
     * @param username
     * @param password
     * @return
     */
    public boolean register(String username, String password){
        if(users.containsKey(username)){
            return false;
        }

        users.put(username, password);
        followingMap.put(username, new LinkedList<>());
        feeds.put(username, new LinkedList<>());
        mailBoxes.put(username, new LinkedList<>());
        postsCounter.put(username, 0);

        return true;
    }


    /**
     *
     * @param username
     * @return
     */
    public boolean logIn(String username, String password, int connectionId){
        boolean didConnect = false;
        if(users.containsKey(username) && users.get(username).equals(password) && !connectedUsers.containsKey(username)){
            didConnect = true;
            connectedUsers.put(username, connectionId);

        }
        return didConnect;
    }

    public void logout(String username) {
        connectedUsers.remove(username);
    }

    public int follow(String username, List<String> toFollowList){
        int success = 0;
        for(String user : toFollowList){
            if(!followingMap.get(username).contains(user)){
                ++success;
                followingMap.get(username).add(user);
            }
        }
        return success;
    }

    public int unFollow(String username, List<String> toFollowList){
        int success = 0;
        for(String user : toFollowList){
            if(followingMap.get(username).contains(user)){
                ++success;
                followingMap.get(username).remove(user);
            }
        }
        return success;
    }

    public UserInfo getUserInfo(String username){
        UserInfo userInfo = null;
        if(users.containsKey(username)){
            List<String> followList = followingMap.get(username);
            List<PostMsg> feed = feeds.get(username);
            List<PMrequestMsg> mailBox = mailBoxes.get(username);
            userInfo = new UserInfo(username, followList, feed, mailBox);
        }
        return userInfo;
    }

    /**
     *
     * @param users
     * @param message
     * @return
     */
    public List<Integer> postOffice(List<String> users, Msg message){
        List<Integer> connectionIds = new LinkedList<>();
        for(String user : users){
            if(connectedUsers.containsKey(user)){
                connectionIds.add(connectedUsers.get(user));
            }
            else{
                if(message.getMsgType() == MsgType.POST){
                    feeds.get(user).add((PostMsg) message);
                }
                else{
                    mailBoxes.get(user).add((PMrequestMsg) message);
                }
            }
        }
        return connectionIds;
    }

    public List<String> getUserList(){
        List<String> usersList = new LinkedList<>();
        for(String user: users.keySet())
            usersList.add(user);
        return usersList;
    }

    public int numOfPosts(String user){
        return postsCounter.get(user);
    }

    public int numOfFollowers(String user){
        int counter = 0;
        for(List following : followingMap.values()){
            if(following.contains(user)){
                counter++;
            }
        }
        return counter;
    }

    public int numOfFollowing(String user){
        return followingMap.get(user).size();
    }

    public void postShared(String username){
        Integer i = postsCounter.get(username);
        i++;
    }

}
