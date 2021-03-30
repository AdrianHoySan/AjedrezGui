package modelo;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.LineBorder;




public class Celda extends JButton implements Serializable {

	private Pieza pieza;
	
	private Dimension dimension = new Dimension(50,50);
	private java.awt.Color colorCeldaNegra = new java.awt.Color(210,129,64);
	private java.awt.Color colorCeldaBlanca = new java.awt.Color(230,205,174);
	
	public Celda() {
		super();
		pieza = null;
		setPreferredSize(dimension);
	}
	
	public Celda(Pieza pieza) {
		this.pieza = pieza;
	}

	public Pieza getPieza() {
		return pieza;
	}

	public void setPieza(Pieza pieza) {
		this.pieza = pieza;
			
		if (pieza != null) {
			Image image = (new ImageIcon(Celda.class.getResource("/media/" + pieza.getTipo().getForma())).getImage());
			ImageIcon imageIconResized = new ImageIcon(getScaledImage(image,25));
			setIcon(imageIconResized);
		} else {
			setIcon(null);
		}
	}	
		
		
	public boolean contienePieza() {
		return pieza != null;
	}
	
	public void setCellBackground(Color color) {
		if(color == Color.WHITE)
			setBackground(colorCeldaBlanca);
		else
			setBackground(colorCeldaNegra);
	}

	public void resaltar(java.awt.Color color, int size) { 
		setBorder(new LineBorder(color, size));
	}
	
	private Image getScaledImage(Image srcImg, int size){
		int h = size, w = size;
		
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}

	
	@Override
	public String toString() {
		if(pieza == null) {
			return " ";
		} else {
			return pieza.toString();
		}
	}
	
}
