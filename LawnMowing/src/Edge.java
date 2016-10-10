
public class Edge {//Edge is unidirectional by default
	Node toNode, fromNode;
	int weight;
	public Node getFromNode() {
		return fromNode;
	}
	public Node getToNode() {
		return toNode;
	}
	public int getWeight() {
		return weight;
	}
	public void setFromNode(Node fromNode) {
		this.fromNode = fromNode;
	}
	public void setToNode(Node toNode) {
		this.toNode = toNode;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
}
