package fr.lille1.iut.p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.UUID;

public class Client {
	
	private static int _dgLength = 5000;
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	
	public Client() throws SocketException{
		dgSocket = new DatagramSocket();
	}
	
	private String receive() throws IOException {
		byte[] buffer = new byte[_dgLength];
		dgPacket = new DatagramPacket(buffer, _dgLength);
		dgSocket.receive(dgPacket);
		return new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
	}
	
	private void send(String msg, InetAddress address, int port) throws IOException {
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(address);
		dgPacket.setPort(port);
		dgSocket.send(dgPacket);
	}

	public static void main(String[] args) throws IOException {
		
		Client client = new Client();
		String msg = "RGTR";
		client.send(msg, InetAddress.getByName(args[0]), 5001);
		String uuid = client.receive();
		System.out.println(uuid);
		System.out.println(client.receive());
		String msg2 = uuid.substring(uuid.indexOf("OK:")+3, uuid.indexOf("OK:")+39);
		System.out.println(msg2);
		/*String msg2 = "RTRV:" + uuid.substring(3, uuid.length());
		client.send(msg2, InetAddress.getByName(args[0]), 5001);
		String nb = client.receive();
		System.out.println(nb);
		_dgLength = Integer.parseInt(nb.substring(3, nb.length()));
		System.out.println(msg2);
		
		String msg3 = "QUIT:" + uuid.substring(3, uuid.length());
		client.send(msg3, InetAddress.getByName(args[0]), 5001);
		String nb3 = client.receive();
		System.out.println(msg3);*/
		
	}

}
