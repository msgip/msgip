package br.ufpb.ci.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import br.ufpb.ci.algorithms.GenomicIslandPredictor;

public class DoProcess extends SwingWorker<String, String>{
	
	private String pathFile;
	private int fragmentSize;
	private double stdFragments;
	private int artificialFragNumber;
	private GenomicIslandPredictor predictor;
	private JTextArea textAreaOutput;
	private JButton btnStartProcess;
	private JButton btnStopProcess;
	private JLabel lblRunningGif;
	
	public DoProcess(String pathFile, int fragmentSize, double stdFragments, int artificialFragNumber,
			JTextArea textAreaOutput, JButton btnStartProcess, JButton btnStopProcess, JLabel lblRunningGif) {
		this.pathFile = pathFile;
		this.fragmentSize = fragmentSize;
		this.stdFragments = stdFragments;
		this.artificialFragNumber = artificialFragNumber;
		this.textAreaOutput = textAreaOutput;
		this.btnStartProcess = btnStartProcess;
		this.btnStopProcess = btnStopProcess;
		this.lblRunningGif = lblRunningGif;
	}

	@Override
	protected String doInBackground() throws Exception {
		btnStartProcess.setEnabled(false);
		btnStopProcess.setEnabled(true);
	    lblRunningGif.setVisible(true);
	    publish("Started Processs\r\n");
		predictor = new GenomicIslandPredictor(pathFile, fragmentSize, stdFragments, artificialFragNumber);
		return  predictor.predictGIs();
	}
	
	@Override
	protected void process(final List<String> chunks) {

		for (final String string : chunks) {
			textAreaOutput.append(string);
		}

	}
	
	@Override
	protected void done() {
		
		if(!isCancelled()){
			try{
				String output = get();
				textAreaOutput.append(output);
			}catch(Exception e){
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				sw.toString(); // stack trace as a string
				publish("An error has ocurred while processing your request. Try again.\r\n"+sw.toString());
			}
		}else{
		    publish("Canceled Processs\r\n");	
		}

		btnStartProcess.setEnabled(true);
		btnStopProcess.setEnabled(false);
	    lblRunningGif.setVisible(false);

	}

}
