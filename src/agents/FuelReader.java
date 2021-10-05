package agents;

import java.io.DataOutputStream;
import java.net.Socket;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;


public class FuelReader extends Agent{
	
	private String In;
	private int agentNumber;
	private String Assembler;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void setup() {
		//Print that agent has been created
		System.out.println(getAID().getLocalName()+" is running and is a fuel reader agent");
		String name = getAID().getLocalName();
		char num = name.charAt(11);
		agentNumber = Character.getNumericValue(num);
		Assembler = "AssemblerAgent"+agentNumber;
		
		// Create Finite state machine
		FSMBehaviour FRFSM = new FSMBehaviour(this) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			//When finite state machine is finished
			public int onEnd() {
				System.out.println("FSM behaviour completed");
				myAgent.doDelete();
				return(1);
			}
		};
		//System.out.println("new finite state machine created in fuel reader");
		// Register different states of FSM
		FRFSM.registerFirstState(new reader(), "read");
		FRFSM.registerState(new forwardmsg(),"forward");
		// Register transitions of FSM
		FRFSM.registerTransition("read","forward", 1);
		FRFSM.registerTransition("forward", "read", 0);
		FRFSM.registerDefaultTransition("read", "forward");
		FRFSM.registerDefaultTransition("forward", "read");
		// Initiate FSM behaviour
		addBehaviour(FRFSM);
		
	}
	// Behaviour that connects to the TCP and reads the fuel values
	private class reader extends OneShotBehaviour{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public void action() {
			//System.out.println("First state has been entered for FR");
			try {	
				// Setup socket
				int portNum = 9000+agentNumber;
				//System.out.println("Port number being used "+portNum);
				Socket client = new Socket("localhost",portNum);
				//System.out.println("Reader Agent called for client connection");
				
				//send request
				DataOutputStream outToServer = new DataOutputStream(client.getOutputStream());	
				String userString = "request";
				byte[] outByteString = userString.getBytes("UTF-8");
				outToServer.write(outByteString);
				//System.out.println("Request sent to server");
				
				//receive reply
				byte[] inByteString = new byte[500] ;
	            int numOfBytes = client.getInputStream().read(inByteString);
	            In = new String(inByteString, 0, numOfBytes, "UTF-8");
				//System.out.println("Received: " + In);	
				
				//Close connection
				client.close();
				//System.out.println("Connection to fuel sensor closed for FR");
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		public int onEnd() {
			//System.out.println("TCP behaviour reset and state changed for FR");
			reset();
			return(1);
		}
		}
	
	// Behaviour that forwards the data to the Assembler
	private class forwardmsg extends OneShotBehaviour{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		//Create and send message
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID(Assembler,AID.ISLOCALNAME));
			msg.setContent(In);
			send(msg);
			//System.out.println("Fuel info sent to"+Assembler);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		public int onEnd() {
			
			//System.out.println("forward state reset and state changed for FR");
			
			//Reset behaviour
			reset();
			return(0);
		}
	}
}
