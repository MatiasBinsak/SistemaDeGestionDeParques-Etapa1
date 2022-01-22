package Turismo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Usuario {

	private String nombre;
	private int dineroDisponible;
	private double tiempoDisponible;
	TipoDeAtraccion atraccionPreferida;
	private ArrayList<Ofertable> sugerenciasConfirmadas;
	private int dineroGastado = 0;
	private double tiempoGastado = 0;

	public Usuario(String nombre, int presupuesto, double tiempoDisponible, TipoDeAtraccion atraccionPreferida) {
		this.nombre = nombre;
		this.dineroDisponible = presupuesto;
		this.tiempoDisponible = tiempoDisponible;
		this.atraccionPreferida = atraccionPreferida;
		this.sugerenciasConfirmadas = new ArrayList<Ofertable>();
	}

	public int getDineroDisponible() {
		return dineroDisponible;
	}

	public void disminuirDineroDisponible(int dineroDisponible) {
		this.dineroDisponible -= dineroDisponible;
	}

	public double getTiempoDisponible() {
		return tiempoDisponible;
	}

	public void disminuirTiempoDisponible(double tiempoDisponible) {
		this.tiempoDisponible -= tiempoDisponible;
	}

	public ArrayList<Ofertable> getSugerenciasConfirmadas() {
		return sugerenciasConfirmadas;
	}

	public int getDineroGastado() {
		return dineroGastado;
	}

	public void aumentarDineroGastado(int dineroGastado) {
		this.dineroGastado += dineroGastado;
	}

	public double getTiempoGastado() {
		return tiempoGastado;
	}

	public void aumentarTiempoGastado(double tiempoGastado) {
		this.tiempoGastado += tiempoGastado;
	}

	public String getNombre() {
		return nombre;
	}

	public TipoDeAtraccion getAtraccionPreferida() {
		return atraccionPreferida;
	}

	public void setSugerenciasConfirmadas(Ofertable confirmada) {
		this.sugerenciasConfirmadas.addAll(confirmada.getAtraccionesIncluidas());
	}

	public void confirmarSugerencia(Ofertable confirmada) {
		setSugerenciasConfirmadas(confirmada);
		confirmada.decrementarCupo();
		setTiempoYDinero(confirmada);
	}

	private void setTiempoYDinero(Ofertable confirmada) {
		disminuirDineroDisponible(confirmada.getCosto());
		aumentarDineroGastado(confirmada.getCosto());
		aumentarTiempoGastado(confirmada.getTiempo());
		disminuirTiempoDisponible(confirmada.getTiempo());
	}

	public boolean yaLaCompro(Ofertable sugerencia) {
		boolean resultado = false;
		resultado = sugerenciasConfirmadas.contains(sugerencia);
		if (!resultado) {
			for (Ofertable s : sugerencia.getAtraccionesIncluidas()) {
				resultado = sugerenciasConfirmadas.contains(s);
			}
		}
		return resultado;
	}

	public boolean tieneDineroDisponible(Ofertable sugerencia) {
		return getDineroDisponible() >= sugerencia.getCosto();
	}

	public boolean tieneTiempoDisponible(Ofertable sugerencia) {
		return getTiempoDisponible() >= sugerencia.getTiempo();
	}

}
