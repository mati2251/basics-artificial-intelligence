package put.ai.games.naiveplayer;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;
import put.ai.games.game.moves.PlaceMove;
import put.ai.games.game.moves.RotateMove.Direction;
import put.ai.games.game.moves.impl.PlaceMoveImpl;
import put.ai.games.pentago.impl.PentagoBoard;
import put.ai.games.pentago.impl.PentagoMove;

import java.util.ArrayList;
import java.util.List;

public class MasterPlayer extends Player {
    boolean first = true;
    int max;
    int half;
    int halfOfHalf;
    int size;
    int probablyWinPoint;

    @Override
    public String getName() {
        return "Mateusz Karłowski 151782";
    }

    @Override
    public Move nextMove(Board b) {
        BoardE boardE = new BoardE((PentagoBoard) b);
        System.out.println("Next");
        if (first) {
            size = b.getSize();
            half = size / 2;
            max = Integer.MAX_VALUE - 1;
            halfOfHalf = half / 2;
            first = false;
            probablyWinPoint = size * 4 * 4;
        }
        return minmax(boardE, 4);
    }

    Move minmax(Board b, int depth) {
        List<PlaceMove> moves = getPlaceMoves(b, getColor());
        BoardEval best = new BoardEval(-max - 1, b);
        PlaceMove bestMove = null;
        for (PlaceMove m : moves) {
            Board copy = b.clone();
            copy.doMove(m);
            BoardEval currMove = minmaxP(copy, depth - 1, -this.max - 1, this.max + 1, new Min());
            if (currMove.value > best.value) {
                best = currMove;
                bestMove = m;
            }
        }
        System.out.println(best.value);
        RotateMove bestRotate = getRotateMove().get(0);
        for (RotateMove m : getRotateMove()) {
            Board copy = b.clone();
            copy.doMove((Move) m);
            int currMove = minmaxR(copy, 4, new Min());
            if (currMove == max) {
                bestRotate = m;
                break;
            }
        }
        return new PentagoMove(bestMove.getX(), bestMove.getY(), bestRotate.srcX, bestRotate.srcY, bestRotate.dstX, bestRotate.dstY, getColor());
    }

    BoardEval minmaxP(Board b, int depth, int alpha, int beta, Strategy strategy) {
        if (depth == 0) {
            return new BoardEval(getEvalByQuarter(b), b);
        }
        List<PlaceMove> moves = getPlaceMoves(b, strategy.getCurrColor());
        BoardEval best = new BoardEval(strategy.getWorstValue(), b);
        for (PlaceMove m : moves) {
            Board copy = b.clone();
            copy.doMove(m);
            BoardEval newMove = minmaxP(copy, depth - 1, alpha, beta, strategy.getOpposite());
            if (strategy.isBetter(newMove.value, best.value))
                best = newMove;
            alpha = strategy.newAlpha(alpha, best.value);
            beta = strategy.newBeta(beta, best.value);
            if (alpha >= beta) {
                break;
            }
        }
        return best;
    }

    int minmaxR(Board b, int depth, Strategy strategy) {
        int value = getWinnerEval(b);
        if (depth == 0 || value == max || value == -max) {
            return value;
        }
        List<RotateMove> moves = getRotateMove();
        for (RotateMove m : moves) {
            Board copy = b.clone();
            copy.doMove((Move) m);
            int newMove = minmaxR(copy, depth - 1, strategy.getOpposite());
            if (strategy.isBetter(newMove, value))
                value = newMove;
        }
        return value;
    }

    static class BoardEval {
        int value;
        Board board;

        public BoardEval(int value, Board board) {
            this.value = value;
            this.board = board;
        }
    }

    PentagoMove toPentagoMove(PlaceMove m) {
        return new PentagoMove(m.getX(), m.getY(), 0, 0, 2, 0, m.getColor());
    }

    List<RotateMove> getRotateMove() {
        return List.of(
                new RotateMove(0, 0, half - 1, 0),
                new RotateMove(half - 1, 0, 0, 0),
                new RotateMove(0, half, half - 1, half),
                new RotateMove(half - 1, half, 0, half),
                new RotateMove(half, 0, size - 1, 0),
                new RotateMove(size - 1, 0, half, 0),
                new RotateMove(half, half, size - 1, half),
                new RotateMove(size - 1, half, half, half));
    }

    static class RotateMove implements Move {
        int srcX;
        int srcY;
        int dstX;
        int dstY;
        Direction direction;

        public RotateMove(int rotateSrcX, int rotateSrcY, int rotateDstX, int rotateDstY) {
            this.srcX = rotateSrcX;
            this.srcY = rotateSrcY;
            this.dstX = rotateDstX;
            this.dstY = rotateDstY;
            int a, b;
            if (rotateSrcX == rotateDstX) {
                a = rotateSrcY;
                b = rotateDstY;
            } else if (rotateSrcY == rotateDstY) {
                a = rotateSrcX;
                b = rotateDstX;
            } else {
                throw new IllegalArgumentException();
            }
            if (a > b)
                this.direction = Direction.COUNTERCLOCKWISE;
            else if (a < b)
                this.direction = Direction.CLOCKWISE;
            else
                throw new IllegalArgumentException();

        }

        @Override
        public Color getColor() {
            return null;
        }
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
            return -max - 1;
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
            return value == max;
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
            return max + 1;
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
            return value == max;
        }
    }

    static class BoardE extends PentagoBoard {
        public BoardE(PentagoBoard b) {
            super(b);
        }

        // fast copy cunstructor
        public BoardE(BoardE b) {
            super(b);
        }

        @Override
        public void doMove(Move _m) {
            if ((_m instanceof PentagoMove))
                super.doMove(_m);
            else if ((_m instanceof PlaceMove))
                state[((PlaceMove) _m).getX()][((PlaceMove) _m).getY()] = (_m).getColor();
            else if ((_m instanceof RotateMove))
                rotate(((RotateMove) _m).srcX, ((RotateMove) _m).srcY, ((RotateMove) _m).direction);
            else
                throw new IllegalArgumentException();
        }

        @Override
        public BoardE clone() {
            return new BoardE(this);
        }
    }

    static class Set {
        int value;
        Color color;
        int x;

        public Set(int value, Color color, int x) {
            this.value = value;
            this.color = color;
            this.x = x;
        }
    }

    int getWinnerEval(Board b) {
        List<GetIndex> getIndexList = List.of(new GetColorHorizontal(0, 0, b), new GetColorVertical(0, 0, b));
        for (GetIndex getIndex : getIndexList) {
            for (int i = 0; i < size; i++) {
                if (getIndex.get(i, half) != Color.EMPTY) {
                    Color curr = getIndex.get(i, half);
                    int counter = 0;
                    for (int j = 0; j < size; j++) {
                        if (getIndex.get(i, j) != curr) {
                            if (j == 0)
                                continue;
                            if (j == 1){
                                counter = 0;
                                continue;
                            }
                            break;
                        }
                        counter++;
                    }
                    if (counter >= size - 2)
                        return curr == getColor() ? max : -max;
                }
            }
        }
        return 0;
    }

    int getEvalByQuarter(Board b) {
        List<Set> zero = setInQuarter(b, 0);
        List<Set> one = setInQuarter(b, 1);
        List<Set> two = setInQuarter(b, 2);
        List<Set> three = setInQuarter(b, 3);
        int points = compareSets(zero, one);
        points += compareSets(zero, three);
        points += compareSets(three, two);
        points += compareSets(one, two);
        points += zero.stream().mapToInt(this::setToPoints).sum();
        points += one.stream().mapToInt(this::setToPoints).sum();
        points += two.stream().mapToInt(this::setToPoints).sum();
        points += three.stream().mapToInt(this::setToPoints).sum();
        return points;
    }

    int setToPoints(Set set) {
        if (set.color == getColor())
            return (set.value - halfOfHalf);
        else
            return -(set.value - halfOfHalf);
    }

    int compareSets(List<Set> one, List<Set> two) {
        int points = 0;
        for (Set s : one) {
            for (Set s2 : two) {
                if (s.color == s2.color && s.x == s2.x && s.value + s2.value >= size - 2) {
                    if (getColor() == s.color)
                        points += probablyWinPoint;
                    else
                        points -= probablyWinPoint;
                }
            }
        }
        return points;
    }

    // Quarter number illustration
    // 0 | 1
    // -----
    // 3 | 2
    //  neighbor quarter numbers are odd/even
    List<Set> setInQuarter(Board b, int quarterNumber) {
        int xStart = quarterNumber == 1 || quarterNumber == 2 ? half : 0;
        int yStart = quarterNumber == 2 || quarterNumber == 3 ? half : 0;
        ArrayList<Set> sets = new ArrayList<>();
        List<GetIndex> getIndexList = List.of(new GetColorHorizontal(xStart, yStart, b), new GetColorVertical(xStart, yStart, b));
        for (GetIndex getIndex : getIndexList) {
            for (int i = 0; i < half; i++) {
                if (getIndex.get(i, halfOfHalf) != Color.EMPTY) {
                    Color curr = getIndex.get(i, halfOfHalf);
                    int counter = 0;
                    for (int j = 0; j < half; j++) {
                        if (getIndex.get(i, j) != curr) {
                            if (j == 0)
                                continue;
                            if (j == 1){
                                counter = 0;
                                continue;
                            }
                            break;
                        }
                        counter++;
                    }
                    if (counter >= half - 2)
                        sets.add(new Set(counter, curr, Math.abs(halfOfHalf - i)));
                }
            }
        }
        return sets;
    }

    List<PlaceMove> getPlaceMoves(Board b, Color color) {
        List<PlaceMove> moves = new ArrayList<>();
        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                if (b.getState(i, j) == Color.EMPTY) {
                    moves.add(new PlaceMoveImpl(i, j, color));
                }
            }
        }
        return moves;
    }

    interface GetIndex {
        Color get(int i, int j);
    }

    static class GetColorVertical implements GetIndex {

        int xStart;
        int yStart;
        Board b;

        public GetColorVertical(int xStart, int yStart, Board b) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.b = b;
        }

        @Override
        public Color get(int i, int j) {
            return b.getState(i + xStart, j + yStart);
        }
    }

    static class GetColorHorizontal implements GetIndex {
        int xStart;
        int yStart;
        Board b;

        public GetColorHorizontal(int xStart, int yStart, Board b) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.b = b;
        }

        @Override
        public Color get(int i, int j) {
            return b.getState(j + xStart, i + yStart);
        }
    }
}
