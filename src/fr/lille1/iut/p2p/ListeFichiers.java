package fr.lille1.iut.p2p;

import java.io.File;
import java.util.ArrayList;

public class ListeFichiers {
	
	private String uuid;
	private String[] tab;
	
	public ListeFichiers(String uuid){
		this.uuid=uuid;
		ListerFichiers();
	}
	
	private void ListerFichiers(){
		File repertoire = new File("/home/infoetu/defivesd/public");
		if (repertoire.isDirectory()){
			tab = repertoire.list();
			if (tab == null){
				System.err.println("Erreur : Aucun fichiers");
			}
		}else{
			System.err.println("Erreur : Aucun repertoire");
		}	
	}
	
	public String[] getArray(){
		return tab;
	}

}
