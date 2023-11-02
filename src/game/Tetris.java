package game;

import graphicsLib.G;
import graphicsLib.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Tetris extends Window implements ActionListener {
    public static Timer timer;
    public static final int H = 20, W = 10, C = 25;// size of the well, C = cell
    public static final int xM = 50, yM = 50;
    public static Color[] color = { Color.red, Color.GREEN, Color.BLUE,
            Color.ORANGE, Color.CYAN, Color.YELLOW,
            Color.MAGENTA, Color.BLACK, Color.pink };
    public static Shape[] shapes = { Shape.Z, Shape.S, Shape.J, Shape.L,
            Shape.I, Shape.O, Shape.T };
    public static int[][] well = new int[W][H]; // grid of spent shapes // ! it's better to creat a well class

    public static Shape shape;

    public static int time = 1, iShape = 0;
    public static int iBack = 7; // index of background color
    public static int zap = 8; // index of the empty space

    public Tetris() {
        super("Tetris", 1000, 700);

        shape = shapes[G.rnd(7)]; // get a random shape
        clearWell();
        timer = new Timer(30, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public void paintComponent(Graphics g) {
        G.clear(g);
        time++;
        // if(time==60){time=0;iShape=(iShape +1)%7;}
        // if(time==30){shapes[iShape].rot();}
        if (time == 30) {
            time = 0;
            shape.drop();
        }
        unZapWell();
        showWell(g);
        shape.show(g);
        // g.setColor(Color.red);
        // g.fillRect(100,100,100,100);
    }

    public void keyPressed(KeyEvent ke) {
        int vk = ke.getKeyCode();
        if (vk == KeyEvent.VK_LEFT) {
            shape.slide(G.LEFT);
        }
        if (vk == KeyEvent.VK_RIGHT) {
            shape.slide(G.RIGHT);
        }
        if (vk == KeyEvent.VK_UP) {
            shape.safeRot();
        }
        if (vk == KeyEvent.VK_DOWN) {
            shape.drop();
        }
    }

    // clear well
    public static void clearWell() {
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                well[x][y] = iBack;
            }
        }
    }

    public static void showWell(Graphics g) {
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                g.setColor(color[well[x][y]]);
                int xx = xM + C * x, yy = yM + C * y;
                g.fillRect(xx, yy, C, C);
                g.setColor(Color.BLACK);
                g.drawRect(xx, yy, C, C);
            }
        }
    }

    public static void zapWell() {
        for (int y = 0; y < H; y++) {
            zapRow(y);
        }

    }

    public static void zapRow(int y) {
        for (int x = 0; x < W; x++) {
            if (well[x][y] == iBack) {
                return;
            } // if one single back exists, don't zap the row
        }
        for (int x = 0; x < W; x++) {
            well[x][y] = zap; // zap a row
        }
    }

    public static void unZapWell() {
        boolean done = false;
        for (int y = 1; y < H; y++) {
            for (int x = 0; x < W; x++) {
                if (well[x][y - 1] != zap && well[x][y] == zap) {
                    done = true;
                    well[x][y] = well[x][y - 1];
                    well[x][y - 1] = (y == 1 ? iBack : zap);
                }
            }
            if (done) {
                return;
            }
        }
    }

    public static void dropNewShape() {
        shape = shapes[G.rnd(7)];
        shape.loc.set(4, 0);// sets loc of new shape, centerd at the top
    }

    public static void main(String[] args) { // static variable in window, J PANEL, must have it to do swing things
        (PANEL = new Tetris()).launch();
    }

    // ----------Shape---------//
    public static class Shape {
        public static Shape Z, S, J, L, I, O, T;
        public static Shape cds = new Shape(new int[] { 0, 0, 0, 0, 0, 0, 0, 0 }, 0); //
        public static G.V temp = new G.V(0, 0);
        public G.V[] a = new G.V[4]; // array that holds the 4 squares for each shape
        public int iColor; // index of Color
        public G.V loc = new G.V();// location of shape

        public Shape(int[] xy, int iC) {
            for (int i = 0; i < 4; i++) {
                a[i] = new G.V(xy[i * 2], xy[i * 2 + 1]);// x / y coord is even/odd number
            }
            iColor = iC;
        }

        public void show(Graphics g) {
            g.setColor(color[iColor]);
            for (int i = 0; i < 4; i++) {
                g.fillRect(x(i), y(i), C, C);
            } // interior color of squares;
            g.setColor(Color.BLACK);
            for (int i = 0; i < 4; i++) {
                g.drawRect(x(i), y(i), C, C);
            } // interior color of squares;
        }

        public int x(int i) {
            return xM + C * (a[i].x + loc.x);
        }

        public int y(int i) {
            return yM + C * (a[i].y + loc.y);
        }

        public void rot() { // 90 degrees
            temp.set(0, 0);
            for (int i = 0; i < 4; i++) {
                a[i].set(-a[i].y, a[i].x); // 90 degree rotation:(x,y) --> (-y,x) two mirrors
                if (temp.x > a[i].x) {
                    temp.x = a[i].x;
                } // keeping track of min x & y
                if (temp.y > a[i].y) {
                    temp.y = a[i].y;
                } // making the temp that min
            }
            temp.set(-temp.x, -temp.y); // negate or 0'ing out a potentially(-) component
            for (int i = 0; i < 4; i++) {
                a[i].add(temp);
            }
        }

        public void safeRot() {
            rot();
            cdsSet();
            if (collisionDetected()) {
                rotCubed();
            }
        }

        public void rotCubed() {
            rot();
            rot();
            rot();
        }

        public static boolean collisionDetected() {
            for (int i = 0; i < 4; i++) { // make sure we are not outside boundary of array
                G.V v = cds.a[i]; // local way to refer to collision item
                if (v.x < 0 || v.x >= W || v.y < 0 || v.y >= H) { // tests for boundary of well
                    return true; // beep boop collision detected
                }
                if (well[v.x][v.y] < iBack) { // tests against "dead" blocks by using color
                    return true;
                }
            }
            return false;
        }

        public void cdsSet() { // call on a existing shape // object -> csd,a[i]
            for (int i = 0; i < 4; i++) {
                cds.a[i].set(a[i]);
                cds.a[i].add(loc);
            }
        }

        public void cdsGet() { // call on a existing shape // csd,a[i]->object
            for (int i = 0; i < 4; i++) {
                a[i].set(cds.a[i]);
            }
        }

        public void cdsAdd(G.V v) { // adds vector to each element in cd
            for (int i = 0; i < 4; i++) {
                cds.a[i].add(v);
            }
        }

        public void slide(G.V dX) {
            cdsSet();// get temp info
            cdsAdd(dX); // try to add
            if (collisionDetected()) {
                return;
            } // if detected, returns. Otherwise:
              // cdsGet(); // copies back to shapes
            else {
                loc.add(dX);
            } // slide is updating loc rather than changing it
        }

        public void drop() {
            cdsSet();
            cdsAdd(G.DOWN);
            if (collisionDetected()) {
                copyToWell();
                zapWell();
                dropNewShape();
                return;
            }
            loc.add(G.DOWN);
        }

        public void copyToWell() { // copy shape to the well
            for (int i = 0; i < 4; i++) {
                well[a[i].x + loc.x][a[i].y + loc.y] = iColor;
            }
        }

        static {
            Z = new Shape(new int[] { 0, 0, 1, 0, 1, 1, 2, 1 }, 0);
            S = new Shape(new int[] { 0, 1, 1, 0, 1, 1, 2, 0 }, 1);
            J = new Shape(new int[] { 0, 0, 0, 1, 1, 1, 2, 1 }, 2);
            L = new Shape(new int[] { 0, 1, 1, 1, 2, 1, 2, 0 }, 3);
            I = new Shape(new int[] { 0, 0, 1, 0, 2, 0, 3, 0 }, 4);
            O = new Shape(new int[] { 0, 0, 1, 0, 0, 1, 1, 1 }, 5);
            T = new Shape(new int[] { 0, 1, 1, 0, 1, 1, 2, 1 }, 6);
        }
    }
}
