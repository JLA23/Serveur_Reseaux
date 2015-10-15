package Serveur_UDP;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Hashtable;

public class Serveur {
	private static final  int _pSrv = 5001;
	private static int _bfLength = 100;
	private Hashtable<String, PeerInfo> peers;
	private byte[] buffer = null; 
	private DatagramSocket dgSocket;
	private String address;
	
	/**
	 * Initialise le serveur
	 * @throws IOException
	 */
	
	public Serveur() throws IOException {
		dgSocket = new DatagramSocket(_pSrv);
		peers = new Hashtable<String, PeerInfo>();
		address = InetAddress.getLocalHost().toString();
	}
	
	/**
	 * Operation que le serveur fait tant qu il fonctionne
	 * @throws IOException
	 */

	private void serve() throws IOException {    	
		while (true) {
			/*Verfie si tous les utilisateurs sont encore connecte*/
			verifUtilisateur();
			/* Reçoi taille du buffer*/
			DatagramPacket dgPacket = receive();
			String msg = new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
			_bfLength = Integer.parseInt(msg);
			/*Reçoi le message*/
			dgPacket = receive();
			msg = new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
			InetAddress address = dgPacket.getAddress();
			int port = dgPacket.getPort();
			String answer = "";
			String[] words = null;
			/*Analyse la commande et lance l'action approprier*/
			if(msg.contains(":")){
				words = msg.split(":");			
				switch(words[0]){
				/*Retourne au client les informations detailler d'un utilisateurs*/
				case("USERS"):
					answer = afficherUtilisateurs(words[1].trim());
					send(address, port, answer);
					_bfLength = 100;
					break;
				/*Retourne au client la liste des utilisateurs*/	
				case("LISTUSERS"):
					answer = listUsers(words[1].trim());
					send(address, port, answer);
					_bfLength = 100;
				/*Deconnection du client au serveur*/	
				case("QUIT"):
					answer = quit(words[1].trim());
					send(address, port, answer);
					notifyPeersQuit(words[1]);
					_bfLength = 100;
					break;
				/*recupere liste fichier d'un client*/	
				case("CDP"):
					listefile(words[1], words[2], address, port);
					_bfLength = 100;
					break;
				/*Envoie la liste de fichier d'un utilisateur au client*/	
				case("LIST"):
					String msg2 = afficherFile(words[1]);
					send(address, port, msg2);
					_bfLength = 100;
					break;
				/*Retourne les informations d'un utilisateur au client*/
				case("INFO"):
					int ports = peers.get(words[1]).getPortTCP();
					String adresse = peers.get(words[1]).getAddress().toString();
					if(adresse.equals("/127.0.0.1")){
						adresse = InetAddress.getLocalHost().toString();
						adresse = adresse.substring(adresse.length() - 14, adresse.length());
						System.out.println(adresse);
					}
					send(address, port, "NPA:"+ports+"-"+adresse);
					_bfLength = 100;
					break;
				/*Enregistre un utilisateur*/
				case("RGTR"):
					if(!peers.containsKey(words[1])){
						answer = "Bienvenue " + words[1] + " ! \n";
						if(address.equals("/127.0.0.1")){
							 answer = answer + register(InetAddress.getByName(this.address), port, words[1]);
						}
						else{
							 answer = answer + register(address, port, words[1]);
						}
						if(peers.size() > 1){
							answer = answer + afficherUtilisateurs(words[1]);
						}
					}
					else{
						answer = "Impossible de se connecter, choissisez un autre Pseudo :";
						}
					send(address, port, answer);
					_bfLength = 100;
					break;
				/*Erreur si commande incorrecte*/	
				default:
					System.out.println("ERROR COMMANDE");
					answer = "ERROR : Commande invalide";
					send(address, port, answer);
					_bfLength = 100;
					break;
				}
				}
			}
		}
	/**
	 * Reçoi message d'un client
	 * @return DatagramPacket
	 * @throws IOException
	 */
	private DatagramPacket receive() throws IOException {
		buffer = new byte[_bfLength];
		DatagramPacket dgPacket = new DatagramPacket(buffer, _bfLength);
		dgSocket.receive(dgPacket);
		return dgPacket;
	}
	
	/**
	 * Envoie au client un message
	 * @param address
	 * @param port
	 * @param msg
	 * @throws IOException
	 */
	private void send(InetAddress address, int port, String msg) throws IOException {
		buffer = msg.getBytes();
		DatagramPacket dgPacket = new DatagramPacket(buffer, 0, buffer.length, address, port);			
		dgSocket.send(dgPacket);
	}
	
	/**
	 * Retourne la liste detailler des utilisateurs qui sera envoyé à un client
	 * @param pseudo
	 * @return String
	 */
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
	
	/**
	 * Un client quitte le serveur
	 * @param pseudo
	 * @return String
	 */
	private String quit(String pseudo) {
		System.out.println("QUITing");

		if ( peers.remove(pseudo) != null ) {
			return "Vous etes deconnecte";
		}
		return "ERROR";
	}
	
	/**
	 * Enregistre un client	
	 * @param address
	 * @param port
	 * @param pseudo
	 * @return
	 * @throws IOException
	 */
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
	
	/**
	 * Informe les clients d'une nouvelle connexion
	 * @param newPeer
	 * @throws IOException
	 */
	private void notifyPeers(PeerInfo newPeer) throws IOException {
		String msg = "Nouveau utilisateur connecte : \n" + newPeer.toString();
		Enumeration<PeerInfo> p = peers.elements();
		while ( p.hasMoreElements() ) {
			PeerInfo peer = p.nextElement();
			send(peer.getAddress(), peer.getPort(), msg);
		}

	}
	
	/**
	 * Informe les clients d'une déconnexions
	 * @param pseudo
	 * @throws IOException
	 */
	private void notifyPeersQuit(String pseudo) throws IOException {
		String msg = "L'utilisateur : " + pseudo + "s'est déconnecté\n";
		Enumeration<PeerInfo> p = peers.elements();
		while ( p.hasMoreElements() ) {
			PeerInfo peer = p.nextElement();
			send(peer.getAddress(), peer.getPort(), msg);
		}

	}
	
	/**
	 * Enregistre liste de fichier d'un client
	 * @param pseudo
	 * @param msg
	 * @param address
	 * @param port
	 * @throws IOException
	 */
	private void listefile(String pseudo, String msg, InetAddress address, int port) throws IOException{
		if(!msg.equals("null")){
			String [] words;
			words = msg.split("-");
			if(peers.containsKey(pseudo)){
				peers.get(pseudo).setListeFichier(words);
				send(address, port, "Liste partagé");
			}
			else{
				send(address, port, "Erreur de Pseudo");
			}
		}
		else if(msg.equals("null") && peers.containsKey(pseudo)){
			String [] words = null;
			peers.get(pseudo).setListeFichier(words);
			send(address, port, "Liste partagé");
			
		}
	}
	
	/**
	 * Envoie liste fichiers d'un utilisateurs
	 * @param pseudo
	 * @return
	 */
	private String afficherFile(String pseudo){
		if(peers.containsKey(pseudo)){
			String [] tab;
			String msg = "";
			tab = peers.get(pseudo).getListeFichiers();
			if(tab != null){
				for(int i = 0; i < tab.length-1; i++){
					msg = msg + tab[i] + "\n";
				}
				msg = msg + tab[tab.length-1];
			}
			else{
				msg = "Aucun fichiers";
			}
			System.out.println("Fini afficherFile");
			return msg;
		}
		return "Le pseudo n'existe pas";
	}
	
	/**
	 * Verifie si tous les client sont encore connecte
	 * @throws IOException
	 */
	private void verifUtilisateur() throws IOException{
		if(!peers.isEmpty()){
			Enumeration<PeerInfo> p = peers.elements();
			while ( p.hasMoreElements() ) {
				PeerInfo peer = p.nextElement();
				send(peer.getAddress(), peer.getPort(), "NPA:Verif");
				buffer = new byte[_bfLength];
				DatagramPacket dgPacket = new DatagramPacket(buffer, _bfLength);
				dgSocket.setSoTimeout(500);
				String msg;
				try{
					dgSocket.receive(dgPacket);
					msg = new String(dgPacket.getData(), dgPacket.getOffset(), dgPacket.getLength());
					System.out.println("Ok:"+msg);
				}
				catch (SocketTimeoutException ste) {
					quit(peer.getPseudo());
					notifyPeersQuit(peer.getPseudo());
				}
			}
			dgSocket.setSoTimeout(0);
		}
	}
	
	/**
	 * retourne la liste des utilisateurs
	 * @param pseudo
	 * @return String
	 */
	private String listUsers(String pseudo){
		String msg = "";
		Enumeration<PeerInfo> p = peers.elements();
		while ( p.hasMoreElements() ) {
			PeerInfo peer = p.nextElement();
			msg = msg + peer.getPseudo() + " - ";
		}
		msg = msg.substring(0, msg.length()-3);
		return msg;
	}
	
	/**
	 * Lance serveur
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new Serveur().serve();
	}

}
