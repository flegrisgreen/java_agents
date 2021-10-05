package agents;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class dummyMultiServer implements Runnable{
	
	private ServerSocket serverSocket;
	
	//Run() function of the Runnable class
	public void run() {
		start();
	}
	
	//Start function that creates a new server socket and a new client handler
	public void start() {
		try {
			serverSocket = new ServerSocket(9000);
		} catch (IOException e) {
			System.out.println("Failed to create new server socket");
			e.printStackTrace();
		}
		while(true) {
			try {
				new ClientHandler(serverSocket.accept()).start();
			} catch (IOException e) {
				System.out.println("Failed to create new client handler");
				e.printStackTrace();
			}
		}
	}
	
	//Stop function that closes server socket
	public void stop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.out.println("Failed to close socket");
			e.printStackTrace();
		}
	}
	
	// Client handler class that reads the client's request and sends a reply
	private static class ClientHandler extends Thread{
		private Socket client;
		private DataOutputStream Out;
		private DataInputStream In;
		
		public ClientHandler(Socket socket) {
			this.client = socket;
		}
		
		public void run() {
			try {
				Out = new DataOutputStream(client.getOutputStream());
				In = new DataInputStream(client.getInputStream());
				
				String[] dataIn = new String[20];
				//System.out.println("Server connection established. Waiting for client");
				
				int i = 0;
				while((dataIn[i] = In.readUTF()) != null) {
					//System.out.println("information received "+dataIn[i]);
					Out.writeUTF("Id01_2LperHour");				//Can change to string with changing fuel consumption. Use Out[]
					i++;
				}
				In.close();
				Out.close();
				client.close();
				
			} catch (IOException e) {
				System.out.println("Data trasfer from server to fuel reader failed");
				e.printStackTrace();
			}
	}
	}
}
