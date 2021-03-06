package com.rentarosato520.dungeoncrawler;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.Random;

import javax.swing.JFrame;

import com.rentarosato520.dungeoncrawler.genDungeon.DungeonGen;
import com.rentarosato520.dungeoncrawler.mob.Mob;
import com.rentarosato520.dungeoncrawler.mob.Niconan;
import com.rentarosato520.dungeoncrawler.room.DungeonObject;
import com.rentarosato520.dungeoncrawler.server.GameClient;
import com.rentarosato520.dungeoncrawler.server.GameServer;
import com.rentarosato520.dungeoncrawler.util.Camera;
import com.rentarosato520.dungeoncrawler.util.Input;

public class GameMain extends Canvas implements Runnable{
	public static final String name = "Dungeon Crawler";
	public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	private boolean running = false;
	private Handler h = new Handler();
	private Input input = new Input(this, h);
	private DungeonGen gen = new DungeonGen(500, 500, 16, h);
	private Random r = new Random();
	private Camera cam = new Camera(0, 0);
	
	private Mob p;
	
	private GameServer socketServer;
	private GameClient socketClient;
	//Player username
	public GameMain(){
		gen.createDungeon();
		
		DungeonObject spawn = DungeonGen.getSpawnRoom(h.object);
		
		p = new Niconan(r.nextInt(spawn.getW())+spawn.getX(),r.nextInt(spawn.getH())+spawn.getY(), 32, 32, 0.5f, true, h);
		
		h.addEntity(p);
	}
	
	public void run() {
		this.requestFocus();
		long lastTime = System.nanoTime();
		double delta = 0;
		double amountOfTicks = 60.0;
		double ns = 1000000000.0 / amountOfTicks;
		long timer = System.currentTimeMillis();
		int frames = 0;
		
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				tick();
				delta--;
			}
			if(running){
				render();
				frames++;
				
				if(System.currentTimeMillis() - timer > 1000){
					timer += 1000;
					frames = 0;
				}
			}
		}
		stop();
	}
	
	public void start(){
		running = true;
		//Creates a thread with this as the runnable
		new Thread(this).start();
		
		//Save for when getting the game running
		/*if(JOptionPane.showConfirmDialog(this, "Do you want to play multiplayer?") == 0){
			socketServer = new GameServer(this);
			socketServer.start();
		}
		socketClient = new GameClient(this, "localhost");
		socketClient.start();
		socketClient.sendData("ping".getBytes());*/
	}
	
	public void stop(){

	}
	
	public void tick(){
		h.tick();
		cam.tick(p);
	}
	
	public void render(){
		if(getWidth() == 0 || getHeight() == 0){
			return;
		}
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			//Creates BufferStrategies if there are none
			try{
				createBufferStrategy(3);
			}catch(Exception e){
				e.printStackTrace();
			}
			requestFocus();
			return;
		}
		//Gets the graphic
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		
		g.setColor(Color.black);
		g.fillRect(0, 0, screenSize.width, screenSize.height);
		
		g2d.translate(cam.getX(), cam.getY());
		//Anything between and the other translate will be affected by the translate
		//Allows the handler to render all of the objects
		//g2d.scale(2, 2);
		h.render(g);
		
		g2d.translate(-cam.getX(), -cam.getY());
		//Gets rid of the graphics g
		g.dispose();
		//Shows the buffers
		bs.show();
	}
	
	public static void main(String[] args){
		GameMain game = new GameMain();
		JFrame window = new JFrame();
		
		game.setMaximumSize(screenSize);
		game.setMinimumSize(screenSize);
		game.setPreferredSize(screenSize);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(screenSize.width, screenSize.height);
		window.setResizable(true);
		window.setVisible(true);
		window.setTitle(GameMain.name);
		window.setLocationRelativeTo(null);
		window.add(game);
		
		game.start();
	}
}
