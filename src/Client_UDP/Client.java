package Client_UDP;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

import Client_TCP.ClientTCP;
import Objects.ListeFichiers;
import Serveur_TCP.ServeurTCP;

public class Client extends Thread{
	
	private DatagramSocket dgSocket;
	private DatagramPacket dgPacket;
	private boolean quit;
	private String pseudo;
	private ListeFichiers list;
	
	public Client() throws SocketException{
		dgSocket = new DatagramSocket();
		this.quit = false;
	}
	
	private void send(String msg, InetAddress address, int port) throws IOException {
		byte[] buffer = msg.getBytes();
		dgPacket = new DatagramPacket(buffer, 0, buffer.length);
		dgPacket.setAddress(address);
		dgPacket.setPort(port);
		dgSocket.send(dgPacket);
	}
	
	private boolean getQuit(){
		return quit;
	}
	
	private ListeFichiers getList(){
		return list;
	}
	
	private void pause() throws InterruptedException{
			Thread.sleep(100);
	}
	
	public DatagramSocket getDgSocket() {
		return dgSocket;
	}

	public DatagramPacket getDgPacket() {
		return dgPacket;
	}

	public void setDgSocket(DatagramSocket dgSocket) {
		this.dgSocket = dgSocket;
	}

	public void setDgPacket(DatagramPacket dgPacket) {
		this.dgPacket = dgPacket;
	}
	
	private void enregistrer(Scanner sc, Notification notif, InetAddress address) throws IOException, InterruptedException{
		System.out.println("Bonjours ! Veuillez donner un pseudo : ");
		String msg = "RGTR";
		pseudo = sc.nextLine();
		System.out.println("");
		msg = msg + ":" + pseudo;
		this.send(msg, address, 5001);
		this.pause();	
		while(notif.getReponse().equals("Impossible de se connecter, choissisez un autre Pseudo :")){
			msg = "RGTR:" + sc.nextLine();
			System.out.println("");
			this.send(msg, address, 5001);
			this.pause();	
		}
	}
	
	private void CDP(String msg, Scanner sc, Notification notif, InetAddress address) throws InterruptedException, IOException{
		msg = msg + ":"+ pseudo;
		System.out.println("Ou se situe votre dossier de partage : ");
		list = new ListeFichiers(sc.nextLine());
		list.ListerFichiers();
		String [] tab = list.getTab();
		msg = msg + ":";
		for(int i = 0 ; i < tab.length - 1; i++){
			msg = msg + tab[i] + "-";
		}
		msg = msg + tab[tab.length-1];
		System.out.println(msg);
		this.send(msg,address, 5001);
		this.pause();
	}
	
	private void list(String msg, Notification notif, InetAddress address) throws IOException, InterruptedException{
		String[] words = msg.split(":");
		if(words.length == 2){
			this.send(msg,address, 5001);
			this.pause();
		}
		else{
			System.out.println("il manque le nom de l'utilisateur");
		}
	}
	
	private void autres(String msg, Notification notif, InetAddress address) throws IOException, InterruptedException{
		msg = msg + ":" + pseudo;
		this.send(msg, address, 5001);
		this.pause();
	}
	
	private void quit(String msg, Notification notif, InetAddress address) throws IOException, InterruptedException{
		msg = msg + ":" + pseudo;
		quit = true;
		this.send(msg, address, 5001);
		this.pause();
	}
	
	private void recup(String msg, Notification notif, InetAddress address) throws IOException, InterruptedException{
		String [] words = msg.split(":");
		this.send("INFO:" + words[1], address, 5001);
		this.pause();
		String reponse = notif.getReponse();
		String [] words2 = reponse.split("-");
		System.out.println(words2[1] + " " + words2[0]);
		ClientTCP cTCP = new ClientTCP(words2[1].substring(1), Integer.parseInt(words2[0]));
		System.out.println(words[2] + " " + list.getAddress());
		cTCP.telecharger(words[2], list.getAddress());
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Client client = new Client();
		Notification notif = new Notification(client);
		Scanner sc = new Scanner(System.in);
		notif.start();
		client.enregistrer(sc, notif, InetAddress.getByName(args[0]));
		String reponse = notif.getReponse();
		if(reponse.contains(":")){
			String [] words = reponse.split(":");
			client.CDP("CDP", sc, notif, InetAddress.getByName(args[0]));
			System.out.println("Serveur TCP n est pas lance");
			System.out.println(words[2].substring(1,6));
			ServeurTCP serverT = new ServeurTCP(Integer.parseInt(words[2].substring(1,6))+1, client.getList());
			System.out.println("Serveur TCP en cours de lancement");
			serverT.start();
			System.out.println("Serveur TCP lance");
		}
		while(client.getQuit() == false){
			String msg = sc.nextLine();
	        System.out.println("");
	        if(msg.contains("LIST:")){
				client.list(msg, notif, InetAddress.getByName(args[0]));
			}
			else if(msg.equals("QUIT")){
				client.quit(msg, notif, InetAddress.getByName(args[0]));
			}
			else if(msg.contains("RECUP:")){
				client.recup(msg, notif, InetAddress.getByName(args[0]));
			}
			else{
				client.autres(msg, notif, InetAddress.getByName(args[0]));
			}
		}
	}
}

