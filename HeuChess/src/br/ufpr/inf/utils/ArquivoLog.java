package br.ufpr.inf.utils;

import java.awt.Toolkit;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;

/**
 * Cria um arquivo texto para Logar as mensagem e eventos da aplicação
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 4 de Novembro de 2005, 14:14
 */
public class ArquivoLog {
    
    public static final int MENSAGEM_NORMAL = 0;
    public static final int MENSAGEM_ALERTA = 1;
    public static final int MENSAGEM_ERRO   = 2;    
    
    private PrintStream arquivoSaida;
    private File        arquivo;
    private boolean     arquivoCriado;
    
    private String                    prefixoArquivo;
    private String                    nomeSistema;
    private String                    nomeDiretorio;    
    private GregorianCalendar         dataCriacao;    
    private boolean                   umArquivoPorDia;
    private ArquivoLogTrocadoListener listener;
    
    private static final SimpleDateFormat formataDataAtual = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss:SSS");
    
    /**
     * Método usado para criar um novo arquivo de Log
     * deve-se passar o nome do diretorio com / no final
     */
    public static ArquivoLog createArquivoLog(String diretorio, String prefixoArquivo, String nomeSistema, boolean umArquivoPorDia){
        
        // Define o nome do Novo Arquivo //
        
        String nomeArquivoLog = montaNomeNovoArquivo(diretorio,prefixoArquivo);
        
        // Cria o Arquivo //
        
        ArquivoLog arquivoLog = new ArquivoLog(nomeArquivoLog);
        arquivoLog.registraCabecalhoAbertura(nomeSistema);
        
        arquivoLog.nomeDiretorio   = diretorio;
        arquivoLog.prefixoArquivo  = prefixoArquivo;
        arquivoLog.nomeSistema     = nomeSistema;
        arquivoLog.umArquivoPorDia = umArquivoPorDia;
        
        return arquivoLog;
    }    
        
    /**
     * Método usado para reabrir um arquivo de Log previamente criado
     */
    public static ArquivoLog reopenArquivoLog(String nomeCompleto){
        
        ArquivoLog arquivoLog = new ArquivoLog(nomeCompleto);
        arquivoLog.registraCabecalhoReabertura();
        return arquivoLog;
    }
    
    public void createNewLogFile(){
        
        // Define o nome do Novo Arquivo //
        
        String nomeArquivoLog = montaNomeNovoArquivo(nomeDiretorio,prefixoArquivo);
        
        // Cria o Arquivo //
        
        criaArquivoExterno(nomeArquivoLog);
        registraCabecalhoAbertura(nomeSistema);
    }
    
    /**
     * Construtor que cria um objeto da Classe arquivoLog (Abre o arquivo pela primeira vez ou reabre caso ele já exista)
     * Este construtor é usado internamento pelo métodos (Factory) que fabricam objetos ArquivoLog
     */
    private ArquivoLog(String nomeArquivoLog)  {        
        listener = null;
        criaArquivoExterno(nomeArquivoLog);
    }
    
    /**
     * Método interno usado para montar o nome do Arquivo
     */
    private static String montaNomeNovoArquivo(String diretorio, String prefixoArquivo){
        
        // Cria o diretorio //
        
        File arquivoDiretorio = new File(diretorio);
        int     cont  = 0;        
        boolean diretorioExiste = false;
        do{
            arquivoDiretorio.mkdirs();
            diretorioExiste = arquivoDiretorio.exists();
            if (diretorioExiste == false){
                cont++;
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    
                }
            }
        }while(!diretorioExiste && cont != 3);
        if (diretorioExiste == false){                        
            String mensagem = "Não foi possível criar o diretório para o arquivo de log ["+diretorio+ "]\n" +
                              "Por isto a aplicação será encerrada!";
            
            Toolkit.getDefaultToolkit().beep();
            System.err.println(mensagem);
            JOptionPane.showMessageDialog(null,mensagem,"Aviso de Erro",JOptionPane.ERROR_MESSAGE);        
            System.exit(-1);
        }
        
        // Define o nome do Arquivo de Log //
        
        Date      dataAtual      = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy'-'MM'-'dd' Hora 'HH-mm-ss-SSS");
        try{
            diretorio = arquivoDiretorio.getCanonicalPath();
        }catch (Exception e){
            String mensagem = "Erro ao tentar recuperar o Nome completo do Diretorio criado [" + diretorio +"]";
            Toolkit.getDefaultToolkit().beep();
            System.err.println(mensagem);
            JOptionPane.showMessageDialog(null,mensagem,"Aviso de Erro",JOptionPane.ERROR_MESSAGE);        
            System.exit(-1);
        }
        
        return diretorio + File.separator + prefixoArquivo + formato.format(dataAtual) + ".log";
    }
    
    /**
     * Cria o arquivo externo a ser utilizado para logar as mensagens
     */
    private void criaArquivoExterno(String nomeArquivoLog){
        
        try{            
            // Cria ou reabre o arquivo de Log //            
            arquivo       = new File(nomeArquivoLog);            
            arquivoSaida  = new PrintStream(new FileOutputStream(arquivo,true),true,"UTF-8");            
            dataCriacao   = new GregorianCalendar();
            arquivoCriado = true;        
        }catch(FileNotFoundException | UnsupportedEncodingException erro){
            arquivoCriado = false;         
            registraExcecao(erro);            
        }     
    }
    
    /**
     * Verifica se o dia atual é diferente do dia da criação do Arquivo de Log,
     * se sim e caso esteja habilitado um Arquivo por Dia, é criado um novo Arquivo
     * de Log
     */
    private void verificaCriacaoNovoArquivo(){
        
        if (umArquivoPorDia){
            
            GregorianCalendar atual = new GregorianCalendar();
            
            if (atual.get(GregorianCalendar.DAY_OF_MONTH) != dataCriacao.get(GregorianCalendar.DAY_OF_MONTH)){
             
                registraCabecalhoFinal();
                fechaArquivo();
                
                // Define o nome do Novo Arquivo e o Cria //                        
                criaArquivoExterno(montaNomeNovoArquivo(nomeDiretorio,prefixoArquivo));
                
                registraCabecalhoAbertura(nomeSistema);
                
                if (listener != null){
                    listener.arquivoLogTrocado(this);
                }
            }
        }
    }
    
    /**
     * Retorna o objeto File que representa o arquivo de Saida
     */
    public File getFile(){
        return arquivo;
    }
    
    /**
     * Define o listener que será notificado quando o arquivo de Log for trocado
     * caso a opção de um arquivo de log por dia esteja marcada
     */
    public void addArquivoLogTrocadoListener(ArquivoLogTrocadoListener e){
        listener = e;
    }
    
    /**
     * Fecha o arquivo de Log sem colocar o cabeçalho final
     */
    public void fechaArquivo(){
        
         if (arquivoCriado){
              arquivoCriado = false;
              arquivoSaida.close();              
         }
    }
    
    public boolean isOpen(){
        return arquivoCriado;
    }
    
    /**
     * Retorna a Data e Hora Atual já formatada em String
     */
    public static String getDataHoraAtual(){     
        return formataDataAtual.format(new Date());         
    }
    
    public String getNomeCompletoArquivo(){
        
        if (arquivoCriado){                    
            try{
                return arquivo.getCanonicalPath();
            }catch(Exception ex){
                registraExcecao(ex);
                return "Erro ao recuperar o Nome Completo do Arquivo de Log";                
            }
        }else{
            return "ArquivoLog não Criado!";
        }
    }
    
    /**
     * Imprime o cabeçalho de reabertura do arquivo de Log
     */
    private void registraCabecalhoAbertura(String nomeSistema)  {
        
        if (arquivoCriado){
            arquivoSaida.println("###########################################################################");
            arquivoSaida.println("####               ARQUIVO DE REGISTRO DE EVENTOS - LOG                ####");
            arquivoSaida.println("###########################################################################");                        
            arquivoSaida.println("#### NOME DO SISTEMA       : ["  + nomeSistema + "]");
            arquivoSaida.println("###########################################################################");            
            arquivoSaida.println("#### DATA DE INICIO DO LOG : [" + getDataHoraAtual() + "]");
            arquivoSaida.println("###########################################################################");            
        }
    }

    /**
     * Imprime o cabeçalho de reabertura do arquivo de Log
     */
    private void registraCabecalhoReabertura()  {
        
        if (arquivoCriado){            
            arquivoSaida.println("###########################################################################");            
            arquivoSaida.println("#### DATA DE REABERTURA DO LOG : [" + getDataHoraAtual() + "]");
            arquivoSaida.println("###########################################################################");            
        }
    }

    /**
     * Registra as informações de uma Exceção 
     */
    public synchronized void registraExcecao(Exception erro){        
        
        // Saida no Console //
        
        erro.printStackTrace(System.err);
        
        System.err.println(erro.getMessage());
        
        // Saida no arquivo de Log //
        
        if (arquivoCriado){
            
            verificaCriacaoNovoArquivo();
            
            arquivoSaida.println("[" + getDataHoraAtual() + "] -> #################################################");                    
            erro.printStackTrace(arquivoSaida);        
            arquivoSaida.println("Descrição do Erro [" + erro.getLocalizedMessage() + "]\n");
            arquivoSaida.println("###########################################################################");
        }
    }
    
    public synchronized void registraMensagemSemPular(String mensagem){                
        
        if (arquivoCriado){            
            
            verificaCriacaoNovoArquivo();
            
            for (int x = 0; x < mensagem.length(); x++){                
                int caracter = mensagem.charAt(x);
                if (caracter == 10){                    
                    arquivoSaida.println();
                }else{
                    arquivoSaida.print(mensagem.charAt(x));
                }                
            }               
        }
    }
    
    public synchronized void registraMensagem(String mensagem){                
        
        if (arquivoCriado){            
            
            verificaCriacaoNovoArquivo();
            
            for (int x = 0; x < mensagem.length(); x++){                
                int caracter = mensagem.charAt(x);
                if (caracter == 10){                    
                    arquivoSaida.println();
                }else{
                    arquivoSaida.print(mensagem.charAt(x));
                }                
            }   
            
            arquivoSaida.println();
        }
    }
     
    /**
     * Registra uma String passada no arquivo de Log, colocando antes as informações de Data e Hora     
     */
    public synchronized void registraMensagemTempo(String mensagem){        
        
        if (arquivoCriado){           
            
            verificaCriacaoNovoArquivo();
            
            mensagem = "[" + getDataHoraAtual() + "] -> "+ mensagem;           
            arquivoSaida.println(mensagem);
        }
    }
    
    /**
     * Registra uma String passada no arquivo de Log, colocando antes as informações de Data e Hora     
     * E caso o tipo da mensagem seja ERRO ou ALERTA imprime um cabeçalho na frente da mensagem
     */
    public synchronized void registraMensagemTempo(int tipo, String mensagem){
        
        if (arquivoCriado){              
            
            verificaCriacaoNovoArquivo();
            
            switch(tipo){
                case MENSAGEM_ERRO:                    
                    mensagem = "[" + getDataHoraAtual() + "] -> "+ "#### [ERRO] ##### : " + mensagem;           
                    break;
                case MENSAGEM_ALERTA:                    
                    mensagem = "[" + getDataHoraAtual() + "] -> "+ "#### [ALERTA] ### : " + mensagem;           
                    break;
                default:
                    mensagem = "[" + getDataHoraAtual() + "] -> " + mensagem;           
                    break;
            }
            arquivoSaida.println(mensagem);
        }        
    }
    
    public synchronized void registraLinhaSeparacao(){
        
        if (arquivoCriado){
            arquivoSaida.println("###########################################################################");
        }
    }
    
    /**
     * Fecha o arquivo de Log e grava cabeçalho final
     */
    public synchronized void registraCabecalhoFinal(){
        
        if (arquivoCriado){
            arquivoSaida.println("###########################################################################");
            arquivoSaida.println("####  ARQUIVO DE LOG ENCERRADO - [ " + getDataHoraAtual() + "]");
            arquivoSaida.println("###########################################################################");            
        }
    }
}