package br.ufpr.inf.utils;

import br.ufpr.inf.utils.gui.UtilsGUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Adaptado de c�digo de Sekkuar 
 * @author Alexandre R�molo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since Nov 3, 2012
 */
public class InstanceManager {

    private static ServerSocket     socket;
    private static InstanceListener subListener;
    
    private static final int PORTA_TCP_INICIAL = 44121;    
    private static final int PORTA_TCP_MAXIMA  = 44140;
    
    private static int         portaTCP;
    private static InetAddress localHost;
    
    /**
     * Mensagens precisam terminar com \n
     */
    private static final String PERGUNTA_PARA_SERVIDOR = "� uma Aplica��o HeuChess Cliente no modo Servidor?\n";
    private static final String RESPOSTA_PARA_CLIENTE  = "Aplica��o HeuChess Cliente como Servidor\n";
    
    private static boolean criadoComoServidor;
    private static boolean conectadoComoCliente;
    
    private final static boolean ABRIR_MESMO_COM_ERRO = false;

    /**
     * Tenta conectar ao socket como servidor, caso a porta j� estiver aberta,
     * tenta conectar como cliente.
     *
     * Essa opera��o � repetida at� conseguir conectar como servidor ou receber
     * uma resposta correta de outra instancia do servidor.
     *
     * O numero da porta � incrementado at� atingir o numero inicial da porta +
     * 20 nessa caso, ele para de tentar fazer conex�es, e retorna o valor em
     * caso de erro.
     *
     * retorna true se conseguir abrir o servidor ou false caso contr�rio.
     * retorna o valor de erro caso ocorra algum problema.
     */
    public static boolean registerInstance() {

        portaTCP = PORTA_TCP_INICIAL;
        
        try {
            localHost = InetAddress.getByAddress("Localhost", new byte[]{127, 0, 0, 1});

        } catch (UnknownHostException e) {
            UtilsGUI.dialogoErro(null, "N�o foi poss�vel recuperar o endere�o Local do computador!\n" +
                                       "Erro: " + e.getMessage());
            
            e.printStackTrace(System.err);
            
            return ABRIR_MESMO_COM_ERRO;
        }

        while (!criadoComoServidor && !conectadoComoCliente) {
            
            startServer();

            if (criadoComoServidor) {
                return true;
            }

            startClient();

            if (conectadoComoCliente) {
                return false;
            }

            if (portaTCP > PORTA_TCP_MAXIMA) {
                UtilsGUI.dialogoErro(null, "N�o foi poss�vel iniciar a aplica��o pois n�o se conseguiu reservar nenhuma porta TCP!\n" +
                                           "Foram testadas de " + UtilsString.formataDouble("###,###,###", PORTA_TCP_INICIAL) + " a " + 
                                           UtilsString.formataDouble("###,###,###", PORTA_TCP_MAXIMA) + ".");
                break;
            }
        }

        return ABRIR_MESMO_COM_ERRO;
    }

    /**
     * Abre um socket como servidor. Se conseguir, inicia uma Thread para
     * receber conex�es de novas instancias
     */
    private static void startServer() {
        
        try {
            System.err.println("Abrindo novo Servidor em " + portaTCP + "...");

            socket = new ServerSocket(portaTCP, 20, localHost);

            System.err.println("Conectado, escutando novas inst�ncias em: " + localHost + ":" + portaTCP);

            new InstanceThread().start();
            
            criadoComoServidor = true;
            
        } catch (IOException ex) {            
            //ex.printStackTrace(System.err);  
            criadoComoServidor = false;
        }
    }

    /**
     * Tenta conectar a um servidor que j� est� aberto. Envia a MENSAGEM e
     * espera pela resposta.
     *
     * Se a resposta for igual a RESPOSTA esperada, significa que outra
     * instancia j� est� em execu��o, neste caso, apenas termine a execu��o da
     * instancia atual.
     *
     * Se a resposta for diferente (ou null) significa que outra aplica��o est�
     * em execu��o nesta porta. Ent�o, incrementa a porta para tentar conectar
     * como sevidor novamente.
     */
    private static void startClient() {
        
        System.err.println("Porta j� est� aberta, notificando a primeira inst�ncia...");
        
        try (Socket clientSocket = new Socket(localHost, portaTCP);
             OutputStream out = clientSocket.getOutputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            
            out.write(PERGUNTA_PARA_SERVIDOR.getBytes());
            String resposta = in.readLine();
            
            System.err.println("Primeira inst�ncia notificada");
            
            conectadoComoCliente = !(resposta == null || !RESPOSTA_PARA_CLIENTE.trim().equals(resposta.trim()));

            if (conectadoComoCliente) {
                System.err.println("Resposta Correta: \""   + resposta + "\"\nAplica��o encerrada.");
            } else {
                System.err.println("Resposta Incorreta: \"" + resposta + "\"");
                portaTCP++;
            }
            
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            conectadoComoCliente = false;
            portaTCP++;
        }
    }

    /**
     * Seta um listener para receber as notifica��es de nova inst�ncia.
     *
     */
    public static void setListener(InstanceListener listener) {
        subListener = listener;
    }

    /**
     * Notifica o listener que uma nova instancia foi detectada.
     */
    private static void fireNewInstance() {
        
        if (subListener != null) {
            subListener.newInstanceCreated();
        }
    }

    /**
     * Thread para aceitar conex�es e receber mensagens pelo socket
     *
     * Ela deve ser iniciada quando a conex�o do tipo servidor for aberta e
     * ficar� rodando enquando o programa estiver em execu��o.
     *
     * Quando receber uma nova conex�o, primeiro verifica se a mensagem recebida
     * corresponde com a MENSAGEM esperada. Caso seja igual, significa que � uma
     * nova instancia da mesma aplica��o, portanto, envia a RESPOSTA que o
     * cliente est� esperando para garantir que � a mesma apli��o. Depois
     * notifica o listener que uma nova instancia foi aberta.
     *
     * Caso a mensagem seja diferente, apenas fecha a conex�o desconhecida.
     */
    static class InstanceThread extends Thread {

        public InstanceThread() {
            super("Socket Listener");

            /**
             * Se esta for a �nica Thread ainda em execu��o na aplica��o, n�o
             * faz sentido continuar.
             *
             * Por isso, colocando daemon = true, impede que a aplica��o
             * continue aberta quando esta for a ultima Thread em execu��o (o
             * que iria causar o programa a rodar eternamente)
             */
            setDaemon(true);
        }

        @Override
        public void run() {
            
            while (!socket.isClosed()) {

                try (Socket client = socket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                     OutputStream out = client.getOutputStream()) {
                    
                    String message = in.readLine();
                    
                    if (PERGUNTA_PARA_SERVIDOR.trim().equals(message.trim())) {
                        
                        System.err.println("Nova inst�ncia do programa detectada: \"" + message + "\"\nResposta enviada.");
                        out.write(RESPOSTA_PARA_CLIENTE.getBytes());
                        
                        fireNewInstance();
                        
                    } else {
                        System.err.println("Conex�o desconhecida detectada: \"" + message + "\"");
                    }

                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }
}
