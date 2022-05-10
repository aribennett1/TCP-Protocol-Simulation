import java.util.*;
import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        try (Socket clientSocket = new Socket("127.0.0.1", 32123);) {
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader receiveReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String packetReceived;
            ArrayList<Integer> missingPackets;
            packetReceived = receiveReader.readLine();
            System.out.println("(outside loop) First packetReceived: " + packetReceived);
            String[] packets = new String[Integer.parseInt(packetReceived.substring(packetReceived.indexOf("`") + 1, packetReceived.indexOf("``")))];
            System.out.println("packets.length: " + packets.length);
            packets[getIndex(packetReceived)] = packetReceived;
            System.out.println("Received packets: ");
            while (true) {
                packetReceived = receiveReader.readLine();
                if (packetReceived.equals("")) {
                    break;
                }
                if (getIndex(packetReceived) != -1) {
                    packets[getIndex(packetReceived)] = packetReceived;
                    System.out.print(getIndex(packetReceived) + ", ");
                }
            }
            System.out.println("\nChecking for missing packets...");
            missingPackets = checkForMissingPackets(packets);
            while (true) {
                if (missingPackets.size() == 1) {
                    printWriter.println(missingPackets.get(0));
                    break;
                }
                System.out.println("\nRequesting Packets:");
                for (int i = 0; i < missingPackets.size(); i++) {
                    printWriter.println(missingPackets.get(i));
                    System.out.print(missingPackets.get(i) + ", ");
                }
                System.out.println("\nReceived Packets: ");
                while (true) {
                    packetReceived = receiveReader.readLine();
                    if (packetReceived.equals("")) {
                        break;
                    }
                    packets[getIndex(packetReceived)] = packetReceived;
                    System.out.print(getIndex(packetReceived) + ", ");
                }
                System.out.println("\nChecking for missing Packets (again)...");
                missingPackets = checkForMissingPackets(packets);
            }
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < packets.length; i++) {
                str.append(getMessage(packets[i]));
            }
            System.out.println("String Received: " + str);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host 127.0.0.1");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static int getIndex(String str) {
        if (!str.contains("`")) {
            System.out.println(str + " doesn't have index");
            return -1;
        }
        return Integer.parseInt(str.substring(0, str.indexOf("`")));
    }

    private static String getMessage(String str) {
        return str.substring(str.indexOf("``") + 2, str.indexOf("```"));
    }

    private static ArrayList<Integer> checkForMissingPackets(String packets[]) {
        ArrayList<Integer> missingPackets = new ArrayList<>();
        for (int i = 0; i < packets.length; i++) {
            if (packets[i] == null) {
                missingPackets.add(i);
            }
        }
        if (missingPackets.size() == 0) {
            missingPackets.add(-2);
        } else {
            missingPackets.add(-1);
        }
        if (missingPackets.get(0) == -2) {
            System.out.println("Not missing any packets");
        } else {
            System.out.println("Missing the following Packets:");
            for (int i = 0; i < missingPackets.size() - 1; i++) {
                System.out.print(missingPackets.get(i) + ", ");
            }
        }
        return missingPackets;
    }


}
