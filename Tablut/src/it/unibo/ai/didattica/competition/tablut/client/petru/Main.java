package it.unibo.ai.didattica.competition.tablut.client.petru;

import it.unibo.ai.didattica.competition.tablut.client.ab.AlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.client.petru.StateTablut.Pawn;
import it.unibo.ai.didattica.competition.tablut.client.petru.StateTablut.Turn;

public class Main {
	
	public static void main(String[] args) {
		
		int depth = 4;
		TablutGame st = new TablutGame(depth);
		StateTablut c = st.getInitialState();
		AlphaBetaSearch<StateTablut, XYWho, Turn> ab = new AlphaBetaSearch<StateTablut, XYWho, Turn> (st, depth);
		
		XYWho a = ab.makeDecision(c);
		System.out.println(ab.getMetrics());
//		System.out.println(a.getX() + " " + a.getY() + " | " + a.getWho()[0] + ", " + a.getWho()[1]);
//		c.setPawn(a.getX(), a.getY(), Pawn.WHITE);
//		c.setPawn(a.getWho()[0], a.getWho()[1], Pawn.EMPTY);
//		printBoard(c.getBoard());
//		
//		c.setTurn(Turn.BLACK);
//		
//		XYWho a2 = ab.makeDecision(c);
//		System.out.println(a2.getX() + " " + a2.getY() + " | " + a2.getWho()[0] + ", " + a2.getWho()[1]);
//		c.setPawn(a2.getX(), a2.getY(), Pawn.BLACK);
//		c.setPawn(a2.getWho()[0], a2.getWho()[1], Pawn.EMPTY);
//		printBoard(c.getBoard());
//		
//		c.setTurn(Turn.WHITE);
//		
//		XYWho a22 = ab.makeDecision(c);
//		c.setPawn(a22.getX(), a22.getY(), Pawn.WHITE);
//		c.setPawn(a22.getWho()[0], a22.getWho()[1], Pawn.EMPTY);
//		printBoard(c.getBoard());
	}
	static void printBoard(Pawn[][] c) {
		for(int i=0; i < 9; i++) {
			for(int j=0; j < 9; j++) {
				System.out.print(c[i][j]+ "");
			}
			System.out.println("");
		}
	}
	
}
