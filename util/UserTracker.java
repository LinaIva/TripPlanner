package util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserTracker {

    private static final Set<String> activeUsers = ConcurrentHashMap.newKeySet();

    public static void userLoggedIn(String username) {
        activeUsers.add(username);
    }

    public static void userLoggedOut(String username) {
        activeUsers.remove(username);
    }

    public static int getActiveUserCount() {
        return activeUsers.size();
    }

    public static Set<String> getActiveUsers() {
        return activeUsers;
    }
}