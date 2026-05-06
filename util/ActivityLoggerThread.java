package util;

public class ActivityLoggerThread extends Thread {

    private boolean running = true;

    @Override
    public void run() {

        while (running) {
            try {
                Thread.sleep(5000);

                System.out.println("========== System Status ==========");
                System.out.println("Time: " + new java.util.Date());
                System.out.println("Active users: " + UserTracker.getActiveUserCount());

                for (String user : UserTracker.getActiveUsers()) {
                    System.out.println(" - " + user);
                }

                System.out.println("===================================");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopLogger() {
        running = false;
    }
}