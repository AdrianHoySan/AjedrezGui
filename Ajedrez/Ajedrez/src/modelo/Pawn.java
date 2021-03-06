package modelo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import entrada.Coordenada;

public class Pawn extends Pieza implements Serializable{

	public Pawn(Color color,Coordenada posicion, JPTablero tablero) {
		super(posicion, tablero);
		// TODO Auto-generated constructor stub
	
		if(color == Color.WHITE) {
			tipo = Tipo.WHITE_PAWN;
		} else {
			tipo = Tipo.BLACK_PAWN;
		}
		colocate(posicion);
	}
	

	@Override
	public Set<Coordenada> getNextMoves() {
		
		Set<Coordenada> lista = new HashSet<Coordenada>();
		
		if(this.getColor() == Color.WHITE) {
			if(tablero.coordenadaEnTablero(posicion.up().left()))
				if(tablero.getCelda(posicion.up().left()).contienePieza()) {
					addCoordenada(posicion.up().left(), lista);
				}
			
			if(tablero.coordenadaEnTablero(posicion.up().right())) 
				if (tablero.getCelda(posicion.up().right()).contienePieza()) {
					addCoordenada(posicion.up().right(), lista);
				}
				
				
			if(tablero.coordenadaEnTablero(posicion.up())) {
				if(tablero.getCelda(posicion.up()).contienePieza()) {
					
				} else {
					addCoordenada(posicion.up(), lista);
					if(posicion.getEjeY() == 2) {
						if(tablero.getCelda(posicion.up().up()).contienePieza() == false)
							addCoordenada(posicion.up().up(), lista);
					} 
				}
			}
			

			
			
		}
		
		
		if(this.getColor() == Color.BLACK) {
			if(tablero.coordenadaEnTablero(posicion.down().left()))
				if(tablero.getCelda(posicion.down().left()).contienePieza()) {
					addCoordenada(posicion.down().left(), lista);
				}
			
			if(tablero.coordenadaEnTablero(posicion.down().right()))
				if (tablero.getCelda(posicion.down().right()).contienePieza()) {
					addCoordenada(posicion.down().right(), lista);
				} 
			

			
			if(tablero.coordenadaEnTablero(posicion.down())) {
				if(tablero.getCelda(posicion.down()).contienePieza()) {
					
				} else {
					addCoordenada(posicion.down(), lista);
					if(posicion.getEjeY() == 7) {
						if(tablero.getCelda(posicion.down().down()).contienePieza() == false)
							addCoordenada(posicion.down().down(), lista);
					} 
				}
			}
		}
		
		
		
		
		
		return lista;
	}
	

	
	@Override
	public void move(Coordenada c) {
		super.move(c);
		if(getColor() == Color.WHITE && posicion.getEjeY() == 8) {
			tablero.saveRemovedPiece(this);
			tablero.getBlancas().add(new Queen(getColor(),posicion,tablero));
		} else if(getColor() == Color.BLACK && posicion.getEjeY() == 1) {
			tablero.saveRemovedPiece(this);
			tablero.getNegras().add(new Queen(getColor(),posicion,tablero));
		}
	}

	private void addCoordenada(Coordenada p, Set<Coordenada> lista) {
		if(tablero.coordenadaEnTablero(p)) {
			if(tablero.getCelda(p).contienePieza()) {
				if(tablero.getCelda(p).getPieza().getColor() != getColor()) {
					lista.add(p);
				}
					
			} else {
				lista.add(p);
			}
		}
		
	}
	
}
