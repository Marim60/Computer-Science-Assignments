package Threads2;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JTextField;
import java.awt.Panel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Form2 {

	private JFrame frame;
	private JTextField N;
	private static JTextField Buffer_Size;
	private JTextField Output_File; 

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Form2 window = new Form2();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Form2() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setEnabled(false);
		frame.getContentPane().setBackground(new Color(128, 128, 128));
		frame.getContentPane().setLayout(null);

		Panel panel = new Panel();
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setBounds(10, 10, 766, 207);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblNewLabel_1 = new JLabel("N");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel_1.setBounds(308, 22, 57, 25);
		lblNewLabel_1.setToolTipText("");
		lblNewLabel_1.setForeground(Color.BLACK);
		lblNewLabel_1.setBackground(Color.WHITE);
		panel.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Buffer Size");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel_2.setBounds(308, 73, 155, 31);
		panel.add(lblNewLabel_2);

		JLabel lblNewLabel = new JLabel("Output File");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel.setBounds(308, 129, 144, 31);
		panel.add(lblNewLabel);

		Panel panel_1 = new Panel();
		panel_1.setBackground(Color.LIGHT_GRAY);
		panel_1.setBounds(10, 332, 500, -129);
		frame.getContentPane().add(panel_1);

		Panel panel_2 = new Panel();
		panel_2.setBackground(Color.LIGHT_GRAY);
		panel_2.setBounds(10, 223, 766, 212);
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(null);

		JLabel count = new JLabel("0");
		count.setFont(new Font("Tahoma", Font.BOLD, 14));
		count.setBounds(358, 57, 288, 35);
		panel_2.add(count);

		JLabel time = new JLabel("0");
		time.setFont(new Font("Tahoma", Font.BOLD, 14));
		time.setBounds(358, 103, 288, 35);
		panel_2.add(time);
		frame.setBounds(100, 100, 802, 484);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel Largest_prime_number = new JLabel("0");
		Largest_prime_number.setFont(new Font("Tahoma", Font.BOLD, 14));
		Largest_prime_number.setBounds(358, 11, 288, 35);
		panel_2.add(Largest_prime_number);

		JButton Start = new JButton("Start Producer");
		Start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					
					Main m=new Main();
					Thread t=new Thread() {
						public  void run() {
							for(;;)
							{
								 if(m.get_flag2())
								{
									Largest_prime_number.setText(Integer.toString(m.get_Max_Prime()));
									time.setText(m.get_time());
									count.setText(Integer.toString(m.get_Count_Prime_Numbers()));
									break;
								}
								Largest_prime_number.setText(Integer.toString(m.get_Max_Prime()));
								time.setText("0");
								count.setText("0");
							}
						}
					};
					m.Work(Integer.parseInt(N.getText()),Integer.parseInt(Buffer_Size.getText()),Output_File.getText());					
					t.start();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		Start.setBackground(new Color(112, 128, 144));
		Start.setFont(new Font("Tahoma", Font.BOLD, 14));
		Start.setBounds(10, 165, 164, 31);
		panel.add(Start);

		N = new JTextField();
		N.setBounds(10, 11, 226, 31);
		panel.add(N);
		N.setColumns(10);

		Buffer_Size = new JTextField();
		Buffer_Size.setBounds(10, 63, 226, 31);
		panel.add(Buffer_Size);
		Buffer_Size.setColumns(10);

		Output_File = new JTextField();
		Output_File.setBounds(10, 117, 226, 31);
		panel.add(Output_File);
		Output_File.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("the largest prime number");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNewLabel_3.setForeground(new Color(210, 105, 30));
		lblNewLabel_3.setBounds(10, 11, 297, 35);
		panel_2.add(lblNewLabel_3);

		JLabel lblNewLabel_3_1 = new JLabel("# of elements (prime number) generated");
		lblNewLabel_3_1.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNewLabel_3_1.setForeground(new Color(210, 105, 30));
		lblNewLabel_3_1.setBounds(10, 57, 297, 35);
		panel_2.add(lblNewLabel_3_1);

		JLabel lblNewLabel_3_2 = new JLabel("time elapsed since the start of processing");
		lblNewLabel_3_2.setForeground(new Color(210, 105, 30));
		lblNewLabel_3_2.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNewLabel_3_2.setBounds(10, 103, 297, 35);
		panel_2.add(lblNewLabel_3_2);

	}

	protected String ToString(int get_Max_Prime) {
		// TODO Auto-generated method stub
		return null;
	}

	protected int ParsInt(String text) {
		// TODO Auto-generated method stub
		return 0;
	}
}
