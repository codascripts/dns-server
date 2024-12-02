
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import static java.lang.System.arraycopy;

public class Main {
    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");

        try (DatagramSocket serverSocket = new DatagramSocket(2053)) {
            while (true) {
                // Receiving DNS query
                final byte[] buffer = new byte[512];
                final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet);
                System.out.println("Received data");

                // Get DNS header, question, and answer
                final var header = DnsMessage.header(buffer);
                final var questionPacket = DnsMessage.question("codecrafters.io", DnsMessage.DnsTypes.Qtype.A, DnsMessage.DnsTypes.Cclass.IN);
                final var answerPacket = DnsMessage.answer("codecrafters.io", DnsMessage.DnsTypes.Qtype.A, DnsMessage.DnsTypes.Cclass.IN);

                // Create the full response packet
                final var bufferResponse = new byte[header.length + questionPacket.length + answerPacket.length];
                arraycopy(header, 0, bufferResponse, 0, header.length);
                arraycopy(questionPacket, 0, bufferResponse, header.length, questionPacket.length);
                arraycopy(answerPacket, 0, bufferResponse, header.length + questionPacket.length, answerPacket.length);

                // Sending response back to the client
                final DatagramPacket packetResponse = new DatagramPacket(bufferResponse, bufferResponse.length, packet.getSocketAddress());
                serverSocket.send(packetResponse);
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}


