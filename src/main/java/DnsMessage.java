import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class DnsMessage {

    // DNS Types and Classes
    public static class DnsTypes {
        public static class Qtype {
            public static final int A = 1;   // A record (IPv4 address)
            public static final int MX = 15; // MX record (Mail exchange)
        }

        public static class Cclass {
            public static final int IN = 1;  // IN class (Internet)
        }
    }

    // Method to generate the Answer Section
    public static byte[] answer(String domain, int qType, int qClass) {
        ByteBuffer buffer = ByteBuffer.allocate(512);
        buffer.put(encodeDomainName(domain));  // Encode domain name
        buffer.putShort((short) qType);  // Set query type (e.g., A for IPv4)
        buffer.putShort((short) qClass); // Set class (IN for internet)
        buffer.putInt(300);              // TTL (Time-to-live in seconds)
        buffer.putShort((short) 4);      // RDLENGTH (length of RDATA for IPv4)
        buffer.put(new byte[] {127, 0, 0, 1}); // RDATA (IPv4 address 127.0.0.1)
        return buffer.array();
    }

    // Method to generate the Question Section
    public static byte[] question(String domain, int qType, int qClass) {
        ByteBuffer buffer = ByteBuffer.allocate(512);
        buffer.put(encodeDomainName(domain)); // Encodes the domain name
        buffer.putShort((short) qType);  // Type (A, MX, etc.)
        buffer.putShort((short) qClass); // Class (IN for internet)
        return buffer.array();
    }

    // Domain Name encoding method
    private static byte[] encodeDomainName(String domain) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (String label : domain.split("\\.")) {
            out.write(label.length());
            out.writeBytes(label.getBytes(StandardCharsets.UTF_8));
        }
        out.write(0); // Null byte to terminate the domain name
        return out.toByteArray();
    }

    // Method to generate the header for DNS response
    public static byte[] header(byte[] received) throws IOException {
        final var dataInputStream = new java.io.DataInputStream(new java.io.ByteArrayInputStream(received));

        final var id = dataInputStream.readShort();
        final int receivedSecondLine = dataInputStream.readShort();
        int secondLine = 1 << 15;

        secondLine = setBits(secondLine, receivedSecondLine & 15 << 11);  // Set OPCODE
        secondLine = setBits(secondLine, receivedSecondLine & 1 << 8);    // Set RD
        secondLine = setBits(secondLine, 1 << 2); // Set RCODE

        return ByteBuffer.allocate(12)
                .order(java.nio.ByteOrder.BIG_ENDIAN)
                .putShort(id)
                .putShort((short) secondLine)
                .putShort((short) 0x01) // qdcount
                .putShort((short) 0x01) // ancount
                .putShort((short) 0x00) // nscount
                .putShort((short) 0x00) // arcount
                .array();
    }



    private static int setBits(int integer, int mask) {
        return integer | mask;
    }

}
