package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EnvConfig {

    private static final Map<String, String> FILE_VALUES = loadEnvFile();

    private EnvConfig() {
    }

    public static String get(String key) {
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        String fileValue = FILE_VALUES.get(key);
        if (fileValue != null && !fileValue.isBlank()) {
            return fileValue;
        }

        return null;
    }

    private static Map<String, String> loadEnvFile() {
        Map<String, String> values = new HashMap<>();

        for (Path path : candidatePaths()) {
            if (!Files.exists(path)) {
                continue;
            }

            try {
                List<String> lines = Files.readAllLines(path);

                for (String rawLine : lines) {
                    String line = rawLine.trim();

                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    int separator = line.indexOf('=');
                    if (separator <= 0) {
                        continue;
                    }

                    String key = line.substring(0, separator).trim();
                    String value = line.substring(separator + 1).trim();
                    values.put(key, value);
                }

                return values;
            } catch (IOException ignored) {
                // Try the next candidate path.
            }
        }

        return values;
    }

    private static Path[] candidatePaths() {
        String userDir = System.getProperty("user.dir", ".");
        String catalinaBase = System.getProperty("catalina.base");
        String userHome = System.getProperty("user.home", "");
        Path projectRootEnv = Paths.get(userHome, "study", "master", "java advanced", "TripPlanner", ".env");

        if (catalinaBase == null || catalinaBase.isBlank()) {
            return new Path[] {
                    Paths.get(".env"),
                    Paths.get(userDir, ".env"),
                    projectRootEnv
            };
        }

        return new Path[] {
                Paths.get(".env"),
                Paths.get(userDir, ".env"),
                projectRootEnv,
                Paths.get(catalinaBase, "webapps", "tripplanner", ".env"),
                Paths.get(catalinaBase, "webapps", "tripplanner", "WEB-INF", "classes", ".env")
        };
    }
}
