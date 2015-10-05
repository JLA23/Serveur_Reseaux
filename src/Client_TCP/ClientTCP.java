package Client_TCP;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    	envoi.println(message);
    	String reponse ="";
    	while(reponse.equals("") || reponse.equals("Ce fichier n existe pas")){
    		try{
    			reponse = reception.readLine();
    			System.out.println(reponse);
    		}
    		catch(IOException e){
    			e.printStackTrace();
    			System.exit(1);
    		}
    	}
    	System.out.println("Telechargement en cours");
    	DataInputStream in = new DataInputStream(clientSocket.getInputStream());
    	int size = Integer.parseInt(reception.readLine().split(": ")[1]);
    	byte[] item = new byte[size];
    	for(int i = 0; i < size; i++)
    	    item[i] = in.readByte();
    	FileOutputStream requestedfile = new FileOutputStream(new File(destination+"/"+file));
    	BufferedOutputStream bos = new BufferedOutputStream(requestedfile);
    	bos.write(item);
    	bos.close();
     	in.close();
    }

}