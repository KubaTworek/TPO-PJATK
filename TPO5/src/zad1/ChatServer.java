/**
 * @author Tworek Jakub S25646
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatServer extends Thread {

    private final Lock lock = new ReentrantLock();
    private final Map<SocketChannel, String> connectedClients = new HashMap<>();
    private final String serverHost;
    private final int serverPort;
    private ServerSocketChannel serverChannel;
    private Selector selector = null;
    private final List<String> logMessages = new ArrayList<>();

    public ChatServer(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }

    public void startServer() throws IOException {
        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.socket().bind(new InetSocketAddress(this.serverHost, this.serverPort));
        this.serverChannel.configureBlocking(false);

        this.selector = Selector.open();
        this.serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        this.start();

        System.out.println("\nServer started");
    }

    public void stopServer() throws IOException {
        try {
            lock.lock();
            this.interrupt();
            this.selector.close();
            this.serverChannel.close();

            System.out.println("\nServer stopped");
        } catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
    }

    public void run() {
        while (!this.isInterrupted()) {
            try {
                selector.select();

                if (this.isInterrupted()) {
                    break;
                }

                Set<SelectionKey> keys = selector.selectedKeys();

                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if (key.isAcceptable()) {
                        SocketChannel clientChannel = this.serverChannel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        continue;
                    }

                    if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        this.handleRequest(clientChannel);
                    }
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    private String readMessage(SocketChannel socketChannel) throws IOException {
        StringBuilder messageBuffer = new StringBuilder();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Charset charset = StandardCharsets.UTF_8;

        while (socketChannel.read(buffer) > 0) {
            buffer.flip();
            CharBuffer charBuffer = charset.decode(buffer);
            while (charBuffer.hasRemaining()) {
                char c = charBuffer.get();
                if (c == '\n') {
                    return messageBuffer.toString();
                }

                messageBuffer.append(c);
            }
            buffer.clear();
        }

        return "";
    }

    private void handleRequest(SocketChannel socketChannel) throws IOException {
        String request = this.readMessage(socketChannel);

        Pattern r = Pattern.compile("([^\\t]+)\\t(.+)");
        Matcher m = r.matcher(request);

        if (!m.find()) {
            return;
        }

        String event = m.group(1);
        String message = m.group(2);

        if (event.equals("login")) {
            connectedClients.put(socketChannel, message);
        }

        String clientId = connectedClients.get(socketChannel);
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));

        String response = this.getLogMessage(event, message, clientId);

        this.logMessages.add(String.format("%s %s", time, response));

        this.broadcast(response);
    }

    private void broadcast(String response) throws IOException {
        for (SocketChannel clientSocket : connectedClients.keySet()) {
            clientSocket.write(StandardCharsets.UTF_8.encode(response + '\n'));
        }
    }

    private String getLogMessage(String event, String message, String clientId) {
        switch (event) {
            case "login":
                return String.format("%s logged in", clientId);
            case "message":
                return String.format("%s: %s", clientId, message);
            case "logout":
                return String.format("%s logged out", clientId);
            default:
                return "";
        }
    }

    public String getServerLog() {
        return String.join("\n", this.logMessages);
    }
}