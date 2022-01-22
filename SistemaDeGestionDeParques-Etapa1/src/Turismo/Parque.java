package Turismo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Parque {
	ArrayList<Usuario> usuarios;
	ArrayList<Ofertable> ofertable;
	int contadorDeSugerencias = 0; // Inicializa variable local para mostrar sugerencias numeradas al usuario

	public Parque() throws IOException {
		this.usuarios = cargaDeUsuariosPorArchivo("usuariosTierraMedia.txt");
		this.ofertable = cargaDeTodasLasAtraccionesYPromocionesPorArchivo("atraccionesTierraMedia.txt",
				"promocionesTierraMedia.txt");
		Collections.sort(ofertable, new Ordenador());
	}

	public void iniciarSistema() {
		System.out.println("************************PARQUE DE ATRACCIONES TIERRA MEDIA************************\n");
		for (Usuario u : usuarios) {
			mensajeDeBienvenida(u);
			generarSugerencias(u, ofertable);
			agregarAlArchivo(u);
		}
		System.out.println("      *****************************EXIT***********************************");

	}

	/**
	 * Metodo para generar sugerencias al usuario a partir de sus preferencias,
	 * tiempo y dinero
	 * 
	 * @param Usuario usuario y Arraylist de TodasLasAtracciones ya ordenadas
	 */

	private void generarSugerencias(Usuario usuario, ArrayList<Ofertable> ofertable) {

		for (Ofertable iterador : ofertable) {
			if (sonDelGustoDelUsuario(usuario, iterador) && !usuario.yaLaCompro(iterador)) {
				confirmacion(usuario, iterador);
			}
		}

		for (Ofertable iterador : ofertable) {
			if (!sonDelGustoDelUsuario(usuario, iterador) && !usuario.yaLaCompro(iterador)) {
				confirmacion(usuario, iterador);
			}
		}
		contadorDeSugerencias = 0;
		System.out.println("Ha terminado tu compra. En instantes estar� disponible tu itinerario. Gracias!");
		System.out.println("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_\n");
	}

	/**
	 * Metodo para mostrar los datos del usuario por pantalla antes de comenzar a
	 * comprar
	 * 
	 * @param Usuario usuario
	 */
	private void mensajeDeBienvenida(Usuario usuario) {
		System.out.println("Bienvenido " + usuario.getNombre() + "!\n- Dinero disponible: "
				+ usuario.getDineroDisponible() + " monedas de oro\n- Tiempo disponible: "
				+ usuario.getTiempoDisponible() + " horas\n- Tipo preferido: "
				+ usuario.getAtraccionPreferida().getNombreDeTipo().toUpperCase());
		System.out.println("");
		System.out.println("Arm� tu propia estad�a en nuestro parque con las siguientes sugerencias!\n");
	}

	/**
	 * Metodo para preguntar si una Atraccion/Promocion es del gusto del usuario
	 * 
	 * @param Usuario usuario y la Atraccion/Promocion
	 * @return Regresa True si es del gusto del usuario
	 */

	protected boolean sonDelGustoDelUsuario(Usuario usuario, Ofertable iterador) {
		return iterador.getTipoDeAtraccion() == usuario.getAtraccionPreferida();
	}

	/**
	 * Metodo para confirmar la sugerencia del Sistema de Gestion al usuario
	 * 
	 * @param Usuario usuario y la Atraccion/Promocion
	 * @throw crea una excepcion al ingresar una tecla incorrecta por teclado, la
	 *        cual salva dentro de catch llamando nuevamente el metodo
	 */

	private void confirmacion(Usuario usuario, Ofertable sugerencia) {
		if (usuario.tieneDineroDisponible(sugerencia) && usuario.tieneTiempoDisponible(sugerencia)
				&& sugerencia.hayCupo()) {
			contadorDeSugerencias++;
			System.out.println(contadorDeSugerencias + ") " + sugerencia.toString() + "\n");
			System.out.println("Desea aceptar la sugerencia? - Ingrese S/N");
			@SuppressWarnings("resource")
			Scanner in = new Scanner(System.in);
			String caracter = in.nextLine();
			caracter = caracter.toLowerCase();

			try {
				if (caracter.equals("s")) {
					usuario.confirmarSugerencia(sugerencia);
				} else if (!caracter.equals("n")) {
					throw new InvalidIO();
				}
			} catch (InvalidIO e) {
				contadorDeSugerencias--;
				confirmacion(usuario, sugerencia);
			}
		}
	}

	/**
	 * Metodo que agrega al archivo las sugerencias aceptadas de un usuario junto
	 * con el tiempo y dinero a emplear.
	 * 
	 * @param Usuario usuario y PrintWriter linea de archivo de texto
	 * @throws IOException
	 */
	private  void agregarAlArchivo(Usuario usuario) {
		PrintWriter salida = null;
		try {
			FileWriter nombreArchivo = new FileWriter(usuario.getNombre() + "-salida.out");
			salida = new PrintWriter(nombreArchivo);
			salida.println("************************PARQUE DE ATRACCIONES TIERRA MEDIA************************");
			salida.println(" ");
			salida.println(" Sr/a: " + usuario.getNombre());
			salida.println(" ");
			if (!usuario.getSugerenciasConfirmadas().isEmpty()) {
				salida.println(" Su itinerario incluye las siguientes atracciones: ");

				for (Ofertable promocion : usuario.getSugerenciasConfirmadas()) {
					salida.println("  � " + ((Atraccion) promocion).getNombreDeAtraccion());
				}
				salida.println(" ");
				salida.println(" El costo total es de " + usuario.getDineroGastado()
						+ " monedas de oro, el tiempo aproximado que pasar� es de " + usuario.getTiempoGastado()
						+ " horas.");
				salida.println(" ");
			} else {
				salida.println("El usuario no realizo ninguna compra");

			}

			salida.println("**************************************************************************************");

		} catch (IOException e) {
			e.printStackTrace();
		}

		finally {
			salida.close();
		}

	}

	/**
	 * Metodo que carga desde archivo los usuarios El formato de archivo debe ser:
	 * nombre,tipoPreferido,dinero,tiempo.
	 * 
	 * @return Regresa un Arraylist con todos los usuarios
	 * @throws En caso de que no se encuentre el archivo o contenga un dato
	 *            incorrecto
	 */

	private ArrayList<Usuario> cargaDeUsuariosPorArchivo(String archivoUsuarios) throws IOException {
		ArrayList<Usuario> usuarios = new ArrayList<Usuario>();
		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(archivoUsuarios);
			br = new BufferedReader(fr);
			String linea;
			while ((linea = br.readLine()) != null) {
				try {
					String[] campos = linea.split(",");
					String nombre = campos[0];
					TipoDeAtraccion tipoPreferido = TipoDeAtraccion.valueOf(campos[1]);
					int dineroDisponible = Integer.parseInt(campos[2]);
					double tiempoDisponible = Double.parseDouble(campos[3]);
					Usuario usuario = new Usuario(nombre, dineroDisponible, tiempoDisponible, tipoPreferido);
					usuarios.add(usuario);
				} catch (NumberFormatException e) {
					System.out.println("Dato ingresado inv�lido durante la lectura de usuarios.");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return usuarios;

	}

	/**
	 * Metodo que carga desde archivo las Atracciones El formato de archivo debe
	 * ser: nombre,costo,tiempo,cupo,tipoDeAtraccion
	 * 
	 * @return Regresa un Arraylist con todas las Atracciones
	 * @throws En caso de que no se encuentre el archivo o contenga un dato
	 *            incorrecto
	 */

	private ArrayList<Atraccion> cargaDeAtraccionesPorArchivo(String archivoAtracciones) throws IOException {
		ArrayList<Atraccion> atracciones = new ArrayList<Atraccion>();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(archivoAtracciones);
			br = new BufferedReader(fr);
			String linea;

			while ((linea = br.readLine()) != null) {
				try {
					String[] campos = linea.split(",");
					String nombre = campos[0];
					int costo = Integer.parseInt(campos[1]);
					double tiempo = Double.parseDouble(campos[2]);
					int cupo = Integer.parseInt(campos[3]);
					TipoDeAtraccion tipo = TipoDeAtraccion.valueOf(campos[4]);
					Atraccion atraccion = new Atraccion(nombre, costo, cupo, tiempo, tipo);
					atracciones.add(atraccion);
				} catch (NumberFormatException e) {
					System.out.println("Dato ingresado inv�lido durante la lectura de atracciones.");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return atracciones;
	}

	/**
	 * Metodo que carga desde archivo las promociones y desde otro metodo las
	 * atracciones. El formato de archivo debe ser por linea:
	 * tipoDePromocion,nombreDePromocion,tipoDeAtraccion,descuento o total, en caso
	 * de ser AxB se ingresa 0,atracciones incluidas separadas por comas. Las
	 * atracciones de una promocion deben ser del mismo tipo y contener al menos
	 * una.
	 * 
	 * @return Regresa un Arraylist con todas las Atracciones y Promociones
	 * @throws IOException
	 */
	private ArrayList<Ofertable> cargaDeTodasLasAtraccionesYPromocionesPorArchivo(String archivoAtracciones,
			String archivoPromociones) throws IOException {
		ArrayList<Atraccion> atracciones = cargaDeAtraccionesPorArchivo(archivoAtracciones);
		ArrayList<Ofertable> ofertable = new ArrayList<Ofertable>();
		ofertable.addAll(atracciones);

		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(archivoPromociones);
			br = new BufferedReader(fr);
			String linea;

			while ((linea = br.readLine()) != null) {
				try {
					String[] campos = linea.split(",");
					int cantidadDeAtraccionesEnPromo = campos.length - 4;
					String tipoDePromo = campos[0];
					String nombrePromocion = campos[1];
					TipoDeAtraccion tipoDeAtraccion = TipoDeAtraccion.valueOf(campos[2]);
					int descuento = Integer.parseInt(campos[3]);
					ArrayList<Atraccion> atraccionesPromocion = new ArrayList<Atraccion>();
					if (cantidadDeAtraccionesEnPromo > 1) {
						for (int i = 0; i < cantidadDeAtraccionesEnPromo; i++) {
							for (Atraccion a : atracciones) {
								if (a.getNombreDeAtraccion().equals(campos[i + 4])) {
									atraccionesPromocion.add(a);
								}
							}
						}
						if (todasSonDelMismoTipo(atraccionesPromocion, tipoDeAtraccion)) {
							Promocion promocion = crearPromocionSegunTipo(tipoDePromo, nombrePromocion, descuento,
									atraccionesPromocion);
							ofertable.add(promocion);
						}
					}
				} catch (NumberFormatException e) {
					System.out.println("Dato ingresado inv�lido durante la lectura de promociones.");
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ofertable;
	}

	/**
	 * Metodo que controla si todas las atracciones de una promo son del mismo tipo
	 * indicado
	 * 
	 * @return True si son todas del mismo tipo
	 */
	private boolean todasSonDelMismoTipo(ArrayList<Atraccion> atraccionesParaLaPromo, TipoDeAtraccion tipoDePromo) {
		boolean valor = true;
		for (Atraccion a : atraccionesParaLaPromo) {
			if (!a.getTipoDeAtraccion().equals(tipoDePromo))
				valor = false;
		}
		return valor;
	}

	/**
	 * Metodo que crea la promocion segun el tipo ingresado en el archivo
	 * 
	 * @param, string del tipo de promocion, nombre de la promocion, descuento o
	 * total, y el arraylist con las atraciones
	 * 
	 * @return Una Promocion del tipo correspondiente
	 */
	private Promocion crearPromocionSegunTipo(String tipoDePromo, String nombrePromocion, Integer descuento,
			ArrayList<Atraccion> atraccionesParaLaPromo) {
		Promocion promocion = null;
		if (tipoDePromo.contains("Absoluta")) {
			promocion = new PromocionAbsoluta(nombrePromocion, atraccionesParaLaPromo, descuento);
		}
		if (tipoDePromo.contains("Porcentual")) {
			promocion = new PromocionPorcentual(nombrePromocion, atraccionesParaLaPromo, descuento);
		}
		if (tipoDePromo.contains("AxB")) {
			promocion = new PromocionAxB(nombrePromocion, atraccionesParaLaPromo);
		}
		return promocion;
	}
}
