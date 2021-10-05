package agents;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;

public class Assembler extends Agent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		private String fuelInfo;
		private int agentNumber;
		private String positionInfo;
		
		protected void setup() {
		System.out.println(getAID().getLocalName()+" is running and is an assembler agent");
		String filename = "C:/Data File/Tractor" + getAID().getLocalName()+".txt"; 
		String name = getAID().getLocalName();
		char num = name.charAt(14);
		agentNumber = Character.getNumericValue(num);
		
		// Note that the Data File folder must exist but the file can be created by this program
		writeFile data = new writeFile(filename,true);
		
		//Create file path
		writeFile iniData = new writeFile(filename);
		try {
			iniData.writeToFile("This is the data from "+getAID().getLocalName());
			//System.out.println("File path created");
		} catch (IOException e1) {
			System.out.println("Couldn't create file path");
			e1.printStackTrace();
		}
		
		// Set up parallel behaviour
		// Note: parallel behaviour and sequential behaviour doesn't work for CNP because CNP gets disturbed before complete!!
		ParallelBehaviour tasks = new ParallelBehaviour(this,ParallelBehaviour.WHEN_ALL);	
		Behaviour receive = new receive(data);
		tasks.addSubBehaviour(receive);
		//Achieve RE Responder Behaviour
		MessageTemplate template = AchieveREResponder.createMessageTemplate(FIPANames.InteractionProtocol.FIPA_REQUEST);
		Behaviour respond = new REResponder(this,template);
		tasks.addSubBehaviour(respond);
		addBehaviour(tasks);
		
		//Contract Net Executes in its own thread
		ThreadedBehaviourFactory cnp = new ThreadedBehaviourFactory();
		// Fill CFP messages and initiate call for proposals
		ACLMessage CFP = new ACLMessage(ACLMessage.CFP);
		CFP.addReceiver(new AID ("PositionAgent1",AID.ISLOCALNAME));
		CFP.addReceiver(new AID("PositionAgent2",AID.ISLOCALNAME));
		CFP.addReceiver(new AID("PositionAgent3",AID.ISLOCALNAME));
		CFP.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		CFP.setReplyByDate(new Date(System.currentTimeMillis()+2000)); //2s to reply
		String agentNum = ""+agentNumber;
		CFP.setContent(agentNum);
		Agent a = this;
		Behaviour CNP = new TickerBehaviour(a,3000){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void onTick() {
				
				Behaviour CNPIni = new CNInitiator(a,CFP,data);
				addBehaviour(cnp.wrap(CNPIni));
			}
			
		};
	addBehaviour(CNP);
	}
		
	// Contract Net Initiator Behaviour
	private class CNInitiator extends ContractNetInitiator{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		writeFile data;

		public CNInitiator(Agent a, ACLMessage cfp,writeFile file) {
			super(a, cfp);
			data = file;
			//System.out.println("CN initiated");
		}
		// This seems to be running in an endless loop and is therefore only receiving one proposal.
		/*protected void handlePropose(ACLMessage propose, Vector accepts) {
			System.out.println("Received proposal "+propose.getContent()+" by "+propose.getSender().getLocalName());
			
		}*/
		protected void handleRefuse(ACLMessage refuse) {
			System.out.println(refuse.getSender()+" refused");
		}
	
		protected void handleAllResponses(Vector responses, Vector accepts) {
			int mostRecent = 0;
			int size = responses.size();
			//System.out.println("size of responses vector "+size);
			AID bestProposer = null;
			ACLMessage accept = null;
			Enumeration e = responses.elements();
			while(e.hasMoreElements()) {
				ACLMessage msg = (ACLMessage) e.nextElement();
				if(msg.getPerformative() == ACLMessage.PROPOSE) {
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
					accepts.addElement(reply);
					int msgLength = msg.getContent().length();
				//	System.out.println("Proposal received: "+msg.getContent()+" by "+msg.getSender().getLocalName());	
					//System.out.println("Length of proposal: "+msgLength);
					if(msgLength>20) {
						String time = msg.getContent().substring(msgLength-6,msgLength);
						int Time = Integer.valueOf(time);
						//System.out.println("Time given by "+msg.getSender().getLocalName()+" is "+Time);
						if(Time>mostRecent) {
							mostRecent = Time;
							bestProposer = msg.getSender();
							accept = reply;
						}
					}
				}
			}
			if(accept!=null) {
				//System.out.println(getAID().getLocalName()+" accepting proposal "+mostRecent+" by "+bestProposer.getLocalName());
				accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				
			}
			else {
				System.out.println("No proposals received");
			}
		}
		
		protected void handleInform(ACLMessage inform) {
			positionInfo = inform.getContent();
			String all;
			if(positionInfo != null) {
			System.out.println("Inform "+positionInfo+" received from "+inform.getSender().getLocalName());
			String farm = inform.getContent().substring(0,5);
			String pos = inform.getContent().substring(5,8);
			String trac = inform.getContent().substring(8,16);
			String timeh = inform.getContent().substring(16,18);
			String timem = inform.getContent().substring(18,20);
			String times = inform.getContent().substring(20,22);
			all = farm+"_"+pos+"_"+trac+"_"+timeh+":"+timem+":"+times;
			//System.out.println("Position data that will be saved: "+all);
			}else {
				String farm = "processing";
				String pos = "processing";
				String trac = "processing";
				String time = "processing";
				all = farm+"_"+pos+"_"+trac+"_"+time;
			}
			try {
				data.writeToFile(all);
				//System.out.println("Position data written to file by "+ getAID().getLocalName());
			} catch (IOException e) {
				System.out.println("Unable to write fuel data to file");
				e.printStackTrace();
			}
			
		}
		protected void handleFailure(ACLMessage failure) {
			System.out.println(getAID().getLocalName()+" reported a failure");
			
		}
	}
		
	// Achieve RE Responder behaviour to send Fuel Info
	private class REResponder extends AchieveREResponder{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		// Constructor
		public REResponder(Agent a, MessageTemplate mt) {
			super(a, mt);
		}
		// Handle request code
		protected ACLMessage handleRequest(ACLMessage request){
			//System.out.println("Assembler Agent 1 received request: "+request.getContent());
			ACLMessage inform = request.createReply();
			inform.setPerformative(ACLMessage.INFORM);
			String Info = fuelInfo+"_"+positionInfo;
			//String Info = "12.1_farm1p21tractor1112730";
			inform.setContent(Info);
			return inform;
		}
	} 
		
	
	//Receive ACL message Behaviour code
	private class receive extends CyclicBehaviour{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		writeFile data;
		public receive(writeFile file){
			data = file;
		}
		public void action() {
			ACLMessage msg = receive();
			if(msg!=null) {
				//AID senderAid = msg.getSender();
				//String AidAsString = senderAid.getLocalName();
				//String senderName = "ReaderAgent"+agentNumber;
				//System.out.println("Sender local name: "+AidAsString+" Required name: "+ senderName);
				String fuelData = msg.getContent();
				if(msg.getContent().length()<5) {
				fuelInfo = fuelData;
				try {
					data.writeToFile(fuelData);
					//System.out.println("Fuel data written to file by "+ getAID().getLocalName());
				} catch (IOException e) {
					System.out.println("Unable to write fuel data to file");
					e.printStackTrace();
				}
				}
			}else block();
			}
	}
	
	//Code for method that writes to file
	public static class writeFile {
	private String path;
	private boolean append = false;
	
	//Constructor that sets the file path
	public writeFile(String filePath) {
		path = filePath;
	}
	//Constructor that sets the file path and the append value
	public writeFile(String filePath, boolean appendVal) {
		path = filePath;
		append = appendVal;
	}
	
	//write to file method body
	public void writeToFile(String data) throws IOException{
		FileWriter write = new FileWriter(path,append); //note that FileWriter writes bytes, therefore we use printwriter
		PrintWriter printLine = new PrintWriter(write);
		printLine.printf("%s"+"%n",data);
		printLine.close();
	}
}
}
