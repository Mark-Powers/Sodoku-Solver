import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Pos;

public class Solver {
	private static int PUZZLES_PER_FILE = 10000;
	private static int DIFFICULTY_LEVELS = 5;

	public static void main(String[] args) {

		// while(true)
		{
			int puzzleID = (int) (Math.random() * PUZZLES_PER_FILE);
			int difficulty_level = (int) (Math.random() * DIFFICULTY_LEVELS);

			System.out.println(String.format("Solving puzzle ID %d with difficulty %d/%d.", puzzleID,
					difficulty_level + 1, DIFFICULTY_LEVELS));

			int[][] puzzle = new int[9][9];

			try {
				File f = new File("puzzles/" + difficulty_level + ".txt");
				BufferedReader br = new BufferedReader(new FileReader(f));
				for (int i = 0; i < puzzleID; i++) {
					br.readLine();
				}
				String[] parts = br.readLine().split("");

				int index = 0;
				for (int row = 0; row < 9; row++) {
					for (int col = 0; col < 9; col++) {
						puzzle[row][col] = Integer.parseInt(parts[index++]);
					}
				}
				br.close();
			} catch (Exception e) {
				System.out.println("No file");
			}

			long startTime = System.currentTimeMillis();
			new Solver(puzzle);
			double elapsedSec = (System.currentTimeMillis() - startTime) / 1000.0;
			System.out.println(String.format("\nTook %f seconds!\n", (float) elapsedSec));
		}
	}

	int[][] puzzle;

	public Solver(int[][] p) {
		puzzle = p;

		printSodoku(puzzle);

		if (runAlgorithm()) {
			System.out.println("\nFinished!");
		} else {
			System.out.println("\nToo hard!");
		}
		printSodoku(puzzle);

	}

	private boolean runAlgorithm() {
		if (isComplete()) {
			return true;
		} else {
			List<Possible> values = calculatePossibleValues();

			for (Possible value : values) {
				puzzle[value.row][value.col] = (int) value.value;
				if (!runAlgorithm()) {
					puzzle[value.row][value.col] = 0;
				} else {
					return true;
				}
			}
			return false;
		}
	}

	private ArrayList<Possible> calculatePossibleValues() {
		ArrayList<Possible> goodGuesses = new ArrayList<Possible>();
		
		ArrayList<ArrayList<ArrayList<Possible>>> rowList = new ArrayList<ArrayList<ArrayList<Possible>>>();
		
		for(int i = 0; i < 9; i++){
			rowList.add(new ArrayList<ArrayList<Possible>>());
		}
		for(int i = 0; i < 9; i++){
			for(int u = 0; u < 9; u++){
				rowList.get(i).add(new ArrayList<Possible>());
			}
		}

		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (puzzle[row][col] == 0) {
					for (int value = 1; value <= 9; value++) {
						if (isValid(row, col, value)) {
							rowList.get(row).get(col).add(new Possible(row, col, value));
						}
					}
				}
			}
		}
		
		for(ArrayList<ArrayList<Possible>> colList : rowList){
			for(ArrayList<Possible> guesses : colList){
				if(guesses.size() == 1){
					goodGuesses.add(guesses.get(0));
				}
			}
		}
		
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				ArrayList<Integer> fittingNumbers = new ArrayList<Integer>();
				for(int value = 1; value <= 9; value++){
					if(rowList.get(row).get(col).contains(new Possible(row, col, value))){
						fittingNumbers.add(value);
					}
				}
				if(fittingNumbers.size() == 1){
					goodGuesses.add(new Possible(row, col, fittingNumbers.get(0)));
				}
			}
		}

		return goodGuesses;
	}

	private boolean isComplete() {
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (puzzle[row][col] == 0) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isValid(int row, int col, int value) {
		for (int i = 0; i < 9; i++) {
			// Value already exists in row
			if (puzzle[row][i] == value) {
				return false;
			}
			// Value already exists in col
			if (puzzle[i][col] == value) {
				return false;
			}
		}
		// Value already exists in box
		int boxRow = (row / 3);
		int boxCol = (col / 3);

		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (puzzle[x + boxRow * 3][y + boxCol * 3] == value) {
					return false;
				}
			}
		}
		return true;
	}

	private void printSodoku(int[][] puzzle) {
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				System.out.print(puzzle[row][col] + " ");
				if (col == 2 || col == 5) {
					System.out.print("| ");
				}
			}
			if (row == 2 || row == 5) {
				System.out.print("\n------+-------+------");
			}
			System.out.println();
		}
	}

	class Possible implements Comparable<Possible> {
		int value;
		int row;
		int col;

		public Possible(int row, int col, int value) {
			this.row = row;
			this.col = col;
			this.value = value;
		}

		@Override
		public int compareTo(Possible o) {
			return row * 9 + col;
		}
	}
}
