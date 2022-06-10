package main;

import javax.swing.*;
import java.awt.*;
import entity.Player;
import world.TileManager;
import java.util.PriorityQueue;
import entity.*;

public class GamePanel extends JPanel implements Runnable {
    static final int ogTileSize = 32; // 32x32 sprites and tiles
    public static final int scale = 2;

    public static final int tileSize = ogTileSize * scale; // 64x64 after scaling
    public static final int colNum = 16;
    public static final int rowNum = 10;

    public static final int screenWidth = colNum * tileSize; // 1024px
    public static final int screenHeight = rowNum * tileSize; // 640px

    Thread gameLoop;
    KeyController keyC = new KeyController();
    Player player = new Player (this, this.keyC);
    public TileManager tM = new TileManager();
    public CollisionChecker checker = new CollisionChecker(this);
    PriorityQueue<Drawable> q = new PriorityQueue<>();

    final int fps = 60;

    public GamePanel () {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(this.keyC);
        this.setFocusable(true);
    }
    public void startGameThread () {
        gameLoop = new Thread(this);
        gameLoop.start();
    }
    @Override
    public void run() {
        double interval = 1e9 / fps;
        double nextDrawTime = System.nanoTime() + interval; 
        while (gameLoop != null) {
            update();
            repaint();
            try {
                double remainder = nextDrawTime - System.nanoTime();
                remainder = remainder/1e6;
                if (remainder < 0)
                    remainder = 0;
                Thread.sleep((long)remainder);
                nextDrawTime = nextDrawTime + interval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void update () {
        player.update();
    }
    @Override 
    public void paintComponent (Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        tM.drawRoom(g2);
        
        // TODO: Priority queue for objects implementing Drawable with Y position as the compare method
        q.add(player);
        for (Entity e : tM.currentRoom.entityList)
            q.add(e);
        for (Drawable entity : q)
            entity.draw(g2);
        q.clear();
        
        g2.dispose();
    }
}
