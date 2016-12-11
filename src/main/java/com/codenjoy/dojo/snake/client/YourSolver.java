package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */



import com.codenjoy.dojo.client.Direction;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.RandomDice;
import edu.princeton.cs.algs4.BreadthFirstPaths;
import edu.princeton.cs.algs4.Graph;
import javafx.scene.input.TouchPoint;

import java.util.*;

import static java.lang.Math.sqrt;
import static java.lang.StrictMath.pow;
import static java.util.Collections.sort;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {

    private static final String USER_NAME = "matvijzjul@gmail.com";

    private Dice dice;
    private Board board;
    private boolean pathBuilt;
    private Graph boardGraph;
    private Queue<TPoint> path;
    private List<TPoint> pointToIndex;

    private TPoint currDest;
    private boolean gotToCorner;
    private boolean goingUp;

    public YourSolver(Dice dice) {
        pathBuilt = false;
        gotToCorner = false;
            goingUp = true;
            this.dice = dice;
        }

        @Override
    public String get(Board board) {
        this.board = board;
        if (board.isGameOver()) {
            gotToCorner = false;
            pathBuilt =false;
            System.err.println("HERE");
        }
        if (board.getHead() == null) {
            gotToCorner = false;
            pathBuilt =false;
            return Direction.STOP.toString();
        }
        TPoint head = new TPoint(board.getHead());
        if (!gotToCorner) {
            if (!pathBuilt) getToCorner();
            Direction[] whereTo = head.directions(currDest);
            if (whereTo[0] != Direction.STOP) return whereTo[0].toString();
            else if (whereTo[1] != Direction.STOP) return whereTo[1].toString();
            else gotToCorner = true;
        }
        if (head.getX() == 1 && isInPointCollection(head.up().x, head.up().y, board.getWalls())) {
            System.err.println("case 1");
            goingUp = false;
        }
        if (head.getX() == 1 && isInPointCollection(head.down().x, head.down().y, board.getWalls())) {
            gotToCorner = false;
            pathBuilt =false;
            return Direction.UP.toString();
        }
        /*else if (isInPointCollection(head.up().x, head.up().y, board.getWalls())) {
            System.err.println("case 2");
            goingUp = false;
            if (isInPointCollection(head.right().x, head.right().y, board.getWalls())) return Direction.LEFT.toString();
            if (isInPointCollection(head.left().x, head.left().y, board.getWalls())) return Direction.RIGHT.toString();
        }
        else if (isInPointCollection(head.down().x, head.down().y, board.getWalls())) {
            System.err.println("case 3");
            goingUp = true;
            if (isInPointCollection(head.right().x, head.right().y, board.getWalls())) return Direction.LEFT.toString();
            if (isInPointCollection(head.left().x, head.left().y, board.getWalls())) return Direction.RIGHT.toString();
        }*/
        if (goingUp) {
            System.err.println(head.up().getX() + " " + head.up().getY());
            if (isInPointCollection(head.up().x, head.up().y, board.getBarriers())) {
                if (isInPointCollection(head.left().x, head.left().y, board.getBarriers())) {
                    System.err.println("case 4");

                    goingUp = !isInPointCollection(head.up().x, head.up().y, board.getWalls());
                    return Direction.RIGHT.toString();
                }
                else {
                    System.err.println("case 5");

                    goingUp = !isInPointCollection(head.up().x, head.up().y, board.getWalls());
                    return Direction.LEFT.toString();
                }
                //if (isInPointCollection(head.up().x, head.up().y, board.getWalls()))
                    //goingUp = !isInPointCollection(head.up().x, head.up().y, board.getWalls());
            }
            else {
                System.err.println("case 6");
                return Direction.UP.toString();
            }
        }
        else {
            if (isInPointCollection(head.down().x, head.down().y, board.getBarriers())) {
                goingUp = isInPointCollection(head.down().x, head.down().y, board.getWalls());
                if (isInPointCollection(head.left().x, head.left().y, board.getBarriers()))
                    return Direction.RIGHT.toString();
                else return Direction.LEFT.toString();
            }
            else return Direction.DOWN.toString();
        }
        /*if (!pathBuilt) {
            boardToGraph();
            populatePath(board.getHead().getX(), board.getHead().getY(),
                    board.getApples().get(0).getX(), board.getApples().get(0).getY());
            pathBuilt = true;
        }
        if (board.isGameOver() || path.isEmpty()) {
            pathBuilt = false;
        }
        if (path.isEmpty())
            return Direction.UP.toString();
        TPoint next = path.remove();
        if (board.getHead() != null ) {
            TPoint head = new TPoint(board.getHead().getX(), board.getHead().getY());
           // System.out.println(board.toString());
           // System.err.println(head.whichWay(next).toString());
            return head.whichWay(next).toString();
        }*/
        //return Direction.UP.toString();
    }

    private void getToCorner() {
        List<Point> walls = board.getWalls();
        sort(walls);
        TPoint corner = new TPoint(walls.get(walls.size()-1));
        TPoint currDest = new TPoint(corner.x - 1, corner.y - 1);
        while (board.getBarriers().contains(currDest)) {
            currDest.move(0, -1);
        }
        this.currDest = currDest;
        pathBuilt = true;
    }

    private void boardToGraph() {
        List<Point> walls = board.getWalls();
        List<Point> barriers = board.getBarriers();
        int dim = walls.size() / 4 + 1;
        int totalPoints = dim*dim - barriers.size();
        boardGraph = new Graph(totalPoints + 1);
        pointToIndex = new ArrayList<TPoint>();
        int key = 0;
        for (int i = 0; i < dim; ++i){
            for (int j = 0; j < dim; ++j) {
                if (!isInPointCollection(i, j, barriers) || isInPointCollection(i, j, board.getHead())) {
                    pointToIndex.add(new TPoint(i, j));
                    key += 1;
                }
            }
        }

        for (int i = 0; i < key; ++i) {
            TPoint tmp = pointToIndex.get(i);
            int right = pointToIndex.indexOf(new TPoint(tmp.x + 1, tmp.y));
            int left = pointToIndex.indexOf(new TPoint(tmp.x - 1, tmp.y));
            int up = pointToIndex.indexOf(new TPoint(tmp.x, tmp.y+1));
            int down = pointToIndex.indexOf(new TPoint(tmp.x, tmp.y-1));
            if (tmp.x+1 < dim && !isInPointCollection(tmp.x+1,tmp.y,barriers)) boardGraph.addEdge(i, right);
            if (tmp.x-1 > 0 && !isInPointCollection(tmp.x-1,tmp.y,barriers)) boardGraph.addEdge(i, left);
            if (tmp.y+1 < dim && !isInPointCollection(tmp.x,tmp.y+1,barriers)) boardGraph.addEdge(i, up);
            if (tmp.y-1 > 0 && !isInPointCollection(tmp.x,tmp.y-1,barriers)) boardGraph.addEdge(i, down);
        }

        System.err.println("pre-built");
    }

    private void populatePath(int x, int y, int applex, int appley) {
        int snake = pointToIndex.indexOf(new TPoint(x, y));
        BreadthFirstPaths bfp = new BreadthFirstPaths(boardGraph, snake);
        Iterable<Integer> ipath = bfp.pathTo(pointToIndex.indexOf(new TPoint(applex, appley)));
        path = new ArrayDeque<TPoint>();
        for (int index : ipath) {
            path.add(pointToIndex.get(index));
        }
    }

    private boolean isInPointCollection(int y, int x, Iterable<Point> col) {
        for (Point p : col) {
            if (p.itsMe(x, y))
                return true;
        }
        return false;
    }

    private boolean isInPointCollection(int x, int y, Point p) {

            if (p.itsMe(x, y))
                return true;
        return false;
    }

    private void goToLeftDownCorner() {
        List<Point> barriers = board.getBarriers();
        sort(barriers);
       // Point corner =
    }

    //public String

    public static void main(String[] args) {
        start(USER_NAME, WebSocketRunner.Host.REMOTE);
    }

    public static void start(String name, WebSocketRunner.Host server) {
        try {
            WebSocketRunner.run(server, name,
                    new YourSolver(new RandomDice()),
                    new Board());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class TPoint  implements Point{
        public int x;
        public int y;

        public TPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public TPoint(Point p) {
            this.x = p.getX();
            this.y = p.getY();
        }

        Direction whichWay(TPoint other) {
            if (other.x <= x)
                return Direction.LEFT;
            if (other.x >= x)
                return Direction.RIGHT;
            if (other.y <= y)
                return Direction.UP;
            if (other.y >= y)
                return Direction.DOWN;
            return Direction.STOP;
        }

        Direction[] directions(Point other) {
            Direction[] d = new Direction[2];
            if (other.getX() < x)
                d[0] = Direction.LEFT;
            else if (other.getX() > x)
                d[0] = Direction.RIGHT;
            else
                d[0] = Direction.STOP;
            if (other.getY() < y)
                d[1] = Direction.UP;
            else if (other.getY() > y)
                d[1] = Direction.DOWN;
            else d[1] = Direction.STOP;
            return d;
        }

        TPoint up() {
            return new TPoint(x, y-1);
        }
        TPoint down() {
            return new TPoint(x, y+1);
        }
        TPoint left() {
            return new TPoint(x-1, y);
        }
        TPoint right() {
            return new TPoint(x+1, y);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TPoint tPoint = (TPoint) o;

            if (x != tPoint.x) return false;
            return y == tPoint.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public void move(int i, int i1) {
            x += i;
            y += i1;
        }

        @Override
        public void move(Point point) {

        }

        @Override
        public PointImpl copy() {
            return null;
        }

        @Override
        public boolean itsMe(Point point) {
            return (x == point.getX() && y == point.getY());
        }

        @Override
        public boolean itsMe(int i, int i1) {
            return (x == i && y == i1);
        }

        @Override
        public boolean isOutOf(int i) {
            return false;
        }

        @Override
        public boolean isOutOf(int i, int i1, int i2) {
            return false;
        }

        @Override
        public double distance(Point point) {
            return sqrt(pow(x-point.getX(),2) + pow(y-point.getY(),2));
        }

        @Override
        public void change(Point point) {

        }

        @Override
        public int compareTo(Point o) {
            if (x > o.getX()) return 1;
            else if (x < o.getX()) return -1;
            else if (x== o.getX()) {
                if (y > o.getY()) return 1;
                else if (y < o.getY()) return -1;
            }
            return 0;
        }
    }

}
