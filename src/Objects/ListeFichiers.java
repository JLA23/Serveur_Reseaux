package Objects;
import java.io.File;
import java.util.ArrayList;

public class ListeFichiers {

	private String[] tab;
	private ArrayList<String> listFile;
	private String address;
	
	public ListeFichiers(String address){
		this.address = address;
	}
	
	public void ListerFichiers(){
		File repertoire = new File(address);
		if (repertoire.isDirectory()){
			tab = repertoire.list();
			if (tab == null){
				System.err.println("Erreur : Aucun fichiers");
			}
		}else{
			System.err.println("Erreur : Aucun repertoire");
		}
		listFile = new ArrayList<String>();
		for(int i = 0; i < tab.length; i ++){
			listFile.add(tab[i]);
			System.out.println(tab[i]);
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
