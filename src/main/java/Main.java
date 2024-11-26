import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

public class Main {
  public static void main(String[] args){

    // You can use print statements as follows for debugging, they'll be visible when running tests.
      System.out.println("Logs from your program will appear here!");

     // setting up a UDP connection
      /*
      DatagramSocket is used to handle UDP communication. It binds the server to port 2053,
       which is typically reserved for DNS
      */

      /* this is the header section (always 12 byte) */
      try(DatagramSocket serverSocket = new DatagramSocket(2053)) {
          // the server continuously listens for incoming packets ensuring it remains operational
          // if it while(false) the server will not listen to any packets
          // ensures that server keeps running to handle multiple client requests
          while(true) {
              //// Request
              // UDP limits messages to 512 bytes
              // buffer memory used to hold data
              final byte[] buf = new byte[512];
              // DatagramPacket encapsulates buffer in a Datapacket(also known as DatagramPacket)
              /*
              DatagramSocket is used to handle UDP communication. It binds the server to
              port 2053, which is typically reserved for DNS
              */
              final DatagramPacket packet = new DatagramPacket(buf, buf.length);
              // blocks the program until a packet is received
              serverSocket.receive(packet);
              System.out.println("Received data");

              //// Response
              // every dns request has a id here we have set the id to 1234
              short ID = (short) 1234;
              // BitSet is used to represent "flags" in Header this flags contains the QR(Query/Response)
              final var bitSet = new BitSet(8);
              // flag is of 16 bit but here we are considering only the most significant bits
              // of it like QR, OPCODE, AA etc
              // and the 8th bit is QR flipping it to 1 means it is a response and 0 - query
              bitSet.flip(7);
              // flag is of 16 bit but here we are considering only the most
              // significant bits of it like QR, OPCODE, AA etc and the 8th bit is QR
              // flipping it to 1 means it is a response and 0 - query
              short zero = (short) 0;
//              A short variable named zero is initialized to 0.
//              This will be used to set fields in the header that are expected to be 0.
              /*

              final var bufResponse = ByteBuffer.allocate(512)
                      .order(ByteOrder.BIG_ENDIAN)
                      .putShort(ID)
                      .put(bitSet.toByteArray())
                      .put((byte) 0)
                      .putShort(zero)
                      .putShort(zero)
                      .putShort(zero)
                      .putShort(zero)
                      .array();
              */
              DnsQuestion dnsQuestion = new DnsQuestion();
              byte[] bufferResponse = dnsQuestion.array();
              // Prepares a response packet addressed to the client that sent the original packet.
              final DatagramPacket packetResponse = new DatagramPacket(
                      bufferResponse, bufferResponse.length, packet.getSocketAddress()
              );
              serverSocket.send(packetResponse);
          }
      } catch (IOException e) {
         System.out.println("IOException: " + e.getMessage());
      }



  }

}
