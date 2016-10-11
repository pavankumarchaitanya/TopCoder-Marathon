import java.util.Set;

public interface Graph {
	void addEdge(Edge edge);
	void addNode(Node node);
	boolean containsEdge(Edge edge);
	boolean containsNode(Node node);
	Edge getEdge(Node node1, Node node2);
	Node getNode(int nodeID);
	void removeEdge(Edge edge);
	void removeNode(Node node);
	Set<Node> getNeighboursOfNode(Node node);
}
