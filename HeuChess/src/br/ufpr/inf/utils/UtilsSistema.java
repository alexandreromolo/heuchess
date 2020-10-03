package br.ufpr.inf.utils;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since Nov 7, 2012
 */
public class UtilsSistema {
    
    /**
     * Alguns Exemplos de Variáveis de Ambiente
     * (Varia de acordo com a plataforma)
     * 
     *  PROCESSOR_IDENTIFIER
     *  PROCESSOR_ARCHITECTURE
     *  PROCESSOR_ARCHITEW6432
     *  NUMBER_OF_PROCESSORS
     *  CLASSPATH
     *  WINDIR
     *  COMPUTERNAME
     *  OS
     *  USERDOMAIN
     *  USERNAME 
     */
    public static void imprimeTodasVariaveisAmbiente() {
        
        Map<String, String> env = System.getenv();
        Set<String> keys = env.keySet();
        
        for (String key : keys) {
            System.out.println(key + " = " + env.get(key));
        }
    }
    
    /**
     * Lista Completa de System Properties       
     * Chave                          Descrição 
     * =========================================================================
     * java.version                  // The version of Java Runtime Environment.
     * java.vendor                   // The name of Java Runtime Environment vendor
     * java.vendor.url               // The URL of Java vendor
     * java.home                     // The directory of Java installation 
     * java.vm.specification.version // The specification version of Java Virtual Machine
     * java.vm.specification.vendor  // The name of specification vendor of Java Virtual Machine 
     * java.vm.specification.name    // Java Virtual Machine specification name
     * java.vm.version               // JVM implementation version
     * java.vm.vendor                // JVM implementation vendor
     * java.vm.name                  // JVM  implementation name
     * java.specification.version    // The name of specification version Java Runtime Environment
     * java.specification.vendor     // JRE specification vendor
     * java.specification.name       // JREspecification name
     * java.class.version            // Java class format version number
     * java.class.path               // Path of java class
     * java.library.path             // List of paths to search when loading libraries
     * java.io.tmpdir                // The path of temp file
     * java.compiler                 // The Name of JIT compiler to use
     * java.ext.dirs                 // The path of extension directory or directories
     * os.name                       // The name of OS name                  
     * os.version                    // The version of OS
     * os.arch                       // The OS architecture
     * file.separator                // The File separator
     * path.separator                // The path separator
     * line.separator                // The line separator
     * user.name                     // The name of account name user
     * user.home                     // The home directory of user 
     * user.dir                      // The current working directory of the user 
     */
    public static void imprimeTodasPropriedadesSistema(){
        
        //System.getProperties().list(System.out);
        
        Properties props = System.getProperties();
        for (String key : props.stringPropertyNames()) {
            System.out.println(key + " = " + props.getProperty(key));
        }
    }
    
    public void imprimeInformacoesRede() {
        
        try {
            InetAddress  ip = InetAddress.getLocalHost();
            System.out.println("Nome Host = "   + ip.getHostName());
            System.out.println("Endereço IP = " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            System.out.print("Endereço Físico (MAC) = ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            System.out.println(sb.toString());

        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace(System.err);
        } 
    }
    
    private static void acrescentaInformacao(StringBuilder builder, String descricao, String valor){    
        
        if (valor != null){
            builder.append(descricao);
            builder.append(" = ");
            builder.append(valor);
            builder.append("\n");
        }
    }
    
    public static String todasInformacoes() {
        
        StringBuilder builder = new StringBuilder();
        
        ////////////////////////////////////////////////////////
        // Todas as Propriedades do Sistema de Forma Ordenada //
        ////////////////////////////////////////////////////////
        
        builder.append("[Propriedades do Sistema]\n\n");
        
        Properties props = System.getProperties();
        SortedMap sortedSystemProperties = new TreeMap(props);
        Set<String> keysSystemProperties = sortedSystemProperties.keySet();
        for (String chave : keysSystemProperties) {    
            acrescentaInformacao(builder, chave, props.getProperty(chave));
        }
        
        //////////////////////////////////////////////
        // Todas as Variáveis de Ambiente Ordenadas //
        //////////////////////////////////////////////
        
        builder.append("\n[Variáveis de Ambiente]\n\n");
        
        Map<String, String> env = System.getenv();
        SortedMap sortedEnvs = new TreeMap(env);
        Set<String> keysEnv  = sortedEnvs.keySet();
        for (String chave : keysEnv) {
            acrescentaInformacao(builder, chave, env.get(chave));
        }
        
        ////////////////////////////
        // Informações de Memória //
        ////////////////////////////
        
        builder.append("\n[Informações sobre a Memória]\n\n");
        
        long maximoMemoria  = Runtime.getRuntime().maxMemory();
        long memoriaAlocada = Runtime.getRuntime().totalMemory();
        long memoriaUsada   = memoriaAlocada - Runtime.getRuntime().freeMemory();
        
        acrescentaInformacao(builder,"Memória Máxima", UtilsString.tamanhoBytesFormatado(maximoMemoria, true));        
        
        builder.append("Memória Alocada = ");
        builder.append(UtilsString.tamanhoBytesFormatado(memoriaAlocada,true));
        builder.append(" ");
        builder.append(UtilsString.porcentagemEmRelacaoTotal(memoriaAlocada, maximoMemoria));
        
        builder.append("\nMemória Usada = ");
        builder.append(UtilsString.tamanhoBytesFormatado(memoriaUsada, true));
        builder.append(" ");
        builder.append(UtilsString.porcentagemEmRelacaoTotal(memoriaUsada, memoriaAlocada));
        
        /////////////////////////////////////
        // Informações sobre Processadores //
        /////////////////////////////////////
        
        builder.append("\n\n[Informações sobre os Processadores]\n\n");
        
        acrescentaInformacao(builder,"Quantidade Processadores",String.valueOf(Runtime.getRuntime().availableProcessors()));
                
        ////////////////////////////////////////
        // Informações do Sistema de Arquivos //
        ////////////////////////////////////////
        
        builder.append("\n[Informações sobre o Sistema de Arquivos]\n\n");
        
        File[] roots = File.listRoots();

        for (File root : roots) {
            acrescentaInformacao(builder,"Caminho Raiz", root.getAbsolutePath());
            acrescentaInformacao(builder,"Total espaço", UtilsString.tamanhoBytesFormatado(root.getTotalSpace(), true));
            acrescentaInformacao(builder,"Espaço Usado", UtilsString.tamanhoBytesFormatado(root.getUsableSpace(),true));
            acrescentaInformacao(builder,"Espaço Livre", UtilsString.tamanhoBytesFormatado(root.getFreeSpace(),  true));            
            builder.append("\n");
        }
        
        /////////////////////////
        // Informações de Rede //
        /////////////////////////
        
        builder.append("[Informações sobre acesso a Rede]\n\n");
        
        try {
            InetAddress  ip = InetAddress.getLocalHost();
            acrescentaInformacao(builder,"Nome Host",  ip.getHostName());
            acrescentaInformacao(builder,"Endereço IP",ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            builder.append("Endereço Físico (MAC) = ");

            for (int i = 0; i < mac.length; i++) {
                builder.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }            

        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace(System.err);
        } 

        return builder.toString(); 
    }
}