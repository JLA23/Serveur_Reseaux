package Serveur_TCP;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import Objects.ListeFichiers;

public class ServeurTCP extends Thread{
	private ServerSocket serveurSocket = null;
	PrintWriter envoi = null;
	BufferedReader reception = null;
	ListeFichiers list;
	
	/**
	 * Initialise le serveur TCP
	 * @param port
	 * @param list
	 */
	public ServeurTCP(int port, ListeFichiers list) {
		try {
			serveurSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.list = list;
	}
	
	/**
	 * Lance le serveur
	 */
	public void run() {
		Socket unClient = null;
		
		while (true ) {
			try {
				unClient = serveurSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		
			realiseService(unClient);
		}
	}
	
	/**
	 * Les service que le serveur TCP propose
	 * @param unClient
	 */
	private void realiseService(Socket unClient) {
		try {
			envoi = new PrintWriter(unClient.getOutputStream(), true);
			
			reception = new BufferedReader(
                    new InputStreamReader(unClient.getInputStream()));
	
			String message = reception.readLine();
			System.out.println(message);
			System.out.println(message.contains("FILE:"));
			if(message.contains("FILE:")){
				String [] words = message.split(":");
				System.out.println("avant OK");
				envoi.println("OK");
				System.out.println("apres ok");
				upload(words[1].trim(), unClient);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 * Envoie un fichier demander à un client
	 * @param file
	 * @param unClient
	 * @throws IOException
	 */
	private void upload(String file, Socket unClient) throws IOException{
		if(list.getListFile().contains(file)){
	        byte buf[] = new byte[2048];
	    	OutputStream out = unClient.getOutputStream();
	    	InputStream in = new FileInputStream(list.getAddress() + "/" + file);
	        int n;
	        while((n=in.read(buf))!=-1){
	            out.write(buf,0,n);
	        }
	            in.close();
	            out.close();
		}
	}
}
