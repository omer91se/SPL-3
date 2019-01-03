package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.bidi.ConnectionsImpl;
import bgu.spl.net.impl.bidi.MessageTypes.Msg;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private ConnectionsImpl<T> connections;
    private static AtomicInteger id = new AtomicInteger(1);

    /**
     *
     * @param sock
     * @param reader
     * @param protocol
     * @param connections
     */
    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol, ConnectionsImpl<T> connections) {
        this.connections = connections;
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        System.out.println("handler - in run()");
        try (Socket sock = this.sock) { //just for automatic closing
            int read;
            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            int connectionId = id.getAndIncrement();
            protocol.start(connectionId, connections);
            connections.add(connectionId,this);
            System.out.println("handler - getting messages");
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                System.out.println("handler - got byte");
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                    System.out.println("handler - got message");

                }
            }
            connections.disconnect(connectionId);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void send(T msg) {
        try {
            if (msg != null) {
                byte[] toSend = encdec.encode(msg);
                out.write(toSend);
                System.out.println("sending encoded message: " + ((Msg)msg).getMsgType());
                out.flush();
            }
        }
        catch(IOException ex){
                ex.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }
}
