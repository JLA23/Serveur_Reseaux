package Serveur_TCP;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import Objects.ListeFichiers;

public class ServeurTCP extends Thread{
	private ServerSocket serveurSocket = null;
	PrintWriter envoi = null;
	BufferedReader reception = null;
	ListeFichiers list;
	
	
	public ServeurTCP(int port, ListeFichiers list) {
		try {
			serveurSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.list = list;
	}
	
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
	
	private void realiseService(Socket unClient) {
		try {
	
			envoi = new PrintWriter(unClient.getOutputStream(), true);
			
			reception = new BufferedReader(
                    new InputStreamReader(unClient.getInputStream()));
	
			String message = reception.readLine();
			if(message.contains("FILE:")){
				String [] words = message.split(":");
				upload(words[1].trim());
			}
			else{
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void upload(String file){
		if(list.getListFile().contains(file)){
			File fichier = new File(list.getAddress() + "/" + file);
			envoi.println(fichier);
		}
	}
}
