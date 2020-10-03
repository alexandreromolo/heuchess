package br.ufpr.inf.heuchess.telas.editorheuristica.panelregiao;

import br.ufpr.inf.heuchess.representacao.heuristica.Regiao;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Casa;
import br.ufpr.inf.heuchess.telas.editorheuristica.PanelEtapa;
import br.ufpr.inf.heuchess.telas.editorheuristica.TelaRegiao;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * 
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 30 de Julho de 2006, 16:19
 */
public class DesenhaRegioes extends JPanel implements UpdateListener, MouseListener, MouseMotionListener {
      
    public enum Ferramenta {
        NENHUMA,
        LAPIS,
        RETANGULO,
        ELIPSE,
        BORRACHA,
        SELECAO
    }
    
    public static Color corPreta  = new Color(209,139,70);
    public static Color corBranca = new Color(254,206,158);
    public static Color corBorda  = new Color(176,176,176);
    
    private static Cursor cursorPadrao = new Cursor(Cursor.DEFAULT_CURSOR);
    private static Cursor cursorMao    = new Cursor(Cursor.HAND_CURSOR);
    
    private int comprimentoTabuleiro;
    private int posXTabuleiro;
    private int posYTabuleiro;    
    public  int comprimentoCasa;    
    
    private Ferramenta ferramentaAtiva;
        
    private ObservableList elements;

    private int x0;
    private int y0;
    private int x1;
    private int y1;
    
    private Color    corFerramenta = Color.black;
    private boolean  active;
    private Elemento selected;
    private float    alpha = .5f;
    
    private ArrayList<Regiao> regioes;    
    
    private PanelEtapa panelEtapa;
    
    private Cursor cursorBorracha, cursorLapiz;
    
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    
    /**
     * Abre o tabuleiro para a edição de novas Regiões
     */
    public DesenhaRegioes(Color cor){
        
        ferramentaAtiva = Ferramenta.NENHUMA;                 
        corFerramenta   = cor;
        
        elements = new ObservableList();
        elements.addUpdateListener(this);
        
        Toolkit tk = Toolkit.getDefaultToolkit();
        
        ImageIcon imageCursorBorracha = new ImageIcon(getClass().getResource("/cursores/cursor_borracha.png")); 
        cursorBorracha = tk.createCustomCursor(imageCursorBorracha.getImage(), new Point(5,10), "Borracha");        
                
        ImageIcon imageCursorLapiz = new ImageIcon(getClass().getResource("/cursores/cursor_lapiz.png")); 
        cursorLapiz = tk.createCustomCursor(imageCursorLapiz.getImage(), new Point(1,19), "Borracha");
                
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    /**
     * Abre o Tabuleiro para Mostrar Regiões já Criadas
     */
    public DesenhaRegioes(PanelEtapa panelEtapa){
        
        this(Color.RED); // A Cor é ignorada 
        
        this.panelEtapa = panelEtapa;
        this.regioes    = panelEtapa.etapa.getRegioes();
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
    
    public void addElement(Elemento element){
        
        elements.addElement(element);   
        
        propertyChangeSupport.firePropertyChange("elemento_incluido", null, null);
    }
    
    public void reset(){
        
        defineFerramenta(Ferramenta.NENHUMA);         
        
        elements = new ObservableList();
        elements.addUpdateListener(this);
    }
    
    private void desenhaTabuleiro(Graphics2D g2d){
        
        ////////////////////////////////////
        // Desenha Linha Legenda Superior //
        ////////////////////////////////////
        
        int posX = posXTabuleiro;
        int posY = posYTabuleiro;
        g2d.setColor(Color.WHITE);
        g2d.fillRect(posX, posY, comprimentoTabuleiro, comprimentoCasa);
        
        g2d.setColor(Color.BLACK);
        for (int coluna = 1; coluna < 9; coluna++){
            posX += comprimentoCasa;
            UtilsGUI.drawText(g2d, Casa.nomeColuna(coluna), posX, posY, comprimentoCasa, comprimentoCasa, 13, SwingConstants.CENTER);
        }
        
        /////////////////////////////////
        // Desenha Linhas do Tabuleiro //
        /////////////////////////////////
        
        Color cor = corBranca;
        for (int linha = 0; linha < 8; linha++){
            
            posX  = posXTabuleiro;
            posY += comprimentoCasa;
            
            g2d.setColor(Color.WHITE);
            g2d.fillRect(posX, posY, comprimentoCasa, comprimentoCasa);
            g2d.setColor(Color.BLACK);
            UtilsGUI.drawText(g2d,String.valueOf(8-linha),posX, posY, comprimentoCasa, comprimentoCasa, 13, SwingConstants.CENTER);
            
            for (int coluna = 0; coluna < 8; coluna++){
                posX += comprimentoCasa;
                g2d.setColor(cor);
                g2d.fillRect(posX, posY, comprimentoCasa, comprimentoCasa);
                if (coluna != 7){
                    if (cor == corBranca){
                        cor = corPreta;
                    }else{
                        cor = corBranca;
                    }
                }
            }
            
            posX += comprimentoCasa;
            
            g2d.setColor(Color.WHITE);
            g2d.fillRect(posX, posY, comprimentoCasa, comprimentoCasa);
            g2d.setColor(Color.BLACK);
            UtilsGUI.drawText(g2d,String.valueOf(8-linha),posX, posY, comprimentoCasa, comprimentoCasa, 13, SwingConstants.CENTER);
        }
        
        ////////////////////////////////////
        // Desenha Linha Legenda Inferior //
        ////////////////////////////////////
        
        posX  = posXTabuleiro;
        posY += comprimentoCasa;
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(posX, posY, comprimentoTabuleiro, comprimentoCasa);
        
        g2d.setColor(Color.BLACK);
        for (int coluna = 1; coluna < 9; coluna++){
            posX += comprimentoCasa;
            UtilsGUI.drawText(g2d, Casa.nomeColuna(coluna), posX, posY, comprimentoCasa, comprimentoCasa, 13, SwingConstants.CENTER);
        }
        
        g2d.setColor(corBorda);        
        g2d.setStroke(new BasicStroke(2.0F)); 
        g2d.drawRect(posXTabuleiro+comprimentoCasa,posYTabuleiro+comprimentoCasa,comprimentoTabuleiro-comprimentoCasa*2,comprimentoTabuleiro-comprimentoCasa*2);
    }
    
    private void desenhaElementos(Graphics2D g2d) {
        
        Rectangle clip = g2d.getClipBounds();
        
        if (clip == null){
            clip = new Rectangle(getSize());
        }
        
        Enumeration els = elements.elements();
        
        while (els.hasMoreElements()) {
            
            Elemento el = (Elemento) els.nextElement();
            
            if ((el != null) && el.getBounds().intersects(clip)){
                el.paint(g2d);
            }
        }        
    }
    
    private void desenhaElementoFerramenta(Graphics2D g2d){
        
        switch(ferramentaAtiva){
            
            case RETANGULO:
                if (active) {
                    g2d.setColor(corFerramenta);                    
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));                  
                    g2d.fillRect(Math.min(x0, x1), Math.min(y0, y1), Math.abs(x0 - x1), Math.abs(y0 - y1));
                }
                break;
                
            case SELECAO:
                if (selected != null) {
                    Rectangle r = selected.getBounds();
                    g2d.setColor(Color.red);
                    g2d.drawRect(r.x + x1 - x0, r.y + y1 - y0, r.width, r.height);
                }
                break;
        }
    }
    
    private void desenhaRegioes(Graphics2D g2d){
        
        float alpha = .8f;
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));        
        
        for (int x = regioes.size() - 1; x >= 0; x--){            
            
            Regiao atual = regioes.get(x);            
            
            if (atual.isVisivel()){
                
                g2d.setColor(atual.getColor());
                
                ArrayList<Casa> casas = atual.getCasas();
                
                for (int y = 0; y < casas.size(); y++){
                    Rectangle posicaoAtual = posicaoCasa(casas.get(y));
                    g2d.fillRect(posicaoAtual.x, posicaoAtual.y, posicaoAtual.width, posicaoAtual.height);
                }
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        ////////////////////////////////////////////////////
        // Calcula os valores para o desenho do Tabuleiro //
        ////////////////////////////////////////////////////
        
        if (getWidth() > getHeight()){
            comprimentoTabuleiro = getHeight();            
        }else{
            comprimentoTabuleiro = getWidth();            
        }        
        
        if (comprimentoTabuleiro % 10 > 0){
            comprimentoTabuleiro -= comprimentoTabuleiro % 10;
        }
        
        posXTabuleiro   = (getWidth()/2)  - (comprimentoTabuleiro/2);
        posYTabuleiro   = (getHeight()/2) - (comprimentoTabuleiro/2);        
        comprimentoCasa = comprimentoTabuleiro/10;
        
        desenhaTabuleiro(g2d); 
        
        if (regioes == null){
            desenhaElementos(g2d); 
            desenhaElementoFerramenta(g2d);        
        }else{
            desenhaRegioes(g2d);
        }
    }
        
    public void defineFerramenta(Ferramenta ferramenta){
        
        ferramentaAtiva = ferramenta;         
        
        switch(ferramenta){
            
            case NENHUMA:
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                break;
                
            case RETANGULO:
                setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                break;
                
            case SELECAO:
                setCursor(new Cursor(Cursor.MOVE_CURSOR));
                break;
                
            case BORRACHA:
                setCursor(cursorBorracha);                
                break;
                
            case LAPIS:                
                setCursor(cursorLapiz);  
                break;
        }
    }
    
    public Ferramenta getFerramenta(){
        return ferramentaAtiva;
    }
    
    public void setCorFerramenta(Color color){
        corFerramenta = color;
    }
    
    public Color getCorFerramenta(){
        return corFerramenta;
    }
    
    @Override
    public void updateOccurred(UpdateEvent e) {
        
        if (selected != null) {
            selected = null;
        }
        
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
        if (regioes != null){
            
            if (e.getClickCount() == 2){
                
                final Regiao regiao = localizaRegiaoAlvo(e.getX(),e.getY());
                
                if (regiao != null){
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            TelaRegiao tela = new TelaRegiao(panelEtapa, panelEtapa, regiao, false);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {       
        
    }

    @Override
    public void mouseExited(MouseEvent e) {        
        
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        
        if (regioes == null){
            
            switch(ferramentaAtiva){
                
                case RETANGULO:
                    if (validaCoordenadaMouse(e.getX(),e.getY())){
                        x0 = corrigePosXCasa(e.getX());
                        y0 = corrigePosYCasa(e.getY());
                    }else{
                        x0 = -1;
                        y0 = -1;
                    }
                    break;
                    
                case SELECAO:
                    if (validaCoordenadaMouse(e.getX(),e.getY())){
                        Elemento elemento = localizaElementoAlvo(e.getX(),e.getY());
                        if (elemento != null){
                            x0 = x1 = e.getX();
                            y0 = y1 = e.getY();
                            repaint();
                        }
                    }else{
                        x0 = -1;
                        y0 = -1;
                    }
                    break;
                    
                case BORRACHA:
                    if (validaCoordenadaMouse(e.getX(),e.getY())){
                        Elemento elemento = localizaElementoAlvo(e.getX(),e.getY());
                        if (elemento != null){
                            elements.removeElement(elemento);
                            propertyChangeSupport.firePropertyChange("elemento_apagado", null, null);
                        }
                    }
                    x0 = -1;
                    y0 = -1;
                    break;
                    
                case LAPIS:
                    if (validaCoordenadaMouse(e.getX(),e.getY())){
                        Elemento elemento = localizaElementoAlvo(e.getX(),e.getY());
                        if (elemento == null){
                            x0 = corrigePosXCasa(e.getX());
                            y0 = corrigePosYCasa(e.getY());
                            elements.addElement(new Ponto(this,casaPosXY(x0,y0),corFerramenta));
                            propertyChangeSupport.firePropertyChange("elemento_incluido", null, null);
                        }
                    }
                    x0 = -1;
                    y0 = -1;
                    break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
        if (regioes == null){
            
            switch(ferramentaAtiva){
                
                case RETANGULO:
                    if (active) {
                        x1 = corrigePosXCasa(x1);
                        y1 = corrigePosYCasa(y1);
                        active = false;
                        elements.addElement(new Retangulo(this,
                                casaPosXY(Math.min(x0, x1),Math.min(y0, y1)),
                                casaPosXY(Math.max(x0,x1),Math.max(y0,y1)),
                                corFerramenta));
                        x0 = -1;
                        y0 = -1;
                        propertyChangeSupport.firePropertyChange("elemento_incluido", null, null);
                    }
                    break;
                    
                case SELECAO:
                    
                    if (selected != null) {
                        
                        x1 = corrigePosX(e.getX());
                        y1 = corrigePosY(e.getY());
                        
                        Rectangle r = selected.getBounds();
                        r.x = r.x + x1 - x0;
                        r.y = r.y + y1 - y0;
                        
                        // Mantém dentro do tabuleiro //
                        
                        if (r.x < (posXTabuleiro + comprimentoCasa)){
                            x1 += (posXTabuleiro + comprimentoCasa) - r.x;
                        }
                        if ((r.x + r.width) > (posXTabuleiro + comprimentoTabuleiro - comprimentoCasa)){
                            x1 -= (r.x + r.width) - (posXTabuleiro + comprimentoTabuleiro - comprimentoCasa);
                        }
                        if (r.y < (posYTabuleiro + comprimentoCasa)){
                            y1 += (posYTabuleiro + comprimentoCasa) - r.y;
                        }
                        if ((r.y + r.height) > (posYTabuleiro + comprimentoTabuleiro - comprimentoCasa)){
                            y1 -= (r.y + r.height) - (posYTabuleiro + comprimentoTabuleiro - comprimentoCasa);
                        }
                        
                        elements.replaceElementAtEnd(selected, selected.getTranslated(x1 - x0, y1 - y0));
                        
                        propertyChangeSupport.firePropertyChange("elemento_movido", null, null);
                        
                        selected = null;
                        repaint();
                        
                        x0 = -1;
                        y0 = -1;
                    }
                    break;
            }
        }
    }    

    @Override
    public void mouseDragged(MouseEvent e) {
        
        if (regioes == null){
            
            switch(ferramentaAtiva){
                
                case RETANGULO:
                    if (x0 != -1 && y0 != -1){
                        int x2 = active ? x1 : x0, y2 = active ? y1 : y0;
                        
                        x1 = corrigePosX(e.getX());
                        y1 = corrigePosY(e.getY());
                        
                        active = true;
                        int xm = Math.min(Math.min(x0, x1), x2),
                                xM = Math.max(Math.max(x0, x1), x2),
                                ym = Math.min(Math.min(y0, y1), y2),
                                yM = Math.max(Math.max(y0, y1), y2);
                        repaint(xm, ym, 1 + xM - xm, 1 + yM - ym);
                    }
                    break;
                    
                case SELECAO:
                    if (selected != null) {
                        
                        x1 = corrigePosX(e.getX());
                        y1 = corrigePosY(e.getY());
                        
                        Rectangle r = selected.getBounds();
                        r.x = r.x + x1 - x0;
                        r.y = r.y + y1 - y0;
                        
                        if (r.x < (posXTabuleiro + comprimentoCasa)){
                            x1 += (posXTabuleiro + comprimentoCasa) - r.x;
                        }
                        if ((r.x + r.width) > (posXTabuleiro + comprimentoTabuleiro - comprimentoCasa)){
                            x1 -= (r.x + r.width) - (posXTabuleiro + comprimentoTabuleiro - comprimentoCasa);
                        }
                        if (r.y < (posYTabuleiro + comprimentoCasa)){
                            y1 += (posYTabuleiro + comprimentoCasa) - r.y;
                        }
                        if ((r.y + r.height) > (posYTabuleiro + comprimentoTabuleiro - comprimentoCasa)){
                            y1 -= (r.y + r.height) - (posYTabuleiro + comprimentoTabuleiro - comprimentoCasa);
                        }
                        
                        repaint();
                    }
                    break;
                    
                case BORRACHA:
                    if (validaCoordenadaMouse(e.getX(),e.getY())){
                        Elemento elemento = localizaElementoAlvo(e.getX(),e.getY());
                        if (elemento != null){
                            elements.removeElement(elemento);
                            propertyChangeSupport.firePropertyChange("elemento_apagado", null, null);
                        }                                             
                    }
                    x0 = -1;
                    y0 = -1;
                    break;
                    
                case LAPIS:
                    if (validaCoordenadaMouse(e.getX(),e.getY())){
                        Elemento elemento = localizaElementoAlvo(e.getX(),e.getY());
                        if (elemento == null){
                            x0 = corrigePosXCasa(e.getX());
                            y0 = corrigePosYCasa(e.getY());
                            elements.addElement(new Ponto(this,casaPosXY(x0,y0),corFerramenta));
                            propertyChangeSupport.firePropertyChange("elemento_incluido", null, null);
                        }
                    }
                    x0 = -1;
                    y0 = -1;
                    break;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
        if (regioes != null){
            
            Regiao regiao = localizaRegiaoAlvo(e.getX(),e.getY());
            
            if (regiao != null){
                setCursor(cursorMao);      
            }else{
                setCursor(cursorPadrao);
            }
        }
    }
    
    private boolean validaCoordenadaMouse(int x, int y){
        
        if (x < (posXTabuleiro + comprimentoCasa)){
            return false;
        }
        if (x > (posXTabuleiro + comprimentoTabuleiro - comprimentoCasa)){
            return false;
        }
        if (y < (posYTabuleiro + comprimentoCasa)){
            return false;
        }
        if (y > (posYTabuleiro + comprimentoTabuleiro - comprimentoCasa)){
            return false;
        }
        
        return true;
    }
    
    private int corrigePosX(int x){
        
        if (x < (posXTabuleiro + comprimentoCasa)){
            return (posXTabuleiro + comprimentoCasa);
        }
        if (x > (posXTabuleiro + comprimentoTabuleiro - comprimentoCasa)){
            return (posXTabuleiro + comprimentoTabuleiro - comprimentoCasa);
        }
        
        return x;
    }
    
    private int corrigePosY(int y){
        
        if (y < (posYTabuleiro + comprimentoCasa)){
            return (posYTabuleiro + comprimentoCasa);
        }
        if (y > (posYTabuleiro + comprimentoTabuleiro - comprimentoCasa)){
            return (posYTabuleiro + comprimentoTabuleiro - comprimentoCasa);
        }
        
        return y;
    }
    
    public int corrigePosXCasa(int x){
        
        x  = corrigePosX(x);
        x -= (posXTabuleiro + comprimentoCasa);
        
        if (x == 0){
            x += comprimentoCasa/2;
        }else
            if (x % comprimentoCasa > 0){
                x -= (x % comprimentoCasa);
                x += comprimentoCasa/2;
            }else{
                x -= comprimentoCasa/2;
            }
        
        return x + (posXTabuleiro + comprimentoCasa);
    }
    
    public int corrigePosYCasa(int y){
        
        y  = corrigePosY(y);
        y -= (posYTabuleiro + comprimentoCasa);
        
        if (y == 0){
            y += comprimentoCasa/2;
        }else
            if (y % comprimentoCasa > 0){
                y -= (y % comprimentoCasa);
                y += comprimentoCasa/2;
            }else{
                y -= comprimentoCasa/2;
            }
        
        return y + (posYTabuleiro + comprimentoCasa);
    }    
     
    private Elemento localizaElementoAlvo(int x, int y){
        
        selected           = null;
        Elemento oSelected = selected;
        Enumeration els    = elements.elements();
        
        while (els.hasMoreElements()) {
            
            Elemento el = (Elemento) els.nextElement();
            
            if ((el != null) && el.getBounds().contains(x, y)){
                selected = el;
            }
        }
        
        if (selected != oSelected){
            return selected;                        
        }else{
            return null;
        }
    }
    
    public Casa casaPosXY(int x, int y){
        
        int linha, coluna;
        
        x = corrigePosXCasa(x);
        y = corrigePosYCasa(y);
        
        x     -= (posXTabuleiro + comprimentoCasa);
        coluna = (x / comprimentoCasa) + 1;
        
        y    -= (posYTabuleiro + comprimentoCasa);
        linha = 8 - (y / comprimentoCasa);
        
        return Casa.porIndices(coluna - 1, linha - 1);
    }
    
    public Dimension distanciaEsquerdaTopo(int x, int y){
        
        Casa casa = casaPosXY(x,y);
        
        int bordaX = ((casa.getColuna() - 1)* comprimentoCasa) + (posXTabuleiro + comprimentoCasa);        
        int bordaY = ((8 - casa.getLinha()) * comprimentoCasa) + (posYTabuleiro + comprimentoCasa);
        
        return new Dimension(x - bordaX, y - bordaY);
    }
    
    public Dimension distanciaDireitaBase(int x, int y){
        
        Casa casa =casaPosXY(x,y);
        
        int bordaX = ((casa.getColuna() - 1)* comprimentoCasa) + (posXTabuleiro + comprimentoCasa);        
        int bordaY = ((8 - casa.getLinha()) * comprimentoCasa) + (posYTabuleiro + comprimentoCasa);
        
        return new Dimension(((bordaX + comprimentoCasa) - x),((bordaY + comprimentoCasa) - y));
    }
    
    public Rectangle posicaoCasa(Casa casa){
        
        int x = ((casa.getColuna() - 1)* comprimentoCasa) + (posXTabuleiro + comprimentoCasa);
        int y = ((8 - casa.getLinha()) * comprimentoCasa) + (posYTabuleiro + comprimentoCasa);
        
        return new Rectangle(x, y, comprimentoCasa, comprimentoCasa);
    }
    
    public int totalElementos(){
        return elements.totalElements();
    }
    
    public ArrayList<Casa> todasCasas(){
        
        ArrayList<Casa> todasCasas = new ArrayList();
        
        Enumeration els = elements.elements();
        
        while (els.hasMoreElements()) {
            
            Elemento el = (Elemento) els.nextElement();
            Casa[] casas = el.getCasas();
            
            for(int x = 0; x < casas.length; x++){
                
                Casa nova = casas[x];                
                
                boolean repetida = false;
                
                /////////////////////////////////////////////////
                // Verifica se já não foi recuperada está casa //                
                /////////////////////////////////////////////////
                
                for (int y = 0; y < todasCasas.size(); y++){
                    
                    if (todasCasas.get(y).equals(nova)){
                        repetida = true;
                        break;
                    }
                }
                
                if (!repetida){
                    todasCasas.add(nova);
                }
            }            
        }
        
        return todasCasas;
    }
    
    public Regiao localizaRegiaoAlvo(int posX, int posY){
        
        for (int x = 0; x < regioes.size(); x++){
            
            Regiao atual = regioes.get(x);
            
            if (atual.isVisivel()){
                
                ArrayList<Casa> casas = atual.getCasas();
                
                for (int y = 0; y < casas.size(); y++){
                    
                    Rectangle posicaoAtual = posicaoCasa(casas.get(y));
                    
                    if ((posicaoAtual.x <= posX) && (posX <= (posicaoAtual.x + posicaoAtual.width)) &&
                        (posicaoAtual.y <= posY) && (posY <= (posicaoAtual.y + posicaoAtual.height))){
                        
                        return atual;
                    }
                }
            }
        }
        
        return null;        
    }
  
    public void transfereCasas(Regiao regiao) {
        
        regiao.getCasas().clear();
        regiao.getCasasSimetricas().clear();

        for (Casa casa : todasCasas()) {
            regiao.addCasa(casa);
        }
    }
}