package com.LawnMowing;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * To be used by the simulator.
 */
public class LawnMowingApplication {

	public static void main(String[] args) throws JsonProcessingException {
	int N = Integer.parseInt(System.console().readLine());
	
	String [] yard = new String[N];
				    for (int i=0; i < N; i++)
				    {
				    	yard[i] = System.console().readLine();
				    }
				    ObjectMapper om = new ObjectMapper();
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
			int startRow) throws JsonProcessingException {
		ObjectMapper om = new ObjectMapper();
		
		System.out.println("yard :"  +om.writeValueAsString(yard));
		System.out.println("turnCost"+turnCost);

		System.out.println("forwardCost" + forwardCost);

		System.out.println("slopeCost" +slopeCost);

		System.out.println("startCol" + startCol);

		System.out.println("startRow"+ startRow);

		System.out.println();

		// TODO Auto-generated method stub
		return "SSS";
	}

}
