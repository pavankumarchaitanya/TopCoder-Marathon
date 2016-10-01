import java.util.HashMap;
import java.util.Map;

public class GraphImpl implements Graph {

	//--------------------------------
	//|<Node 1, <Node 2, edgeWeight>>|
	//--------------------------------
	//The data structure represents an edge connecting Node 1 and Node 2 of edgeWeight
	Map<Node,Map<Node, Integer>> nodeMap = new HashMap<>();
	
	@Override
	public void addNode(Node node) {
		Map<Node,Integer> map;
		if(!this.containsNode(node)){
			map = new HashMap<>();
			nodeMap.put(node, map);
		}
	}

	@Override
	public void removeNode(Node node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addEdge(Edge edge) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeEdge(Edge edge) {
		// TODO Auto-generated method stub

	}

	@Override
	public Edge getEdge(Node node1, Node node2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getNode(int nodeID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsNode(Node node) {
		return nodeMap.containsKey(node);
	}

	@Override
	public boolean containsEdge(Edge edge) {
		
		return false;
	}

}
