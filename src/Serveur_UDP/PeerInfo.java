package Serveur_UDP;


import java.net.InetAddress;
import java.util.UUID;

public class PeerInfo {
	private UUID uuid;
	private InetAddress address;
	private int port;
	private int portTCP;
	private String pseudo;
	private String[] listeFichiers;
	
	/**
	 * Initialise les informations d'un Peer
	 * @param address
	 * @param port
	 * @param pseudo
	 */
	public PeerInfo(InetAddress address, int port, String pseudo) {
		this.uuid = UUID.randomUUID();
		this.address = address;
		this.port = port;
		this.portTCP = port + 1;
		this.pseudo = pseudo;
		this.listeFichiers = null;
	}
	
	/**
	 * Recupere le port TCP
	 * @return int
	 */
	public int getPortTCP() {
		return portTCP;
	}
	
	/**
	 * Modifie port TCP
	 * @param portTCP
	 */
	public void setPortTCP(int portTCP) {
		this.portTCP = portTCP;
	}
	
	/**
	 * Retourne la liste de fichier
	 * @return String []
	 */
	public String[] getListeFichiers(){
		return listeFichiers;
	}
	
	/**
	 * Modifie Liste de fichiers
	 * @param listeFichiers
	 */
	
	public void setListeFichier(String [] listeFichiers){
		this.listeFichiers = listeFichiers;
	}
	
	/**
	 * Retourne pseudo
	 * @return
	 */
	public String getPseudo(){
		return pseudo;
	}
	
	/**
	 * Retourne une adresse
	 * @return InetAddress
	 */
	public InetAddress getAddress() {
		return address;
	}

	/**
	 * Retourne le port
	 * @return int
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Retourne l'UUID
	 * @return UUID
	 */
	public UUID getUUID() {
		return uuid;
	}
	
	/**
	 * Affiche l'integralite des informations d'un utilsateur en String
	 */
	@Override
	public String toString() {
		return pseudo + " - UUID : " + uuid + "\n  Adresse : " + address + "\n  Port : " + port + "\n";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeerInfo other = (PeerInfo) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
