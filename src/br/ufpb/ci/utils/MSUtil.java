package br.ufpb.ci.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import br.ufpb.ci.model.Pattern;

public class MSUtil {

	/*
	 * This method divides the genome into non-overlapping fragments, according to 
	 * the fragment size, provided by the user.
	 */
	public List<Pattern> fileToFragments(String pathFile, int fragmentSize) {

		// Each fragment can be viewed as a point in a 4-dimensional space.
		List<Pattern> points = new ArrayList<Pattern>();
		String line;
		double pointCoords[] = new double[4];
		StringBuilder sb = new StringBuilder();
		String fullGenome;
		int sum = 0;
		BufferedReader bReader = null;

		try {
			bReader = new BufferedReader(new FileReader(pathFile));
			bReader.readLine(); // ignores the first line that contais the header

			// Put all  lines in a single string
			while ((line = bReader.readLine()) != null) {
				sb.append(line);
			}
			fullGenome = sb.toString();

			for (int i = 0; i <= fullGenome.length(); i++) {
				if (sum == fragmentSize) {
				
					points.add(new Pattern(pointCoords));
					// Restart the variables
					sum = 0;
					pointCoords = new double[4];
					i--;
				} else if (i < fullGenome.length()) {
					char base = fullGenome.charAt(i);

					switch (base) {
					case 'A': {
						pointCoords[0]++;
						sum++;
						break;
					}
					case 'T': {
						pointCoords[1]++;
						sum++;
						break;
					}
					case 'C': {
						pointCoords[2]++;
						sum++;
						break;
					}
					case 'G': {
						pointCoords[3]++;
						sum++;
						break;
					}
					default: {
						sum++;
						break;
					}
					}

				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				bReader.close();
			} catch (IOException e) {}
		}

		return points;

	}

	private double[] calculateGenomeBasesAvg(List<Pattern> fragmentsList) {

		double[] nucleotidesMeans = new double[4];

		for (Pattern f : fragmentsList) {

			for (int i = 0; i < 4; i++) {
				nucleotidesMeans[i] += f.getCoords()[i];
			}
		}

		for (int i = 0; i < 4; i++) {
			nucleotidesMeans[i] = nucleotidesMeans[i] / fragmentsList.size();
		}

		return nucleotidesMeans;
	}

	private double[] calculateGenomeBasesStd(double[] means, List<Pattern> fragmentList) {

		double[] stdGenome = new double[4];
		double[] sumBases = new double[4]; // stores (Xi - M)^2
		int n = fragmentList.size();

		// Computes the standard deviation
		for (Pattern f : fragmentList) {
			for (int i = 0; i < 4; i++) {
				sumBases[i] += Math.pow(f.getCoords()[i] - means[i], 2);
			}
		}

		for (int i = 0; i < 4; i++) {
			stdGenome[i] = Math.sqrt(sumBases[i] / n);
		}
		return stdGenome;

	}

	private List<String> fileToStringFragment(String pathFile, int fragmentSize){
		
		List<String> genomeFragments = new ArrayList<String>();
		String line;
		StringBuilder sb = new StringBuilder();
		String completeGenome;
		BufferedReader bReader = null;
		
		try {
			bReader = new BufferedReader(new FileReader(pathFile));
			bReader.readLine(); // ignores the first line
			
			while ((line = bReader.readLine()) != null) {
				sb.append(line);
			}
			completeGenome = sb.toString();
			
			for(int i = 0; i<completeGenome.length()/fragmentSize; i++){
				genomeFragments.add(completeGenome.substring(i*fragmentSize, i*fragmentSize + fragmentSize));
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return genomeFragments;

		
	}
	
	public void copyFileFragment(String src, String dest, int fragmentSize) {

		BufferedReader br = null;
		PrintWriter p = null;
		StringBuilder sb = new StringBuilder(); 
		
		try {
			br = new BufferedReader(new FileReader(src));
			p = new PrintWriter(dest);
			String firstLine = br.readLine();
			p.write(firstLine); //Writes the first line
			String line;
			//Adds full genome to StringBuilder
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			String genome = sb.toString();

			int i = 0;
			for(i = 0; i<genome.length() - genome.length()%fragmentSize; i++){
				if(i%70 == 0){
					p.append("\n");
				}
				p.append(genome.charAt(i));
			}
			p.append("\n");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
			p.close();
		}
	}
		
	public Pattern stringToPattern(String fragment) {

		double patternValues[] = new double[4];
		for (int i = 0; i < fragment.length(); i++) {

			char base = fragment.charAt(i);
			switch (base) {
			case 'A':
				patternValues[0]++;
				break;
			case 'T':
				patternValues[1]++;
				break;
			case 'C':
				patternValues[2]++;
				break;
			case 'G':
				patternValues[3]++;
				break;
			}
		}

		return new Pattern(patternValues);

	}
	
	public String getHeaderDescription(String pathGenome){
		
		BufferedReader br = null;
		String header = "";
		try {
			br = new BufferedReader(new FileReader(pathGenome));
			 header = br.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return header;
	}
	
	public int selectArtificialFragments(String filePath, String outputFilePath, double stdCoefficient,
			int fragmentSize, int numOfArtificialFrags) {
		
		//At this point, the file located in filePath must be copied to the file in outputFilePath
		copyFileFragment(filePath, outputFilePath, fragmentSize);
		
		// Computes mean and standard deviation of all the genome fragments according to the fragment size
		List<Pattern> listaGenes = fileToFragments(filePath, fragmentSize);
		double[] medias = calculateGenomeBasesAvg(listaGenes);
		double[] dp = calculateGenomeBasesStd(medias, listaGenes);
		
		//Use the header to avoid selection of fragments of the same sequence
		String header = getHeaderDescription(filePath);
		
		String tmpCandidateHeader;

		// It stores the all probable fragments used for artificial insertion
		List<String> genomeFiles = new ArrayList<String>();
		
		// Stores the genome parts according with the fragment size. 
		List<Pattern> tmpCandidateFragments = new ArrayList<Pattern>();
		
		List<String> tmpCandidateStringFragments = new ArrayList<String>();

		//Adds all the genomes in folder "genomes" the list
		File f= new File("genomes");
		File[] files = f.listFiles();
		for(int i = 0; i<files.length; i++){
			genomeFiles.add(f.getName()+"/"+files[i].getName());
		}

		int approvedFragments = 0;
		int count = 0;
		int indexPart = 0;
		boolean acceptedFlag = false;
		
		List<Pattern> selectedArtificialFrags = new ArrayList<Pattern>();

		try {

			for (int i = 0; i < genomeFiles.size(); i++) {
								
				tmpCandidateHeader = getHeaderDescription(genomeFiles.get(i));
				tmpCandidateFragments = fileToFragments(genomeFiles.get(i),fragmentSize);
				tmpCandidateStringFragments = fileToStringFragment(genomeFiles.get(i), fragmentSize);

				// Compare the header of the candidate genome with the header of
				// investigated genome.
				if (!header.equals(tmpCandidateHeader)) {

					while (indexPart < tmpCandidateFragments.size() && acceptedFlag == false
							&& approvedFragments < numOfArtificialFrags) {
						for (int j = 0; j < 4; j++) {

							if (tmpCandidateFragments.get(indexPart).getCoords()[j] > (medias[j]
									+ stdCoefficient * dp[j])
									|| tmpCandidateFragments.get(indexPart).getCoords()[j] < (medias[j]
											- stdCoefficient * dp[j])) {
								count++;

							}

							// Here we look if there are similarity between
							// artificial fragments
							if (!selectedArtificialFrags.isEmpty()) {

								for (int k = 0; k < selectedArtificialFrags.size(); k++) {

									for (int l = 0; l < 4; l++) {
										if (tmpCandidateFragments.get(indexPart)
												.getCoords()[l] < selectedArtificialFrags.get(k).getCoords()[l]
														- selectedArtificialFrags.get(k).getCoords()[l] * 7.5 / 100
												|| tmpCandidateFragments.get(indexPart)
														.getCoords()[l] > selectedArtificialFrags.get(k).getCoords()[l]
																+ selectedArtificialFrags.get(k).getCoords()[l] * 7.5
																		/ 100) {
										} else {
											count--;
										}
									}

								}

							}

						}

						if (count == 4) {
							// At this point we have the index of the selected
							// part.
							String partGenome = tmpCandidateStringFragments.get(indexPart);
							Pattern p = stringToPattern(partGenome);
							selectedArtificialFrags.add(p); // Add the selected
															// genome to the
															// list
							String parsedStr = partGenome.replaceAll("(.{70})", "$1\n");
							FileWriter fw = new FileWriter(outputFilePath, true);
							fw.append(parsedStr);
							fw.append("\n");
							fw.close();
							acceptedFlag = true;
							approvedFragments++;
							count = 0;

						} else {
							count = 0;
							indexPart++;
						}
					}
					acceptedFlag = false;
					indexPart = 0;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return approvedFragments;
		
	}
	
}
