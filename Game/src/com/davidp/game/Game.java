package com.davidp.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class Game extends JPanel implements KeyListener, Runnable { // KeyListener for keyboard inputs and Runnable for threads

	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 400;
	public static final int HEIGHT = 630;
	public static final Font main = new Font("Arial", Font.PLAIN, 28);
	private Thread game;
	private boolean running;
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private GameBoard board;

	
	private long startTime;
	private long elapsed;
	private boolean set;
	
	
	public Game() {
		setFocusable(true); //allows keyboard input to be set
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		addKeyListener(this);
		
		board = new GameBoard(WIDTH/2-GameBoard.BOARD_WIDTH/2, HEIGHT - GameBoard.BOARD_HEIGHT - 10);
		
	}
	
	private void update() {
		//will be called 60 times per second
		//GAMELOGIC
		board.update();
		Keyboard.update();
		
	}
	
	private void render() {
		//GRAPHICS
		/* Will be called 60 times per second
		 * The graphics object lets us draw to an image. 
		 * And this image is stored in memory and keeps track of everything we draw on screen.
		*/
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		board.render(g);
		g.dispose();
		
		//This is going to get the graphics of the JPanel and draw this on the JPanel
		Graphics2D g2d = (Graphics2D) getGraphics();
		g2d.drawImage(image,0,0,null);
		g2d.dispose();
	}
	
	@Override
	public void run() {
		//this will be called when we start our thread
		int fps = 0, updates = 0;
		long fpsTimer = System.currentTimeMillis();
		double nsPerUpdate = 1000000000.0/60; //keeps track of how many milliseconds per frame
		
		
		//last update time in nanoseconds
		double then = System.nanoTime();
		double unprocessed = 0;
		
		while (running) {
	        boolean shouldRender = false;
	        double now = System.nanoTime();
	        unprocessed += (now - then) / nsPerUpdate;
	        then = now;

	        // Update queue
	        while (unprocessed >= 1) {
	            updates++;
	            update();
	            unprocessed--;
	            shouldRender = true;
	        }

	        // Render
	        if (shouldRender) {
	            fps++;
	            render();
	        } else {
	            try {
	                // Adjust sleep time to better control frame rate
	                Thread.sleep(2);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }

	        // FPS timer update
	        if (System.currentTimeMillis() - fpsTimer > 1000) {
	            fpsTimer += 1000;
	            System.out.println("FPS: " + fps + " | Updates: " + updates);
	            fps = 0;
	            updates = 0;
	        }
	    }
		
		//FPS Timer
		if(System.currentTimeMillis()-fpsTimer > 1000) {
			System.out.printf("%d fps %d updates", fps, updates);
			System.out.println();
			fps = 0;
			updates = 0;
			fpsTimer += 1000;
		}
	}
	
	
	public synchronized void start() {
		if(running)return;
		running = true;
		game = new Thread(this,"game");
		game.start();
	}
	
	public synchronized void stop() {
		if(!running)return;
		running = false;
		System.exit(0);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Keyboard.keyPressed(e);
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Keyboard.keyReleased(e);
		
	}
	
	
	
}
