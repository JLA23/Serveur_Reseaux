package Client_TCP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTCP{
    private Socket clientSocket = null;
    private PrintWriter envoi = null;
    private BufferedReader reception = null;
    
    public ClientTCP(String host, int port) {
	try {
		System.out.println("Host : " + host + " - Port : " + port);
	    clientSocket = new Socket(host, port);
	} catch (UnknownHostException e) {
	    e.printStackTrace();
	    System.exit(1);
	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(1);
	}
		
	try {
		envoi = new PrintWriter(clientSocket.getOutputStream(), true);
		reception = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	} catch (IOException e) {
		e.printStackTrace();
		System.exit(1);
	}
    }
	   
    public String envoyer(String message) {
	    envoi.println(message);
		    
		    try {
				return reception.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		    return null;
	}
    
    public void telecharger(String file, String destination) throws NumberFormatException, IOException{
    	String message = "FILE:"+file;
    	System.out.println(message);
    	envoi.println(message);
    	System.out.println("après envoie");
    	String reponse = reception.readLine();
    	System.out.println(reponse);
    	if(reponse.equals("Ce fichier n existe pas")){
    		System.out.println("Aucun fichier");
    	}
    	else if(reponse.equals("OK")){
    		System.out.println("Telechargement en cours");
    	      File fichier = new File(destination + "/" + file);
  	        if(!fichier.exists()){
  	        	fichier.createNewFile();
  	        }
    		byte buf[] = new byte[2048];
    		OutputStream out = new FileOutputStream(fichier);
    		InputStream in = clientSocket.getInputStream();
    		int n;
    		while((n=in.read(buf))!=-1){
    			out.write(buf,0,n);
    		}
    		in.close();
    		out.close();
    		System.out.println("Telechargement terminer");
    	}
	}
}

