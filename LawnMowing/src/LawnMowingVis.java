/*
Change log
----------
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;

class Constants {
    public static final int[] DY = new int[]{1, 0, -1, 0};
    public static final int[] DX = new int[]{0, -1, 0, 1};
    public static final double PENALTY = 100;
}

class TestCase {
    public static final int MIN_YARD_SIZE = 20;
    public static final int MAX_YARD_SIZE = 80;
    public static final int MAX_HEIGHT = 10;
    public static final int MIN_BEDS = 0;
    public static final int MAX_BEDS = 30;
    public static final int MIN_LEVELS = 1;
    public static final int MAX_LEVELS = 100;
    public static final int MAX_TURN_COST = 10;
    public static final int MAX_FORWARD_COST = 10;
    public static final int MAX_SLOPE_COST = 10;

    public int yardSize;
    public char[][] yard;
    public int startX, startY;
    public int turnCost, forwardCost, slopeCost;

    public SecureRandom rnd = null;

    public void addRectangular(int maxr, char ch)
    {
        int x1 = rnd.nextInt(yardSize);
        int y1 = rnd.nextInt(yardSize);
        int x2 = x1 + rnd.nextInt(maxr);
        int y2 = y1 + rnd.nextInt(maxr);
        for (int x=x1;x<=x2;x++)
            for (int y=y1;y<=y2;y++)
                yard[y%yardSize][x%yardSize] = ch;
    }

    public void addCircular(int maxr, char ch)
    {
        int x1 = rnd.nextInt(yardSize);
        int y1 = rnd.nextInt(yardSize);
        int r = rnd.nextInt(maxr);
        double rx = 1.0 + rnd.nextDouble();
        double ry = 1.0 + rnd.nextDouble();
        for (int x=x1-r*2;x<=x1+r*2;x++)
            for (int y=y1-r*2;y<=y1+r*2;y++)
            {
                double d = (x-x1)*(x-x1)*rx+(y-y1)*(y-y1)*ry;
                if (d<=r*r)
                {
                    yard[(y+yardSize*2)%yardSize][(x+yardSize*2)%yardSize] = ch;
                }
            }
    }

    public TestCase(long seed) {

        try {
            rnd = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            System.err.println("ERROR: unable to generate test case.");
            System.exit(1);
        }

        rnd.setSeed(seed);

        yardSize = rnd.nextInt(MAX_YARD_SIZE - MIN_YARD_SIZE + 1) + MIN_YARD_SIZE;
        if (seed==1) yardSize = 20;
        yard = new char[yardSize][yardSize];

        turnCost = yardSize*(rnd.nextInt(MAX_TURN_COST)+1)/4;
        forwardCost = rnd.nextInt(MAX_FORWARD_COST)+1;
        slopeCost = yardSize*(rnd.nextInt(MAX_SLOPE_COST)+1);
        int nLevels = rnd.nextInt(MAX_LEVELS - MIN_LEVELS + 1) + MIN_LEVELS;
        int nBeds = rnd.nextInt(MAX_BEDS - MIN_BEDS + 1) + MIN_BEDS;

        // Make a clean level area
        for (int y=0;y<yardSize;y++)
            for (int x=0;x<yardSize;x++)
                yard[y][x] = '0';
        // Add levels
        for (int i=0;i<nLevels;i++)
        {
            int h = rnd.nextInt(MAX_HEIGHT);
            if (rnd.nextInt(2)==0)
            {
                addCircular(yardSize/2, (char)('0'+h));
            } else
            {
                addRectangular(yardSize/2, (char)('0'+h));
            }
        }
        // Smooth levels
        for (int k=0;k<2;k++)
        for (int y=0;y<yardSize;y++)
            for (int x=0;x<yardSize;x++)
            {
                int v = (yard[y][x]*4 +
                        yard[(y+1)%yardSize][x] +
                        yard[(y-1+yardSize)%yardSize][x] +
                        yard[y][(x+1)%yardSize] +
                        yard[y][(x-1+yardSize)%yardSize]) / 8;
                yard[y][x] = (char)v;
            }
        // Add beds
        for (int i=0;i<nBeds;i++)
        {
            if (rnd.nextInt(2)==0)
            {
                addCircular(yardSize/5, '.');
            } else
            {
                addRectangular(yardSize/5, '.');
            }
        }
        // Add mower
        do
        {
            startX = rnd.nextInt(yardSize);
            startY = rnd.nextInt(yardSize);
        } while (yard[startY][startX]=='.');

    }
}

class Drawer extends JFrame {
    public static final int EXTRA_WIDTH = 300;
    public static final int EXTRA_HEIGHT = 50;

    public World world;
    public DrawerPanel panel;

    public int cellSize, yardSize;
    public int width, height;

    public boolean pauseMode = false;
    public boolean debugMode = false;

    class DrawerKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            synchronized (keyMutex) {
                if (e.getKeyChar() == ' ') {
                    pauseMode = !pauseMode;
                }
                if (e.getKeyChar() == 'd') {
                    debugMode = !debugMode;
                }
                keyPressed = true;
                keyMutex.notifyAll();
            }
        }
    }

    class DrawerPanel extends JPanel {
        public void paint(Graphics g) {
            synchronized (world.worldLock) {

            g.setColor(new Color(0,128,0));
            g.fillRect(15, 15, cellSize * yardSize + 1, cellSize * yardSize + 1);
            g.setColor(Color.BLACK);
            for (int i = 0; i <= yardSize; i++) {
                g.drawLine(15 + i * cellSize, 15, 15 + i * cellSize, 15 + cellSize * yardSize);
                g.drawLine(15, 15 + i * cellSize, 15 + cellSize * yardSize, 15 + i * cellSize);
            }

            for (int i=0; i < yardSize; i++) {
                for (int j=0; j < yardSize; j++) {
                    if (world.tc.yard[i][j]>='0' && world.tc.yard[i][j]<='9') {
                        int col = 64 + (world.tc.yard[i][j]-'0')*192/10;
                        g.setColor(new Color(0, col, 0));
                        g.fillRect(15 + j * cellSize + 1, 15 + i * cellSize + 1, cellSize - 1, cellSize - 1);
                    } else if (world.tc.yard[i][j]=='.') {
                        g.setColor(new Color(64, 64, 0));
                        g.fillRect(15 + j * cellSize + 1, 15 + i * cellSize + 1, cellSize - 1, cellSize - 1);
                    } else if (world.tc.yard[i][j]>='A' && world.tc.yard[i][j]<='J') {
                        int col = 64 + (world.tc.yard[i][j]-'A')*192/10;
                        for (int yy=0;yy<4;yy++)
                        for (int xx=0;xx<4;xx++)
                        {
                            if (((xx+yy)&1)==1)
                                g.setColor(new Color(0, col, 0));
                                else
                                g.setColor(new Color(0, 0, 0));
                            g.fillRect(15 + j * cellSize + 1+xx*cellSize/4, 15 + i * cellSize + 1+yy*cellSize/4, cellSize/4, cellSize/4);
                        }
                    }
                }
            }

            g.setColor(Color.WHITE);
            g.fillOval(15 + (world.mowX) * cellSize + cellSize/4, 15 + (world.mowY) * cellSize + cellSize/4, cellSize - cellSize/2, cellSize - cellSize/2);
            g.drawLine(15 + (world.mowX) * cellSize + cellSize/2, 15 + (world.mowY) * cellSize + cellSize/2,
                       15 + (world.mowX+Constants.DX[world.mowD]) * cellSize + cellSize/2, 15 + (world.mowY+Constants.DY[world.mowD]) * cellSize + cellSize/2);
            g.drawRect(15 + world.tc.startX * cellSize, 15 + world.tc.startY * cellSize, cellSize+1, cellSize+1);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            Graphics2D g2 = (Graphics2D)g;

            int horPos = 40 + yardSize * cellSize;
            g2.drawString("Yard size = " + yardSize, horPos, 30);
            g2.drawString("Step = " + world.curStep, horPos, 50);
            g2.drawString(String.format("Forward cost = %.2f",world.numForward * world.tc.forwardCost), horPos, 70);
            g2.drawString(String.format("Turn cost = %.2f", world.numTurns * world.tc.turnCost), horPos, 90);
            g2.drawString(String.format("Slope cost = %.2f", world.numSlope * world.tc.slopeCost), horPos, 110);
            double score = world.numForward * world.tc.forwardCost + world.numTurns * world.tc.turnCost + world.numSlope * world.tc.slopeCost;
            int grass = 0;
            for (int y=0;y<world.tc.yardSize;y++)
                for (int x=0;x<world.tc.yardSize;x++)
                    if (world.isGrass(x,y)) grass++;
            g2.drawString(String.format("Penalty = %.2f", world.tc.slopeCost*Constants.PENALTY*grass ), horPos, 130);
            score += world.tc.slopeCost*Constants.PENALTY*grass;
            g2.drawString(String.format("Score = %.2f", score), horPos, 160);
            }
        }
    }

    class DrawerWindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent event) {
            LawnMowingVis.stopSolution();
            System.exit(0);
        }
    }

    final Object keyMutex = new Object();
    boolean keyPressed;

    public void processPause() {
        synchronized (keyMutex) {
            if (!pauseMode) {
                return;
            }
            keyPressed = false;
            while (!keyPressed) {
                try {
                    keyMutex.wait();
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
    }

    public Drawer(World world, int cellSize) {
        super();

        panel = new DrawerPanel();
        getContentPane().add(panel);

        addWindowListener(new DrawerWindowListener());

        this.world = world;

        yardSize = world.tc.yardSize;
        this.cellSize = cellSize;
        width = cellSize * yardSize + EXTRA_WIDTH;
        height = cellSize * yardSize + EXTRA_HEIGHT;

        addKeyListener(new DrawerKeyListener());

        setSize(width, height);
        setTitle("TCO'15 Marathon Championship Round");

        setResizable(false);
        setVisible(true);
    }
}

class World {
    final Object worldLock = new Object();
    TestCase tc;
    int curStep = -1;
    double numTurns;
    double numSlope;
    double numForward;
    int mowX, mowY, mowD;

    public World(TestCase tc) {
        this.tc = tc;
        numTurns = numSlope = numForward = 0;
        mowX = tc.startX;
        mowY = tc.startY;
        mowD = 0;
    }

    public int getHeight(int x, int y)
    {
        if (tc.yard[y][x]>='0' && tc.yard[y][x]<='9')
        {
            return tc.yard[y][x]-'0';
        }
        else if (tc.yard[y][x]>='A' && tc.yard[y][x]<='J')
        {
            return tc.yard[y][x]-'A';
        }
        return 0;
    }

    public boolean isGrass(int x, int y)
    {
        return (tc.yard[y][x]>='0' && tc.yard[y][x]<='9');
    }

    public boolean updateMower(char cmd) {
        synchronized (worldLock) {
            if (cmd=='S')
            {
                int newX = (mowX+Constants.DX[mowD]+tc.yardSize)%tc.yardSize;
                int newY = (mowY+Constants.DY[mowD]+tc.yardSize)%tc.yardSize;
                int slope = Math.max(0, getHeight(newX, newY) - getHeight(mowX, mowY));
                if (isGrass(mowX, mowY))
                {
                    tc.yard[mowY][mowX] = (char)(tc.yard[mowY][mowX]-'0'+'A');
                    numForward += 1.0;
                    numSlope += slope;
                } else
                {
                    // already mowed, add 20% of cost
                    numForward += 0.2;
                    numSlope += 0.2*slope;
                }
                mowX = newX;
                mowY = newY;
                if (tc.yard[mowY][mowX]=='.')
                {
                    System.err.println("ERROR: You can only move over grass areas. Invalid move going over non-grass area at ("+mowY+","+mowX+").");
                    return false;
                }
            } else if (cmd=='L')
            {
                mowD = (3+mowD) % 4;
                if (isGrass(mowX, mowY))
                    numTurns += 1.0;
                else
                    numTurns += 0.2;
            } else if (cmd=='R')
            {
                mowD = (1+mowD) % 4;
                if (isGrass(mowX, mowY))
                    numTurns += 1.0;
                else
                    numTurns += 0.2;
            } else {
                System.err.println("ERROR: Invalid command [" + cmd +"] received, command must be L,R or S.");
                return false;
            }
        }
        return true;
    }

    public void startNewStep() {
        curStep++;
    }

}

public class LawnMowingVis {
    public static String execCommand = null;
    public static long seed = 1;
    public static boolean vis = true;
    public static boolean debug = false;
    public static int cellSize = 12;
    public static int delay = 100;
    public static boolean startPaused = false;

    public static Process solution;

    public double runTest() {
        solution = null;

        try {
            solution = Runtime.getRuntime().exec(execCommand);
        } catch (Exception e) {
            System.err.println("ERROR: Unable to execute your solution using the provided command: "
                    + execCommand + ".");
            return -1.0;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(solution.getInputStream()));
        PrintWriter writer = new PrintWriter(solution.getOutputStream());
        new ErrorStreamRedirector(solution.getErrorStream()).start();

        TestCase tc = new TestCase(seed);
        World world = new World(tc);

        writer.println(tc.yardSize);
        // Yard information
        for (int y=0;y<tc.yardSize;y++) {
            String row = "";
            for (int x=0;x<tc.yardSize;x++) {
                row += tc.yard[y][x];
            }
            writer.println(row);
        }
        writer.println(tc.turnCost);
        writer.println(tc.forwardCost);
        writer.println(tc.slopeCost);
        writer.println(tc.startX);
        writer.println(tc.startY);
        writer.flush();

        Drawer drawer = null;
        if (vis) {
            drawer = new Drawer(world, cellSize);
            drawer.debugMode = debug;
            if (startPaused) {
                drawer.pauseMode = true;
            }
        }

        String cmd;

        try {
            cmd = reader.readLine();
        } catch (Exception e) {
            System.err.println("ERROR: Unable to get the move commands from your solution.");
            return -1.0;
        }

        for (int t=0;t<cmd.length();t++)
        {
            if (vis) {
                drawer.processPause();
                drawer.repaint();
                try {
                    Thread.sleep(delay);
                } catch (Exception e) {
                    // do nothing
                }
            }
            world.startNewStep();
            if (!world.updateMower(cmd.charAt(t)))
            {
                return -1.0;
            }
        }
        // Count un-cut grass
        int grass = 0;
        for (int y=0;y<tc.yardSize;y++)
            for (int x=0;x<tc.yardSize;x++)
                if (world.isGrass(x,y))
                {
                    grass++;
                }
        // Should end at starting cell
        if (world.mowX!=tc.startX || world.mowY!=tc.startY)
        {
            System.err.println("ERROR: Ending location should be where you started. The mower ended at ("+world.mowY+","+world.mowX+") instead of ("+tc.startY+","+tc.startX+").");
            return -1.0;
        }

        stopSolution();

        double score = world.numTurns*tc.turnCost +
                    world.numForward*tc.forwardCost +
                    world.numSlope*tc.slopeCost;
        double penalty = tc.slopeCost*Constants.PENALTY*grass;
        score += penalty;

        System.err.println("Turn cost = " + world.numTurns);
        System.err.println("Forward cost = " + world.numForward);
        System.err.println("Slope cost = " + world.numSlope);
        System.err.println("Penalty = " + penalty);

        return score;
    }

    public static void stopSolution() {
        if (solution != null) {
            try {
                solution.destroy();
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++)
            if (args[i].equals("-exec")) {
                execCommand = args[++i];
            } else if (args[i].equals("-seed")) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("-novis")) {
                vis = false;
            } else if (args[i].equals("-debug")) {
                debug = true;
            } else if (args[i].equals("-sz")) {
                cellSize = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-delay")) {
                delay = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-pause")) {
                startPaused = true;
            } else {
                System.out.println("WARNING: unknown argument " + args[i] + ".");
            }

        if (execCommand == null) {
            System.err.println("ERROR: You did not provide the command to execute your solution." +
                    " Please use -exec <command> for this.");
            System.exit(1);
        }

        LawnMowingVis vis = new LawnMowingVis();
        try {
            double score = vis.runTest();
            System.out.println("Score = " + score);
        } catch (RuntimeException e) {
            System.err.println("ERROR: Unexpected error while running your test case.");
            e.printStackTrace();
            LawnMowingVis.stopSolution();
        }
    }
}

class ErrorStreamRedirector extends Thread {
    public BufferedReader reader;

    public ErrorStreamRedirector(InputStream is) {
        reader = new BufferedReader(new InputStreamReader(is));
    }

    public void run() {
        while (true) {
            String s;
            try {
                s = reader.readLine();
            } catch (Exception e) {
                //e.printStackTrace();
                return;
            }
            if (s == null) {
                break;
            }
            System.err.println(s);
        }
    }
}
