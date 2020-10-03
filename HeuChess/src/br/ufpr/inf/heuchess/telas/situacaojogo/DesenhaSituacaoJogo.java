package br.ufpr.inf.heuchess.telas.situacaojogo;

import br.ufpr.inf.heuchess.representacao.heuristica.DHJOG;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Casa;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Peca;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import br.ufpr.inf.heuchess.representacao.situacaojogo.TipoPeca;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

/**
 *
 * @author Luis - Adapato Alexandre
 * Created on 10 de Julho de 2007, 18:52
 */
public class DesenhaSituacaoJogo extends JPanel implements MouseListener {

    private static Color corPreta  = new Color(209, 139, 70);
    private static Color corBranca = new Color(254, 206, 158);
    private static Color corBorda  = new Color(176, 176, 176);
    
    private Cursor cursorPadrao = new Cursor(Cursor.DEFAULT_CURSOR);
    
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
     // Icone Pecas Pretas
    
    private ImageIcon iconReiPreto    = new ImageIcon(getClass().getResource("/pecas/k21s.PNG"));
    private ImageIcon iconDamaPreta   = new ImageIcon(getClass().getResource("/pecas/q21s.PNG"));
    private ImageIcon iconBispoPreto  = new ImageIcon(getClass().getResource("/pecas/b21s.PNG"));
    private ImageIcon iconCavaloPreto = new ImageIcon(getClass().getResource("/pecas/n21s.PNG"));
    private ImageIcon iconTorrePreta  = new ImageIcon(getClass().getResource("/pecas/r21s.PNG"));
    private ImageIcon iconPeaoPreto   = new ImageIcon(getClass().getResource("/pecas/p21s.PNG"));
    
    // Icone Pecas Brancas
    
    private ImageIcon iconReiBranco    = new ImageIcon(getClass().getResource("/pecas/k21o.PNG"));
    private ImageIcon iconDamaBranca   = new ImageIcon(getClass().getResource("/pecas/q21o.PNG"));
    private ImageIcon iconBispoBranco  = new ImageIcon(getClass().getResource("/pecas/b21o.PNG"));
    private ImageIcon iconCavaloBranco = new ImageIcon(getClass().getResource("/pecas/n21o.PNG"));
    private ImageIcon iconTorreBranca  = new ImageIcon(getClass().getResource("/pecas/r21o.PNG"));
    private ImageIcon iconPeaoBranco   = new ImageIcon(getClass().getResource("/pecas/p21o.PNG"));
    
    private ImageIcon iconBorracha = new ImageIcon(getClass().getResource("/icones/borracha.png"));
    
    private final HashMap<ImageIcon, Cursor> iconesToCursor = new HashMap<>();
    
    private int comprimentoTabuleiro;
    private int posXTabuleiro;
    private int posYTabuleiro;
    private int comprimentoCasa;
    
    private boolean editavel;
    
    private ArrayList<JLabel> casas = new ArrayList();
    
    private ImageIcon  iconPecaAtiva;
    private Peca      pecaAtiva;
    private boolean    borracha;
    
    private boolean jogadorBrancoAtivo = true;
    
    private boolean possivelRoqueMaiorBrancas = true;
    private boolean possivelRoqueMenorBrancas = true;
    private boolean possivelRoqueMaiorPretas  = true;
    private boolean possivelRoqueMenorPretas  = true;
    
    private Casa casaEnPassant;
    
    private int quantidadeMovimentos = 0;
    private int quantidadeJogadas    = 1;
    
    private String atualFEN;
    
    private JFrame jFramePai;
    
    public DesenhaSituacaoJogo(JFrame jFrame) {

        jFramePai = jFrame;
        
        for (int ix = 1; ix <= 64; ix++) {

            JLabel lblCasa = new JLabel();

            if (ix % 2 == 1) {
                lblCasa.setBackground(corBranca);
            } else {
                lblCasa.setBackground(corPreta);
            }
            
            lblCasa.setName("p" + (ix - 1));
            lblCasa.setIcon(null);
            lblCasa.setVisible(true);
            
            casas.add(lblCasa);
            add(lblCasa);
        }
        
        Toolkit tk = Toolkit.getDefaultToolkit();
        
        ImageIcon cursorPeaoBranco = new ImageIcon(getClass().getResource("/cursores/cursor_peao_branco.png"));
        iconesToCursor.put(iconPeaoBranco, tk.createCustomCursor(cursorPeaoBranco.getImage(), new Point(6,7), "PeaoBranco"));        
        
        ImageIcon cursorTorreBranca = new ImageIcon(getClass().getResource("/cursores/cursor_torre_branca.png"));
        iconesToCursor.put(iconTorreBranca, tk.createCustomCursor(cursorTorreBranca.getImage(), new Point(7,8), "TorreBranca"));
                
        ImageIcon cursorCavaloBranco = new ImageIcon(getClass().getResource("/cursores/cursor_cavalo_branco.png"));
        iconesToCursor.put(iconCavaloBranco, tk.createCustomCursor(cursorCavaloBranco.getImage(), new Point(8,8), "CavaloBranco"));
        
        ImageIcon cursorBispoBranco = new ImageIcon(getClass().getResource("/cursores/cursor_bispo_branco.png"));        
        iconesToCursor.put(iconBispoBranco, tk.createCustomCursor(cursorBispoBranco.getImage(), new Point(9,9), "BispoBranco"));
        
        ImageIcon cursorDamaBranca = new ImageIcon(getClass().getResource("/cursores/cursor_dama_branca.png"));        
        iconesToCursor.put(iconDamaBranca, tk.createCustomCursor(cursorDamaBranca.getImage(), new Point(9,9), "DamaBranca"));
        
        ImageIcon cursorReiBranco = new ImageIcon(getClass().getResource("/cursores/cursor_rei_branco.png"));        
        iconesToCursor.put(iconReiBranco, tk.createCustomCursor(cursorReiBranco.getImage(), new Point(9,9), "ReiBranco"));
        
        ImageIcon cursorPeaoPreto = new ImageIcon(getClass().getResource("/cursores/cursor_peao_preto.png"));        
        iconesToCursor.put(iconPeaoPreto, tk.createCustomCursor(cursorPeaoPreto.getImage(), new Point(6,7), "PeaoPreto"));
        
        ImageIcon cursorTorrePreta = new ImageIcon(getClass().getResource("/cursores/cursor_torre_preta.png"));                
        iconesToCursor.put(iconTorrePreta, tk.createCustomCursor(cursorTorrePreta.getImage(), new Point(7,8), "TorrePreta"));
        
        ImageIcon cursorCavaloPreto = new ImageIcon(getClass().getResource("/cursores/cursor_cavalo_preto.png"));                
        iconesToCursor.put(iconCavaloPreto, tk.createCustomCursor(cursorCavaloPreto.getImage(), new Point(8,8), "CavaloPreto"));
        
        ImageIcon cursorBispoPreto = new ImageIcon(getClass().getResource("/cursores/cursor_bispo_preto.png"));                        
        iconesToCursor.put(iconBispoPreto, tk.createCustomCursor(cursorBispoPreto.getImage(), new Point(9,9), "BispoPreto"));
        
        ImageIcon cursorDamaPreta = new ImageIcon(getClass().getResource("/cursores/cursor_dama_preta.png"));                                
        iconesToCursor.put(iconDamaPreta, tk.createCustomCursor(cursorDamaPreta.getImage(), new Point(9,9), "DamaPreta"));
        
        ImageIcon cursorReiPreto = new ImageIcon(getClass().getResource("/cursores/cursor_rei_preto.png")); 
        iconesToCursor.put(iconReiPreto, tk.createCustomCursor(cursorReiPreto.getImage(), new Point(9,9), "ReiPreto"));
        
        ImageIcon cursorBorracha = new ImageIcon(getClass().getResource("/cursores/cursor_borracha.png")); 
        iconesToCursor.put(iconBorracha, tk.createCustomCursor(cursorBorracha.getImage(), new Point(5,10), "Borracha"));
        
        atualizaFEN(false);
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener changeListener) {
        
        assert changeListener != null;

        propertyChangeSupport.addPropertyChangeListener(changeListener);
    }
    
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener changeListener) {
        
        assert changeListener  != null;

        propertyChangeSupport.removePropertyChangeListener(changeListener);
    }
    
    public boolean isEditavel() {
        return editavel;
    }

    public void setEditavel(boolean permitirEdicao) {
        
        editavel = permitirEdicao;
        
        if (editavel) {
            addMouseListener(this);
            for (int ix = 0; ix < casas.size(); ix++) {
                ((JLabel) casas.get(ix)).addMouseListener(this);
            }
        } else {
            removeMouseListener(this);
            for (int ix = 0; ix < casas.size(); ix++) {
                ((JLabel) casas.get(ix)).removeMouseListener(this);
            }
        }
    }

    public void setPecaEscolhida(Peca peca) {
        
        switch(peca){
            case PEAO_BRANCO:
                iconPecaAtiva = iconPeaoBranco;
                break;
            case PEAO_PRETO:
                iconPecaAtiva = iconPeaoPreto;
                break;
            case TORRE_BRANCA:
                iconPecaAtiva = iconTorreBranca;
                break;
            case TORRE_PRETA:
                iconPecaAtiva = iconTorrePreta;
                break;
            case CAVALO_BRANCO:
                iconPecaAtiva = iconCavaloBranco;
                break;
            case CAVALO_PRETO:
                iconPecaAtiva = iconCavaloPreto;
                break;
            case BISPO_BRANCO:
                iconPecaAtiva = iconBispoBranco;
                break;
            case BISPO_PRETO:
                iconPecaAtiva = iconBispoPreto;
                break;
            case DAMA_BRANCA:
                iconPecaAtiva = iconDamaBranca;
                break;
            case DAMA_PRETA:
                iconPecaAtiva = iconDamaPreta;
                break;
            case REI_BRANCO:
                iconPecaAtiva = iconReiBranco;
                break;
            case REI_PRETO:
                iconPecaAtiva = iconReiPreto;
                break;
            default:
                throw new IllegalArgumentException("Peça inválida");
        }
        
        pecaAtiva = peca;
        borracha  = false;
    }

    public void setBorracha(){
        borracha      = true;
        iconPecaAtiva = null;
        pecaAtiva     = null;
    }
    
    public void desativaSelecaoPeca(){
        iconPecaAtiva = null;
        pecaAtiva     = null;
        borracha      = false;
    }
    
    public void setJogadorBrancoAtivo(boolean estado){
        
        jogadorBrancoAtivo = estado;
        
        atualizaFEN(true);
    }
    
    public boolean isJogadorBrancoAtivo() {
        return jogadorBrancoAtivo;
    }
    
    public void setRoqueMaiorBrancas(boolean estado){
        
        possivelRoqueMaiorBrancas = estado;
        
        atualizaFEN(true);
    }
    
    public boolean isPossivelRoqueMaiorBrancas() {
        return possivelRoqueMaiorBrancas;
    }
     
    public void setRoqueMenorBrancas(boolean estado){
        
        possivelRoqueMenorBrancas = estado;
        
        atualizaFEN(true);
    }
    
    public boolean isPossivelRoqueMenorBrancas() {
        return possivelRoqueMenorBrancas;
    }
    
    public void setRoqueMaiorPretas(boolean estado){
        
        possivelRoqueMaiorPretas = estado;
        
        atualizaFEN(true);
    }
    
    public boolean isPossivelRoqueMaiorPretas() {
        return possivelRoqueMaiorPretas;
    }
     
    public void setRoqueMenorPretas(boolean estado){
        
        possivelRoqueMenorPretas = estado;
        
        atualizaFEN(true);
    }
    
    public boolean isPossivelRoqueMenorPretas() {
        return possivelRoqueMenorPretas;
    }
    
    public void setCasaEnPassant(String nomeCasa) throws Exception {
        
        if (nomeCasa == null){
            throw new IllegalArgumentException("O nome da casa en Passant não pode ser nula!");
        }
        
        if (nomeCasa.equals("-")) {
            casaEnPassant = null;
        }else{       
            casaEnPassant = Casa.porFEN(nomeCasa);
        }
        
        atualizaFEN(true);
    }

    public Casa getCasaEnPassant() {
        return casaEnPassant;
    }
     
    public void setQuantidadeMovimentos(int quantidade){
        
        quantidadeMovimentos = quantidade;
        
        atualizaFEN(true);
    }
    
    public int getQuantidadeMovimentos() {
        return quantidadeMovimentos;
    }
    
    public void setQuantidadeJogadas(int quantidade){
        
        quantidadeJogadas = quantidade;
        
        atualizaFEN(true);
    }
    
    public int getQuantidadeJogadas() {
        return quantidadeJogadas;
    }
    
    private void apagaPeca(int pos) {
        
        if (pos < 0 || pos >= casas.size()){
            throw new IllegalArgumentException("Posição Inválida de Peça [" + pos + "]");
        }
        
        JLabel lblCasa = (JLabel) casas.get(pos);
        lblCasa.setIcon(null);
        lblCasa.repaint();
    }

    public void apagaPecaSemEvento(int linha, int coluna) {
        
        if (linha < 0  || linha >= 8){
            throw new IllegalArgumentException("Linha Inválida de Peça [" + linha + "]");
        }
        if (coluna < 0 || coluna >= 8){
            throw new IllegalArgumentException("Coluna Inválida de Peça [" + coluna + "]");
        }
         
        JLabel lblCasa = (JLabel) casas.get(linha * 8 + coluna);
        lblCasa.setIcon(null);
        lblCasa.repaint();
    }
    
    public void configura(String fen) {

        Tabuleiro tabuleiro;
        
        try {
            tabuleiro = new Tabuleiro(fen);
            
        } catch (Exception ex) {
            //Utils.registraException(ex);
            UtilsGUI.dialogoErro(jFramePai, ex.getMessage());            
            return;
        }
        
        configura(tabuleiro);
    }

    public void configura(Tabuleiro tabuleiro) {
        
        for (int linha = 0; linha < Tabuleiro.TOTAL_LINHAS; linha++) {

            for (int coluna = 0; coluna < Tabuleiro.TOTAL_COLUNAS; coluna++) {

                Peca peca = tabuleiro.getPeca(coluna, linha);

                if (peca == null) {
                    apagaPeca(linha * 8 + coluna);
                } else {
                    definePeca(linha * 8 + coluna, peca);
                }
            }
        }

        jogadorBrancoAtivo = tabuleiro.isWhiteActive();

        /////////////////
        // Roqueamento //
        /////////////////

        possivelRoqueMaiorBrancas = tabuleiro.getPodeRoqueMaior(true);
        possivelRoqueMenorBrancas = tabuleiro.getPodeRoqueMenor(true);
        possivelRoqueMaiorPretas = tabuleiro.getPodeRoqueMaior(false);
        possivelRoqueMenorPretas = tabuleiro.getPodeRoqueMenor(false);

        /////////////////////
        // Casa En Passant //
        /////////////////////

        casaEnPassant = tabuleiro.getCasaEnPassant();

        /////////////////
        // Quantidades //
        /////////////////

        quantidadeMovimentos = tabuleiro.getQuantidadeMovimentos();
        quantidadeJogadas = tabuleiro.getQuantidadeJogadas();

        atualizaFEN(false);
    }
    
    public String getFEN(){
        return atualFEN;
    }
    
    public void atualizaFEN(boolean gerarEvento) {
        
        int espaco;
        
        StringBuilder fenBuilder = new StringBuilder();
        
        for (int linha = 7; linha >= 0; linha--) {
            
            espaco = 0;
            
            for (int coluna = 0; coluna < 8; coluna++) {
                                
                JLabel labelCasa = (JLabel) casas.get(linha * 8 + coluna);
                
                if (labelCasa.getIcon() != null){
                    
                    if (espaco > 0) {
                        fenBuilder.append(Integer.toString(espaco));
                        espaco = 0;
                    }
                    
                    int pos = labelCasa.getIcon().toString().lastIndexOf(".");
                    
                    String nomePeca = labelCasa.getIcon().toString().substring(pos-4, pos);
                    
                    if (nomePeca.charAt(3) == 's') {  // peca preta
                        fenBuilder.append(nomePeca.substring(0,1));
                    } else {
                        fenBuilder.append(nomePeca.substring(0, 1).toUpperCase());
                    }
                } else {
                    espaco++;
                }
            }
            if (espaco > 0) {
                fenBuilder.append(Integer.toString(espaco));
            }
            if (linha > 0) {
                fenBuilder.append("/");
            }
        }
        
        if (isJogadorBrancoAtivo()){
            fenBuilder.append(" w ");
        }else{
            fenBuilder.append(" b ");
        }
        
        if (!isPossivelRoqueMenorBrancas() && 
            !isPossivelRoqueMaiorBrancas() &&
            !isPossivelRoqueMenorPretas()  &&
            !isPossivelRoqueMaiorPretas()){
            
            fenBuilder.append("-");
            
        } else {
            if (isPossivelRoqueMenorBrancas()) {
                fenBuilder.append("K");
            }
            if (isPossivelRoqueMaiorBrancas()) {
                fenBuilder.append("Q");
            }
            if (isPossivelRoqueMenorPretas()) {
                fenBuilder.append("k");
            }
            if (isPossivelRoqueMaiorPretas()) {
                fenBuilder.append("q");
            }
        }
        
        if (getCasaEnPassant() == null){
            fenBuilder.append(" - ");
        }else{
            fenBuilder.append(" ");
            fenBuilder.append(getCasaEnPassant());
            fenBuilder.append(" ");
        }
        
        fenBuilder.append(getQuantidadeMovimentos());
        fenBuilder.append(" ");
        fenBuilder.append(getQuantidadeJogadas());
        
        atualFEN = fenBuilder.toString();
        
        if (gerarEvento){
            propertyChangeSupport.firePropertyChange("fen_atualizado", null, null);
        }
    }
    
    private void definePeca(int pos, Peca peca) {
        
        JLabel lblCasa = (JLabel) casas.get(pos);
        
        if (peca.getCor() == DHJOG.Cor.BRANCAS) {
            
            switch (peca.getTipo()) {
                case PEAO:
                    lblCasa.setIcon(iconPeaoBranco);
                    break;
                case TORRE:
                    lblCasa.setIcon(iconTorreBranca);
                    break;
                case CAVALO:
                    lblCasa.setIcon(iconCavaloBranco);
                    break;
                case BISPO:
                    lblCasa.setIcon(iconBispoBranco);
                    break;
                case DAMA:
                    lblCasa.setIcon(iconDamaBranca);
                    break;
                case REI:
                    lblCasa.setIcon(iconReiBranco);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo inválido de Peça [" + peca.getTipo() + "]");
            }
            
        } else {
            
            switch (peca.getTipo()) {
                case PEAO:
                    lblCasa.setIcon(iconPeaoPreto);
                    break;
                case TORRE:
                    lblCasa.setIcon(iconTorrePreta);
                    break;
                case CAVALO:
                    lblCasa.setIcon(iconCavaloPreto);
                    break;
                case BISPO:
                    lblCasa.setIcon(iconBispoPreto);
                    break;
                case DAMA:
                    lblCasa.setIcon(iconDamaPreta);
                    break;
                case REI:
                    lblCasa.setIcon(iconReiPreto);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo inválido de Peça [" + peca.getTipo() + "]");
            }
        }
        
        lblCasa.setVisible(true);
        lblCasa.repaint();
    }
    
    private void desenhaTabuleiro(Graphics2D g2d) {

        ////////////////////////////////////
        // Desenha Linha Legenda Superior //
        ////////////////////////////////////

        int posX = posXTabuleiro;
        int posY = posYTabuleiro;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(posX, posY, comprimentoTabuleiro, comprimentoCasa);

        g2d.setColor(Color.BLACK);
        for (int coluna = 1; coluna < 9; coluna++) {
            posX += comprimentoCasa;
            UtilsGUI.drawText(g2d, Casa.nomeColuna(coluna), posX, posY, comprimentoCasa, comprimentoCasa, 13, SwingConstants.CENTER);
        }

        /////////////////////////////////
        // Desenha Linhas do Tabuleiro //
        /////////////////////////////////

        Color cor = corBranca;
        for (int linha = 0; linha < 8; linha++) {

            posX = posXTabuleiro;
            posY += comprimentoCasa;

            g2d.setColor(Color.WHITE);
            g2d.fillRect(posX, posY, comprimentoCasa, comprimentoCasa);
            g2d.setColor(Color.BLACK);
            UtilsGUI.drawText(g2d, String.valueOf(8 - linha), posX, posY, comprimentoCasa, comprimentoCasa, 13, SwingConstants.CENTER);

            for (int coluna = 0; coluna < 8; coluna++) {
                posX += comprimentoCasa;
                g2d.setColor(cor);
                g2d.fillRect(posX, posY, comprimentoCasa, comprimentoCasa);
                if (coluna != 7) {
                    if (cor == corBranca) {
                        cor = corPreta;
                    } else {
                        cor = corBranca;
                    }
                }
            }

            posX += comprimentoCasa;

            g2d.setColor(Color.WHITE);
            g2d.fillRect(posX, posY, comprimentoCasa, comprimentoCasa);
            g2d.setColor(Color.BLACK);
            UtilsGUI.drawText(g2d, String.valueOf(8 - linha), posX, posY, comprimentoCasa, comprimentoCasa, 13, SwingConstants.CENTER);
        }

        ////////////////////////////////////
        // Desenha Linha Legenda Inferior //
        ////////////////////////////////////

        posX = posXTabuleiro;
        posY += comprimentoCasa;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(posX, posY, comprimentoTabuleiro, comprimentoCasa);

        g2d.setColor(Color.BLACK);
        for (int coluna = 1; coluna < 9; coluna++) {
            posX += comprimentoCasa;
            UtilsGUI.drawText(g2d, Casa.nomeColuna(coluna), posX, posY, comprimentoCasa, comprimentoCasa, 13, SwingConstants.CENTER);
        }

        g2d.setColor(corBorda);
        g2d.setStroke(new BasicStroke(2.0F));
        g2d.drawRect(posXTabuleiro + comprimentoCasa, posYTabuleiro + comprimentoCasa, comprimentoTabuleiro - comprimentoCasa * 2, comprimentoTabuleiro - comprimentoCasa * 2);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        
        ////////////////////////////////////////////////////
        // Calcula os valores para o desenho do Tabuleiro //
        ////////////////////////////////////////////////////

        if (getWidth() > getHeight()) {
            comprimentoTabuleiro = getHeight();
        } else {
            comprimentoTabuleiro = getWidth();
        }

        if (comprimentoTabuleiro % 10 > 0) {
            comprimentoTabuleiro -= comprimentoTabuleiro % 10;
        }

        posXTabuleiro = (getWidth() / 2) - (comprimentoTabuleiro / 2);
        posYTabuleiro = (getHeight() / 2) - (comprimentoTabuleiro / 2);
        comprimentoCasa = comprimentoTabuleiro / 10;

        desenhaTabuleiro(g2d);
        
        // define a posição na tela dos labels pra mostrar os icones
        
        int x, y, l, c;

        l = 7;
        while (l >= 0) {
            for (c = 0; c <= 7; c++) {
                JLabel lblCasa = (JLabel) casas.get((l * 8) + c);
                lblCasa.setSize(comprimentoCasa, comprimentoCasa);

                x = posXTabuleiro + ((c + 1) * comprimentoCasa) + 2;
                y = posYTabuleiro + ((8 - l) * comprimentoCasa);
                lblCasa.setLocation(x, y);
            }
            l--;
        }
    }

    private int contaPecaJogador(Icon icon) {
        
        int qtd = 0;
        
        for(Object obj : casas){
            
            JLabel labelCasa = (JLabel) obj;
            
            if (labelCasa.getIcon() == icon){
                qtd++;
            }            
        }
        
        return qtd;
    }
    
    public boolean possuiRei(boolean jogadorBranco){
        
        if (jogadorBranco){
            if (contaPecaJogador(iconReiBranco) == 0){
                return false;
            }else{
                return true;
            }
        }else{
            if (contaPecaJogador(iconReiPreto) == 0){
                return false;
            }else{
                return true;
            }
        }
    }
    
    public int contaPecasJogador(boolean jogadorBranco){
        
        int qtd = 0;

        for (Object obj : casas) {

            JLabel labelCasa = (JLabel) obj;

            if (labelCasa.getIcon() != null) {
                
                int pos = labelCasa.getIcon().toString().lastIndexOf(".");
                String nomePeca = labelCasa.getIcon().toString().substring(pos - 4, pos);

                if (nomePeca.charAt(3) == 's') {  // peca Preta
                    if (!jogadorBranco) {
                        qtd++;
                    }
                } else { // peca Branca
                    if (jogadorBranco) {
                        qtd++;
                    }
                }
            }
        }
        
        return qtd;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {

        if (!editavel || (iconPecaAtiva == null && !borracha) || !(e.getSource() instanceof JLabel)){
            return;
        }
        
        JLabel casaPeca = (JLabel) e.getSource();
        
        if (e.getButton() == MouseEvent.BUTTON3 || borracha) {
            
            casaPeca.setIcon(null);             
            atualizaFEN(true);            
            propertyChangeSupport.firePropertyChange("peca_apagada", null, null);
            
        }else
            if (e.getButton() == MouseEvent.BUTTON1) {
                
                boolean proximaPecaBranca = pecaAtiva.isWhite();
                TipoPeca proximoTipoPeca  = pecaAtiva.getTipo();

                int quantidade = contaPecasJogador(proximaPecaBranca);

                if (quantidade >= Tabuleiro.QUANTIDADE_MAXIMA_PECAS_POR_JOGADOR) {
                    UtilsGUI.dialogoErro(null,"Cada Jogador pode ter no máximo " + Tabuleiro.QUANTIDADE_MAXIMA_PECAS_POR_JOGADOR + " peças\n"
                                            + "O jogador com as " + (proximaPecaBranca ? "Brancas" : "Pretas") + " já possui o máximo!");
                    return;
                }
                
                int quantidadeMaximaPeca;
                
                if (proximaPecaBranca){
                    quantidadeMaximaPeca = Tabuleiro.quantidadeMaximaPorJogadorTipoPeca(contaPecaJogador(iconPeaoBranco),proximoTipoPeca);
                }else{
                    quantidadeMaximaPeca = Tabuleiro.quantidadeMaximaPorJogadorTipoPeca(contaPecaJogador(iconPeaoPreto), proximoTipoPeca);
                }

                if ((contaPecaJogador(iconPecaAtiva)) >= quantidadeMaximaPeca) {
                    UtilsGUI.dialogoErro(null,"Neste caso, cada Jogador pode ter no máximo " + quantidadeMaximaPeca + " peças do tipo " + proximoTipoPeca +
                                              "\nO jogador com as " + (proximaPecaBranca ? "Brancas" : "Pretas") + " já possui o máximo!");
                    return;
                }

                if (proximoTipoPeca == TipoPeca.PEAO){
                    
                    int indice = casas.indexOf(casaPeca);
                    
                    if (proximaPecaBranca && indice >= 0 && indice <= 7) {
                        UtilsGUI.dialogoErro(null,"Um Peão só se movimenta para frente então é impossível a existência de um\n" +
                                                  "Peão Branco na linha 1, como na casa [" + Casa.porIndice(indice) + "].");
                        return;
                    }
                    
                    if (!proximaPecaBranca && indice >= 56 && indice <= 63) {
                        UtilsGUI.dialogoErro(null,"Um Peão só se movimenta para frente então é impossível a existência de um\n" +
                                                  "Peão Preto na linha 8, como na casa [" + Casa.porIndice(indice) + "].");
                        return;
                    }
                }
                
                casaPeca.setIcon(iconPecaAtiva);
                atualizaFEN(true);
                propertyChangeSupport.firePropertyChange("peca_incluida", null, null);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {       
        
        if (borracha){
            setCursor(iconesToCursor.get(iconBorracha));               
        }else
            if (iconPecaAtiva != null && editavel){
                setCursor(iconesToCursor.get(iconPecaAtiva));                
            }else{
                setCursor(cursorPadrao);
            }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setCursor(cursorPadrao);
    }
}
