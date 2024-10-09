import java.io.*;
import java.util.*;

public class FlowLogParser {

    // Stores tag mappings from the lookup file
    private static Map<String, String> tagMap = new HashMap<>();
    
    // Counters for tag matches and port/protocol matches
    private static Map<String, Integer> tagCounts = new HashMap<>();
    private static Map<String, Integer> portProtocolCounts = new HashMap<>();

    public static void main(String[] args) {
        String flowLogFile = "flowlogs.txt";
        String lookupFile = "lookup.csv";

        try {
            loadLookupTable(lookupFile);
            parseFlowLogs(flowLogFile);
            generateOutput();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Load the lookup table into memory
    private static void loadLookupTable(String lookupFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(lookupFile));
        String line;
        
        // Skip the header
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length < 3) {
                System.err.println("Skipping invalid lookup row: " + line);
                continue;
            }
            String dstport = parts[0].trim();
            String protocol = parts[1].trim().toLowerCase();
            String tag = parts[2].trim();

            // Map port/protocol combination to tag
            String key = dstport + "," + protocol;
            tagMap.put(key, tag);
        }
        
        reader.close();
    }

    // Parse the flow log data and map rows to tags
    private static void parseFlowLogs(String flowLogFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(flowLogFile));
        String line;

        while ((line = reader.readLine()) != null) {
            // Skip empty or blank lines
            if (line.trim().isEmpty()) {
                continue;
            }

            // Split the line by one or more whitespace characters
            String[] parts = line.trim().split("\\s+");

            // Ensure we have at least 8 parts to extract the necessary fields
            if (parts.length < 14) {
                System.err.println("Malformed line (skipped): " + line);
                continue;
            }

            String dstport = parts[6].trim(); // destination port is the 7th element (index 6)

            // Correctly extract and map protocol from parts[7] (8th element)
            String protocolNum = parts[7].trim();
            String protocol;
            if (protocolNum.equals("6")) {
                protocol = "tcp";
            } else if (protocolNum.equals("17")) {
                protocol = "udp";
            } else {
                protocol = "other";
            }

            String key = dstport + "," + protocol;

            // Update port/protocol match count
            portProtocolCounts.put(key, portProtocolCounts.getOrDefault(key, 0) + 1);

            // Match to tag
            String tag = tagMap.getOrDefault(key, "Untagged");
            tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
        }

        reader.close();
    }

    // Generate the final output
    private static void generateOutput() throws IOException {
        // Write tag counts to file in CSV format
        BufferedWriter tagWriter = new BufferedWriter(new FileWriter("tag_counts.csv"));
        
        // Write header for the tag counts
        tagWriter.write("Tag,Count\n");

        // Write each entry in the tagCounts map
        for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
            tagWriter.write(entry.getKey() + "," + entry.getValue() + "\n");
        }
        tagWriter.close();
        
        // Write port/protocol combination counts to file in CSV format
        BufferedWriter portProtocolWriter = new BufferedWriter(new FileWriter("port_protocol_counts.csv"));
        
        // Write header for port/protocol counts
        portProtocolWriter.write("Port,Protocol,Count\n");

        // Write each entry in the portProtocolCounts map
        for (Map.Entry<String, Integer> entry : portProtocolCounts.entrySet()) {
            String[] keyParts = entry.getKey().split(",");
            portProtocolWriter.write(keyParts[0] + "," + keyParts[1] + "," + entry.getValue() + "\n");
        }
        portProtocolWriter.close();
        
        // Print output to console in the required format for quick verification
        System.out.println("Tag Counts:");
        for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
            System.out.println(entry.getKey() + "," + entry.getValue());
        }

        System.out.println("\nPort/Protocol Combination Counts:");
        for (Map.Entry<String, Integer> entry : portProtocolCounts.entrySet()) {
            String[] keyParts = entry.getKey().split(",");
            System.out.println(keyParts[0] + "," + keyParts[1] + "," + entry.getValue());
        }
    }

}