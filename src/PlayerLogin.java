import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PlayerLogin {
    String address;
    int port;
    String login_player_name;


    public PlayerLogin(String address, int port, String login_player_name) {
    	this.address = address;
    	this.port = port;
    	this.login_player_name = login_player_name;
    }

    public void PlayerState() throws IOException {
	    InetSocketAddress host = new InetSocketAddress(address, port);
	    Socket socket = new Socket();
	    System.out.println("Connecting...");
	    socket.connect(host, 3000);
	    System.out.println("Done!");
	    System.out.println("Making streams...");
	    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
	    DataInputStream input = new DataInputStream(socket.getInputStream());

	    System.out.println("Done!");
	    System.out.println("Attempting handshake... "+host.getAddress().toString());

	    // C->S : Handshake State=2
	    // send packet length and packet
	    byte [] handshakeMessage = createHandshakeMessage(address, port);
	    writeVarInt(output, handshakeMessage.length);
	    output.write(handshakeMessage);

//	    // C->S : Request
//	    output.writeByte(0x01); //size is only 1
//	    output.writeByte(0x00); //packet id for ping
//
//	    // S->C : Response
//	    int size = readVarInt(input);
//	    int packetId = readVarInt(input);
//
//	    if (packetId == -1) {
//	        throw new IOException("Premature end of stream.");
//	    }
//
//	    if (packetId != 0x00) { //we want a status response
//	        throw new IOException("Invalid packetID");
//	    }
//	    int length = readVarInt(input); //length of json string
//
//	    if (length == -1) {
//	        throw new IOException("Premature end of stream.");
//	    }
//
//	    if (length == 0) {
//	        throw new IOException("Invalid string length.");
//	    }
//
//	    byte[] in = new byte[length];
//	    input.readFully(in);  //read json string
//	    String json = new String(in);
//	    System.out.println(json);


	//    // C->S : Ping
	//    long now = System.currentTimeMillis();
	//    output.writeByte(0x09); //size of packet
	//    output.writeByte(0x01); //0x01 for ping
	//    output.writeLong(now); //time!?
	//
	//    // S->C : Pong
	//    readVarInt(input);
	//    int packetId = readVarInt(input);
	//    if (packetId == -1) {
	//        throw new IOException("Premature end of stream.");
	//    }
	//
	//    if (packetId != 0x01) {
	//        throw new IOException("Invalid packetID");
	//    }
	//    long pingtime = input.readLong(); //read response

	    // C->S : Login Start
	    byte [] loginMessage = createLoginMessage(login_player_name);
	    writeVarInt(output, loginMessage.length);
	    output.write(loginMessage);
	    System.out.println(login_player_name + " joined the game.");
    }

    public static byte [] createLoginMessage(String username) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream loginmessage = new DataOutputStream(buffer);
        loginmessage.writeByte(0x00);
        writeString(loginmessage, username, StandardCharsets.UTF_8);
        return buffer.toByteArray();
    }

    public static byte [] createHandshakeMessage(String host, int port) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream handshake = new DataOutputStream(buffer);
        handshake.writeByte(0x00); //packet id for handshake
        writeVarInt(handshake, 340); //protocol version
        writeString(handshake, host, StandardCharsets.UTF_8);
        handshake.writeShort(port); //port
        writeVarInt(handshake, 2); //state (1 for handshake)
        return buffer.toByteArray();
    }

    public static void writeString(DataOutputStream out, String string, Charset charset) throws IOException {
        byte [] bytes = string.getBytes(charset);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    public static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
              out.writeByte(paramInt);
              return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }
}
