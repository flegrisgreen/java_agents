package agents;

public class Launch {

	public static void main(String[] args) {
		
		//dummyMultiServer dummy = new dummyMultiServer();
	    //Thread serverThread = new Thread(dummy);
	   // serverThread.start(); 
		
        String[] args1 = new String[3];
        args1[0] = "-gui";
        args1[1] = "-agents";

        //agents to run at startup
        String[] a = new String[12];
        a[0] = "MySniffer:jade.tools.sniffer.Sniffer;";
        a[1] = "AssemblerAgent1:agents.Assembler;";
        a[2] = "AssemblerAgent2:agents.Assembler;";
        a[3] = "ReaderAgent1:agents.FuelReader;";
        a[4] = "ReaderAgent2:agents.FuelReader;";
        a[5] = "DashboardAgent:agents.Dashboard;";
        a[6] = "PositionAgent1:agents.PositionReader;";
        a[7] = "PositionAgent2:agents.PositionReader;";
        a[8] = "PositionAgent3:agents.PositionReader";
        args1[2] = a[0]+a[1]+a[2]+a[3]+a[4]+a[5]+a[6]+a[7]+a[8];
       
        jade.Boot.main(args1);  	
	
	}
}


