/**
 * @author Tworek Jakub S25646
 */

package zad1;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ChatClientTask extends FutureTask<ChatClient> {
    public ChatClientTask(Callable<ChatClient> callable) {
        super(callable);
    }

    public static ChatClientTask create(ChatClient client, List<String> messages, int wait) {
        return new ChatClientTask(() -> runTask(client, messages, wait));
    }

    private static ChatClient runTask(ChatClient client, List<String> messages, int wait) {
        try {
            client.login();
            sleep(wait);

            for (String message : messages) {
                client.send(message);
                sleep(wait);
            }

            client.logout();
            sleep(wait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return client;
    }

    private static void sleep(int waitTime) throws InterruptedException {
        if (waitTime == 0) {
            return;
        }

        Thread.sleep(waitTime);
    }

    public ChatClient getClient() {
        try {
            return this.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
