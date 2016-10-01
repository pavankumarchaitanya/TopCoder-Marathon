
public interface Graph {
	void addNode(Node node);
	void removeNode(Node node);
	void addEdge(Edge edge);
	void removeEdge(Edge edge);
	Edge getEdge(Node node1, Node node2);
	Node getNode(int nodeID);
	boolean containsNode(Node node);
	boolean containsEdge(Edge edge);
}
