/**
 * 
 * To be used by the simulator.
 */
public class LawnMowingApplication {

	public static void main(String[] args) {
	int N = Integer.parseInt(System.console().readLine());
	
	String [] yard = new String[N];
				    for (int i=0; i < N; i++)
				    {
				    	yard[i] = System.console().readLine();
				    }
				    
				  int  turnCost = Integer.parseInt(System.console().readLine());
				  int  forwardCost = Integer.parseInt(System.console().readLine());
				  int  slopeCost = Integer.parseInt(System.console().readLine());
				   int  startCol = Integer.parseInt(System.console().readLine());
				   int startRow = Integer.parseInt(System.console().readLine());

				    String ret = getMoves(yard, turnCost, forwardCost, slopeCost, startCol, startRow);
				    System.out.println(ret);
				   // flush(stdout)	
	}

	public static String getMoves(String[] yard, int turnCost, int forwardCost, int slopeCost, int startCol,
			int startRow) {
		// TODO Auto-generated method stub
		return null;
	}

}
