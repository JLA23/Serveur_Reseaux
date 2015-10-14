package Objects;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class ListeFichiers {

	private String[] tab;
	private ArrayList<String> listFile;
	private String address;
	private Scanner sc;
	
	public ListeFichiers(String address, Scanner sc){
		this.address = address;
		this.sc = sc;
	}
	
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
	
	public ArrayList<String> getListFile() {
		return listFile;
	}

	public void setListFile(ArrayList<String> listFile) {
		this.listFile = listFile;
	}

	public String[] getTab() {
		return tab;
	}

	public void setTab(String[] tab) {
		this.tab = tab;
	}
	
	public String getAddress(){
		return address;
	}
	
	public void setAddress(String address){
		this.address = address;
	}

}
