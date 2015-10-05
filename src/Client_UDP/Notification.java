package Client_UDP;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Notification extends Thread {
	
	private boolean exit;
	private static int _dgLength = 5000;
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	
	private String reponse;

	public Notification(Client client){
		this.dgPacket = client.getDgPacket();
		this.dgSocket = client.getDgSocket();
		this.reponse = "";
	}
	
	public void run() {
		this.exit = false;
		while(exit == false){
			byte[] buffer = new byte[_dgLength];
			dgPacket = new DatagramPacket(buffer, _dgLength);
			try {
				dgSocket.receive(dgPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			reponse = new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
			if(reponse.equals("Vous etes deconnecte")){
				exit = true;
			}
			else if(!reponse.contains("NPA")){
				System.out.println(reponse);
			}
			else{
				reponse = reponse.split(":")[1];
			}
		}
	}
		
	public String getReponse(){
		return reponse;
	}
}
	
