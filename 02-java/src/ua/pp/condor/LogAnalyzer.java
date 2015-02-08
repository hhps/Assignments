package ua.pp.condor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * This tool analyzes specified log file and prints a list of users
 * in descending order by their online time in format:
 *     User #: online time
 *
 * The format of log file:
 *     UNIX time, user id, action (login/logout)
 *
 * Input:
 *     123456, 12, login
 *     123856, 12, logout
 *
 * Output:
 *     User #12: 6 minutes, 40 seconds
 */
public final class LogAnalyzer {

    // User actions
    private static final String LOGIN = "login";
    private static final String LOGOUT = "logout";

    // Time periods in seconds
    private static final long MINUTE = 60;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;

    // Used to save login time for each user: Map<userId, loginTime>
    private static final Map<Long, Long> loginTimes = new HashMap<>();
    // Used to save online time for each user: Map<userId, onlineTime>
    private static final Map<Long, Long> onlineTimes = new HashMap<>();

    /**
     * Gets the file for specified path and checks that it is a readable file.
     */
    private static File getFile(final String path) {
        File logFile = new File(path);
        if (!logFile.isFile()) {
            throw new IllegalArgumentException(path + " - is not a file");
        }
        if (!logFile.canRead()) {
            throw new IllegalArgumentException(path + " - is not a readable file");
        }
        return logFile;
    }

    /**
     * Processes the one line of log file.
     */
    private static void processLine(final String line, final int lineNumber) {
        String[] lineComponents = line.split(",");
        if (lineComponents.length != 3) {
            throw new IllegalStateException(String.format(
                    "Error in line %d, incorrect log line: %s", lineNumber, line));
        }
        long unixTime = Long.parseLong(lineComponents[0].trim());
        long userId = Long.parseLong(lineComponents[1].trim());
        String action = lineComponents[2].trim();

        switch (action) {
            case LOGIN: {
                Long value = loginTimes.put(userId, unixTime);
                if (value != null) {
                    throw new IllegalStateException(String.format(
                            "Error in line %d, user(#%d) can not login 2 times: %s", lineNumber, userId, line));
                }
                break;
            }
            case LOGOUT: {
                Long loginTime = loginTimes.remove(userId);
                if (loginTime == null) {
                    throw new IllegalStateException(String.format(
                            "Error in line %d, user(#%d) can not logout befor login: %s", lineNumber, userId, line));
                }
                long onlineTime = unixTime - loginTime;
                if (onlineTime < 0) {
                    throw new IllegalStateException(String.format(
                            "Error in line %d, user(#%d) can not be online less than 0 seconds: %s",
                            lineNumber, userId, line));
                }

                Long currentOnlineTime = onlineTimes.get(userId);
                onlineTimes.put(userId, currentOnlineTime == null ?
                        onlineTime : currentOnlineTime + onlineTime);
                break;
            }
            default: {
                throw new IllegalStateException(String.format(
                        "Error in line %d, incorrect user action: %s", lineNumber, action));
            }
        }
    }

    /**
     * Prints results in descending order by online time for each user in format:
     * User #: online time
     */
    private static void printResult() {
        Map<Long, Long> sortedMap = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<Long, Long> entry : onlineTimes.entrySet()) {
            sortedMap.put(entry.getValue(), entry.getKey());
        }

        StringBuilder sb = new StringBuilder(80);
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.out), true)) {
            for (Map.Entry<Long, Long> entry : sortedMap.entrySet()) {
                sb.append("User #").append(entry.getValue()).append(": ");

                long seconds = entry.getKey();
                if (seconds == 0) {
                    sb.append(0).append(" seconds");
                    writer.println(sb);
                    sb.setLength(0);
                    continue;
                }
                if (seconds >= DAY) {
                    long days = seconds / DAY;
                    sb.append(days).append(" day(s), ");
                    seconds %= DAY;
                }
                if (seconds >= HOUR) {
                    long hours = seconds / HOUR;
                    sb.append(hours).append(" hour(s), ");
                    seconds %= HOUR;
                }
                if (seconds >= MINUTE) {
                    long minutes = seconds / MINUTE;
                    sb.append(minutes).append(" minute(s), ");
                    seconds %= MINUTE;
                }
                if (seconds > 0) {
                    sb.append(seconds).append(" second(s), ");
                }

                sb.setLength(sb.length() - 2);
                writer.println(sb);
                sb.setLength(0);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Incorrect argument number.\n" +
                    "Use: java LogAnalyzer pathToLogFile");
        }

        File logFile = getFile(args[0]);
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                processLine(line, lineNumber++);
            }
        }
        assert loginTimes.isEmpty();
        printResult();
    }
}
