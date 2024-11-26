
//Question Section (12 byte) of DNS contains the list of questions the sender wants to ask
// Question Section has 2 parts
//1. Domain Name(ex google.com)
//2. Type of the record(2byte): In the "question" section of a DNS query, the "type" field specifies the specific type of DNS record being requested,
//   such as "A" for an IPv4 address, "AAAA" for an IPv6 address, "MX" for a mail exchanger, or "CNAME" for a canonical name,
//   essentially telling the DNS server what kind of information you are looking for about the queried domain name.
//3. Class(2byte): the class is a two-octet code that specifies the class of the query.
//   The most common class is IN, which stands for "Internet".

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class DnsQuestion {
    private short id = 1234;
    // because domain names are encoded in hex code
    private short flags = (short)0b10000000_00000000;
    /*
    In the updated code, the qdcount field,
    which represents the number of questions in the DNS message,
    is initialized to 1. This change indicates that there will always be one question in the DNS query that the server is expected to handle. Previously, qdcount was not initialized, which meant its value was undefined and needed to be set later.
    By initializing it directly in the class field declaration,
    the code ensures that the DNS message header will always correctly indicate the presence of one question.
    private short qdcount = 1;
    */
    // number of questions in the Question Section
    private short qdcount = 1;
    // number of resource records in the Answer Section
    private short ancount;
    // number of resource records in the Authority Section
    private short nscount;
    // number of resource records in the Additional Section
    private short arcount;

    public DnsQuestion() {}


    private byte[] encodeDomainName(String domainName) {
        /*
        The added method encodeDomainName takes a domain name as a string and encodes it into the format expected by the DNS protocol.
        It uses a ByteArrayOutputStream to construct the byte array.
        The domain is split into labels using the split method on the period character.
        For each label, the length is written as a single byte, followed by the actual bytes of the label.
        After processing all labels, a null byte (0) is written to signify the end of the domain name sequence.
        The resulting byte array represents the encoded domain name, ready to be included in the DNS message.
        */
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for(String label : domainName.split("\\.")){
            out.write(label.length());
            out.writeBytes(label.getBytes());
        }
        // to specify the end of the domain name
        out.write(0);
        return out.toByteArray();
    }

    // after refractoring
    private ByteBuffer writeHeader(ByteBuffer buffer) {
        buffer.putShort(id);
        buffer.putShort(flags);
        buffer.putShort(qdcount);
        buffer.putShort(ancount);
        buffer.putShort(nscount);
        buffer.putShort(arcount);

        return buffer;
    }

    /*
    google.com is encoded as \x06google\x03com\x00 (in hex: 06 67 6f 6f 67 6c 65 03 63 6f 6d 00)
        \x06google is the first label
            \x06 is a single byte, which is the length of the label
            google is the content of the label
        \x03com is the second label
            \x03 is a single byte, which is the length of the label
            com is the content of the label
        \x00 is the null byte that terminates the domain name
    */

    private ByteBuffer writeQuestion(ByteBuffer buffer) {
        /*
        The writeQuestion method is responsible for adding the question section to the DNS message.
        It takes a ByteBuffer as an argument, which is used to construct the message
        */
        buffer.put(encodeDomainName("codecrafters.io")); // Domain Name
//        The Type is set to 1, which corresponds to an A record (host address),
//        and the Class is set to 1, which corresponds to the IN (Internet) class.
//        After adding these values, the buffer is returned,
        buffer.putShort((short)1); // Type = A
        buffer.putShort((short)1); // Class = IN
        return buffer;
    }


    // before refractoring
    public byte[] array(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        writeHeader(byteBuffer);
        writeQuestion(byteBuffer);
        return byteBuffer.array();
    }
}
