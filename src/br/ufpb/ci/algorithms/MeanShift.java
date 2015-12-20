package br.ufpb.ci.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufpb.ci.model.Cluster;
import br.ufpb.ci.model.Pattern;

public class MeanShift {
	
	private List<Pattern> patterns;
	private double bandwidth;
	private double threshold;
	private Pattern newCentroid, previousCentroid;
	private HashMap<Pattern, Cluster> clusters;
	private List<Cluster> partition;
	private int numClusters;
	private List<Pattern> centroids;
	
	public MeanShift(List<Pattern> patterns, double bandwidth){
		this.patterns = patterns;
		this.bandwidth = bandwidth;
		this.clusters = new HashMap<Pattern, Cluster>();
		this.partition = new ArrayList<Cluster>();
		this.threshold =  1E-3;
		this.partition = new ArrayList<Cluster>();
		this.centroids = new ArrayList<Pattern>();
	}

	public List<Cluster> doClustering() {
	
		for (int i = 0; i < patterns.size(); i++) {
				
			Pattern currentPattern = patterns.get(i);
			
			previousCentroid = currentPattern.initiateCentroid();
			newCentroid = currentPattern.computeCentroid(patterns, currentPattern, bandwidth);
			while (true) {
				if (currentPattern.calculateDistance(newCentroid, previousCentroid) <= threshold) { 
					//Algorithm converged
					if (clusters.containsKey(newCentroid)) { //Centroid already exists
						currentPattern.setCluster(clusters.get(newCentroid).getClusterId());
						clusters.get(newCentroid).getPatterns().add(currentPattern);
					} else { // Centroid still doesn't exist
						if (!centroids.isEmpty()) { // The centroid list is not
													// empty. Search for the
													// nearest centroid
							Pattern nearest = currentPattern.nearestCentroide(
									newCentroid, centroids);
							if (currentPattern.calculateDistance(newCentroid,
									nearest) <= bandwidth / 4) { //Checks merge possibility	
								currentPattern.setCluster(clusters.get(nearest).getClusterId());
								clusters.get(nearest).getPatterns().add(currentPattern);

							} else {
								Cluster c = new Cluster(null, ++numClusters);
								partition.add(c);
								List<Pattern> p = new ArrayList<Pattern>();
								currentPattern.setCluster(numClusters);
								p.add(currentPattern);
								c.setPatterns(p);
								clusters.put(newCentroid, c);
								centroids.add(newCentroid);

							}
						} else { 
							Cluster c = new Cluster(null, ++numClusters);
							partition.add(c);
							List<Pattern> p = new ArrayList<Pattern>();
							currentPattern.setCluster(numClusters);
							p.add(currentPattern);
							c.setPatterns(p);
							clusters.put(newCentroid, c);
							centroids.add(newCentroid);
						}
						
					}
					previousCentroid = currentPattern.initiateCentroid();
					newCentroid = currentPattern.initiateCentroid();
					break;

				} else {
					previousCentroid = newCentroid;
					newCentroid = currentPattern.computeCentroid(patterns,
							previousCentroid, bandwidth);
				}
			}
		}
		return partition;

	}

}
