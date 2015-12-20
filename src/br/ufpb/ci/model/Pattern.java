package br.ufpb.ci.model;

import java.util.List;

public class Pattern {

	private double coords[];
	private int numCluster;
	public String label;

	public Pattern(double coords[]) {
		this.coords = coords;
	}
	
	public Pattern(double coords[], String label) {
		this.coords = coords;
		this.label = label;
	}

	public double[] getCoords() {
		return coords;
	}

	public void setCoords(double[] coords) {
		this.coords = coords;
	}
	
	public double calculateDistance(Pattern p1, Pattern p2) {

		double distance = 0.0;

		for (int i = 0; i < coords.length; i++) {
			distance += Math.pow(
					p1.getCoords()[i] - p2.getCoords()[i], 2);
		}

		return Math.sqrt(distance);
	}

	public boolean isInsideCircle(Pattern point, Pattern center, double radius) {

		double distance = 0;

		for (int i = 0; i < coords.length; i++) {
			distance += Math.pow(point.getCoords()[i] - center.getCoords()[i],
					2);
		}
		return distance < Math.pow(radius, 2);
	}

	public Pattern computeCentroid(List<Pattern> points, Pattern center, double radius) {

		double[] contributionPoints = new double[coords.length];
		double[] point = new double[coords.length];
		int count = 0; // Stores the amount of points that contributes to the
						// calculation of the centroid

		for (Pattern p : points) {
			if (isInsideCircle(p, center, radius)) {
				for (int i = 0; i < coords.length; i++) {
					contributionPoints[i] += p.getCoords()[i];
				}
				count++;
			}
		}

		for (int i = 0; i < coords.length; i++) {
			point[i] = contributionPoints[i] / count;
		}

		return new Pattern(point, null);
	}

	public void setCluster(int numCluster) {
		this.numCluster = numCluster;
	}

	public int getCluster() {
		return numCluster;
	}

	public Pattern initiateCentroid() {

		double[] point = new double[coords.length];

		for (int i = 0; i < coords.length; i++) {
			point[i] = Double.NEGATIVE_INFINITY;
		}

		return new Pattern(point);
	}

	public Pattern nearestCentroide(Pattern centroide, List<Pattern> centroides) {

		Pattern nearest = null;
		double minDist = Double.POSITIVE_INFINITY;

		for (Pattern ponto : centroides) {
			if (calculateDistance(centroide, ponto) < minDist) {
				minDist = calculateDistance(centroide, ponto);
				nearest = ponto;
			}
		}
		return nearest;
	}

	public String toString() {
		String coords = "";

		for (int i = 0; i < this.coords.length; i++) {

			coords += getCoords()[i] + (i != (this.coords.length - 1) ? ", " : "");
		}

//		return " Id: " + getClasse() + ". Cluster: " + getCluster();
//		 return "Ponto: (" + coords + ").Cluster: " + getCluster();
		 return coords + "#";

	}

	public String toHash() {
		String coords = "";

		for (int i = 0; i < this.coords.length; i++) {

			coords += getCoords()[i] + (i != 15 ? ", " : "");
		}
		return coords;
	}

	public int hashCode() {
		// TODO Auto-generated method stub
		return toHash().hashCode();
	}

	public boolean equals(Object obj) {
		Pattern p = (Pattern) obj;

		for (int i = 0; i < coords.length; i++) {
			if (p.getCoords()[i] != getCoords()[i])
				return false;
		}
		return true;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
