package agents;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class DTGUI {
	
	private Dashboard agent;
	private JFrame frame;
	private JTextField textFieldFuel;
	private JTextField textFieldFarm;
	private JTextField textFieldPos;
	private JTextField textFieldTime;
	private JTextField textFieldTrac;
	private int numberOfTractor = 3;
	

	/**
	 * Launch the application.
	 */
	public void visible() {
		frame.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public DTGUI(Dashboard a) {
		a.getLocalName();
		agent = a;
		initialize(a);		
		//TODO: pass number of tractors to position reader agent
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(Dashboard agent) {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
	
		// "Contact" Button code
		JButton btnContact = new JButton("Contact");
		btnContact.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			String TracNum = textFieldTrac.getText();
			int Num = Integer.parseInt(TracNum);
			agent.request(Num);
			}
		});
		btnContact.setBounds(32, 125, 89, 23);
		frame.getContentPane().add(btnContact);
		
		textFieldFuel = new JTextField();
		textFieldFuel.setBounds(295, 30, 78, 23);
		frame.getContentPane().add(textFieldFuel);
		textFieldFuel.setColumns(10);
		
		textFieldFarm = new JTextField();
		textFieldFarm.setBounds(295, 64, 78, 20);
		frame.getContentPane().add(textFieldFarm);
		textFieldFarm.setColumns(10);
		
		textFieldPos = new JTextField();
		textFieldPos.setBounds(295, 95, 78, 20);
		frame.getContentPane().add(textFieldPos);
		textFieldPos.setColumns(10);
		
		textFieldTime = new JTextField();
		textFieldTime.setBounds(295, 126, 78, 20);
		frame.getContentPane().add(textFieldTime);
		textFieldTime.setColumns(10);
		
		textFieldTrac = new JTextField();
		textFieldTrac.setBounds(35, 92, 86, 20);
		frame.getContentPane().add(textFieldTrac);
		textFieldTrac.setColumns(10);
		
		JLabel lblFuelConsumption = new JLabel("Fuel Consumption:");
		lblFuelConsumption.setBounds(143, 34, 112, 14);
		frame.getContentPane().add(lblFuelConsumption);
		
		JLabel lblFarm = new JLabel("Farm:");
		lblFarm.setBounds(203, 67, 52, 14);
		frame.getContentPane().add(lblFarm);
		
		JLabel lblPosition = new JLabel("Position:");
		lblPosition.setBounds(189, 98, 66, 14);
		frame.getContentPane().add(lblPosition);
		
		JLabel lblNewLabel = new JLabel("Time:");
		lblNewLabel.setBounds(203, 129, 52, 14);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblEnterTractorNumber = new JLabel("Enter tractor number:");
		lblEnterTractorNumber.setBounds(27, 67, 138, 14);
		frame.getContentPane().add(lblEnterTractorNumber);
		
		JLabel lblKml = new JLabel("km/L");
		lblKml.setBounds(378, 34, 46, 14);
		frame.getContentPane().add(lblKml);
		JButton btnAddTractor = new JButton("Add Tractor");
		btnAddTractor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				agent.create(numberOfTractor);
				String msg = "Tractor"+numberOfTractor+" added";
				numberOfTractor++;
				JOptionPane.showMessageDialog(null,msg);
			}
		});
		btnAddTractor.setBounds(32, 189, 133, 23);
		frame.getContentPane().add(btnAddTractor);
	}
	public void update() {
		textFieldFuel.setText(agent.fuel);
		textFieldFarm.setText(agent.farm);
		textFieldPos.setText(agent.position);
		textFieldTime.setText(agent.time);
	}
}
