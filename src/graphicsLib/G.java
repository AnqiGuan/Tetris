package graphicsLib;

import java.awt.*;
import java.util.Random;

public class G {
    public static final Random RANDOM = new Random();

    public static int rnd(int max) {
        return RANDOM.nextInt(max); // [0,n)
    } // wrappers are to clear up functions

    public static G.V LEFT = new G.V(-1, 0), RIGHT = new G.V(1, 0);
    public static G.V UP = new G.V(0, -1), DOWN = new G.V(0, 1);

    public static Color rndColor(){return new Color(rnd(256), rnd(256), rnd(256));}
    public static void clearBack (Graphics g) {
        // clear background by fill the full screen to white for windows, mac will automatically do it for users
        g.setColor(Color.white);
        g.fillRect(0, 0, 5000,5000);
    }
    public static void clear(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, 5000, 5000);
    }
    // nested class: have a file doing sth, need helper data structure(nested inside this class)
    // --------------V----------------//
    // Vector: two data points
    public static class V{
        public int x, y;
        public V(){set(0,0);}
        public V(int x, int y){set(x, y);}
        public void set(int x, int y){this.x = x; this.y = y;}// need existing vector object in order to set values for nonstatic functions
        public void set(V v) {this.x = v.x;this.y = v.y;}
        public void add(V v){x += v.x; y += v.y;}
    }
    // --------------VS----------------//
    // For rectangle: Vector and size to figure out where the corner is.
    public static class VS{
        public V loc, size;
        public VS(int x, int y, int w, int h){loc = new V(x, y); size = new V(w, h);}
        public void fill(Graphics g, Color c){g.setColor(c); g.fillRect(loc.x, loc.y, size.x, size.y);}
        public boolean hit(int x, int y){return loc.x <= x && x <= loc.x + size.x && loc.y <= y && y <= loc.y + size.y;}
    }
    // --------------LoHi----------------//
    // two numbers: lo and hi represent a range of numbers.
    public static class LoHi{
        public int lo, hi;

    }
    // --------------BBox----------------//
    // bounding box: min x,y and max x,y
    // vertical and horizontal
    public static class BBox{
        public LoHi h, v;
    }
    // --------------PL----------------//
    // Polyline: a whole long list of x,y coords
    public static class PL{

    }
}
