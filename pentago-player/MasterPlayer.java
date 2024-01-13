package put.ai.games.naiveplayer;

import java.util.ArrayList;
import java.util.List;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;
import put.ai.games.game.moves.PlaceAndRotateMove;

public class MasterPlayer extends Player {
    boolean first = true;

    @Override
    public String getName() {
        return "Mateusz Karłowski 151782";
    }

    @Override
    public Move nextMove(Board b) {
        if (first) {
            int a = (b.getSize() / 2) - 1;
            first = false;
            return b.getMovesFor(getColor()).stream().filter(m -> m instanceof PlaceAndRotateMove).filter(
                    m -> ((PlaceAndRotateMove) m).getPlaceX() == a && ((PlaceAndRotateMove) m).getPlaceY() == a && ((PlaceAndRotateMove) m).getRotateDstX() > a).findFirst().get();
        }
        MoveWithValue m = minmax(b, 4);
        return m.move;
    }

    public class MoveWithValue {
        int value;
        Move move;

        MoveWithValue(int value, Move move) {
            this.move = move;
            this.value = value;
        }
    }

    MoveWithValue minmax(Board b, int depth) {
        return minmax(b, depth, -4, 4, new Max());
    }

    MoveWithValue minmax(Board b, int depth, int alpha, int beta, Strategy strategy) {
        int value = eval(b, strategy.getCurrColor());
        if (depth == 0 || value == 4 || value == -4) {
            return new MoveWithValue(value, null);
        }
        List<Move> moves = b.getMovesFor(strategy.getCurrColor());
        MoveWithValue best = new MoveWithValue(strategy.getWorstValue(), null);
        for (Move m : moves) {
            Board copy = b.clone();
            copy.doMove(m);
            if (strategy.breakCondition(best.value))
                return best;
            MoveWithValue newMove = minmax(copy, depth - 1, alpha, beta, strategy.getOpposite());
            if (strategy.isBetter(newMove.value, best.value))
                best = new MoveWithValue(newMove.value, m);
            alpha = strategy.newAlpha(alpha, best.value);
            beta = strategy.newBeta(beta, best.value);
            if (alpha >= beta)
                break;
        }
        return best;
    }

    interface Strategy {
        boolean isBetter(int a, int b);

        Strategy getOpposite();

        int getWorstValue();

        Color getCurrColor();

        int newAlpha(int alpha, int value);

        int newBeta(int beta, int value);

        boolean breakCondition(int value);
    }

    class Max implements Strategy {
        @Override
        public boolean isBetter(int a, int b) {
            return a > b;
        }

        @Override
        public Strategy getOpposite() {
            return new Min();
        }

        @Override
        public int getWorstValue() {
            return -4;
        }

        @Override
        public Color getCurrColor() {
            return getColor();
        }

        @Override
        public int newAlpha(int alpha, int value) {
            return Math.max(alpha, value);
        }

        @Override
        public int newBeta(int beta, int value) {
            return beta;
        }

        @Override
        public boolean breakCondition(int value) {
            return value == 4;
        }
    }

    class Min implements Strategy {
        @Override
        public boolean isBetter(int a, int b) {
            return a < b;
        }

        @Override
        public Strategy getOpposite() {
            return new Max();
        }

        @Override
        public int getWorstValue() {
            return 4;
        }

        @Override
        public Color getCurrColor() {
            return getOpponent(getColor());
        }

        @Override
        public int newAlpha(int alpha, int value) {
            return alpha;
        }

        @Override
        public int newBeta(int beta, int value) {
            return Math.min(beta, value);
        }

        @Override
        public boolean breakCondition(int value) {
            return value == -1;
        }
    }

    class Set {
        int value;
        Color color;
        boolean isColumn;
        int xIndex;
        int yIndex;

        Set(int value, Color color, boolean column, int xIndex, int yIndex) {
            this.value = value;
            this.color = color;
            this.isColumn = column;
            this.xIndex = xIndex;
            this.yIndex = yIndex;
        }
    }

    int eval(Board b, Color curr) {
        Color winner = b.getWinner(curr);
        if (winner == getColor())
            return 4;
        if (winner == getOpponent(getColor()))
            return -4;
        List<Set> sets = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            sets.addAll(getQuater(b, i));
        }
        int max = sets.stream().filter(s -> s.color == getColor()).mapToInt(s -> s.value).max().orElse(0);
        int oponentMax = sets.stream().filter(s -> s.color == getOpponent(getColor())).mapToInt(s -> s.value).max()
                .orElse(0);
        return max - oponentMax;
    }

    List<Set> getQuater(Board b, int quaterNumber) {
        int half = b.getSize() / 2;
        int xStart = ((quaterNumber - 1) % 2) * half;
        int yStart = quaterNumber < 3 ? 0 : half;
        List<GetIndex> getIndexs = List.of(new GetColorVertical(), new GetColorHorizontal());
        List<Set> sets = new ArrayList<>();
        Color color = getColor();
        int currentSetSize = 0;
        for (GetIndex getIndex : getIndexs) {
            for (int i = xStart; i < xStart + half; i++) {
                for (int j = yStart; j < yStart + half; j++) {
                    Color c = getIndex.get(b, i, j);
                    if (c == Color.EMPTY || c != color) {
                        if (currentSetSize >= half - 1) {
                            sets.add(new Set(currentSetSize - half + 2, color, getIndex instanceof GetColorVertical,
                                    getIndex.getX(i, j), getIndex.getY(i, j)));
                        }
                        currentSetSize = 0;
                    }
                    if (c != Color.EMPTY) {
                        currentSetSize++;
                        color = c;
                    }
                }
                if (currentSetSize >= half - 1) {
                    sets.add(new Set(currentSetSize - half + 2, color, getIndex instanceof GetColorVertical,
                            getIndex.getX(i, yStart + half - 1), getIndex.getY(i, yStart + half - 1)));
                }
                currentSetSize = 0;
            }
        }
        return sets;
    }

    interface GetIndex {
        Color get(Board b, int i, int j);

        int getX(int i, int j);

        int getY(int i, int j);
    }

    class GetColorVertical implements GetIndex {
        @Override
        public Color get(Board b, int i, int j) {
            return b.getState(i, j);
        }

        @Override
        public int getX(int i, int j) {
            return i;
        }

        @Override
        public int getY(int i, int j) {
            return j;
        }
    }

    class GetColorHorizontal implements GetIndex {
        @Override
        public Color get(Board b, int i, int j) {
            return b.getState(j, i);
        }

        @Override
        public int getX(int i, int j) {
            return j;
        }

        @Override
        public int getY(int i, int j) {
            return i;
        }
    }
}
