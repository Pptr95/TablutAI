package it.unibo.ai.didattica.competition.tablut.client.petru;

import java.util.List;



import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.client.petru.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.client.petru.State.Turn;


public class TablutGame implements Game<StateTablut, XYWho, String> {

	private StateTablut initialState;
	
	public TablutGame() {
		this.initialState = new StateTablut();
	}
	@Override
	public List<XYWho> getActions(StateTablut state) {
		return state.getAllLegalMoves();
	}

	@Override
	public StateTablut getInitialState() {
		return this.initialState;
	}


	public StateTablut getState() {
		return this.initialState;
	}
	
	public void setState(StateTablut newState) {
		 this.initialState = newState;
	}

	@Override
	public String getPlayer(StateTablut state) {
		return state.getTurn().toString();
	}

	@Override
	public String[] getPlayers() {
		return new String[] {Turn.WHITE.toString(), Turn.BLACK.toString()};
	}

	@Override
	public double getUtility(StateTablut state, String player) {
		double result = state.getUtility();
		if (result != -1) {
			if (player == Pawn.BLACK.toString()) {
				result = 1 - result;
			}
		} else {
			throw new IllegalArgumentException("State is not terminal.");
		}
		return result;
	}

	@Override
	public boolean isTerminal(StateTablut state) {
		return state.getUtility() != -1;
	}

	@Override
	public StateTablut getResult(StateTablut state, XYWho action) {
		StateTablut result = state.clone();
		Pawn[][] b = result.getBoard();
		b[action.getX()][action.getY()] = this.getState().getBoard()[action.getWho()[0]][action.getWho()[1]];
		b[action.getWho()[0]][action.getWho()[1]] = Pawn.EMPTY;
		this.getState().setBoard(b);
		return result;
	}
}