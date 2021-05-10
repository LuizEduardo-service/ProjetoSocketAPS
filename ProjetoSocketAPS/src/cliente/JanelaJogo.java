package cliente;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import cliente.Jogador.JogadorAtivo;

public class JanelaJogo extends javax.swing.JFrame {

	public JanelaJogo(JogadorAtivo jogador) {
		this.jogador = jogador;
		initComponents();
	}



	private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {
		jogador.enviarMensagem(jTextField1.getText());
	}

	private void pos00ActionPerformed(java.awt.event.ActionEvent evt) {
		jogador.enviarJogada(0, 0);
	}

	private void pos10ActionPerformed(java.awt.event.ActionEvent evt) {
		jogador.enviarJogada(1, 0);
	}

	private void pos20ActionPerformed(java.awt.event.ActionEvent evt) {
		jogador.enviarJogada(2, 0);
	}

	private void pos01ActionPerformed(java.awt.event.ActionEvent evt) {
		jogador.enviarJogada(0, 1);
	}

	private void pos11ActionPerformed(java.awt.event.ActionEvent evt) {
		jogador.enviarJogada(1, 1);
	}

	private void pos21ActionPerformed(java.awt.event.ActionEvent evt) {
		jogador.enviarJogada(2, 1);
	}

	private void pos02ActionPerformed(java.awt.event.ActionEvent evt) {
		jogador.enviarJogada(0, 2);
	}

	private void pos12ActionPerformed(java.awt.event.ActionEvent evt) {
		jogador.enviarJogada(1, 2);
	}

	private void pos22ActionPerformed(java.awt.event.ActionEvent evt) {
		jogador.enviarJogada(2, 2);
	}

	private void formWindowClosed(java.awt.event.ActionEvent evt) {
		try {
			jogador.sairJogo();
		} catch (Throwable e) {
			Logger.getLogger(JanelaJogo.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public void computarJogada(int x, int y, int idJogador) {
		ImageIcon simbolo = new ImageIcon();
		JButton botao = new JButton();

		if (idJogador == 1) {
			simbolo = new ImageIcon(getClass().getResource("/cliente/x.png"));

		} else if (idJogador == 2) {
			simbolo = new ImageIcon(getClass().getResource("/cliente/o.png"));
		}

		switch (x) {
		case 0:
			if (y == 0) {
				botao = pos00;
			} else if (y == 1) {
				botao = pos01;
			} else if (y == 2) {
				botao = pos02;
			}
			break;

		case 1:
			if (y == 0) {
				botao = pos10;
			} else if (y == 1) {
				botao = pos11;
			} else if (y == 2) {
				botao = pos12;
			}
			break;
		case 2:
			if (y == 0) {
				botao = pos20;
			} else if (y == 1) {
				botao = pos21;
			} else if (y == 2) {
				botao = pos22;
			}
			break;
			default:
				break;
		

	}
		
		atribuirJogada(botao,simbolo);

}

	private void atribuirJogada(JButton botao, ImageIcon simbolo) {
		botao.setIcon(simbolo);
		botao.setDisabledIcon(simbolo);
		botao.setEnabled(false);
		
	}
	

	
	
	private JogadorAtivo jogador;
	
	private javax.swing.JTextArea console;
	private javax.swing.JButton jButton10;
	private javax.swing.JLabel jLabel;
	private javax.swing.JScrollPane jScrollPane;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JButton pos00;
	private javax.swing.JButton pos01;
	private javax.swing.JButton pos02;
	private javax.swing.JButton pos10;
	private javax.swing.JButton pos11;
	private javax.swing.JButton pos12;
	private javax.swing.JButton pos20;
	private javax.swing.JButton pos21;
	private  javax.swing.JButton pos22;
	

	

}
	
	
