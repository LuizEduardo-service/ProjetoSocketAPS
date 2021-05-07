import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

class Jogador {
	public Socket cliente;
	public String ip;
	public String apelido;
	public boolean isHostSala;

	public Jogador(Socket cliente) {
		this.cliente = cliente;
		isHostSala = false;
		int aleatório = (int) (Math.random() * 1000);
		this.apelido = "Jogador" + aleatório;
		this.ip = cliente.getInetAddress().getHostAddress();
	}
}

class Sala {
	private int idSala;
	private String nomeSala;
	private int maxJogadores;
	private String ipHost;
	private int numJogadores;

	public Sala(int idSala, String nomeSala, int maxJogadores, String ipHost, int numJogadores) {
		this.idSala = idSala;
		this.nomeSala = nomeSala;
		this.maxJogadores = maxJogadores;
		this.ipHost = ipHost;
		this.numJogadores = numJogadores;
	}

	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		obj.put("idSala", this.idSala);
		obj.put("ipHost", this.ipHost);
		obj.put("nomeSala", this.nomeSala);
		obj.put("maxJogadores", this.maxJogadores);
		obj.put("numJogadores", this.numJogadores);

		return obj;

	}

}

public class Servidor {

	public int porta = 12345;
	public List<Jogador> jogadores;
	public List<JSONObject> salas;
	public int lastIDSala;

	public Servidor(int porta) {
		this.lastIDSala = -1;
		this.porta = porta;
		jogadores = new ArrayList<>();
		salas = new ArrayList<>();
	}

	public int getIDSala() {
		lastIDSala += 1;
		return lastIDSala;

	}

	public JSONObject listarSalas() {
		return new JSONObject();
	}

	public int criarSala(String nomeSala, int maxJogadores, String ipHost) {
		int id = getIDSala();
		Sala sala = new Sala(id, nomeSala, maxJogadores, ipHost, 1);
		salas.add(sala.toJSON());
		System.out.println("Sala criada!" + nomeSala);
		return id;
	}

	public void excluirSala(int idSala) {
		for (int i = 0; i < salas.size(); i++) {
			if (salas.get(i).getInt("idSala") == idSala) {
				salas.remove(i);
				break;
			}
		}
	}

	public void removerJogadorSala(int idSala) {

		for (int i = 0; i < salas.size(); i++) {
			if (salas.get(i).getInt("idSala") == idSala) {
				int n = salas.get(i).getInt("numJogadores");
				salas.get(i).remove("numJogadores");
				salas.get(i).put("numJogadores", n - 1);
				break;
			}

		}

	}

	public void broadcast(String mensagem) {

	}

	public void executa() throws IOException {

		ServerSocket servidor = new ServerSocket(porta);

		System.out.println("Porta 12345 aberta");

		while (true) {

			Socket cliente = servidor.accept();
			System.out.println("Nova conexão com o cliente " + cliente.getInetAddress().getHostAddress());

		}

		Jogador jogador = new Jogador(cliente);
		this.jogadores.add(jogador);

		TrataCliente tc = new TrataCliente(jogador, this);

		new Thread(tc).start();
	}

}

class TrataCliente implements Runnable {
	private InputStream clienteIn;
	private PrintStream clienteOut;
	private Servidor servidor;
	private Jogador jogador;
	JSONObject requisicao;
	public int idSala;
	public boolean isHostSala;

	public TrataCliente(Jogador jogador, Servidor servidor) throws IOException {
		this.isHostSala = false;
		this.idSala = -1;
		this.jogador = jogador;
		this.clienteIn = jogador.cliente.getInputStream();
		this.clienteOut = new PrintStream(jogador.cliente.getOutputStream());
		this.servidor = servidor;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
