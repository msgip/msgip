package br.ufpb.ci.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MSGui {

	private JFrame frame;
	private JTextField textFieldPathFile;
	private JTextField textFieldFragmentSize;
	private JTextField textFieldArtificialFragNum;
	private JLabel lblStandardDeviationOf;
	private JTextField textFieldStdFrags;
	private JLabel lblRunningGif;
	private JTextArea textAreaOutput;
	private DoProcess msProccess;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MSGui window = new MSGui();
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
	public MSGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Mean Shift Genomic Island Predictor - MSGIP");
		frame.setResizable(false);
		frame.setBounds(100, 100, 560, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnOpenFile = new JButton("Open .fna file");
		final JFileChooser fc = new JFileChooser();
		btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					// This is where a real application would open the file.
					textFieldPathFile.setText(file.getAbsolutePath());
				} else {
//					log.append("Open command cancelled by user." + newline);
				}
			}
		});
		btnOpenFile.setBounds(10, 11, 120, 23);
		frame.getContentPane().add(btnOpenFile);
		
		textFieldPathFile = new JTextField();
		textFieldPathFile.setEditable(false);
		textFieldPathFile.setBounds(140, 12, 404, 20);
		frame.getContentPane().add(textFieldPathFile);
		textFieldPathFile.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Fragment Size (Kb):");
		lblNewLabel.setBounds(10, 42, 112, 14);
		frame.getContentPane().add(lblNewLabel);
		
		textFieldFragmentSize = new JTextField();
		textFieldFragmentSize.setBounds(132, 39, 99, 20);
		frame.getContentPane().add(textFieldFragmentSize);
		textFieldFragmentSize.setColumns(10);
		
		JLabel lblNumberOfArtificial = new JLabel("Number of artificial fragments:");
		lblNumberOfArtificial.setBounds(267, 43, 174, 14);
		frame.getContentPane().add(lblNumberOfArtificial);
		
		textFieldArtificialFragNum = new JTextField();
		textFieldArtificialFragNum.setColumns(10);
		textFieldArtificialFragNum.setBounds(455, 39, 89, 20);
		frame.getContentPane().add(textFieldArtificialFragNum);
		
		lblStandardDeviationOf = new JLabel("Standard Deviation of Selected Fragments:");
		lblStandardDeviationOf.setBounds(10, 73, 260, 14);
		frame.getContentPane().add(lblStandardDeviationOf);
		
		textFieldStdFrags = new JTextField();
		textFieldStdFrags.setColumns(10);
		textFieldStdFrags.setBounds(267, 70, 89, 20);
		frame.getContentPane().add(textFieldStdFrags);
		
		JButton btnSaveOutput = new JButton("Save");
		btnSaveOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser chooser = new JFileChooser();
				// Demonstrate "Save" dialog:
				int rVal = chooser.showSaveDialog(frame);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					FileWriter fw = null;
					try {
						fw = new FileWriter(chooser.getSelectedFile() + ".txt");
						fw.write(textAreaOutput.getText());
					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(frame, "An error has occurred while saving the file",
								"Error while saving", JOptionPane.ERROR_MESSAGE);
					} finally {
						try {
							fw.close();
							JOptionPane.showMessageDialog(frame, "File saved successfully",
									"Success", JOptionPane.INFORMATION_MESSAGE);
						} catch (IOException e) {}		

					}
				}
				if (rVal == JFileChooser.CANCEL_OPTION) {
					JOptionPane.showMessageDialog(frame, "You have canceled the saving process",
							"Saving cancelled", JOptionPane.WARNING_MESSAGE);;
				}

			}
		});
		btnSaveOutput.setBounds(455, 337, 89, 23);
		frame.getContentPane().add(btnSaveOutput);
		
		final JButton btnStartProcess = new JButton("Start");
		final JButton btnStopProcess = new JButton("Stop");
		btnStopProcess.setEnabled(false);
				
		btnStartProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
								
				btnStopProcess.setEnabled(true);

				if (textFieldArtificialFragNum.getText().equals("") || textFieldPathFile.getText().equals("")
						|| textFieldStdFrags.getText().equals("")
						|| textFieldFragmentSize.getText().equals("")) {
					
					JOptionPane.showMessageDialog(frame, "Please, fill out all the required parameters",
							null, JOptionPane.ERROR_MESSAGE);
					
				}else{
					//Convert all the values
					int artificialFragNumber = Integer.parseInt(textFieldArtificialFragNum.getText());
					double stdFragments = Double.parseDouble(textFieldStdFrags.getText());
					int fragmentSize = Integer.parseInt(textFieldFragmentSize.getText());
					
					msProccess = new DoProcess(textFieldPathFile.getText(), fragmentSize, stdFragments,
							artificialFragNumber, textAreaOutput,btnStartProcess,btnStopProcess,lblRunningGif);	
					try {
						msProccess.execute();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
														
				}
				
			}
		});
		btnStartProcess.setBounds(255, 337, 89, 23);
		frame.getContentPane().add(btnStartProcess);
		
		btnStopProcess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				msProccess.cancel(true);
			}
		});
		btnStopProcess.setBounds(354, 337, 89, 23);
		frame.getContentPane().add(btnStopProcess);
		
		JButton btnNewButton = new JButton("Default parameters");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textFieldFragmentSize.setText("50000");
				textFieldArtificialFragNum.setText("5");
				textFieldStdFrags.setText("1");
			}
				
		});
		btnNewButton.setBounds(370, 68, 174, 23);
		frame.getContentPane().add(btnNewButton);
		
		URL imageURL = null;
		try {
			File myFile=new File("resources/ellipsis.gif");
			imageURL = myFile.toURI().toURL();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ImageIcon runningGif = new ImageIcon(imageURL);
		lblRunningGif = new JLabel(runningGif);
		lblRunningGif.setVisible(false);
		lblRunningGif.setBounds(38, 337, 155, 23);
		frame.getContentPane().add(lblRunningGif);
		
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 119, 534, 207);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		frame.getContentPane().add(scrollPane);
		
		textAreaOutput = new JTextArea();
		textAreaOutput.setEditable(false);
		scrollPane.setViewportView(textAreaOutput);
		
		textAreaOutput.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)){
					int dialogResult = JOptionPane.showConfirmDialog(scrollPane, "Clear output area?");
					if(dialogResult == JOptionPane.YES_OPTION){
						textAreaOutput.setText("");
					}
				}
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		JLabel lblOutput = new JLabel("Output:");
		lblOutput.setBounds(10, 98, 46, 14);
		frame.getContentPane().add(lblOutput);
	}
}
