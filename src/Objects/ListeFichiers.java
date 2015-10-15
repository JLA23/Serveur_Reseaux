package Objects;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class ListeFichiers {

	private String[] tab;
	private ArrayList<String> listFile;
	private String address;
	private Scanner sc;
	
	/**
	 * Initialise les informations des fichiers du dossier de partage
	 * @param address
	 * @param sc
	 */
	public ListeFichiers(String address, Scanner sc){
		this.address = address;
		this.sc = sc;
	}
	
	/**
	 * Liste les fichier contenu dans le dossier de partage
	 */
	public void ListerFichiers(){
		File repertoire = new File(address);
		while(!repertoire.isDirectory()){
			System.out.println("Aucun repertoire, le chemin d'accée n'existe pas");
			address = sc.nextLine();
			repertoire = new File(address);
		}
		tab = repertoire.list();
		listFile = new ArrayList<String>();
		if (tab == null){
			System.out.println("Aucun fichiers dans votre répertoire");
		}
	}
	
	/**
	 * Retourne la liste des fichier (Pour TCP)
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getListFile() {
		return listFile;
	}
	
	/**
	 * Modifie la liste des fichiers (Pour TCP)
	 * @param listFile
	 */
	public void setListFile(ArrayList<String> listFile) {
		this.listFile = listFile;
	}
	
	/**
	 * Retourne la liste des fichiers (UDP)
	 * @return String[]
	 */
	public String[] getTab() {
		return tab;
	}
	
	/**
	 * Modifie la lsite des fichier (UDP)
	 * @param tab
	 */
	public void setTab(String[] tab) {
		this.tab = tab;
	}
	
	/**
	 * Retourne l'adresse du dossier
	 * @return String
	 */
	public String getAddress(){
		return address;
	}
	
	/**
	 * Modifie l'adresse du dossier
	 * @param address
	 */
	public void setAddress(String address){
		this.address = address;
	}

}
