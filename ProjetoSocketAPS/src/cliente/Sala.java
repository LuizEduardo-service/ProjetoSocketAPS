package cliente;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import Servidor.Sala;

public class Sala implements Runnable{
	
	private ServerSocket servidor;
	private String ipHost;
	private int maxJogadores;
	private String nomeSala;
	private List<TrataJogador> jogadores;
	private int[][] tabuleiro = new int[3][3];
	int lastID;
	int vezJogador;
	
	public Sala(String nomeSala) {
	jogadores = new ArrayList<>();
	servidor = new ServerSocket(9999);
	servidor.setReuseAddress(true);
	ipHost=servidor.getInetAddress().getHostAddress();
	vezJogador= 1;
	lastID=1;
	
	
	}

	public void resetarTabuleiro() {
		tabuleiro = new int[3][3];
	}
	
	public void Jogada(int idJogador,int x, int y) {
		int vitoria;
		
		if(idJogador==1) {
			tabuleiro[x][y]=1;
			
		}else if(idJogador==2) {
			tabuleiro[x][y]=-1;
		}
		
		vezJogador=(vezJogador==1)? 2:1;
		
	}
	
	public boolean fimJogo() {
		int vitoria=vitoria();
		
		if(vitoria!=0) {
			return true;
		}else {
			return false;
		}
	}

	public int vitoria() {
		int retorno=0;
		
		if(checarLinhas()!=0) {
			retorno=checarLinhas();
		}
		else if(checarColunas()!=0) {
			retorno =checarColunas();
		}
		else if(checarDiagonais()!=0) {
			retorno =checarDiagonais();
		}
		else if(tabuleiroPreenchido()) {
			retorno =-1;
		}
		
		
		return retorno;
		
		
	}

	private boolean tabuleiroPreenchido() {
 int soma=0;
 for(int x=0;x<3;x++) {
	 for(int y=0;y<3;y++) {
		 soma++;
	 }
 }

		return (soma==9)? true:false;
	}

	private int checarDiagonais() {
		if((tabuleiro[0][0]+tabuleiro[1][1]+tabuleiro[2][2])==-3){
			return 2;
		}
		if((tabuleiro[0][0]+tabuleiro[1][1]+tabuleiro[2][2])==3){
			return 1;
		}
		if((tabuleiro[0][2]+tabuleiro[1][1]+tabuleiro[2][0])==-3){
			return 2;
		}
		if((tabuleiro[0][2]+tabuleiro[1][1]+tabuleiro[2][0])==3){
			return 1;
		}
		return 0;
	}

	private int checarColunas() {
		for(int colunas =0;colunas<3;colunas++) {
			if((tabuleiro[colunas][0]+tabuleiro[colunas][1]+tabuleiro[colunas][2])==-3){
				return 2;
			}
			
			if((tabuleiro[colunas][0]+tabuleiro[colunas][1]+tabuleiro[colunas][2])==3){
				return 1;
			}
		}
		return 0;
	}

	private int checarLinhas() {
		for(int linha =0;linha<3;linha++) {
			if((tabuleiro[linha][0]+tabuleiro[linha][1]+tabuleiro[linha][2])==-3){
				return 2;
			}
			
			if((tabuleiro[linha][0]+tabuleiro[linha][1]+tabuleiro[linha][2])==3){
				return 1;
			}
		}
		return 0;
	}
	
	public void fecharSala()throws IOException,Throwable{
		System.out.println("fechar");
		this.servidor.close();
		this.finalize();
	}
	
	public void removerJogador(int idJogador) {
		for(int i=0;i<jogadores.size();i++) {
			
			if(jogadores.get(i).idJogador==idJogador) {
				jogadores.remove(i);
				lastID=idJogador;
			}
		
		}
	}

	public List<TrataJogador>getListaJogadores(){
		return jogadores;
	}
	
	public String getIpHost() {
		return this.ipHost;
		
	}
	
	public void setIpHost(String novoIp) {
		this.ipHost=novoIp;
	}

	@Override
	public void run() {

		try {
			while(true) {
				Socket cliente = servidor.accept();
				boolean isHost =(this.ipHost.equals(cliente.getInetAddress().getHostAddress()));
				TrataJogador tc = new TrataJogador(cliente,this,"Jogador"+(lastID),lastID,isHost);
				lastID=(lastID==1)? 2:1;
				jogadores.add(tc).start();
			}
		} catch (IOException e) {
		Logger.getLogger(Sala.class.getName()).log(Level.SEVERE,null,e);
		}
		
	}
	
	class TrataJogador implements Runnable{
		
		private InputStream clienteIn;
		private PrintStream clienteOut;
		private JSONObject requisicao;
		private JSONObject resposta;
		String nomeJogador;
		int idJogador;
		private Sala sala;
		private List<TrataJogador>jogadores;
		public boolean isHostSala;
		private Socket cliente;
		
		public TrataJogador(Socket cliente, Sala sala, String nomeJogador,int id,boolean host)throws IOException {

			this.clienteIn = cliente.getInputStream();
			this.clienteOut = new PrintStream(cliente.getOutputStream());
			this.nomeJogador = nomeJogador;
			this.idJogador = id;
			this.sala = sala;
			this.isHostSala = host;
		
		}
		
		public void enviaMensagem(String mensagem) {
			JSONObject obj = new JSONObject();
			obj.put("tipo", "mensagem");
			obj.put("mensagem", mensagem);
			clienteOut.println(obj);
		}

		public void enviaJogada(int x,int y,int idJogador) {
			JSONObject obj = new JSONObject();
			obj.put("tipo", "jogada");
			obj.put("x", x);
			obj.put("y", y);
			obj.put("idJogador",idJogador);
			clienteOut.println(obj); 
		}

		public void fimJogo() {
			int vitorioso = sala.vitoria();
			JSONObject obj = new JSONObject();
			obj.put("tipo", "fimJogo");
			String mensagem="";
			
			if(vitorioso==-1) {
				mensagem="Empate";
				
			}else if(vitorioso==idJogador) {
				mensagem="Você Venceu!!";
			}else {
				mensagem="Você perdeu!!";
			}
			
			obj.put("mensagem", mensagem);
			clienteOut.println(obj);
		}

		
		private void desconectar() throws Throwable{
			cliente.close();
			this.finalize();
		}

		
		
		@Override
		public void run() {
			Scanner scan = new Scanner(clienteIn);
			String tipoSolicitacao = "";
			JSONObject resposta;
			
			while(scan.hasNextLine()) {
				requisicao = new JSONObject(scan.nextLine());
				
				tipoSolicitacao = requisicao.getString("tipo");
				
				switch(tipoSolicitacao) {
				case "mensagem":
					jogadores=sala.getListaJogadores();
					for(int i =0;i<jogadores .size();i++) {
						jogadores.get(i).enviaMensagem(nomeJogador + ": "+ requisicao.getInt("mensagem"));
					}
					break;
				case "desconectar":
					try {
						if(!isHostSala) {
							desconectar();
						}
						else {
							sala.fecharSala();
						}
					} catch (Throwable e) {
						Logger.getLogger(Sala.class.getName()).log(Level.SEVERE,null,e);
					}
				}
			}
					
			
		}

		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
