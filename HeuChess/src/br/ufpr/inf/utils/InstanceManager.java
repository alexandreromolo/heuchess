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
 * Adaptado de código de Sekkuar 
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
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
    private static final String PERGUNTA_PARA_SERVIDOR = "É uma Aplicação HeuChess Cliente no modo Servidor?\n";
    private static final String RESPOSTA_PARA_CLIENTE  = "Aplicação HeuChess Cliente como Servidor\n";
    
    private static boolean criadoComoServidor;
    private static boolean conectadoComoCliente;
    
    private final static boolean ABRIR_MESMO_COM_ERRO = false;

    /**
     * Tenta conectar ao socket como servidor, caso a porta já estiver aberta,
     * tenta conectar como cliente.
     *
     * Essa operação é repetida até conseguir conectar como servidor ou receber
     * uma resposta correta de outra instancia do servidor.
     *
     * O numero da porta é incrementado até atingir o numero inicial da porta +
     * 20 nessa caso, ele para de tentar fazer conexões, e retorna o valor em
     * caso de erro.
     *
     * retorna true se conseguir abrir o servidor ou false caso contrário.
     * retorna o valor de erro caso ocorra algum problema.
     */
    public static boolean registerInstance() {

        portaTCP = PORTA_TCP_INICIAL;
        
        try {
            localHost = InetAddress.getByAddress("Localhost", new byte[]{127, 0, 0, 1});

        } catch (UnknownHostException e) {
            UtilsGUI.dialogoErro(null, "Não foi possível recuperar o endereço Local do computador!\n" +
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
                UtilsGUI.dialogoErro(null, "Não foi possível iniciar a aplicação pois não se conseguiu reservar nenhuma porta TCP!\n" +
                                           "Foram testadas de " + UtilsString.formataDouble("###,###,###", PORTA_TCP_INICIAL) + " a " + 
                                           UtilsString.formataDouble("###,###,###", PORTA_TCP_MAXIMA) + ".");
                break;
            }
        }

        return ABRIR_MESMO_COM_ERRO;
    }

    /**
     * Abre um socket como servidor. Se conseguir, inicia uma Thread para
     * receber conexões de novas instancias
     */
    private static void startServer() {
        
        try {
            System.err.println("Abrindo novo Servidor em " + portaTCP + "...");

            socket = new ServerSocket(portaTCP, 20, localHost);

            System.err.println("Conectado, escutando novas instâncias em: " + localHost + ":" + portaTCP);

            new InstanceThread().start();
            
            criadoComoServidor = true;
            
        } catch (IOException ex) {            
            //ex.printStackTrace(System.err);  
            criadoComoServidor = false;
        }
    }

    /**
     * Tenta conectar a um servidor que já está aberto. Envia a MENSAGEM e
     * espera pela resposta.
     *
     * Se a resposta for igual a RESPOSTA esperada, significa que outra
     * instancia já está em execução, neste caso, apenas termine a execução da
     * instancia atual.
     *
     * Se a resposta for diferente (ou null) significa que outra aplicação está
     * em execução nesta porta. Então, incrementa a porta para tentar conectar
     * como sevidor novamente.
     */
    private static void startClient() {
        
        System.err.println("Porta já está aberta, notificando a primeira instância...");
        
        try (Socket clientSocket = new Socket(localHost, portaTCP);
             OutputStream out = clientSocket.getOutputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            
            out.write(PERGUNTA_PARA_SERVIDOR.getBytes());
            String resposta = in.readLine();
            
            System.err.println("Primeira instância notificada");
            
            conectadoComoCliente = !(resposta == null || !RESPOSTA_PARA_CLIENTE.trim().equals(resposta.trim()));

            if (conectadoComoCliente) {
                System.err.println("Resposta Correta: \""   + resposta + "\"\nAplicação encerrada.");
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
     * Seta um listener para receber as notificações de nova instância.
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
     * Thread para aceitar conexões e receber mensagens pelo socket
     *
     * Ela deve ser iniciada quando a conexão do tipo servidor for aberta e
     * ficará rodando enquando o programa estiver em execução.
     *
     * Quando receber uma nova conexão, primeiro verifica se a mensagem recebida
     * corresponde com a MENSAGEM esperada. Caso seja igual, significa que é uma
     * nova instancia da mesma aplicação, portanto, envia a RESPOSTA que o
     * cliente está esperando para garantir que é a mesma aplição. Depois
     * notifica o listener que uma nova instancia foi aberta.
     *
     * Caso a mensagem seja diferente, apenas fecha a conexão desconhecida.
     */
    static class InstanceThread extends Thread {

        public InstanceThread() {
            super("Socket Listener");

            /**
             * Se esta for a única Thread ainda em execução na aplicação, não
             * faz sentido continuar.
             *
             * Por isso, colocando daemon = true, impede que a aplicação
             * continue aberta quando esta for a ultima Thread em execução (o
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
                        
                        System.err.println("Nova instância do programa detectada: \"" + message + "\"\nResposta enviada.");
                        out.write(RESPOSTA_PARA_CLIENTE.getBytes());
                        
                        fireNewInstance();
                        
                    } else {
                        System.err.println("Conexão desconhecida detectada: \"" + message + "\"");
                    }

                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }
}
