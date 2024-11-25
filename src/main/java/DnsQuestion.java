
//Question Section (12 byte) of DNS contains the list of questions the sender wants to ask
//1. Header )
//2. Type of the record(2bit)
//3. Class(2bit)

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class DnsQuestion {
    private short id = 1234;
    private short flags = (short)0b10000000_00000000;
//    private short qdcount;
    private short qdcount = 1;
    private short ancount;
    private short nscount;
    private short arcount;

    public DnsQuestion() {}

    private ByteBuffer writeHeader(ByteBuffer buffer) {
        buffer.putShort(id);
        buffer.putShort(flags);
        buffer.putShort(qdcount);
        buffer.putShort(qdcount);
        buffer.putShort(ancount);
        buffer.putShort(nscount);
        return buffer;
    }

    private ByteBuffer writeQuestion(ByteBuffer buffer) {
        buffer.put(encodeDomainName("codecrafters.io"));
        buffer.putShort((short)1);
        buffer.putShort((short)1);
        return buffer;
    }

    private byte[] encodeDomainName(String domainName) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for(String label : domainName.split("\\.")){
            out.write(label.length());
            out.writeBytes(label.getBytes());
        }
        out.write(0);
        return out.toByteArray();
    }

    public byte[] array(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        writeHeader(byteBuffer);
        writeQuestion(byteBuffer);
        return byteBuffer.array();
    }

}
