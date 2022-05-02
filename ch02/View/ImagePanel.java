package ch02.View;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private BufferedImage bgImage;
	private String imagePath = "background.png";

	public ImagePanel() {
		setSize(500, 500);
		try {
			bgImage = ImageIO.read(new File(""+imagePath));
		} catch (IOException e) {
			System.out.println("이미지 파일을 찾지 못하였습니다.");
		}
		setLayout(null);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
	}
}