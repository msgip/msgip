package br.ufpb.ci.algorithms;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.ufpb.ci.model.Cluster;
import br.ufpb.ci.model.Pattern;
import br.ufpb.ci.utils.MSUtil;

public class GenomicIslandPredictor {

	private MSUtil util;
	private double stdCoefficient;
	private int numArtificialFrags;
	private int fragmentSize;
	private String pathGenome;

	public GenomicIslandPredictor(String pathGenome, int fragmentSize, double stdCoefficient, int numArtficialFrags) {

		this.util = new MSUtil();
		this.stdCoefficient = stdCoefficient;
		this.numArtificialFrags = numArtficialFrags;
		this.fragmentSize = fragmentSize;
		this.pathGenome = pathGenome;

	}

	/*
	 * Predict GIs in a genome using Mean Shift based method. Writes the output
	 * in a text file. Flowchart of MSGIP: 
	 * 1º - Load the complete genome in format FASTA .fna 
	 * 2º - Divide it into non-overlapping fragments with user supplied size 
	 * 3º - Select artificial segments and insert them into original genome 
	 * 4º - Execute mean shift multiple times, until the artificial fragments
	 *  are separated into individual clusters. Each time deacresing the bandwidth 
	 * 5º - With the optimal bandwidth selected, check if there are fragments 
	 *  into separate clusters (maximum 200kb) and show them as putative genomic islands
	 */

	/*
	 * This method uses a heuristic to select the optimal bandwidth...
	 */
	public String predictGIs() {

		// Select artificial fragments and verify if it was possible to select
		// the desired number of frags
		long startTime = System.currentTimeMillis();
		int numSelectedFragments = util.selectArtificialFragments(pathGenome, pathGenome + ".temp", stdCoefficient,
				fragmentSize, numArtificialFrags);
		double optBandwidth = 8000; // It starts with a great value
		boolean flagOptBandwidth = false;

		List<Pattern> fragmentList = util.fileToFragments(pathGenome + ".temp", fragmentSize);
		MeanShift algorithm;
		int countSeparated;
		List<Cluster> clusterResult = null;

		while (!flagOptBandwidth) {

			countSeparated = numSelectedFragments;

			algorithm = new MeanShift(fragmentList, optBandwidth);
			clusterResult = algorithm.doClustering();

			// Evaluate clusters' content to verify if the artificial fragments
			// were separated from the rest of the frags
			
			for (int i = fragmentList.size() - numSelectedFragments; i < fragmentList.size(); i++) {
				if (clusterResult.get(fragmentList.get(i).getCluster() - 1).getPatterns().size() == 1) {
					countSeparated--;
				}
			}

			if (countSeparated == 0) {
				break;
			} else if (optBandwidth > 0) {
				optBandwidth -= 10;
			} else {
				optBandwidth = -1; // The heuristic didn't work out.
				break;
			}

		}

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		// Deletes the temp file
		boolean delete = new File(pathGenome + ".temp").delete();
		if (!delete) {
			System.err.println("System could not remove temp file");
		}

		StringBuilder sb = new StringBuilder();

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		try {
			sb.append(
					"----------------------------------------Summary of Execution----------------------------------------\r\n");
			sb.append("Date/Time: " + dateFormat.format(date) + "\r\n");
			sb.append("Total execution time: " + totalTime / 1000 + " seconds\r\n");

			// Maximum GI size of 200kb
			int maxGIFragNumber = Math.round(200000 / fragmentSize);

			boolean flagIslands = false; // Defines if the method identify any
											// islands.

			for (int i = 0; i < fragmentList.size() - numSelectedFragments; i++) {

				// Mudar aqui de acordo com o tamanho da janela
				if (clusterResult.get(fragmentList.get(i).getCluster() - 1).getPatterns().size() <= maxGIFragNumber) {
					flagIslands = true;
					String s = String.format("Putative Genomic Island: %.3f - %.3fMb.\r\n",
							(double) i * fragmentSize / 1000000, (double) (i * fragmentSize + fragmentSize) / 1000000);
					sb.append(s);
				}

			}

			if (!flagIslands) {
				sb.append("No Genomic Island has been identified by MSGIP.\r\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("It couldn't write a result file");
		}

		return sb.toString();

	}

}
