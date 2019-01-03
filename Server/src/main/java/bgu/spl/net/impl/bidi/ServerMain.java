package bgu.spl.net.impl.bidi;

import bgu.spl.net.srv.Server;

public class ServerMain {
    public static void main(String[] args){
        BGSDB DB = new BGSDB();

        Server.threadPerClient(
                7777,
                 ()-> new BidiMessagingProtocolImpl<>(DB),
                 MessageEncoderDecoderImpl::new
        ).serve();

//        Server.reactor(
//                        Runtime.getRuntime().availableProcessors(),
//                   7777,
//                        ()-> new BidiMessagingProtocolImpl<>(DB),
//                        MessageEncoderDecoderImpl::new
//        ).serve();
    }

}
