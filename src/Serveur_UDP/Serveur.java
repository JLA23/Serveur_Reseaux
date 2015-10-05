package Serveur_UDP;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;

import Objects.PeerInfo;

public class Serveur {
	private static final  int _pSrv = 5001;
	private static final int _bfLength = 41;
	private Hashtable<String, PeerInfo> peers;
	private byte[] buffer = null; 
	private DatagramSocket dgSocket;
	
	public Serveur() throws IOException {
		dgSocket = new DatagramSocket(_pSrv);
		peers = new Hashtable<String, PeerInfo>();
	}

	private void serve() throws IOException {    	
		while (true) {
			DatagramPacket dgPacket = receive();
			String msg = new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
			InetAddress address = dgPacket.getAddress();
			int port = dgPacket.getPort();
			String answer = "";
			String[] words = null;
			if(msg.contains(":")){
				words = msg.split(":");			
				switch(words[0]){
				case("USERS"):
					answer = afficherUtilisateurs(words[1].trim());
					send(address, port, answer);
					break;
				case("QUIT"):
					answer = quit(words[1].trim());
					send(address, port, answer);
					break;
				case("CDP"):
					listefile(words[1], words[2]);
					send(address, port, "Liste partage");
					break;
				case("LIST"):
					String msg2 = afficherFile(words[1]);
					send(address, port, msg2);
					break;
				case("INFO"):
					int ports = peers.get(words[1]).getPortTCP();
					String adresse = peers.get(words[1]).getAddress().toString();
					send(address, port, "NPA:"+ports+"-"+adresse);
					break;
				case("RGTR"):
					if(!peers.containsKey(words[1])){
						answer = "Bienvenue " + words[1] + " ! \n"  + register(address, port, words[1]);
						if(peers.size() > 1){
							answer = answer + afficherUtilisateurs(words[1]);
						}
					}
					else{
						answer = "Impossible de se connecter, choissisez un autre Pseudo :";
						}
					send(address, port, answer);
					break;
				default:
					System.out.println("ERROR");
					answer = "ERROR";
					send(address, port, answer);
					break;
				}
				}
			}
		}

	private DatagramPacket receive() throws IOException {
		buffer = new byte[_bfLength];
		DatagramPacket dgPacket = new DatagramPacket(buffer, _bfLength);
		dgSocket.receive(dgPacket);
		return dgPacket;
	}

	private void send(InetAddress address, int port, String msg) throws IOException {
		buffer = msg.getBytes();
		DatagramPacket dgPacket = new DatagramPacket(buffer, 0, buffer.length, address, port);			
		dgSocket.send(dgPacket);
	}

	private String afficherUtilisateurs(String pseudo) {
		System.out.println("ReTRieVing");

		if (peers.containsKey(pseudo) && peers.size() > 1) {
			StringBuilder sb = new StringBuilder();
			Enumeration<PeerInfo> p = peers.elements();
			while ( p.hasMoreElements() ) {
				PeerInfo peer = p.nextElement();
				if ( ! peer.getPseudo().toString().equals(pseudo) ) {
					sb.append(peer.toString()); 
					sb.append("\n");
				}
			}
			if ( sb.length() != 0 ) {
				sb.deleteCharAt(sb.length()-1);
			}

			return "Ces Utilisateurs sont connecte : \n" + sb.toString();
		}
		return "ERROR";
	}

	private String quit(String pseudo) {
		System.out.println("QUITing");

		if ( peers.remove(pseudo) != null ) {
			return "Vous etes deconnecte";
		}
		return "ERROR";
	}

	private String register(InetAddress address, int port, String pseudo) throws IOException {
		System.out.println("ReGisTeRing");

		PeerInfo p = new PeerInfo(address, port, pseudo);
		notifyPeers(p);
		peers.put(pseudo, p);

		StringBuilder sb = new StringBuilder();
		sb.append("Votre UUID : ");
		sb.append(p.getUUID());
		sb.append("\n Port : " + p.getPort());

		return sb.toString()+"\n";    
	}

	private void notifyPeers(PeerInfo newPeer) throws IOException {
		String msg = "Nouveau utilisateur connecte : \n" + newPeer.toString();
		Enumeration<PeerInfo> p = peers.elements();
		while ( p.hasMoreElements() ) {
			PeerInfo peer = p.nextElement();
			send(peer.getAddress(), peer.getPort(), msg);
		}

	}
	
	private void listefile(String pseudo, String msg){
		if(msg.contains("-")){
			String [] words;
			words = msg.split("-");
			if(peers.containsKey(pseudo)){
				peers.get(pseudo).setListeFichier(words);
			}
			
		}
	}

	private String afficherFile(String pseudo){
		if(peers.containsKey(pseudo)){
			String [] tab;
			String msg = "";
			tab = peers.get(pseudo).getListeFichiers();
			for(int i = 0; i < tab.length-1; i++){
				msg = msg + tab[i] + "\n";
			}
			msg = msg + tab[tab.length-1];
			System.out.println("Fini afficherFile");
			return msg;
		}
		return "Le pseudo n'existe pas";
	}
	
	public static void main(String[] args) throws IOException {
		new Serveur().serve();
	}

}
