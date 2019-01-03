package bgu.spl.net.impl.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.bidi.MessageTypes.*;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoderImpl<T> implements MessageEncoderDecoder<T> {
    private byte[] bytes = new byte[1 << 10];
    byte[] encodeBytes;
    int encodeBytesInd;
    int currByte = 0;


    @Override
    public T decodeNextByte(byte nextByte) {
        Msg message = null;
        if(nextByte == '\n') {
            byte[] opBytes =  new byte[2];
            opBytes[0] = bytes[0];
            opBytes[1] = bytes[1];
            short op = bytesToShort(opBytes);
            switch (op){
                case(1):
                    byte[] usernameByte1 = subArrayToZero(2);
                    String username1 = new String(usernameByte1, StandardCharsets.UTF_8);
                    byte[] passwordByte1 = subArrayToZero(2 + usernameByte1.length);
                    String password1 = new String(passwordByte1, StandardCharsets.UTF_8);

                    message = new RegisterMsg(username1, password1);
                    break;
                case(2):
                    byte[] usernameByte2 = subArrayToZero(2);
                    String username2 = new String(usernameByte2, StandardCharsets.UTF_8);
                    byte[] passwordByte2 = subArrayToZero(2 + usernameByte2.length);
                    String password2 = new String(passwordByte2, StandardCharsets.UTF_8);

                    message = new LoginMsg(username2,password2);
                    break;

                case(3):
                    message = new LogoutMsg();
                    break;

                case(4):
                    boolean isFollow = false;
                    if (bytes[2] == 0)
                        isFollow = true;

                    byte[] nOfUsers = new byte[2];
                    nOfUsers[0] = bytes[3];
                    nOfUsers[1] = bytes[4];
                    short numOfUsers = bytesToShort(nOfUsers);
                    int from = 5;
                    List<String> users = new LinkedList<>();
                    for (int i = 0; i < numOfUsers; i++) {
                        byte[] usernameByte4 = subArrayToZero(from);
                        from = from + usernameByte4.length +1;
                        users.add(new String(usernameByte4,StandardCharsets.UTF_8));
                    }

                    message = new FollowMsg(isFollow, users);
                    break;

                case(5):
                    byte[] contentByte5 = subArrayToZero(2);
                    message = new PostMsg(new String(contentByte5, StandardCharsets.UTF_8));
                    break;

                case(6):
                    byte[] usernameByte6 = subArrayToZero(2);
                    String username6 = new String(usernameByte6, StandardCharsets.UTF_8);
                    byte[] contentByte6 = subArrayToZero(2 + usernameByte6.length);
                    String content6 = new String(contentByte6, StandardCharsets.UTF_8);

                    message = new PMrequestMsg(username6, content6);
                    break;

                case(7):
                    message = new UserListReqMsg();
                    break;

                case(8):
                    byte[] usernameByte8 = subArrayToZero(2);
                    message = new StatMsg(new String(usernameByte8, StandardCharsets.UTF_8));
                    break;

                case(9):
                    boolean isPM = false;
                    byte charByte = bytes[2];
                    char ch = (char)charByte;
                    if(ch == '0'){
                        isPM =true;
                    }

                    byte[] postingUserBytes = subArrayToZero(3);
                    String postingUser = new String(postingUserBytes, StandardCharsets.UTF_8);

                    byte[] contentByte9 = subArrayToZero(3 + postingUserBytes.length);
                    String content9 = new String(contentByte9, StandardCharsets.UTF_8);

                    message = new NotificationMsg(isPM, postingUser, content9);
                    break;

            }

            bytes = new byte[1 << 10];
            currByte = 0;
            return (T)message;
        }

        else{
            bytes[currByte] = nextByte;
            currByte++;
            return null;}
    }

    /**
     * Encodes a message T to byte[]
     * @param message the message to encode
     * @return encoded byte[]
     */
    @Override
    public byte[] encode(T message) {
        encodeBytes = new byte[1 << 10];
        encodeBytesInd = 0;

        switch(((Msg)message).getMsgType()){
            case NOTIFICATION:

                //Encodes op
                short notifOp = 9;
                byte[] notifOpBytes = shortToBytes(notifOp);
                pushBytes(notifOpBytes);


                //Encode PM ot Public
                short type;
                if(((NotificationMsg)message).isPM())
                    type = 0;
                else
                    type = 1;

                byte[] tmp = shortToBytes(type);
                byte[] typeByte = new byte[1];
                typeByte[0] = tmp[1];
                pushBytes(typeByte);

                //Encode posting user
                byte[] postingUserByte = (((NotificationMsg) message).getPostingUser()).getBytes();
                pushBytes(postingUserByte);

                pushZero();

                //Encode content
                byte[] contentByte = (((NotificationMsg) message).getContent()).getBytes();
                pushBytes(contentByte);

                pushZero();

                break;

            case ACK:

                //Encode opcode
                short opAck = 10;
                byte[] AckBytes = shortToBytes(opAck);
                pushBytes(AckBytes);

                //Encode message opcode
                short messageOp = ((AckMsg)message).getOp();
                byte[] messageOpBytes = shortToBytes(messageOp);
                pushBytes(messageOpBytes);

                switch (((AckMsg) message).getOp()){

                    case(4):
                        //Encode numOfUsers
                        short numOfUsers = ((AckMsg) message).getNumOfUsers();
                        byte[] numOfUsersBytes = shortToBytes(numOfUsers);
                        pushBytes(numOfUsersBytes);

                        //Encode usernameList
                        for(String username :((AckMsg) message).getUsernameList()){
                            byte[] usernameBytes = username.getBytes();
                            pushBytes(usernameBytes);
                            pushZero();
                        }

                        break;

                    case(7):

                        //Encode numOfUsers
                        short numOfUsers2 = ((AckMsg) message).getNumOfUsers();
                        byte[] numOfUsersBytes2 = shortToBytes(numOfUsers2);
                        pushBytes(numOfUsersBytes2);

                        //Encode usernameList
                        for(String username :((AckMsg) message).getUsernameList()){
                            byte[] usernameBytes = username.getBytes();
                            pushBytes(usernameBytes);
                            pushZero();
                        }

                        break;

                    case(8):

                        //Encode numOfPosts
                        byte[] numOfPosts = shortToBytes(((AckMsg) message).getNumOfPosts());
                        pushBytes(numOfPosts);

                        //Encode numOfFollowers
                        byte[] numOfFollowers = shortToBytes(((AckMsg) message).getNumOfFollowers());
                        pushBytes(numOfFollowers);

                        //Encode numOfFollowing
                        byte[] numOfFollowing = shortToBytes(((AckMsg) message).getNumOfFollowing());
                        pushBytes(numOfFollowing);

                        break;

                }

                break;

            case ERROR:

                //Encode opcode
                short opError = 11;
                byte[] ErrorBytes = shortToBytes(opError);
                pushBytes(ErrorBytes);

                //Encode message opcode
                short messageOp2 = ((ErrorMsg)message).getMessageOp();
                byte[] messageOpBytes2 = shortToBytes(messageOp2);
                pushBytes(messageOpBytes2);

                break;
        }

        return encodeBytes;
    }


    private void pushBytes(byte[] push){
        for(int i = 0; i < push.length; i++){
            encodeBytes[encodeBytesInd] = push[i];
            encodeBytesInd++;
        }

    }

    private void pushZero(){
        byte[] zeroByte = shortToBytes((short)0);
        byte[] toPush = new byte[1];
        toPush[0] = zeroByte[1];
        pushBytes(toPush);
    }


    public short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    /**
     *
     * @param from
     * @return byte[] array from {@code from} to the next '\0' byte in the bytes array.
     */

    private byte[] subArrayToZero(int from){
        int size = 0;
        int i = 0;
        for(i = from; i < bytes.length; i++){
            size++;
            if(bytes[i] == '\0' | bytes[i] == '\n'){
                break;
            }
        }
        byte[] output = new byte[size];
        for(int j = 0; j < output.length ; j++){
            output[j] = bytes[from];
            from++;
        }

        return output;
    }

}
