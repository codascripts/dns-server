//Your program will need to respond with a DNS reply packet that contains:
//        a header section (same as in stage #5)
//        a question section (new in this stage)
//        an answer section (new in this stage)



import javax.imageio.IIOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static java.nio.ByteOrder.BIG_ENDIAN;

//import static sun.jvm.hotspot.utilities.Bits.setBits;

public class DnsMessage {
    private static final int OPCODE = 15 << 11;
//    private static final int OPCODE_QUERY = 1 << 11; // Opcode for query (1)
//    private static final int OPCODE_RESPONSE = 0 << 11; // Opcode for response (0)
    private static final int RD = 1 << 8;  // Recursion Desired flag
    private static final int RCODE = 1 << 2;  // Response Code (no error)

    private short id = 1234;  // Transaction ID
    private short qdcount = 1;  // Number of questions
    private short ancount = 1;  // Number of answers
    private short nscount = 0;  // Number of authority records
    private short arcount = 0;  // Number of additional records
    private int secondLine = 0;  // Will hold QR, Opcode, RD, RCODE

    public DnsMessage() {}

    // This header method reads the received byte array and builds the header section
    public static byte[] header(byte[] received) throws IOException {
        final var dataInputStream = new DataInputStream(new ByteArrayInputStream(received));
        final var id = dataInputStream.readShort();  // Transaction ID
        final int receivedSecondLine = dataInputStream.readShort();  // Flags

        // QR = 1 for response
        int secondLine = 1 << 15;  // QR = 1 for response
        secondLine = setBits(secondLine, receivedSecondLine & OPCODE);  // Copy Opcode
        secondLine = setBits(secondLine, receivedSecondLine & RD);      // Copy Recursion Desired
        secondLine = setBits(secondLine, RCODE); // Set Response Code to 0 (no error)

        // Build the DNS header and return it as a byte array
        return ByteBuffer.allocate(12)
                .order(BIG_ENDIAN)
                .putShort(id)
                .putShort((short) secondLine)  // QR, Opcode, RD, RCODE
                .putShort((short) 0x01)  // qdcount
                .putShort((short) 0x01)  // ancount
                .putShort((short) 0x00)  // nscount
                .putShort((short) 0x00)  // arcount
                .array();
    }

    // Set bits for flags (QR, Opcode, RD, RCODE)
    private static int setBits(int integer, int mask) {
        return integer | mask;
    }

    // Generate the complete DNS message (header + question + answer)
    public byte[] array(byte[] received, String domain) throws IOException {
        byte[] header = header(received);  // Get header
        ByteBuffer byteBuffer = ByteBuffer.allocate(512); // Max size for DNS message
        byteBuffer.put(header);  // Add header

        // Write Question Section
//        byteBuffer.put(encodeDomainName("codecrafters.io"));
        byteBuffer.put(encodeDomainName(domain));
        byteBuffer.putShort((short) 1);  // Type A (IPv4 address)
        byteBuffer.putShort((short) 1);  // Class IN (Internet)

        // Write Answer Section
//        byteBuffer.put(encodeDomainName("codecrafters.io"));
        byteBuffer.put(encodeDomainName(domain));
        byteBuffer.putShort((short) 1);  // Type A (IPv4 address)
        byteBuffer.putShort((short) 1);  // Class IN (Internet)
        byteBuffer.putInt(60);           // TTL = 60 seconds
        byteBuffer.putShort((short) 4);  // RDLENGTH = 4 (IPv4 address length)
        byteBuffer.put(new byte[]{8, 8, 8, 8});  // RDATA = 8.8.8.8

        return byteBuffer.array();  // Return the full DNS response

    }

    // Encode the domain name in DNS format
    public static byte[] encodeDomainName(String domain) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (String label : domain.split("\\.")) {
            out.write(label.length());  // Write label length
            out.writeBytes(label.getBytes());  // Write label itself
        }
        out.write(0);  // Null byte to terminate the domain name
        return out.toByteArray();
    }

    // Set the transaction ID dynamically
    public void setId(short id) {
        this.id = id;
    }

    // Modify Recursion Desired flag
    public void setRecursionDesiredFlag(boolean desired) {
        if (desired) {
            secondLine = setBits(secondLine, RD); // Set RD bit
        } else {
            secondLine = setBits(secondLine, 0); // Clear RD bit
        }
    }
}
