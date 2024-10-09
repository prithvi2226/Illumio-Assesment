# Flow Log Parser

## Project Overview

The **Flow Log Parser** is a Java program that processes AWS VPC Flow Logs to generate counts of matches for each tag and port/protocol combination based on a provided lookup table.

## Assumptions

- **Flow Log Format**: Supports only the default AWS VPC Flow Logs format, specifically version **2**.
- **Protocol Mapping**:
  - Protocol numbers are mapped as follows:
    - `6` ➔ `tcp`
    - `17` ➔ `udp`
    - All other protocol numbers are categorized as `other`.
- **Case Insensitivity**: All matches for protocol names and tags are case-insensitive.
- **Multiple Tags**: A single tag can map to multiple port/protocol combinations.
- **Input Files**:
  - Flow log files are plain text (ASCII) files up to **10 MB** in size.
  - The lookup file can have up to **10,000** mappings.

## Compilation and Execution Instructions

1. **Ensure Java is Installed**:

   - The program requires Java Development Kit (JDK) version 8 or higher.

2. **Place Input Files**:

   - Ensure that `FlowLogParser.java`, `flowlogs.txt`, and `lookup.csv` are in the same directory.

3. **Compile and Run the Program**:

   Open a terminal or command prompt in the directory containing the files and run:

   ```bash
   javac -d bin src/FlowLogParser.java
   cd bin             
   java FlowLogParser flowlogs.txt lookup.csv output.csv
   ```

4. **Result**:

   - The program will process flowlogs.txt and lookup.csv and generate two output files:
	•	tag_counts.csv
	•	port_protocol_counts.csv