package Client_UDP;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;

public class Notification extends Thread {
	
	private boolean exit;
	private static int _dgLength = 5000;
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	private Client client;
	
	private String reponse;

	public Notification(Client client){
		this.dgPacket = client.getDgPacket();
		this.dgSocket = client.getDgSocket();
		this.reponse = "";
		this.client = client;
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
			String msg = new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
			if(msg.equals("Vous etes deconnecte")){
				exit = true;
			}
			else if(!msg.contains("NPA")){
				reponse = msg;
				System.out.println(msg);
			}
			else if(msg.equals("NPA:Verif")){
				try {
					client.verifOk();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				reponse = msg.split(":")[1];
			}
		}
	}
		
	public String getReponse(){
		return reponse;
	}
}
	
