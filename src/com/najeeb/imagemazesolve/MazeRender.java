package com.najeeb.imagemazesolve;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import com.najeeb.imagemazesolve.Maze.Region;

public class MazeRender {
	public static BufferedImage drawRegions(ArrayList<Region> regions, int w, int h, int colormode) {
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = image.getGraphics();
		Random rand = new Random();
		g.clearRect(0, 0, w, h);
		int gb = 255;
		for (Region r : regions) {
			int colr = 0;
			int colg = 0;
			int colb = 0;
			if (colormode == -1) {
				rand.nextInt(128);
				colr = rand.nextInt(128) + 100;
				colg = rand.nextInt(128) + 100;
				colb = rand.nextInt(128) + 100;
			}
			else {
				colr = 255;
				gb = (gb > 1)? gb - 2 : gb;
				colg = gb;
				colb = gb;
			}
			g.setColor(new Color(colr, colg, colb));
			g.fillRect(r.topl.px, r.topl.py, r.botr.px - r.topl.px + 1, r.botr.py - r.topl.py + 1);

		}
		return image;
	}

	public static void writeImageToPng(BufferedImage image, String filepath) {
		try {
			ImageIO.write(image, "png", new File(filepath + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
