package RECUIT;

import OPERATIONS.FLIGHT.Flight;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;


public class GlobalSettings {
    public static boolean DISABLE_OPTIMIZATION = false;
    private static Flight[] tableFlight;
    private static String FILENAME_PREFIX = ""; // e.g., "PMS_eliminate0_150%_reduced_filteredSAREX"
    private static String LOG_FILE_PATH = FILENAME_PREFIX + "_node_conflicts.log"; // conflicts
    private static String NODE_LOG_FILE_PATH = FILENAME_PREFIX + "_node_events.log"; // node events
    private static String LINK_TRAVEL_TIME_LOG_FILE_PATH = FILENAME_PREFIX + "_link_travel_time.log"; // link travel time
    private static PrintWriter nodeLogWriter;
    private static PrintWriter conflictLogWriter;
    private static PrintWriter linkTravelTimeLogWriter;
    public static boolean ENABLE_LOG = false;
    public static int[] entryWithPMS; // PMS entry numbers, like 0,1,2,3

    public static void deleteFileIfExists(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void initializeGlobalSettings(String filename_prefix) {
        // Initialize default values
        DISABLE_OPTIMIZATION = false;
        ENABLE_LOG = false;
        FILENAME_PREFIX = filename_prefix.replaceAll("[^a-zA-Z0-9]", "");
        entryWithPMS = new int[0];

        LOG_FILE_PATH = FILENAME_PREFIX + "_node_conflicts.log";
        NODE_LOG_FILE_PATH = FILENAME_PREFIX + "_node_events.log";
        LINK_TRAVEL_TIME_LOG_FILE_PATH = FILENAME_PREFIX + "_link_travel_time.log";

        // Delete existing log files if they exist
        deleteFileIfExists(LOG_FILE_PATH);
        deleteFileIfExists(NODE_LOG_FILE_PATH);
        deleteFileIfExists(LINK_TRAVEL_TIME_LOG_FILE_PATH);

        // Initialize log writers
        try {
            conflictLogWriter = new PrintWriter(new FileWriter(LOG_FILE_PATH, true));
            nodeLogWriter = new PrintWriter(new FileWriter(NODE_LOG_FILE_PATH, true));
            linkTravelTimeLogWriter = new PrintWriter(new FileWriter(LINK_TRAVEL_TIME_LOG_FILE_PATH, true));
        } catch (IOException e) {
            System.err.println("Error initializing log writers in constructor: " + e.getMessage());
        }
    }

    // Get, set for PMS entry numbers
    public static int[] getEntryWithPMS() {
        return entryWithPMS;
    }
    public static void setEntryWithPMS(int[] entryWithPMS) {
        GlobalSettings.entryWithPMS = entryWithPMS;
    }

    public static void setTableFlight(Flight[] tableFlight) {
        GlobalSettings.tableFlight = tableFlight;
    }

    public static String showFlightInfo(int flightNumber) {
        String message = "Flight number: " + tableFlight[flightNumber].flightNumber + "\n" +
                         "Callsign: " + tableFlight[flightNumber].callsign + "\n" +
                         "Entry number: " + tableFlight[flightNumber].entryNumber + "\n" +
                         "Entry time: " + tableFlight[flightNumber].entryTime + "\n" +
                         "Speed in: " + tableFlight[flightNumber].speedIn + "\n" +
                         "WTCat: " + tableFlight[flightNumber].wTCat + "\n";
        return message;
    }

    public static void writeToNodeLogFile(String message) {
        if (ENABLE_LOG) {
            if (nodeLogWriter != null) {
                nodeLogWriter.println(message);
                nodeLogWriter.flush(); // Ensure the message is written immediately
            } else {
                System.err.println("Node log writer is not initialized. Message: " + message);
            }
        }
    }

    public static void writeToLinkTravelTimeLogFile(String message) {
        if (ENABLE_LOG) {
            if (linkTravelTimeLogWriter != null) {
                linkTravelTimeLogWriter.println(message);
                linkTravelTimeLogWriter.flush(); // Ensure the message is written immediately
            }
        }
    }
    

    public static void writeToConflictLogFile(String message) {
        if (ENABLE_LOG) {
            if (conflictLogWriter != null) {
                conflictLogWriter.println(message);
                conflictLogWriter.flush(); // Ensure the message is written immediately
            } else {
                System.err.println("Log writer is not initialized. Message: " + message);
            }
        }
    }

    public static void closeLogWriters() {
        if (conflictLogWriter != null) {
            conflictLogWriter.close();
        }
        if (nodeLogWriter != null) {
            nodeLogWriter.close();
        }
        if (linkTravelTimeLogWriter != null) {
            linkTravelTimeLogWriter.close();
        }
    }
}

