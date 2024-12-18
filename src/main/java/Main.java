import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

public class Main {
  public static void main(String[] args){
    try (DatagramSocket serverSocket = new DatagramSocket(2053)) {
      while (true) {
        final byte[] buf = new byte[512];
        final DatagramPacket requestPacket = new DatagramPacket(buf, buf.length);
        serverSocket.receive(requestPacket);
        System.out.println("Received data");

        /*
        // getting the header of the dns server
        byte[] header = DnsMessage.header(buf);

        ByteBuffer responseBuffer = ByteBuffer.allocate(512);
        responseBuffer.put(header);  // Add DNS header to the response

        DnsMessage dnsMessage = new DnsMessage();
//        byte[] responseData = dnsMessage.array(requestPacket.getData());

        // Send the response packet back to the client
        byte[] responseData = responseBuffer.array(requestPacket.getData());

         */

        // Step 2: Extract domain name from query
        String domain = extractDomain(requestPacket.getData());
        System.out.println("Requested domain: " + domain);

        // Step 3: Generate DNS response
        DnsMessage dnsMessage = new DnsMessage();
        byte[] responseData = dnsMessage.array(requestPacket.getData(), domain);


        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, requestPacket.getSocketAddress());
        serverSocket.send(responsePacket);
        System.out.println("Sent DNS response to client");
      }
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

  // Method to extract domain name from the DNS query packet (question section)
  private static String extractDomain(byte[] receivedData) {
    StringBuilder domain = new StringBuilder();
    int index = 12;  // Skip DNS header part (12 bytes)

    while (receivedData[index] != 0) {  // A null byte indicates the end of the domain name
      int labelLength = receivedData[index];
      index++;
      for (int i = 0; i < labelLength; i++) {
        domain.append((char) receivedData[index + i]);
      }
      index += labelLength;
      domain.append(".");
    }

    // Remove the last dot (.)
    return domain.length() > 0 ? domain.substring(0, domain.length() - 1) : "";
  }





    /*
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage

     try(DatagramSocket serverSocket = new DatagramSocket(2053)) {
       while(true) {
           // request
         final byte[] buf = new byte[512];
         final DatagramPacket requestPacket = new DatagramPacket(buf, buf.length);
         serverSocket.receive(requestPacket);
         System.out.println("Received data");

         ByteBuffer requestBuffer = ByteBuffer.wrap(requestPacket.getData());
         short transactionId = requestBuffer.getShort(0);

//            Use DnsMessage to generate the full DNS response
         DnsMessage dnsMessage = new DnsMessage();
         dnsMessage.setId(transactionId);
         dnsMessage.setRecursionDesiredFlag(true);



         byte[] bufResponse = dnsMessage.array(requestPacket.getData());
//         short ID = (short) 1234;
//           byte[] bufResponse = dnsMessage.array(); // This method generates header, question, and answer sections
         // we are considering bitset of 8 actually in real header is of
//         final var bitSet = new BitSet(8);
         // 7th bit tells whether if it's a query or response
//         bitSet.flip(7);
//         final var bufResponse = ByteBuffer.allocate(512)
//                 .order(ByteOrder.BIG_ENDIAN)
//                 .putShort(ID)
//                 .put(bitSet.toByteArray())
//                 .putShort((byte)0)
//                 .putShort((short)0)
//                 .putShort((short)0)
//                 .putShort((short)0)
//                 .putShort((short)0)
//                 .array();
            // we are already creating buff response above
//         final byte[] bufResponse = new byte[512];
         final DatagramPacket responsePacket = new DatagramPacket(bufResponse, bufResponse.length, requestPacket.getSocketAddress());
         serverSocket.send(responsePacket);
       }
     } catch (IOException e) {
         System.out.println("IOException: " + e.getMessage());
     }
  }
     */
}
