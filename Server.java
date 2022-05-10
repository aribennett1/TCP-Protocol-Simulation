import java.util.*;
import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) {
        String str = "An operating system (OS) is system software that manages computer hardware, software resources, and provides common services for computer programs. Time-sharing operating systems schedule tasks for efficient use of the system and may also include accounting software for cost allocation of processor time, mass storage, printing, and other resources.";
        System.out.println("str: " + str);
        System.out.println("str.length(): " + str.length());
        final int charsPerPacket = 20;
        int packetsNeeded = (int) Math.ceil((double) str.length() / (double) charsPerPacket);
        System.out.println("packetsNeeded: " + packetsNeeded);
        int endIndex;
        String[] packets = new String[packetsNeeded];
        for (int i = 0; i < packets.length - 1; i++) {
            packets[i] = ("" + i + "`" + packetsNeeded + "``" + str.substring(0, charsPerPacket)) + "```";
            str = str.substring(charsPerPacket);
        }
        packets[packets.length - 1] = "" + (packets.length - 1) + "``" + str + "```";
        int portNumber = 32123;
        try (ServerSocket serverSocket = new ServerSocket(portNumber);
             Socket clientSocket1 = serverSocket.accept();
             PrintWriter sendWriter = new PrintWriter(clientSocket1.getOutputStream(), true);
             BufferedReader receiveReader = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
        ) {
            System.out.print("Sending Packets: ");
            for (int i = 1; i < packets.length - 1; i++) {
                if (Math.random() > .2) {
                System.out.print(packets[i].substring(0, packets[i].indexOf("`")) + "; ");
                sendWriter.println(packets[i]);
                }
            }
            System.out.print("\nLast send: " + (packets[packets.length - 1].substring(0, packets[packets.length - 1].indexOf("`"))));
            sendWriter.println(packets[packets.length - 1]);
            sendWriter.println("");
            ArrayList<Integer> droppedPackets = new ArrayList<>();
            int tempInt;
            String tempIntStr;
            while (true) {
                tempIntStr = receiveReader.readLine();
                tempInt = Integer.parseInt(tempIntStr);
                if (tempInt == -2) {
                    System.out.println("\nSending complete, all packets received");
                    break;
                }
                while (true) {
                    if (tempInt == -1) {break;}
                    droppedPackets.add(tempInt);
                    tempIntStr = receiveReader.readLine();
                    tempInt = Integer.parseInt(tempIntStr);
                }
                System.out.print("\nSending Packets: ");
                for (int i = 0; i < droppedPackets.size(); i++) {
                    if (Math.random() > .2) {
                        System.out.print(packets[droppedPackets.get(i)].substring(0, packets[droppedPackets.get(i)].indexOf("`")) + "; ");
                        sendWriter.println(packets[droppedPackets.get(i)]);
                        droppedPackets.remove(i);
                    }
                }
                sendWriter.println("");
            }
            System.out.println("All Packets Sent!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}

