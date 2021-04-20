package controlador;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

import configuracion.MyConfig;
import entrada.Coordenada;
import entrada.Herramientas;
import modelo.Celda;
import modelo.Color;
import modelo.GestionFichasEliminadas;
import modelo.JPTablero;
import modelo.Movement;

import modelo.Pawn;
import modelo.Pieza;
import modelo.Player;

import vista.JPTurno;
import vista.VistaChess;
import vista.VistaPropiedades;

public class ControladorJuego implements ActionListener, MouseListener{


	

	private VistaChess vista = new VistaChess();
	private VistaPropiedades propiedades;
	private Pieza piezaSeleccionada = null;
	private GestionFichasEliminadas gestionFichasEliminadas;
	private DefaultListModel<Movement> dlm;
	private Deque<Movement> stack;
	
	
	
	
	public ControladorJuego(VistaChess vista) {	
		this.vista = vista;
		
		stack = new ArrayDeque<Movement>();
		
		inicializar();
	}
	
	private void inicializar() {
		
		
		
		gestionFichasEliminadas = new ControladorFichasEliminadas(vista.getPanelEliminadas());
		
		Component[] components = vista.getPanelTablero().getComponents();
		
		for(Component component : components) {
			
			if(component instanceof Celda) {
				((Celda) component).addActionListener(this);
			}
			
		}
		
		//Añadir los MouseListener
		vista.getPanelMovements().getList().addMouseListener(this);
		
		//Añadir ActionListener
		vista.getMntmProperties().addActionListener(this);
		vista.getPanelMovements().getBtnDelante().addActionListener(this);
		vista.getPanelMovements().getButtonAtras().addActionListener(this);
		
		//Añadir ActionCommand
		vista.getMntmProperties().setActionCommand("Abrir preferencias");
		vista.getPanelMovements().getBtnDelante().setActionCommand("Next Movement");
		vista.getPanelMovements().getButtonAtras().setActionCommand("Previous Movement");
		
		dlm = new DefaultListModel<Movement>();
		vista.getPanelMovements().getList().setModel(dlm);
		
		
		
	}
	

	
	public void go() {
		
		vista.setVisible(true);

	
	}
	
	


	@Override
	public void actionPerformed(ActionEvent arg0) {
		String comando = arg0.getActionCommand();
		
		if(comando.equals("Abrir preferencias")) {
			
			abrirPreferencias();
			
		} else if(comando.equals("Cambiar color celda blanca")) {
			
			cambiarColorCeldaBlanca();
			
		} else if(comando.equals("Cambiar color celda negra")) {
			
			cambiarColorCeldaNegra();
			
		} else if(arg0.getSource() instanceof Celda) {
			
			comprobarMovimiento((Celda)arg0.getSource());
			
		} else if(comando.equals("Previous Movement")) {
			
			previousMovement();
			
		} else if(comando.equals("Next Movement")) {
			
			nextMovement();
			
		}
		
		
	}

	private void nextMovement() {
		
		try {
			
			Movement m = stack.pop();
			dlm.addElement(m);
			Coordenada origen,destino;
			
			origen = m.getOrigen();
			destino = m.getDestino();
			
			switch(m.getTipoAccion()) {
			case Movement.NOT_KILL : 
				

				
				vista.getPanelTablero().getCelda(origen).getPieza().move(destino);
				
				break;
			case Movement.KILL :
				
				gestionFichasEliminadas.addPiece(vista.getPanelTablero().getCelda(destino).getPieza());
				vista.getPanelTablero().getCelda(origen).getPieza().move(destino);
				
				
				
			break;
			case Movement.RISE :
				
				vista.getPanelTablero().getCelda(origen).getPieza().move(destino);
				
				
				break;
				
			default:throw new Exception("Tipo desconocido");
				
			}
			
			vista.getPanelTurno().cambiarTurno();
			Movement.increaseNumberOfMovements();
			
		
		} catch (NoSuchElementException ne) {
		
			JOptionPane.showMessageDialog(vista, "No hay movimientos para avanzar", "Error", JOptionPane.ERROR_MESSAGE);
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	private void previousMovement() {
		
		try {
			
			Movement m = dlm.remove(dlm.getSize() -1);
			stack.push(m);
			Coordenada origen,destino;
			
			destino = m.getDestino();
			origen = m.getOrigen();
			
			switch(m.getTipoAccion()) {
			case Movement.NOT_KILL :
				

				
				
				vista.getPanelTablero().getCelda(destino).getPieza().move(origen);
				
				
				
				break;
			case Movement.KILL :
				
		
				
				vista.getPanelTablero().getCelda(destino).getPieza().move(origen);
				vista.getPanelTablero().getCelda(destino).setPieza(m.getFicha());
				
				gestionFichasEliminadas.removePiece(m.getFicha());
				
				if(m.getFicha().getColor() == Color.WHITE) {
					vista.getPanelTablero().getBlancas().add(m.getFicha());
				} else {
					vista.getPanelTablero().getNegras().add(m.getFicha());
				}
				
				break;
				
			case Movement.RISE :
				
				vista.getPanelTablero().getCelda(origen).setPieza(m.getFichaPeon());
				vista.getPanelTablero().getCelda(destino).setPieza(null);
				

				break;
				
			default:throw new Exception("Tipo desconocido");
			
			}
			
			vista.getPanelTurno().cambiarTurno();
			Movement.decreaseNumberOfMovements();
			
		} catch (ArrayIndexOutOfBoundsException ae) {
			JOptionPane.showMessageDialog(vista, "No hay anteriores movimientos", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		
	}

	private void comprobarMovimiento(Celda c) {
		
		if(piezaSeleccionada==null) {
			movimientoSinPiezaSeleccionada(c);
		} else {
			movimientoConPiezaSeleccionada(c);
		}
		
	}

	private void movimientoConPiezaSeleccionada(Celda c) {
		JPTablero tablero = vista.getPanelTablero();
		Coordenada coor = tablero.getCoordenadaFromCell(c);
		
		if(piezaSeleccionada.getNextMoves().contains(coor) == false) {
			JOptionPane.showMessageDialog(vista, "No puedes mover ahi", "Error", JOptionPane.ERROR_MESSAGE);

		} else {
			Movement m = null;
			
			Coordenada origen = piezaSeleccionada.getPosicion();
			Coordenada destino = tablero.getCoordenadaFromCell(c);
			
			desmarcarPosiblesDestinos(tablero);
			
			// Comprobamos si matamos pieza
			if(c.contienePieza()) {
				// Comprobamos si un peon promociona
				if((tablero.getCoordenadaFromCell(c).getEjeY() == 8 || tablero.getCoordenadaFromCell(c).getEjeY() == 1) && piezaSeleccionada instanceof Pawn) {
					m = new Movement(origen,destino,Movement.RISE_AND_KILL,c.getPieza(),null,piezaSeleccionada);
					
				} else {
					m = new Movement(origen,destino,Movement.KILL,c.getPieza(),null,null);
				}
				gestionFichasEliminadas.addPiece(c.getPieza());
			}
			//Comprobamos si no se ha comido pieza (m != null) y comprobamos si el peon promociona
			if(m == null && (tablero.getCoordenadaFromCell(c).getEjeY() == 8 || tablero.getCoordenadaFromCell(c).getEjeY() == 1) && piezaSeleccionada instanceof Pawn) {
				
				m = new Movement(origen,destino,Movement.RISE,null,null,piezaSeleccionada);
				
				// Comprobamos si no se ha dado ninguna de las condiciones anteriores por lo tanto es un movimiento sin muerte ni promoción
			} else if(m == null) {
				
				m = new Movement(origen,destino,Movement.NOT_KILL,null,null,null);
			}
			//Agragamos el movimiento a la lista
			dlm.addElement(m);
			piezaSeleccionada.move(coor);
			//Comprobamos si ha habido promocion y si la ha habido indicamos en el movimiento el tipo de ficha en la que ha promocionado el peon
			if(m.getTipoAccion() == Movement.RISE || m.getTipoAccion() == Movement.RISE_AND_KILL) {
				m.setFichaGenerada(c.getPieza());
			}
			
			piezaSeleccionada = null;
			borrarPiezaSeleccionada();
			vista.getPanelTurno().cambiarTurno();
			comprobacionesFinales(tablero);

			
		}
		
	}

	private void desmarcarPosiblesDestinos(JPTablero tablero) {
		for(Coordenada coordenada : piezaSeleccionada.getNextMoves()) {
			tablero.getCelda(coordenada).setBorder(new LineBorder(java.awt.Color.gray,1));
		}
	}

	private void borrarPiezaSeleccionada() {
		
		vista.getPanelTurno().getLblSelectedPiece().setOpaque(false);
		vista.getPanelTurno().getLblSelectedPiece().setIcon(null);
		
	}

	private void comprobacionesFinales(JPTablero tablero) {
		if(tablero.whiteCheck()) {
			JOptionPane.showMessageDialog(vista, "El rey negro esta en jaque", "Info", JOptionPane.INFORMATION_MESSAGE);

		}  
		if(tablero.blackCheck()) {
			JOptionPane.showMessageDialog(vista, "El rey blanco esta en jaque", "Info", JOptionPane.INFORMATION_MESSAGE);

		}
		if(!tablero.getBlancas().contains(tablero.getWhiteKing())) {
			JOptionPane.showMessageDialog(vista, "Las blancas pierden", "Info", JOptionPane.INFORMATION_MESSAGE);
			terminarJuego();
		} 
		if(!tablero.getNegras().contains(tablero.getBlackKing())) {
			JOptionPane.showMessageDialog(vista, "Las negras pierden", "Info", JOptionPane.INFORMATION_MESSAGE);
			terminarJuego();
		}
	}

	private void movimientoSinPiezaSeleccionada(Celda c) {
		
		if(!c.contienePieza()) {
			JOptionPane.showMessageDialog(vista, "Debes seleccionar una pieza", "Error", JOptionPane.ERROR_MESSAGE);
		} else if(c.getPieza().getColor()!=vista.getPanelTurno().getTurno()) {
			JOptionPane.showMessageDialog(vista, "Debes seleccionar una pieza de tu color", "Error", JOptionPane.ERROR_MESSAGE);
		} else if(c.getPieza().getNextMoves().size()==0) {
			JOptionPane.showMessageDialog(vista, "Esa pieza no la puedes mover", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			piezaSeleccionada = c.getPieza();
			selectPiece();
			posiblesDestinos();
			
		}
		
		

		
	}

	private void selectPiece() {
		
		vista.getPanelTurno().getLblSelectedPiece().setOpaque(true);
		vista.getPanelTurno().getLblSelectedPiece().setIcon(new ImageIcon(Celda.class.getResource("/media/" + piezaSeleccionada.getTipo().getForma())));
		
		
	}

	private void posiblesDestinos() {
		Set<Coordenada> posiblesMovimientos = piezaSeleccionada.getNextMoves();
		JPTablero tablero = vista.getPanelTablero();
		for(Coordenada coor : posiblesMovimientos)  {
			Celda celda = tablero.getCelda(coor);
			if(!celda.contienePieza())
				celda.resaltar(Celda.colorBordeCelda, 2);
			else
				celda.resaltar(Celda.colorBordeCeldaComer, 2);
		}
	}

	private void cambiarColorCeldaBlanca() {
		
		java.awt.Color color = JColorChooser.showDialog(propiedades.getBtnColorCeldaBlanca(),"Selecciona un color",propiedades.getBtnColorCeldaBlanca().getBackground());
		
		if(color != null) {
			
			propiedades.getBtnColorCeldaBlanca().setBackground(color);
			MyConfig.getInstance().setWhiteCellColor(color);
			Celda.colorCeldaBlanca = color;
			vista.getPanelTablero().repaintBoard();
			
		}
		
	}
	
	private void cambiarColorCeldaNegra() {
		
		java.awt.Color color = JColorChooser.showDialog(propiedades.getBtnColorCeldaNegra(), "Selecciona un color", propiedades.getBtnColorCeldaNegra().getBackground());
		
		if(color != null) {
			
			propiedades.getBtnColorCeldaNegra().setBackground(color);
			MyConfig.getInstance().setBlackCellColor(color);
			Celda.colorCeldaNegra = color;
			vista.getPanelTablero().repaintBoard();
			
		}
	}

	private void abrirPreferencias() {
		
		propiedades = new VistaPropiedades();
		
		propiedades.setVisible(true);
		
		//Add Action Listener
		propiedades.getBtnColorCeldaBlanca().addActionListener(this);
		propiedades.getBtnColorCeldaNegra().addActionListener(this);
		propiedades.getBtnBordeNormal().addActionListener(this);
		propiedades.getBtnBordeComer().addActionListener(this);
		
		//Add Action Command
		propiedades.getBtnColorCeldaBlanca().setActionCommand("Cambiar color celda blanca");
		propiedades.getBtnColorCeldaNegra().setActionCommand("Cambiar color celda negra");
		
		
	}
	
	private void terminarJuego() {
		
		Component[] components = vista.getPanelTablero().getComponents();
		
		for(Component component : components) {
			
			if(component instanceof Celda) {
				((Celda) component).setEnabled(false);
			}
			
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		
		Component c = me.getComponent();
		
		if(c == vista.getPanelMovements().getList()) {
			int index = vista.getPanelMovements().getList().getSelectedIndex();
			while(dlm.getSize() > index) {
				previousMovement();
			
			}
		}
		
		
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
}
