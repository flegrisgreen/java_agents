package agents;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class DummyServer implements Runnable{
	
	public void run() {
		System.out.println("Server is running");
		ServerSocket serverSocket = null;					//Initialize server socket
		try {
			serverSocket = new ServerSocket(9000);		//Set server socket address
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {	//Server infrastructure
			Socket server = serverSocket.accept();		//Accept Client request
			DataInputStream fromClient = new DataInputStream(server.getInputStream());
			DataOutputStream toClient = new DataOutputStream(server.getOutputStream());
			//String[] Out = new String[20];
			String[] In = new String[20];
			System.out.println("Server connection established. Waiting for client");
				
			//Read message from client and send reply
			int i = 0;
			while(true) {
				while((In[i] = fromClient.readUTF()) != null) {
					System.out.println("information received"+In[i]);
					toClient.writeUTF("Id01_2LperHour");				//Can change to string with changing fuel consumption. Use Out[]
					//toClient.close();
					i++;	
				}
			if (i==19) {
				break;
			}
				
			}
			
			serverSocket.close();
			System.out.println("Server socket has been terminated");
			
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Server failed");
		}
	}

}
