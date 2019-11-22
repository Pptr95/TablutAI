package it.unibo.ai.didattica.competition.tablut.client.petru;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;
import java.util.Random;




public class StateTablut implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public enum Turn {
		WHITE("W"), BLACK("B"), WHITEWIN("WW"), BLACKWIN("BW"), DRAW("D");
		private final String turn;

		private Turn(String s) {
			this.turn = s;
		}

		public boolean equalsTurn(String otherName) {
			return (otherName == null) ? false : this.turn.equals(otherName);
		}

		public String toString() {
			return this.turn;
		}
	}

	public enum Pawn {
		EMPTY(" "), WHITE("W"), BLACK("B"), THRONE("T"), KING("K");
		private final String pawn;

		private Pawn(String s) {
			this.pawn = s;
		}

		public boolean equalsPawn(String otherPawn) {
			return (otherPawn == null) ? false : this.pawn.equals(otherPawn);
		}

		public String toString() {
			return this.pawn;
		}

	}
	
	public enum Area {
		NORMAL("N"), CASTLE("K"), CAMPS("C"), ESCAPES("E");
		private final String area;

		private Area(String a) {
			this.area = a;
		}

		public boolean equalsArea(String otherArea) {
			return (otherArea == null) ? false : this.area.equals(otherArea);
		}

		public String toString() {
			return this.area;
		}
	}

	public static final int WIDTH = 9; 
	public static final int HEIGHT = 9; 
	public static final int KING_POSITION = 4; // this 4 is meant as the initial king position which is [4, 4]
	
	private Pawn board[][];
	private Area boardArea[][];
	private Turn turn;
	public int depth;
	private double utility = -1;
	
	
	public StateTablut() {
		this.setBoard(new Pawn[StateTablut.WIDTH][StateTablut.HEIGHT]);
		this.setBoardArea(new Area[StateTablut.WIDTH][StateTablut.WIDTH]);
		this.initBoard();
		this.depth = 0;
		this.setTurn(Turn.WHITE);
	}
	
	
	public void checkGameStatus() {
		if(this.getUtility() == -1) {
			//check if WHITE won
			if(this.hasWhiteWon()) {
				this.utility = 1; // 1 means that white won
			} else
			//check if BLACK won 
			if(this.hasBlackWon()) {
				this.utility = 0; // 0 means that black won
			} else
			//check if DRAW
			if(this.isDraw()) {
				this.utility = -1; // // 0.5 means that black won
			}
		}
	}
	
	private boolean hasWhiteWon() {
		// white won when the king is on an escape area
		for(int i=0; i < this.getBoard().length; i++) {
			for(int j=0; j < this.getBoard().length; j++) {
				if(this.getPawn(i, j).equals(Pawn.KING) && this.getArea(i, j).equals(Area.ESCAPES)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean hasBlackWon() {
		// case where the king is in the castle and is surrounded by 4 blacks
		for(int i=0; i < this.getBoard().length; i++) {
			for(int j=0; j < this.getBoard().length; j++) {
				if(this.getPawn(i, j).equals(Pawn.KING) && this.getArea(i, j).equals(Area.CASTLE)) {
					if(this.getPawn(i, j - 1).equals(Pawn.BLACK) && this.getPawn(i, j + 1).equals(Pawn.BLACK)
							&& this.getPawn(i - 1, j).equals(Pawn.BLACK) && this.getPawn(i + 1, j).equals(Pawn.BLACK)) {
						return true;
					}
				}
			}
		}
		// case if the king is adjacent to the Castle, it must be surround on all the three free sides
		if(this.getPawn(StateTablut.KING_POSITION, StateTablut.KING_POSITION - 1).equals(Pawn.KING)) {
			if(this.getPawn(StateTablut.KING_POSITION, StateTablut.KING_POSITION - 2).equals(Pawn.BLACK)
					&& this.getPawn(StateTablut.KING_POSITION - 1, StateTablut.KING_POSITION - 1).equals(Pawn.BLACK)
					&& this.getPawn(StateTablut.KING_POSITION + 1, StateTablut.KING_POSITION - 1).equals(Pawn.BLACK)) {
				return true;
			}
			
		} else if(this.getPawn(StateTablut.KING_POSITION, StateTablut.KING_POSITION + 1).equals(Pawn.KING)) {
			if(this.getPawn(StateTablut.KING_POSITION, StateTablut.KING_POSITION + 2).equals(Pawn.BLACK)
					&& this.getPawn(StateTablut.KING_POSITION + 1, StateTablut.KING_POSITION + 1).equals(Pawn.BLACK)
					&& this.getPawn(StateTablut.KING_POSITION - 1, StateTablut.KING_POSITION + 1).equals(Pawn.BLACK)) {
				return true;
			}
		} else if(this.getPawn(StateTablut.KING_POSITION - 1, StateTablut.KING_POSITION).equals(Pawn.KING)) {
			if(this.getPawn(StateTablut.KING_POSITION - 1, StateTablut.KING_POSITION - 1).equals(Pawn.BLACK)
					&& this.getPawn(StateTablut.KING_POSITION - 2, StateTablut.KING_POSITION).equals(Pawn.BLACK)
					&& this.getPawn(StateTablut.KING_POSITION - 1, StateTablut.KING_POSITION + 1).equals(Pawn.BLACK)) {
				return true;
			}
			
		} else if(this.getPawn(StateTablut.KING_POSITION + 1, StateTablut.KING_POSITION).equals(Pawn.KING)) {
			if(this.getPawn(StateTablut.KING_POSITION + 1, StateTablut.KING_POSITION + 1).equals(Pawn.BLACK)
					&& this.getPawn(StateTablut.KING_POSITION + 2, StateTablut.KING_POSITION).equals(Pawn.BLACK)
					&& this.getPawn(StateTablut.KING_POSITION + 1, StateTablut.KING_POSITION - 1).equals(Pawn.BLACK)) {
				return true;
			}
		}
		
		// case if the king is adjacent to a camp, it is sufficient to surround it with a checker on the opposite side of the camp.
		for(int i=0; i < this.getBoard().length; i++) { // i=1 and this.getBoard().length - 1
			for(int j=0; j < this.getBoard().length; j++) { // j=1 and this.getBoard().length - 1
				if(this.getPawn(i, j).equals(Pawn.KING)) {
					// normal case
					if(this.getArea(i + 1, j).equals(Area.CAMPS) && this.getPawn(i - 1, j).equals(Pawn.BLACK)) {
						return true;
					} else if(this.getArea(i - 1, j).equals(Area.CAMPS) && this.getPawn(i + 1, j).equals(Pawn.BLACK)) {
						return true;
					} else if(this.getArea(i, j - 1).equals(Area.CAMPS) && this.getPawn(i, j + 1).equals(Pawn.BLACK)) {
						return true;
					} else if(this.getArea(i, j + 1).equals(Area.CAMPS) && this.getPawn(i, j - 1).equals(Pawn.BLACK)) {
						return true;
					}
					
					// angle case
					if(this.getArea(i - 1, j).equals(Area.CAMPS) && this.getArea(i, j + 1).equals(Area.CAMPS)
							&& (this.getPawn(i + 1, j).equals(Pawn.BLACK) || this.getPawn(i, j - 1).equals(Pawn.BLACK))) {
						return true;
					} else if(this.getArea(i - 1, j).equals(Area.CAMPS) && this.getArea(i, j - 1).equals(Area.CAMPS)
							&& (this.getPawn(i + 1, j).equals(Pawn.BLACK) || this.getPawn(i, j + 1).equals(Pawn.BLACK))) {
						return true;
					} else if(this.getArea(i, j + 1).equals(Area.CAMPS) && this.getArea(i + 1, j).equals(Area.CAMPS)
							&& (this.getPawn(i - 1, j).equals(Pawn.BLACK) || this.getPawn(i, j - 1).equals(Pawn.BLACK))) {
						return true;
					} else if(this.getArea(i - 1, j).equals(Area.CAMPS) && this.getArea(i, j + 1).equals(Area.CAMPS)
							&& (this.getPawn(i, j - 1).equals(Pawn.BLACK) || this.getPawn(i + 1, j).equals(Pawn.BLACK))) {
						return true;
					} else if(this.getArea(i + 1, j).equals(Area.CAMPS) && this.getArea(i, j - 1).equals(Area.CAMPS)
							&& (this.getPawn(i - 1, j).equals(Pawn.BLACK) || this.getPawn(i, j + 1).equals(Pawn.BLACK))) {
						return true;
					} else if(this.getArea(i + 1, j).equals(Area.CAMPS) && this.getArea(i, j + 1).equals(Area.CAMPS)
							&& (this.getPawn(i - 1, j).equals(Pawn.BLACK) || this.getPawn(i, j - 1).equals(Pawn.BLACK))) {
						return true;
					} else if(this.getArea(i, j - 1).equals(Area.CAMPS) && this.getArea(i - 1, j).equals(Area.CAMPS)
							&& (this.getPawn(i + 1, j).equals(Pawn.BLACK) || this.getPawn(i, j + 1).equals(Pawn.BLACK))) {
						return true;
					} else if(this.getArea(i, j - 1).equals(Area.CAMPS) && this.getArea(i + 1, j).equals(Area.CAMPS)
							&& (this.getPawn(i - 1, j).equals(Pawn.BLACK) || this.getPawn(i, j + 1).equals(Pawn.BLACK))) {
						return true;
					}
				}
			}
		}
		// return false whether any of the previous conditions aren't satisfied
		return false;
	}

	private boolean isDraw() {
		return false;
	}
	
	
	public StateTablut(Pawn[][] board, Turn playerToMove) {
		this.setBoard(board);
		this.setTurn(playerToMove);
	}
	
	public Turn getPlayerToMove() {
		return this.getTurn();
	}
	
	public double getUtility() {
		return this.utility;
	}
	
	public void setUtility(double u) {
		this.utility = u;
	}
	
	public List<XYWho> getAllLegalMoves() {
		Pawn[][] currentBoardState = this.getBoard();
		// all possible moves for white
		if(this.getTurn().equals(Turn.WHITE)) {
			List<XYWho> whiteLegalMoves = new ArrayList<>();
			List<XYWho> whitePositions = new ArrayList<>();
			XYWho buf;
			for (int i = 0; i < currentBoardState.length; i++) {
				for (int j = 0; j < currentBoardState.length; j++) {
					if (this.getPawn(i, j).equalsPawn(StateTablut.Pawn.WHITE.toString()) || this.getPawn(i, j).equalsPawn(StateTablut.Pawn.KING.toString()))  {
						buf = new XYWho(i, j, new int[]{i, j}, false);
						whitePositions.add(buf);
					}
				}
			}
			// for each (i, j) in white position, try every possible move
			for (XYWho whitePawn : whitePositions) {
				// move each pawn vertically
				for (int j = 0; j < currentBoardState.length; j++) {
					// (x, y - j) UP
					if(((whitePawn.getY() - j) >= 0) && this.getPawn(whitePawn.getX(), whitePawn.getY() - j) == Pawn.EMPTY && (this.getArea(whitePawn.getX(), whitePawn.getY() - j) != Area.CAMPS) && (this.getArea(whitePawn.getX(), whitePawn.getY() - j) != Area.CASTLE)) {
						int howManyEmptyPawns = 0;
						for (int f = whitePawn.getY() - 1; f > whitePawn.getY() - j; f--) {
							if(this.getPawn(whitePawn.getX(), f) != Pawn.EMPTY || this.getArea(whitePawn.getX(), f) == Area.CAMPS || this.getArea(whitePawn.getX(), f) == Area.CASTLE) {
								howManyEmptyPawns++;
							}
						}
						if(howManyEmptyPawns == 0) {
							whiteLegalMoves.add(new XYWho(whitePawn.getX(), whitePawn.getY() - j, new int[]{whitePawn.getX(), whitePawn.getY()}, false));
						}
					}
					// (x, y + j) DOWN
					if(((whitePawn.getY() + j) < currentBoardState.length) && this.getPawn(whitePawn.getX(), whitePawn.getY() + j) == Pawn.EMPTY && (this.getArea(whitePawn.getX(), whitePawn.getY() + j) != Area.CAMPS) && (this.getArea(whitePawn.getX(), whitePawn.getY() + j) != Area.CASTLE)) {
						int howManyEmptyPawns = 0;
						for (int f = whitePawn.getY() + 1; f < whitePawn.getY() + j; f++) {
							if(this.getPawn(whitePawn.getX(), f) != Pawn.EMPTY || this.getArea(whitePawn.getX(), f) == Area.CAMPS || this.getArea(whitePawn.getX(), f) == Area.CASTLE) {
								howManyEmptyPawns++;
							}
						}
						if(howManyEmptyPawns == 0) {
							whiteLegalMoves.add(new XYWho(whitePawn.getX(), whitePawn.getY() + j, new int[]{whitePawn.getX(), whitePawn.getY()}, false));
						}
					}
				}
				// move each pawn horizontally
				for (int i = 0; i < currentBoardState.length; i++) {
					// (x - i, y) LEFT
					if(((whitePawn.getX() - i) >= 0) && this.getPawn(whitePawn.getX() - i, whitePawn.getY()) == Pawn.EMPTY && (this.getArea(whitePawn.getX() - i, whitePawn.getY()) != Area.CAMPS) && (this.getArea(whitePawn.getX() - i, whitePawn.getY()) != Area.CASTLE)) {
						int howManyEmptyPawns = 0;
						for (int f = whitePawn.getX() - 1; f > whitePawn.getX() - i; f--) {
							if(this.getPawn(f, whitePawn.getY()) != Pawn.EMPTY || this.getArea(f, whitePawn.getY()) == Area.CAMPS || this.getArea(f, whitePawn.getY()) == Area.CASTLE) {
								howManyEmptyPawns++;
							}
						}
						if(howManyEmptyPawns == 0) {
							whiteLegalMoves.add(new XYWho(whitePawn.getX() - i, whitePawn.getY(), new int[]{whitePawn.getX(), whitePawn.getY()}, false));
						}
					}
					// (x + i, y) RIGHT
					if(((whitePawn.getX() + i) < currentBoardState.length) && this.getPawn(whitePawn.getX() + i, whitePawn.getY()) == Pawn.EMPTY && (this.getArea(whitePawn.getX() + i, whitePawn.getY()) != Area.CAMPS) && (this.getArea(whitePawn.getX() + i, whitePawn.getY()) != Area.CASTLE)) {
						int howManyEmptyPawns = 0;
						for (int f = whitePawn.getX() + 1; f < whitePawn.getX() + i; f++) {
							if(this.getPawn(f, whitePawn.getY()) != Pawn.EMPTY || this.getArea(f, whitePawn.getY()) == Area.CAMPS || this.getArea(f, whitePawn.getY()) == Area.CASTLE) {
								howManyEmptyPawns++;
							}
						}
						if(howManyEmptyPawns == 0) {
							whiteLegalMoves.add(new XYWho(whitePawn.getX() + i, whitePawn.getY(), new int[]{whitePawn.getX(), whitePawn.getY()}, false));
						}
					}
				}
					
			}
			// add all possible moves for the king. If he leaves the castle, he cannot enter anymore
//			int[] king_position = this.getKingPosition();
//			for (int j = 0; j < currentBoardState.length; j++) {
//				// (x, y - j) UP
//				if(((king_position[1] - j) >= 0) && this.getPawn(king_position[0], king_position[1] - j) == Pawn.EMPTY && (this.getArea(king_position[0], king_position[1] - j) != Area.CAMPS) && (king_position[0] != StateTablut.KING_POSITION || (king_position[1] - j) != StateTablut.KING_POSITION)) {
//					whiteLegalMoves.add(new XYWho(king_position[0], king_position[1] - j, new int[]{king_position[0], king_position[1]}, false));
//				}
//				// (x, y + j) DOWN
//				if(((king_position[1] + j) < currentBoardState.length) && this.getPawn(king_position[0], king_position[1] + j) == Pawn.EMPTY && (this.getArea(king_position[0], king_position[1] + j) != Area.CAMPS) && (king_position[0] != StateTablut.KING_POSITION | (king_position[1] + j) != StateTablut.KING_POSITION)) {
//					whiteLegalMoves.add(new XYWho(king_position[0], king_position[1] + j, new int[]{king_position[0], king_position[1]}, false));
//				}
//			}
//			for (int i = 0; i < currentBoardState.length; i++) {
//				// (x - i, y) LEFT
//				if(((king_position[0] - i) >= 0) && this.getPawn(king_position[0] - i, king_position[1]) == Pawn.EMPTY && (this.getArea(king_position[0] - i, king_position[1]) != Area.CAMPS) && ((king_position[0] - i) != StateTablut.KING_POSITION || (king_position[1]) != StateTablut.KING_POSITION)) {
//					whiteLegalMoves.add(new XYWho(king_position[0] - i, king_position[1], new int[]{king_position[0], king_position[1]}, false));
//				}
//				// (x + i, y) RIGHT
//				if(((king_position[0] + i) < currentBoardState.length) && this.getPawn(king_position[0] + i, king_position[1]) == Pawn.EMPTY && (this.getArea(king_position[0] + i, king_position[1]) != Area.CAMPS) && ((king_position[0] + i) != StateTablut.KING_POSITION || (king_position[1]) != StateTablut.KING_POSITION)) {
//					whiteLegalMoves.add(new XYWho(king_position[0] + i, king_position[1], new int[]{king_position[0], king_position[1]}, false));
//				}
//			}
			return whiteLegalMoves;
			
			
		} else {
			// all possible moves for black
			List<XYWho> blackLegalMoves = new ArrayList<>();
			List<XYWho> blackPositions = new ArrayList<>();
			XYWho buf;
			for (int i = 0; i < currentBoardState.length; i++) {
				for (int j = 0; j < currentBoardState.length; j++) {
					if (this.getPawn(i, j).equalsPawn(StateTablut.Pawn.BLACK.toString()))  {
						if(this.getArea(i, j).equalsArea(Area.CAMPS.toString())) { // if a black is still in a camp, he can move into it
							buf = new XYWho(i, j, new int[]{i, j}, false);
							blackPositions.add(buf);
						} else if (this.getArea(i, j).equalsArea(Area.CAMPS.toString()) || this.getArea(i, j).equalsArea(Area.NORMAL.toString()) || this.getArea(i, j).equalsArea(Area.ESCAPES.toString())) {
							buf = new XYWho(i, j, new int[]{i, j}, true); // if a black is no more in a camp, he cannot enter in any camp anymore
							blackPositions.add(buf);
						}
					}
				}
			}
			// for each (i, j) in black position, try every possible move
			for (XYWho blackPawn : blackPositions) {
				// move each pawn vertically
				for (int j = 0; j < currentBoardState.length; j++) {
					// (x, y - j) UP
					if(((blackPawn.getY() - j) >= 0) && this.getPawn(blackPawn.getX(), blackPawn.getY() - j) == Pawn.EMPTY && (this.getArea(blackPawn.getX(), blackPawn.getY() - j) != Area.CASTLE)) {
						int howManyEmptyPawns = 0;
						for (int f = blackPawn.getY() - 1; f > blackPawn.getY() - j; f--) {
							if(this.getPawn(blackPawn.getX(), f) != Pawn.EMPTY || (this.getArea(blackPawn.getX(), f) == Area.CAMPS && blackPawn.hasLeftTheCamp()) || this.getArea(blackPawn.getX(), f) == Area.CASTLE) {
								howManyEmptyPawns++;
							}
						}
						if(howManyEmptyPawns == 0) {
							// if a black is no more in a camp, he cannot enter in any camp anymore
							if((blackPawn.hasLeftTheCamp() && (this.getArea(blackPawn.getX(), blackPawn.getY() - j) != Area.CAMPS)) || (!blackPawn.hasLeftTheCamp())) {
								blackLegalMoves.add(new XYWho(blackPawn.getX(), blackPawn.getY() - j, new int[]{blackPawn.getX(), blackPawn.getY()}, blackPawn.hasLeftTheCamp()));
							}
						}
					}
					// (x, y + j) DOWN
					if(((blackPawn.getY() + j) < currentBoardState.length) && this.getPawn(blackPawn.getX(), blackPawn.getY() + j) == Pawn.EMPTY && (this.getArea(blackPawn.getX(), blackPawn.getY() + j) != Area.CASTLE)) {
						int howManyEmptyPawns = 0;
						for (int f = blackPawn.getY() + 1; f < blackPawn.getY() + j; f++) {
							if(this.getPawn(blackPawn.getX(), f) != Pawn.EMPTY || (this.getArea(blackPawn.getX(), f) == Area.CAMPS && blackPawn.hasLeftTheCamp()) || this.getArea(blackPawn.getX(), f) == Area.CASTLE) {
								howManyEmptyPawns++;
							}
						}
						if(howManyEmptyPawns == 0) {
							if((blackPawn.hasLeftTheCamp() && (this.getArea(blackPawn.getX(), blackPawn.getY() + j) != Area.CAMPS)) || (!blackPawn.hasLeftTheCamp())) {
								blackLegalMoves.add(new XYWho(blackPawn.getX(), blackPawn.getY() + j, new int[]{blackPawn.getX(), blackPawn.getY()}, blackPawn.hasLeftTheCamp()));
							}
						}
					}
				}
				// move each pawn horizontally
				for (int i = 0; i < currentBoardState.length; i++) {
					// (x - i, y) LEFT
					if(((blackPawn.getX() - i) >= 0) && this.getPawn(blackPawn.getX() - i, blackPawn.getY()) == Pawn.EMPTY && (this.getArea(blackPawn.getX() - i, blackPawn.getY()) != Area.CASTLE)) {
						int howManyEmptyPawns = 0;
						for (int f = blackPawn.getX() - 1; f > blackPawn.getX() - i; f--) {
							if(this.getPawn(f, blackPawn.getY()) != Pawn.EMPTY || (this.getArea(f, blackPawn.getY()) == Area.CAMPS && blackPawn.hasLeftTheCamp()) || this.getArea(f, blackPawn.getY()) == Area.CASTLE) {
								howManyEmptyPawns++;
							}
						}
						if(howManyEmptyPawns == 0) {
							if((blackPawn.hasLeftTheCamp() && (this.getArea(blackPawn.getX() - i, blackPawn.getY()) != Area.CAMPS)) || (!blackPawn.hasLeftTheCamp())) {
								blackLegalMoves.add(new XYWho(blackPawn.getX() - i, blackPawn.getY(), new int[]{blackPawn.getX(), blackPawn.getY()}, blackPawn.hasLeftTheCamp()));
							}
						}
						
					}
					// (x + i, y) RIGHT
					if(((blackPawn.getX() + i) < currentBoardState.length) && this.getPawn(blackPawn.getX() + i, blackPawn.getY()) == Pawn.EMPTY && (this.getArea(blackPawn.getX() + i, blackPawn.getY()) != Area.CASTLE)) {
						int howManyEmptyPawns = 0;
						for (int f = blackPawn.getX() + 1; f < blackPawn.getX() + i; f++) {
							if(this.getPawn(f, blackPawn.getY()) != Pawn.EMPTY || (this.getArea(f, blackPawn.getY()) == Area.CAMPS && blackPawn.hasLeftTheCamp()) || this.getArea(f, blackPawn.getY()) == Area.CASTLE) {
								howManyEmptyPawns++;
							}
						}
						if(howManyEmptyPawns == 0) {
							if((blackPawn.hasLeftTheCamp() && (this.getArea(blackPawn.getX() + i, blackPawn.getY()) != Area.CAMPS)) || (!blackPawn.hasLeftTheCamp())) {
								blackLegalMoves.add(new XYWho(blackPawn.getX() + i, blackPawn.getY(), new int[]{blackPawn.getX(), blackPawn.getY()}, blackPawn.hasLeftTheCamp()));
							}
						}
					}
				}
					
			}
			return blackLegalMoves;
		}
			
	} 
	//TODO: check whether for each white and black they do their legal moves (check it before add contraints and when I will add constraints)
	public static void main(String[] args) {
		StateTablut s = new StateTablut();
		List<XYWho> white = s.getAllLegalMoves();
		for(int i = 0; i < white.size(); i++) {
			//System.out.println("who: (" + white.get(i).getWho()[0] + ", " + white.get(i).getWho()[1] +") x: "+white.get(i).getX()+ " y: " + white.get(i).getY());
		}
		s.printBoard();
		//s.printBoardArea();
		//System.out.println(white.size());
		
	}
	
	
	
	private void initBoard() {
		// initialize pawns on board
		for (int i = 0; i < this.getBoard().length; i++) {
			for (int j = 0; j < this.getBoard().length; j++) {
				this.setPawn(i,  j, Pawn.EMPTY);
			}
		}
		this.setPawn(StateTablut.KING_POSITION,  StateTablut.KING_POSITION, Pawn.THRONE);
		this.setPawn(StateTablut.KING_POSITION,  StateTablut.KING_POSITION, Pawn.KING);
		
		this.setPawn(2,  4, Pawn.WHITE);
		this.setPawn(3,  4, Pawn.WHITE);
		this.setPawn(5,  4, Pawn.WHITE);
		this.setPawn(6,  4, Pawn.WHITE);
		this.setPawn(4,  2, Pawn.WHITE);
		this.setPawn(4,  3, Pawn.WHITE);
		this.setPawn(4,  5, Pawn.WHITE);
		this.setPawn(4,  6, Pawn.WHITE);
		
		
		this.setPawn(0,  3, Pawn.BLACK);
		this.setPawn(0,  4, Pawn.BLACK);
		this.setPawn(0,  5, Pawn.BLACK);
		this.setPawn(1,  4, Pawn.BLACK);
		this.setPawn(8,  3, Pawn.BLACK);
		this.setPawn(8,  4, Pawn.BLACK);
		this.setPawn(8,  5, Pawn.BLACK);
		this.setPawn(7,  4, Pawn.BLACK);
		this.setPawn(3,  0, Pawn.BLACK);
		this.setPawn(4,  0, Pawn.BLACK);
		this.setPawn(5,  0, Pawn.BLACK);
		this.setPawn(4,  1, Pawn.BLACK);
		this.setPawn(3,  8, Pawn.BLACK);
		this.setPawn(4,  8, Pawn.BLACK);
		this.setPawn(5,  8, Pawn.BLACK);
		this.setPawn(4,  7, Pawn.BLACK);
		
		// initialize area on board
		for (int i = 0; i < this.getBoardArea().length; i++) {
			for (int j = 0; j < this.getBoardArea().length; j++) {
				this.setArea(i, j, Area.NORMAL);
			}
		}

		this.setArea(4, 4, Area.CASTLE);
		
		this.setArea(0, 0, Area.ESCAPES);
		this.setArea(0, 1, Area.ESCAPES);
		this.setArea(0, 2, Area.ESCAPES);
		this.setArea(0, 6, Area.ESCAPES);
		this.setArea(0, 7, Area.ESCAPES);
		this.setArea(0, 8, Area.ESCAPES);
		this.setArea(1, 0, Area.ESCAPES);
		this.setArea(2, 0, Area.ESCAPES);
		this.setArea(6, 0, Area.ESCAPES);
		this.setArea(7, 0, Area.ESCAPES);
		this.setArea(8, 0, Area.ESCAPES);
		this.setArea(8, 1, Area.ESCAPES);
		this.setArea(8, 2, Area.ESCAPES);
		this.setArea(8, 6, Area.ESCAPES);
		this.setArea(8, 7, Area.ESCAPES);
		this.setArea(8, 8, Area.ESCAPES);
		this.setArea(1, 8, Area.ESCAPES);
		this.setArea(2, 8, Area.ESCAPES);
		this.setArea(6, 8, Area.ESCAPES);
		this.setArea(7, 8, Area.ESCAPES);

		this.setArea(0, 3, Area.CAMPS);
		this.setArea(0, 4, Area.CAMPS);
		this.setArea(0, 5, Area.CAMPS);
		this.setArea(1, 4, Area.CAMPS);
		this.setArea(8, 3, Area.CAMPS);
		this.setArea(8, 4, Area.CAMPS);
		this.setArea(8, 5, Area.CAMPS);
		this.setArea(7, 4, Area.CAMPS);
		this.setArea(3, 0, Area.CAMPS);
		this.setArea(4, 0, Area.CAMPS);
		this.setArea(5, 0, Area.CAMPS);
		this.setArea(4, 1, Area.CAMPS);
		this.setArea(3, 8, Area.CAMPS);
		this.setArea(4, 8, Area.CAMPS);
		this.setArea(5, 8, Area.CAMPS);
		this.setArea(4, 7, Area.CAMPS);
	}
	

	

	public StateTablut clone() {
		StateTablut result = new StateTablut();

		Pawn oldboard[][] = this.getBoard();
		Pawn newboard[][] = result.getBoard();

		for (int i = 0; i < this.getBoard().length; i++) {
			for (int j = 0; j < this.getBoard().length; j++) {
				newboard[i][j] = oldboard[i][j];
			}
		}

		result.setBoard(newboard);
		result.setTurn(this.getTurn());
		return result;
	}
	

	
	public Pawn[][] getBoard() {
		return this.board;
	}

	public void setBoard(Pawn[][] board) {
		this.board = board;
	}
	
	public Area[][] getBoardArea() {
		return this.boardArea;
	}
	
	public void setBoardArea(Area[][] boardArea) {
		this.boardArea = boardArea;
	}
	
	public Turn getTurn() {
		return this.turn;
	}

	public void setTurn(Turn turn) {
		this.turn = turn;
	}

	public Pawn getPawn(int row, int column) {
		return this.board[row][column];
	}

	public void setPawn(int row, int column, Pawn pawn) {
		this.board[row][column] = pawn;
	}
	
	public Area getArea(int row, int column) {
		return this.boardArea[row][column];
	}

	public void setArea(int row, int column, Area area) {
		this.boardArea[row][column] = area;
	}
	
	
	public void removePawn(int row, int column) {
		this.board[row][column] = Pawn.EMPTY;
	}

	public boolean hasTheKingMoved() {
		return this.getKingPosition()[0] == KING_POSITION;
	}
	
	public int[] getKingPosition() {
		for (int i = 0; i < this.getBoard().length; i++) {
			for (int j = 0; j < this.getBoard().length; j++) {
				if(this.getPawn(i, j) == Pawn.KING) {
					return new int[] {i,j};
				}
			}
		}
		return null;
	}
	
	/**
	 * Counts the number of checkers of a specific color on the board. Note: the king is not taken into account for white, it must be checked separately
	 * @param color The color of the checker that will be counted. It is possible also to use EMPTY to count empty cells.
	 * @return The number of cells of the board that contains a checker of that color.
	 */
	public int getNumberOf(Pawn color) {
		int count = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == color)
					count++;
			}
		}
		return count;
	}
	
	public void printBoardArea() {
		for(int i=0; i < this.getBoardArea().length; i++) {
			for(int j=0; j < this.getBoardArea().length; j++) {
				System.out.print(this.getBoardArea()[i][j]+ "  ");
			}
			System.out.println("\n");
		}
	}
	
	public void printBoard() {
		for(int i=0; i < this.getBoard().length; i++) {
			for(int j=0; j < this.getBoard().length; j++) {
				System.out.print(this.getBoard()[i][j]+ "  ");
			}
			System.out.println("\n");
		}
	}
	
	
	public String boardString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board.length; j++) {
				result.append(this.board[i][j].toString());
				if (j == 8) {
					result.append("\n");
				}
			}
		}
		return result.toString();
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();

		// board
		result.append("");
		result.append(this.boardString());

		result.append("-------");
		result.append("\n");

		// TURNO
		result.append(this.turn.toString()+"\n");
		result.append("-------");
		return result.toString();
	}

	public String toLinearString() {
		StringBuffer result = new StringBuffer();

		// board
		result.append("");
		result.append(this.boardString().replace("\n", ""));
		result.append(this.turn.toString());

		return result.toString();
	}
	

	public String getBox(int row, int column) {
		String ret;
		char col = (char) (column + 97);
		ret = col + "" + (row + 1);
		return ret;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		StateTablut other = (StateTablut) obj;
		if (this.getBoard() == null) {
			if (other.getBoard() != null)
				return false;
		} else {
			if (other.getBoard() == null)
				return false;
			if (this.getBoard().length != other.getBoard().length)
				return false;
			if (this.getBoard()[0].length != other.getBoard()[0].length)
				return false;
			for (int i = 0; i < other.getBoard().length; i++)
				for (int j = 0; j < other.getBoard()[i].length; j++)
					if (!this.getBoard()[i][j].equals(other.getBoard()[i][j]))
						return false;
		}
		if (this.getTurn()!= other.getTurn())
			return false;
		return true;
	}

}