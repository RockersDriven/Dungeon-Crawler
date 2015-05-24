package com.rentarosato520.dungeoncrawler.mob;

import java.awt.Graphics;
import java.util.Random;

import com.rentarosato520.dungeoncrawler.Handler;

public class Zombie extends Mob{
	public int health = 150, maxHealth =150;
	public boolean Regenerate= false;
	public boolean ItemDrop= true;
	public Random r = new Random();
	
public Zombie(float x, float y, int w, int h, float weight, Handler han){
	super(x, y, w, h, weight, han);
	
	calRegenerate();
}

public void calRegenerate(){
	//Regeneration effects of Zombie
	if(health < maxHealth && Regenerate){
		health++;
	}
}

public void tick(){
	
}

public void render(Graphics g){
	
}
}
