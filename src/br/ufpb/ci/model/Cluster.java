package br.ufpb.ci.model;

import java.util.List;

public class Cluster {

	List<Pattern> patterns;
	int clusterId;

	public Cluster(List<Pattern> patterns, int clusterId) {
		this.patterns = patterns;
		this.clusterId = clusterId;
	}

	public List<Pattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(List<Pattern> patterns) {
		this.patterns = patterns;
	}

	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

}
