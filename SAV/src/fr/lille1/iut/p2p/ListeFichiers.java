package fr.lille1.iut.p2p;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;

public class ListeFichiers {
	
	private String uuid;
	private String[] tab;
	private InetAddress address;
	private int port;
	
	public ListeFichiers(String uuid, InetAddress address, int port){
		this.uuid=uuid;
		this.address = address;
		this.port = port;
		ListerFichiers();
	}
	
	private void ListerFichiers(){
		System.out.println("Bonjours");
		File repertoire = new File("fr/lille1/iut/p2p/data");
		
		System.out.println(repertoire.isDirectory() + "");
		if (repertoire.isDirectory()){
			tab = repertoire.list();
			if (tab == null){
				System.err.println("Erreur : Aucun fichiers");
			}
		}else{
			System.err.println("Erreur : Aucun repertoire");
		}	
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String[] getTab() {
		return tab;
	}

	public void setTab(String[] tab) {
		this.tab = tab;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	

}
