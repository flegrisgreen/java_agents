package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class Dashboard extends Agent {
	
	public String fuel;
	public String tractor;
	public String position;
	public String time;
	public String farm;
	private AgentController read = null;
	private AgentController assem = null;
	private int Tick=1;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DTGUI gui;
	
	protected void setup(){
		
		//Launch GUI
		gui = new DTGUI(this);
		gui.visible();
	}
	
public void create(int num) {
		
		String reader = "ReaderAgent"+num;
		String assembler = "AssemblerAgent"+num;
		
		try {
			AgentContainer container = (AgentContainer)getContainerController();
			read = container.createNewAgent(reader, "agents.FuelReader", null);
			read.start();
			System.out.println("Reader agent created");
			assem = container.createNewAgent(assembler, "agents.Assembler", null);
			assem.start();
		}catch(Exception any){
			any.printStackTrace();
		}
	}
	
	 public void request(int aNum) {
		 Agent agent = this;
		 
		 Behaviour ticker = new TickerBehaviour(agent,3000) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			protected void onTick() {
				if(aNum != Tick) {
					stop();
					Tick = aNum;
				}else {
				ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
				request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
				request.addReceiver(new AID("AssemblerAgent"+aNum,AID.ISLOCALNAME));
				request.setContent("Request for fuel data");
				Behaviour initiator = new REInitiator(agent,request);
				addBehaviour(initiator);
				if(tractor == null) {
					fuel = "processing";
					farm = "processing";
					position = "processing";
					tractor = "processing";
					time = "processing";	
				}
				gui.update();
				}	
			} 
		 };
		 addBehaviour(ticker);
	 }
	 private class REInitiator extends AchieveREInitiator{
		 
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		// Constructor
		public REInitiator(Agent a, ACLMessage msg) {
			super(a, msg);
			//System.out.println("Dashboard initiated RE behaviour and sent message: "+ msg.getContent());
		}
		
		// Process information from Assembler
		protected void handleInform(ACLMessage inform) {
			String displayData;
			System.out.println("Information received by dashboard: "+inform.getContent()+" from "+inform.getSender().getLocalName());
			displayData = inform.getContent();
			divideInfo(displayData);
		
		}
	 }
	 
	 private void divideInfo(String info) {
		// System.out.println("Length of info string "+info.length());
		 fuel = info.substring(0,4);
		 farm = info.substring(5,10);
		 position = info.substring(10,13);
		 tractor = info.substring(14,21);
		 time = info.substring(21,23)+":"+info.substring(23,25)+":"+info.substring(25,27);
	 }
}
