package cliente;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONObject;

import Servidor.Jogador;
import Servidor.Sala;
import javafx.stage.WindowEvent;

public class Jogador {
	private String ip;
	private String apelido;
	private String host;
	private int porta;
	public boolean isHostSala;
	private boolean conectadoServidor;
	Socket servidor;
	Socket sala;
	Sala hSala;

	public boolean conectarServidor(String host, int porta) throws UnknownHostException {
		boolean sucesso = true;
		this.host = host;
		this.porta = porta;

		try {
			servidor = new Socket(host, porta);
			this.ip = servidor.getLocalAddress().toString();
			conectadoServidor = true;
		} catch (Exception e) {
			sucesso = false;
		}

		return sucesso;
	}

	public String getIP() {
		return this.ip;
	}

	public void entrarSala(String ipSala) throws IOException {
		JSONObject requisicao = new JSONObject();
		requisicao.put("tipo", "entrarSala");
		requisicao.put("ipHost", ipSala);

		PrintStream out = new PrintStream(servidor.getOutputStream());
		out.println(requisicao);

		Scanner in = new Scanner(servidor.getInputStream());
		JSONObject resposta = new JSONObject(in.nextLine());

		if (resposta.getBoolean("aceito")) {

			sala = new Socket(ipSala, 9999);
			JogadorAtivo ja = new JogadorAtivo(sala, this);
			new Thread(ja).start();

		} else {
			JOptionPane.showMessageDialog(new JFrame(), "Sala cheia", "Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	public JSONObject getListaSala() throws IOException {

		JSONObject requisicao = new JSONObject();
		requisicao.put("tipo", "getListaSalas");

		PrintStream out = new PrintStream(servidor.getOutputStream());
		out.println(requisicao);

		Scanner in = new Scanner(servidor.getInputStream());

		JSONObject lista = new JSONObject(in.nextLine());

		return lista;

	}

	public void criarSala(String nomeSala) throws IOException {
		JSONObject requisicao = new JSONObject();
		requisicao.put("tipo", "criarSala");
		requisicao.put("nomeSala", nomeSala);
		requisicao.put("maxJogadores", 2);

		PrintStream out = new PrintStream(servidor.getOutputStream());
		out.println(requisicao);

		hSala = new Sala(nomeSala);
		new Thread(hSala).start();

		sala = new Socket(hSala.getIpHost(), 9999);
		JogadorAtivo ja = new JogadorAtivo(sala, this);
		new Thread(ja).start();

	}

	public void desconectar() throws IOException {
		sala.close();
	}

	public void sairSala() throws IOException, Throwable {

		if (this.isHostSala) {
			destruirSala();
		} else {
			JSONObject requisicao = new JSONObject();
			requisicao.put("tipo", "sairSala");
			PrintStream out = new PrintStream(servidor.getOutputStream());
			out.println(requisicao);
			desconectar();
		}

	}

	public void destruirSala() throws IOException, Throwable {
		JSONObject requisicao = new JSONObject();
		requisicao.put("tipo", "destruirSala");

		PrintStream out = new PrintStream(servidor.getOutputStream());
		out.println(requisicao);

		hSala.fecharSala();
	}

	public String getApelido() {
		return this.apelido;
	}

	public void setApelido() {
		this.apelido = apelido;
	}

	public static void main(String[] args) throws UnknownHostException {
		Jogador jogador = new Jogador();
		jogador.conectadoServidor = false;

		JFrame janela = new JanelaSelecionaServidor(jogador);
		janela.setTitle("Jogo da Velha");
		janela.setLocationRelativeTo(null);
		janela.setVisible(true);

		while (!jogador.conectadoServidor) {
			System.out.println("");
		}

		janela.dispose();
		janela = new JanelaSelecionarSala(jogador);
		janela.setTitle("Jogo da Velha");
		janela.setLocationRelativeTo(null);
		janela.setVisible(true);

	}

	class JogadorAtivo implements Runnable {

		private Socket servidorSala;
		private JanelaJogo janela;
		private InputStream salaIn;
		private PrintStream salaOut;
		private JSONObject requisicao;
		private JSONObject resposta;
		private Jogador jogador;

		public JogadorAtivo(Socket servidorSala, Jogador jogador) throws IOException {
			janela = new JanelaJogo(this);
			janela.setTitle("Jogo da Velha");
			janela.setLocationRelativeTo(null);
			janela.setVisible(true);

			this.jogador = jogador;
			this.servidorSala = servidorSala;
			salaIn = servidorSala.getInputStream();
			salaOut = new PrintStream(servidorSala.getOutputStream());

		}

		public void novoJogo() {

			janela.dispatchEvent(new WindowEvent(janela, WindowEvent.WINDOW_CLOSING));
			janela = new JanelaJogo(this);
			janela.setTitle("Jogo da Velha");
			janela.setLocationRelativeTo(null);
			janela.setVisible(true);

		}

		public void sairJogo() throws IOException, Throwable {
			resposta = new JSONObject();
			resposta.put("tipo", "desconectar");
			salaOut.println(resposta);
			servidorSala.close();
			jogador.sairSala();
			janela.setVisible(false);
			janela.dispose();

		}

		public void enviarJogada(int x, int y) {
			JSONObject jogada = new JSONObject();
			jogada.put("tipo", "jogada");
			jogada.put("x", x);
			jogada.put("y", y);
			salaOut.println(jogada);

		}

		public void enviarMensagem(String mensagem) {
			requisicao = new JSONObject();
			requisicao.put("tipo", "mensagem");
			requisicao.put("mensagem", mensagem);
			salaOut.println(requisicao);
		}

		@Override
		public void run() {
			Scanner scan = new Scanner(this.salaIn);
			String tipoSolicitacao = "";
			JSONObject resposta;

			while (scan.hasNextLine()) {
				requisicao = new JSONObject();
				tipoSolicitacao = requisicao.getString("tipo");

				switch (tipoSolicitacao) {
				case "mensagem":
					janela.imprimirMensagem(requisicao.getString("mensagem"));
					break;
				case "jogada":
					janela.computarJogada(requisicao.getInt("x"), requisicao.getInt("y"),
							requisicao.getInt("idJogador"));
					break;
				case "sairSala":
					try {
						jogador.desconectar();
						janela.setVisble(false);
						janela.dispose();
						this.finalize();
					} catch (Throwable ex) {
						Logger.getLogger(JogadorAtivo.class.getName()).log(Level.SEVERE, null, ex);
					}
					break;
				case "fimJogo":
					Object[] options = { "Sim", "Não" };

					int outra = JOptionPane.showOptionDialog(new JFrame(),
							requisicao.getString("mensagem") + "Jogar Novamente?", requisicao.getString("mensagem"),
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					if (outra == 0) {
						novoJogo();
					} else {
						try {
							sairJogo();
						} catch (Throwable e) {
							Logger.getLogger(JogadorAtivo.class.getName()).log(Level.SEVERE, null, e);
						}
					}
					break;
				default:

					break;

				}

			}

		}

	}

}
