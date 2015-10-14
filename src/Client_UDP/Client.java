package Client_UDP;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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
	private String address;
	
	public Client() throws SocketException{
		System.setProperty( "file.encoding", "UTF-8" );
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
	
	public void verifOk() throws UnknownHostException, IOException{
		send(pseudo, InetAddress.getByName(address), 5001);
	}
	private boolean charSpeciaux(String pseudo){
		boolean containt = false;
		String cs = "[^\\wàâäÄÀÂéèêëÈÊËìîïÌÏÎòöôÒÖÔùüûÙÜÛç!#$€%&'`(),;:/@...]";
		for(int i = 0; i < cs.length(); i++){
			if(pseudo.indexOf(cs.charAt(i)) != -1){
				containt = true;
				break;
			}			
		}
		return containt;
	}
	
	private void enregistrer(Scanner sc, Notification notif, InetAddress address) throws IOException, InterruptedException{
		System.out.println("Bonjours ! Veuillez donner un pseudo : ");
		String msg = "RGTR";
		pseudo = sc.nextLine();
		while(charSpeciaux(pseudo)){
			System.out.println("Votre pseudo contient des caractere interdit. \n Veuillez en choisir un autre :");
			pseudo = sc.nextLine();
		}
		msg = msg + ":" + pseudo;
		this.send(msg.length()+"", address, 5001);
		this.send(msg, address, 5001);
		this.pause();	
		while(notif.getReponse().equals("Impossible de se connecter, choissisez un autre Pseudo :")){
			msg = "RGTR:" + sc.nextLine();
			this.send(msg.length()+"", address, 5001);
			this.send(msg, address, 5001);
			this.pause();	
		}
	}
	
	private void CDP(Scanner sc, InetAddress address) throws InterruptedException, IOException{
		String msg = "CDP:"+ pseudo;
		System.out.println("Ou se situe votre dossier de partage : ");
		list = new ListeFichiers(sc.nextLine(), sc);
		list.ListerFichiers();
		envoieListFichier(this, msg, address);
	}
	
	private void envoieListFichier(Client client, String msg, InetAddress address) throws InterruptedException, IOException{
		String [] tab = list.getTab();
		msg = msg + ":";
		if(tab.length != 0){
			for(int i = 0 ; i < tab.length - 1; i++){
				msg = msg + tab[i] + "-";
			}
			msg = msg + tab[tab.length-1];
		}
		else{
			msg = msg + "null";
		}
		client.send(msg.length()+"", address, 5001);
		client.send(msg,address, 5001);
		client.pause();
	}
	
	private void list(String msg, Notification notif, InetAddress address) throws IOException, InterruptedException{
		String[] words = msg.split(":");
		if(words.length == 2){
			this.send(msg.length()+"", address, 5001);
			this.send(msg,address, 5001);
			this.pause();
		}
		else{
			System.out.println("il manque le nom de l'utilisateur");
		}
	}
	
	private void autres(String msg, Notification notif, InetAddress address) throws IOException, InterruptedException{
		msg = msg + ":" + pseudo;
		this.send(msg.length()+"", address, 5001);
		this.send(msg, address, 5001);
		this.pause();
	}
	
	private void quit(String msg, Notification notif, InetAddress address) throws IOException, InterruptedException{
		msg = msg + ":" + pseudo;
		quit = true;
		this.send(msg.length()+"", address, 5001);
		this.send(msg, address, 5001);
		this.pause();
	}
	
	private void recup(String msg, Notification notif, InetAddress address) throws IOException, InterruptedException{
		String [] words = msg.split(":");
		String message = "INFO:" + words[1];
		this.send(message.length()+"", address, 5001);
		this.send(message, address, 5001);
		this.pause();
		String reponse = notif.getReponse();
		String [] words2 = reponse.split("-");
		ClientTCP cTCP = new ClientTCP(words2[1].substring(1), Integer.parseInt(words2[0]));
		cTCP.telecharger(words[2], list.getAddress());
		list.ListerFichiers();
		envoieListFichier(this, "CDP:"+pseudo, address);
		
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Client client = new Client();
		client.setAddress(args[0]);
		Notification notif = new Notification(client);
		Scanner sc = new Scanner(System.in);
		notif.start();
		client.enregistrer(sc, notif, InetAddress.getByName(args[0]));
		client.pause();
		String reponse = notif.getReponse();
		client.CDP(sc, InetAddress.getByName(args[0]));
		if(reponse.contains(":")){
			String [] words = reponse.split(":");
			ServeurTCP serverT = new ServeurTCP(Integer.parseInt(words[2].substring(1,6))+1, client.getList());
			serverT.start();
			System.out.println("Serveur TCP lance");
			while(client.getQuit() == false){
				String msg = sc.nextLine();
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
		else{
			System.err.println("Erreur du serveur");
		}
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}

