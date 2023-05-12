/**
 * @author Tworek Jakub S25646
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatClient extends Thread {
    private final Lock lock = new ReentrantLock();
    private SocketChannel channel = null;
    private final String clientId;
    private final List<String> chatLogs = new ArrayList<>();
    private final String serverHost;
    private final int serverPort;

    public ChatClient(String serverHost, int serverPort, String clientId) {
        this.clientId = clientId;
        this.serverHost = serverHost;
        this.serverPort = serverPort;

        this.chatLogs.add(String.format("\n=== %s chat view", clientId));
    }

    public void login() {
        try {
            this.channel = SocketChannel.open(new InetSocketAddress(this.serverHost, this.serverPort));
            channel.configureBlocking(false);

            this.makeRequest("login", this.clientId);

            this.start();
        } catch (Exception e) {
            this.logException(e);
        }
    }

    public void logout() {
        try {
            this.makeRequest("logout", this.clientId);

            this.chatLogs.add(String.format("%s logged out", this.clientId));

            lock.lock();
            this.interrupt();
        } catch (Exception e) {
            this.logException(e);
        } finally {
            lock.unlock();
        }
    }

    public void send(String message) {
        this.makeRequest("message", message);
    }

    private void makeRequest(String event, String data) {
        try {
            String requestString = String.format("%s\t%s", event, data);

            this.channel.write(StandardCharsets.UTF_8.encode(requestString + '\n'));
        } catch (Exception e) {
            this.logException(e);
        }
    }

    public String getChatView() {
        return String.join("\n", this.chatLogs);
    }

    public void run() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        int bytesRead = 0;

        while (!this.isInterrupted()) {
            Charset charset = StandardCharsets.UTF_8;
            StringBuilder stringBuffer = new StringBuilder();

            do {
                try {
                    lock.lock();
                    bytesRead = channel.read(buffer);
                    if (bytesRead > 0) {
                        buffer.flip();
                        CharBuffer charBuffer = charset.decode(buffer);
                        while (charBuffer.hasRemaining()) {
                            char c = charBuffer.get();

                            if (c != '\n') {
                                stringBuffer.append(c);
                            }
                        }
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                } finally {
                    lock.unlock();
                }
            } while (bytesRead == 0 && !this.isInterrupted());

            String response = stringBuffer.toString();

            if (!response.equals("")) {
                this.chatLogs.add(response);
            }

            buffer.clear();
            bytesRead = 0;
        }
    }

    private void logException(Exception exception) {
        this.chatLogs.add(String.format("*** %s", exception.toString()));
    }
}
