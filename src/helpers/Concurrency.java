package helpers;

public class Concurrency {
    public static void runDaemon(Runnable target) {
        Thread t = new Thread(target);
        t.setDaemon(true);
        t.start();
    }
}
