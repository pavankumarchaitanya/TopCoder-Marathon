import java.util.HashMap;
import java.util.Map;

public class GraphImpl implements Graph {

	// --------------------------------
	// |<Node 1, <Node 2, edgeWeight>>|
	// --------------------------------
	// The data structure represents an edge connecting Node 1 and Node 2 of
	// edgeWeight
	Map<Node, Map<Node, Integer>> nodeMap = new HashMap<>();

	@Override
	public void addNode(Node node) {
		Map<Node, Integer> map;
		if (!this.containsNode(node)) {
			map = new HashMap<>();
			nodeMap.put(node, map);
		}
	}

	@Override
	public void removeNode(Node node) {

		for (Node tempNode : nodeMap.keySet()) {
			{
				Map<Node, Integer> map = nodeMap.get(tempNode);
				map.remove(node);
			}

		}

		nodeMap.remove(node);
	}

	@Override
	public void addEdge(Edge edge) {
		Node fromNode = edge.getFromNode();
		Node toNode = edge.getToNode();

		addNode(fromNode);
		nodeMap.get(fromNode).put(toNode, edge.getWeight());

	}

	@Override
	public void removeEdge(Edge edge) {
		Node fromNode = edge.getFromNode();
		Node toNode = edge.getToNode();
		nodeMap.get(fromNode).remove(toNode);
	}

	@Override
	public Edge getEdge(Node fromNode, Node toNode) {

		if (nodeMap.containsKey(fromNode)) {
			Map<Node, Integer> tempMap = nodeMap.get(fromNode);
			if (tempMap != null) {
				Edge tempEdge = new Edge();
				tempEdge.setWeight(tempMap.get(toNode));
				tempEdge.setFromNode(fromNode);
				tempEdge.setToNode(toNode);
				return tempEdge;
			}
		}
		return null;
	}

	@Override
	public Node getNode(int nodeID) {
		Node tempNode = new Node();
		tempNode.setId(nodeID);
		if (nodeMap.containsKey(tempNode)) {
			nodeMap.get(tempNode);
		}
		return null;

	}

	@Override
	public boolean containsNode(Node node) {
		return nodeMap.containsKey(node);
	}

	@Override
	public boolean containsEdge(Edge edge) {
		Node fromNode = edge.getFromNode();
		Node toNode = edge.getToNode();
		if (nodeMap.get(fromNode) != null) {
			return nodeMap.get(fromNode).containsKey(toNode);
		} else
			return false;
	}

}
