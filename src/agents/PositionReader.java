package agents;

import java.io.DataOutputStream;
import java.net.Socket;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.proto.SSContractNetResponder;
import jade.proto.SSResponderDispatcher;

public class PositionReader extends Agent{
	
	private String[] In = new String[6];
	private String[] Out = new String[4];
	private int agentNumber;
	private Agent agent = this;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void setup() {
		System.out.println("Position Reader agent "+getAID().getLocalName()+"is running");
		String name = getAID().getLocalName();
		char num = name.charAt(13);
		agentNumber = Character.getNumericValue(num);
		//Initialize out variables
		Out[0] = "refuse";
		Out[1] = "refuse";
		Out[2] = "refuse";
		Out[3] = "refuse";
		
		// Set up parallel behaviour
		ParallelBehaviour ParallelTasks = new ParallelBehaviour(this,ParallelBehaviour.WHEN_ALL);
		
		// Receive() parallel behaviour
		// Read position information from TCP
		ParallelTasks.addSubBehaviour(new P_reader());
		
		//Contract Net Responder Behaviour
		MessageTemplate CFPTemplate = MessageTemplate.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),MessageTemplate.MatchPerformative(ACLMessage.CFP));
		ParallelTasks.addSubBehaviour(new parallelResponder(this,CFPTemplate));
		
		// Execute parallel tasks
		addBehaviour(ParallelTasks);
	}
	// Single Session Responder Dispatcher to account for multiple CNPs at once
	private  class parallelResponder extends SSResponderDispatcher{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public parallelResponder(Agent a, MessageTemplate tpl) {
			super(a, tpl);
		}
		protected Behaviour createResponder(ACLMessage cfp) {		
			return new PositionCNResponder(agent,cfp);			
		}
	}
	
	// Contract Net Responder 
	private class PositionCNResponder extends SSContractNetResponder{

		public PositionCNResponder(Agent a, ACLMessage cfp) {
			super(a, cfp);
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		protected ACLMessage handleCfp(ACLMessage cfp) {
			//System.out.println(getAID().getLocalName()+"Received cfp: "+cfp.getContent());
			char Tractor = cfp.getContent().charAt(0);
			int tractor = Character.getNumericValue(Tractor);
			//int i = 1;//Only send one proposal
			//if(i==1) {
			if (tractor == 1) {
				ACLMessage proposal = cfp.createReply();
				proposal.setPerformative(ACLMessage.PROPOSE);
				proposal.setContent(Out[0]);
				//System.out.println(getAID().getLocalName()+" proposed: "+proposal.getContent());
				//i++;
				return(proposal);
			}else if(tractor == 2) {
				ACLMessage proposal = cfp.createReply();
				proposal.setPerformative(ACLMessage.PROPOSE);
				proposal.setContent(Out[1]);
				//System.out.println(getAID().getLocalName()+" proposed: "+proposal.getContent());
				//i++;
				return(proposal);
			}else if(tractor == 3){
				ACLMessage proposal = cfp.createReply();
				proposal.setPerformative(ACLMessage.PROPOSE);
				proposal.setContent(Out[2]);
				//System.out.println(getAID().getLocalName()+" proposed: "+proposal.getContent());
				return(proposal);
			}else if(tractor == 3){
				ACLMessage proposal = cfp.createReply();
				proposal.setPerformative(ACLMessage.PROPOSE);
				proposal.setContent(Out[3]);
				//System.out.println(getAID().getLocalName()+" proposed: "+proposal.getContent());
				return(proposal);		
			}else {
				ACLMessage proposal = cfp.createReply();
				proposal.setPerformative(ACLMessage.REFUSE);
				proposal.setContent("refuse");
				//System.out.println(getAID().getLocalName()+" proposed: "+proposal.getContent());
				return(proposal);
			}
			/*}else {
				System.out.println(getAID().getLocalName()+"threw refuse exception and terminated handlecfp");
				ACLMessage proposal = cfp.createReply();
				proposal.setPerformative(ACLMessage.REFUSE);
				proposal.setContent("refuse");
				System.out.println(getAID().getLocalName()+" proposed: "+proposal.getContent());
				return(proposal);
			}*/
		}
		protected ACLMessage handleAcceptProposal(ACLMessage cfp,ACLMessage propose,ACLMessage accept) {
			ACLMessage inform = accept.createReply();
			inform.setPerformative(ACLMessage.INFORM);
			inform.setContent(propose.getContent());
			//System.out.println(getAID().getLocalName()+" was accepted, Inform sent back");
			return inform;
		}
		protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose,ACLMessage reject) {
			//System.out.println(reject.getSender().getLocalName()+" rejected proposal");
		}
		public int onEnd() {
			reset();
			return(0);
		}
		
	}
	
	//TODO: Adapt for increase in tractors (pass value from GUI)
	// Sort() sorts the information received from TCP according to tractor number
	@SuppressWarnings("null")
	private String[] sort(String[] in) {
		
		String[] farm = new String[4];
		String[] position = new String[4];
		String[] tractor = new String[4];
		String[] time = new String[4];
		int[] Time1= new int[2];
		int[] Time2=new int[2];
		int[] Time3=new int[2];
		int[] Time4=new int[2];

		//Differentiate between tractor 1 and 2 (position 0 in string[] is for tractor 1 etc.)
		for(int i=0;i<6;i++) {
		char test = in[i].charAt(10);
		char Tinfo = 't';
		//System.out.println("Required String: "+ Tinfo+" Received string: "+test);
		
		if(test == Tinfo) {
		char tnum = in[i].charAt(17);
		//System.out.println("char at 18 is "+tnum);
		int Tnum = Character.getNumericValue(tnum);
		//System.out.println("tractor number identified as: "+Tnum);
		switch(Tnum) {
		case 1:
			//Based on time set other values
			//Get most recent position according to time for each tractor
			time[0] = in[i].substring(19,25);
			//Get numeric value of time is string
			Time1[1] = Time1[0];
			Time1[0] = Integer.valueOf(time[0]);
			
			if(Time1[0]>Time1[1]) {			
			farm[0]=in[i].substring(0,5);
			position[0] = in[i].substring(6,9);
			tractor[0] = "tractor1";
			}
			break;
			
		case 2:
			time[1] = in[i].substring(19,25);
			Time2[1] = Time2[0];
			Time2[0] = Integer.valueOf(time[1]);
			if(Time2[0]>Time2[1]) {			
			farm[1]=in[i].substring(0,5);
			position[1] = in[i].substring(6,9);
			tractor[1] = "tractor2";
			}
			break;
			
		case 3:
			time[2] = in[i].substring(19,25);
			Time3[1] = Time3[0];
			Time3[0] = Integer.valueOf(time[2]);
			if(Time3[0]>Time3[1]) {			
			farm[2]=in[i].substring(0,5);
			position[2] = in[i].substring(6,9);
			tractor[2] = "tractor3";
			}
			break;
			
		case 4:
			time[3] = in[i].substring(19,25);
			Time4[1] = Time4[0];
			Time4[0] = Integer.valueOf(time[3]);
			if(Time4[0]>Time4[1]) {			
			farm[3]=in[i].substring(0,5);
			position[3] = in[i].substring(6,9);
			tractor[3] = "tractor4";
			}
			break;
			
		default:
			System.out.println("Tractor number not recognized");
		}
		}
		}
		// String[] out contains most recent position according to time for each tractor
		String[] out = new String[4];
		for(int j =0;j<4;j++) {
			if(farm[j]==null) {
				farm[j] = "farm"+agentNumber;
			}
			if(position[j]==null) {
				position[j]="_pXY_";
			}
			if(tractor[j]==null) {
				tractor[j]="none_";
			}
			if(time[j]==null) {
				time[j]="none";
			}
			out[j] = farm[j]+position[j]+tractor[j]+time[j];
		}		
		return(out);
	}
	
	
	// Behaviour that connects to the TCP and reads the position values
		private class P_reader extends OneShotBehaviour{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public void action() {
				//System.out.println("First state has been entered");
				
				try {	
					// Setup socket
					//TODO: modify code to read all position values per farm
					int i = 0;	
					for(int row = 1;row<3;row++) {
						for(int col = 1;col<4;col++) {					
					int portNum = 9100+agentNumber;
					//System.out.println("Port number being used "+portNum);
					Socket clientP = new Socket("localhost",portNum);
					//System.out.println("Position agent called for client connection");
					
					//send request
							//System.out.println("For loops is position reader entered and the value are row: "+row+"and column: "+col);
							DataOutputStream outToServer = new DataOutputStream(clientP.getOutputStream());	
							String userString = "farm2_p"+row+col;
							byte[] outByteString = userString.getBytes("UTF-8");
							outToServer.write(outByteString);
							//System.out.println("Request sent to server: "+userString);
							
							//receive reply
							byte[] inByteString = new byte[500] ;
							// Code terminate as the line just below
				            int numOfBytes = clientP.getInputStream().read(inByteString);
				            In[i] = new String(inByteString, 0, numOfBytes, "UTF-8");				            
							//System.out.println("Position Received: " + In[i]);	
							i++;
						
					//Close connection
					clientP.close();
					//System.out.println("Connection to fuel sensor closed");
						}
					}
					Out = sort(In);
					System.out.println("Position information in: "+getAID().getLocalName());
					System.out.println(Out[0]);
					System.out.println(Out[1]);
					System.out.println(Out[2]);
					System.out.println(Out[3]);
				}catch(Exception e) {
					e.printStackTrace();
				}
				
			}
			public int onEnd() {
				//System.out.println("TCP behaviour reset and state changed");
				reset();
				return(1);
			}
			}
}
