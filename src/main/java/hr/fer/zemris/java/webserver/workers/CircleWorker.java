package hr.fer.zemris.java.webserver.workers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Produces a PNG image with a single filled circle and displays it.
 * @author 0036502252
 *
 */
public class CircleWorker implements IWebWorker {
	/**
	 * Width of the bounding rectangle.
	 */
	private static final int WIDTH = 200;
	/**
	 * Height of the bounding rectangle.
	 */
	private static final int HEIGHT = 200;
	/**
	 * Predefined X coordinate of the bounding rectangle.
	 */
	private static final int X_POS = 0;
	/**
	 * Predefined Y coordinate of the bounding rectangle.
	 */
	private static final int Y_POS = 0;
	
	@Override
	public void processRequest(RequestContext context) throws Exception {
		BufferedImage bim = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
		
		Graphics2D g2d = bim.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(X_POS, Y_POS, WIDTH, HEIGHT);
		g2d.setColor(Color.BLUE);
		
		g2d.fillOval(X_POS, Y_POS, WIDTH, HEIGHT);
		g2d.dispose();
		
		context.setMimeType("image/png");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(bim, "png", bos);
			context.write(bos.toByteArray());
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
}