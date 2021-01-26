package com.najeeb.imagemazesolve;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MazeInterpreter {
	
	public static boolean[][] getObstacleFromImage(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		boolean[][] obstacle = new boolean[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int clr =  img.getRGB(i,j); 
				int  r   = (clr & 0x00ff0000) >> 16;
				int  g = (clr & 0x0000ff00) >> 8;
				int  b =  clr & 0x000000ff;
				if (r+g+b > 386)obstacle[i][j] = false;
				else obstacle[i][j] = true;
			}
		}
		return obstacle;
		
	}
	
	public static BufferedImage readPNGFile(String filepath) {
		try {
			BufferedImage image = ImageIO.read(new File(filepath+".png"));
			return image;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
