package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.competicaoheuristica.*;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Casa;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Peca;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro;
import static br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro.TOTAL_COLUNAS;
import static br.ufpr.inf.heuchess.representacao.situacaojogo.Tabuleiro.TOTAL_LINHAS;
import java.awt.*;
import static java.awt.Image.SCALE_SMOOTH;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Sep 12, 2012
 */
public class DesenhaTabuleiro extends JComponent {

    private static final long serialVersionUID = 6200021664655625524L;
    
    private static final int DEFAULT_BORDER_THICKNESS = 16;
    
    private static final Color  OVER_ENABLED_COLOR      = new Color(0, 127, 0, 64);
    private static final Color  OVER_DISABLED_COLOR     = new Color(127, 0, 0, 64);
    private static final Color  AVAILABLE_COLOR         = new Color(127, 127, 0, 64);
    private static final Color  SELECTED_COLOR          = new Color(0, 255, 0, 64);
    private static final Color  HIGHLIGHTED_MOVE_COLOR  = new Color(160, 160, 216, 192);
    private static final Stroke HIGHLIGHTED_MOVE_STROKE = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    private final List<LanceListener> lanceListeners = new ArrayList<>(2);
    private final DesenhaPecas       desenhaPecas;
    
    private transient Image tabuleiroBackground;
    
    private int cellSideLength;
    private int borderThickness = DEFAULT_BORDER_THICKNESS;
    private int tabuleiroLF;
    private int pecaLF;
    
    private Tabuleiro tabuleiro;
    private Lance     highLightedMove;
    
    private Casa   casaSob;
    private Casa   casaSelecionada;
    private Casa[] availableTargets;
    
    private boolean highlightLastMove = true;
    private boolean highlightValids   = true;
    private boolean flipView;

    public DesenhaTabuleiro(final Partida pGame, final DesenhaPecas pDesenhaPecas) {

        assert pGame         != null;
        assert pDesenhaPecas != null;

        desenhaPecas = pDesenhaPecas;

        setCellSideLength(64);

        setTabuleiro(pGame.getTabuleiro());

        final MouseAdapter ma = new TabuleiroMouseAdapter(this);

        addMouseListener(ma);
        addMouseMotionListener(ma);

        final Partida game = pGame;

        game.addPropertyChangeListener("position", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent pEvt) {

                assert pEvt != null;

                setTabuleiro(game.getTabuleiro());
                setHighlightedMove(game.getCurrentMove());
            }
        });

        clearState();
    }

    public DesenhaTabuleiro(final DesenhaPecas pDesenhaPecas) {

        assert pDesenhaPecas  != null;

        desenhaPecas = pDesenhaPecas;

        setCellSideLength(64);

        final MouseAdapter ma = new TabuleiroMouseAdapter(this);

        addMouseListener(ma);
        addMouseMotionListener(ma);

        clearState();
    }
    
    public void addLanceListener(LanceListener lanceListener) {

        assert lanceListener != null;

        if (!lanceListeners.contains(lanceListener)) {
            lanceListeners.add(lanceListener);
        }
    }
    
    public void setTabuleiroLF(final int pNumero) {

        assert pNumero >= 0;

        if (pNumero != tabuleiroLF) {
            tabuleiroLF = pNumero;
            tabuleiroBackground = null;
            repaint();
        }
    }

    public final void setCellSideLength(final int novaLargura) {

        assert novaLargura >= 1;

        if (novaLargura != cellSideLength) {
            cellSideLength = novaLargura;
            tabuleiroBackground = null;
            desenhaPecas.initialize(novaLargura, "/pecas/lf" + pecaLF + "/");
        }
    }

    public void setCoordinatesPainted(final boolean pAffiche) {

        if (pAffiche) {
            borderThickness = DEFAULT_BORDER_THICKNESS;
        } else {
            borderThickness = 0;
        }

        tabuleiroBackground = null;
        
        repaint();
    }

    @Override
    public void setEnabled(final boolean pFlag) {

        super.setEnabled(pFlag);

        clearState();
    }

    public void setFlipView(boolean pflipViewe) {

        if (pflipViewe != flipView) {
            
            flipView = pflipViewe;
            tabuleiroBackground = null;
            
            repaint();
        }
    }

    public Lance getHighlightedMove(){
        return highLightedMove;
    }
    
    public void setHighlightedMove(Lance move) {
        
        highLightedMove = move;

        clearState();
    }

    public void setHighlightLastMove(final boolean pMontre) {
        
        highlightLastMove = pMontre;

        repaint();
    }

    public void setHighlightValids(final boolean pSurligne) {
        highlightValids = pSurligne;
    }

    public void setPecaLF(final int pNumero) {

        assert pNumero >= 0;

        if (pNumero != pecaLF) {
            pecaLF = pNumero;
            desenhaPecas.initialize(cellSideLength, "/pecas/lf" + pecaLF + "/");
            
            repaint();
        }
    }

    public int getCellSideLength() {
        return cellSideLength;
    }

    @Override
    public Dimension getPreferredSize() {
        
        final int cote = getSideLength();
        
        return new Dimension(cote, cote);
    }

    @Override
    public void paint(final Graphics pGraph) {

        final Graphics2D g2d = (Graphics2D) pGraph;

        g2d.drawImage(getTabuleiroBackground(), 0, 0, null);

        final int dimCase = getCellSideLength();

        if (highlightLastMove && (highLightedMove != null)) {

            g2d.setColor(HIGHLIGHTED_MOVE_COLOR);
            g2d.setStroke(HIGHLIGHTED_MOVE_STROKE);
            final Casa src = highLightedMove.getCasaOrigem();
            final Casa dst = highLightedMove.getCasaDestino();
            final int demieCase = dimCase / 2;

            if (flipView) {
                g2d.drawLine(borderThickness + (TOTAL_COLUNAS - src.getIndiceColuna() - 1) * dimCase + demieCase,
                        borderThickness + src.getIndiceLinha() * dimCase + demieCase,
                        borderThickness + (TOTAL_COLUNAS - dst.getIndiceColuna() - 1) * dimCase + demieCase,
                        borderThickness + dst.getIndiceLinha() * dimCase + demieCase);
            } else {
                g2d.drawLine(borderThickness + src.getIndiceColuna() * dimCase + demieCase,
                        borderThickness + (TOTAL_LINHAS - src.getIndiceLinha() - 1) * dimCase + demieCase,
                        borderThickness + dst.getIndiceColuna() * dimCase + demieCase,
                        borderThickness + (TOTAL_LINHAS - dst.getIndiceLinha() - 1) * dimCase + demieCase);
            }
        }

        if (casaSob != null) {

            if ((casaSelecionada == null) && (availableTargets != null) && (availableTargets.length != 0)) {
                g2d.setColor(OVER_ENABLED_COLOR);
            } else if ((casaSelecionada != null) && (availableTargets != null) && (isAvailable(casaSob))) {
                g2d.setColor(OVER_ENABLED_COLOR);
            } else {
                g2d.setColor(OVER_DISABLED_COLOR);
            }

            final int cX = casaSob.getIndiceColuna();
            final int cY = casaSob.getIndiceLinha();
            final int dx;
            final int dy;

            if (flipView) {
                dx = borderThickness + (TOTAL_COLUNAS - cX - 1) * dimCase + 1;
                dy = borderThickness + cY * dimCase + 1;
            } else {
                dx = borderThickness + cX * dimCase + 1;
                dy = borderThickness + (TOTAL_LINHAS - cY - 1) * dimCase + 1;
            }

            if (borderThickness > 0) {

                final int cote = getSideLength();
                g2d.fillRect(1, dy, borderThickness - 1, dimCase - 1);
                g2d.fillRect(cote - borderThickness, dy, borderThickness - 1, dimCase - 1);
                g2d.fillRect(dx, 1, dimCase - 1, borderThickness - 1);
                g2d.fillRect(dx, cote - borderThickness, dimCase - 1, borderThickness - 1);
            }

            g2d.fillRect(dx, dy, dimCase - 1, dimCase - 1);
        }

        if (casaSelecionada != null) {

            g2d.setColor(SELECTED_COLOR);

            if (flipView) {
                g2d.fillRect(borderThickness + (TOTAL_COLUNAS - casaSelecionada.getIndiceColuna() - 1) * dimCase + 1,
                        borderThickness + casaSelecionada.getIndiceLinha() * dimCase + 1,
                        dimCase - 1,
                        dimCase - 1);
            } else {
                g2d.fillRect(borderThickness + casaSelecionada.getIndiceColuna() * dimCase + 1,
                        borderThickness + (TOTAL_LINHAS - casaSelecionada.getIndiceLinha() - 1) * dimCase + 1,
                        dimCase - 1,
                        dimCase - 1);
            }
        }

        for (int indice = 0; indice < Tabuleiro.TOTAL_CASAS; indice++) {

            Casa casa = Casa.porIndice(indice);
            
            if (highlightValids && (availableTargets != null) && isAvailable(casa)) {

                g2d.setColor(AVAILABLE_COLOR);

                if (flipView) {
                    g2d.fillRect(borderThickness + (TOTAL_COLUNAS - casa.getIndiceColuna() - 1) * dimCase + 1,
                            borderThickness + casa.getIndiceLinha() * dimCase + 1,
                            dimCase - 1,
                            dimCase - 1);
                } else {
                    g2d.fillRect(borderThickness + casa.getIndiceColuna() * dimCase + 1,
                            borderThickness + (TOTAL_LINHAS - casa.getIndiceLinha() - 1) * dimCase + 1,
                            dimCase - 1,
                            dimCase - 1);
                }
            }

            if (tabuleiro != null) {
                
                final Peca p = tabuleiro.getPeca(casa);

                if ((p != null) && (casa != casaSelecionada)) {

                    if (flipView) {
                        g2d.drawImage(desenhaPecas.getImage(p),
                                borderThickness + (TOTAL_COLUNAS - casa.getIndiceColuna() - 1) * dimCase,
                                borderThickness + casa.getIndiceLinha() * dimCase,
                                null);
                    } else {
                        g2d.drawImage(desenhaPecas.getImage(p),
                                borderThickness + casa.getIndiceColuna() * dimCase,
                                borderThickness + (TOTAL_LINHAS - casa.getIndiceLinha() - 1) * dimCase,
                                null);
                    }
                }
            }
        }
    }
    
    public void clearAll(){
    
        tabuleiro = null;
        
        casaSob        = null;
        casaSelecionada    = null;
        availableTargets  = null;
        highlightLastMove = false;
        
        repaint();        
    }
    
    public void showTabuleiro(final Tabuleiro tabuleiro, final Lance lastMove){
    
        assert tabuleiro != null;

        this.tabuleiro = tabuleiro;
                
        casaSob = null;
        casaSelecionada = null;
        availableTargets = null;
        
        if (lastMove != null){
            highlightLastMove = true;
            highLightedMove   = lastMove;
        }else{
            highlightLastMove = false;
            highLightedMove   = null;
        }
        
        repaint();        
    }
    
    private void fireMoveEvent(Lance movimento) {

        assert movimento != null;

        final LanceEvent evt = new LanceEvent(this, movimento);

        for (final LanceListener l : lanceListeners) {
            l.moved(evt);
        }
    }

    private Image getTabuleiroBackground() {

        if (tabuleiroBackground == null) {

            final String lf = "/pecas/lf" + tabuleiroLF + "/";

            final int dimCase = getCellSideLength();
            Image tmpImg = new ImageIcon(getClass().getResource(lf + "bs.jpg")).getImage();

            final Image caseNoire = new ImageIcon(tmpImg.getScaledInstance(dimCase, dimCase, SCALE_SMOOTH)).getImage();
            tmpImg = new ImageIcon(getClass().getResource(lf + "ws.jpg")).getImage();

            final Image caseBlanche = new ImageIcon(tmpImg.getScaledInstance(dimCase, dimCase, SCALE_SMOOTH)).getImage();
            final int cote = getSideLength();

            final BufferedImage fond = new BufferedImage(cote, cote, TYPE_INT_RGB);
            final Graphics2D g2d = fond.createGraphics();

            if (borderThickness > 0) {
                g2d.drawImage(caseNoire, borderThickness - dimCase, borderThickness - dimCase, null);
                g2d.drawImage(caseNoire, cote - borderThickness, cote - borderThickness, null);
                g2d.drawImage(caseBlanche, cote - borderThickness, borderThickness - dimCase, null);
                g2d.drawImage(caseBlanche, borderThickness - dimCase, cote - borderThickness, null);

                g2d.setColor(Color.WHITE);
                g2d.fillRect(borderThickness, 0, cote - 2 * borderThickness, borderThickness);
                g2d.fillRect(borderThickness, cote - borderThickness, cote - 2 * borderThickness, borderThickness);
                g2d.fillRect(0, borderThickness, borderThickness, cote - 2 * borderThickness);
                g2d.fillRect(cote - borderThickness, borderThickness, borderThickness, cote - 2 * borderThickness);
            }

            boolean blanc = true;

            for (int y = borderThickness; y < (cote - dimCase); y += dimCase) {
                for (int x = borderThickness; x < (cote - dimCase); x += dimCase) {

                    if (blanc) {
                        g2d.drawImage(caseBlanche, x, y, null);
                    } else {
                        g2d.drawImage(caseNoire, x, y, null);
                    }
                    blanc = !blanc;
                }
                blanc = !blanc;
            }

            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, cote - 1, cote - 1);

            for (int i = borderThickness; i < cote; i += dimCase) {
                g2d.drawLine(i, 0, i, cote);
                g2d.drawLine(0, i, cote, i);
            }

            if (borderThickness > 0) {

                final FontMetrics fm = g2d.getFontMetrics();
                final int hFonte = fm.getHeight();
                final int dyCol = (borderThickness - hFonte) / 2;

                for (int i = TOTAL_COLUNAS - 1; i >= 0; i--) {

                    final char cCol;

                    if (flipView) {
                        cCol = (char) ('h' - i);
                    } else {
                        cCol = (char) ('a' + i);
                    }

                    final String col = Character.toString(cCol);
                    final int dxCol = borderThickness + (dimCase - fm.charWidth(cCol)) / 2;

                    g2d.drawString(col, i * dimCase + dxCol, borderThickness - dyCol - 3);
                    g2d.drawString(col, i * dimCase + dxCol, cote - dyCol - 4);
                }

                final int dyLig = borderThickness + (dimCase - hFonte) / 2 + hFonte - 1;

                for (int i = TOTAL_LINHAS - 1; i >= 0; i--) {

                    final char cLig;

                    if (flipView) {
                        cLig = (char) ('1' + i);
                    } else {
                        cLig = (char) (('0' + TOTAL_LINHAS) - i);
                    }

                    final String lig = Character.toString(cLig);
                    final int dxLig = (borderThickness - fm.charWidth(cLig)) / 2;
                    g2d.drawString(lig, dxLig, i * dimCase + dyLig);
                    g2d.drawString(lig, cote - borderThickness + dxLig, i * dimCase + dyLig);
                }
            }
            g2d.dispose();

            tabuleiroBackground = fond;
        }

        assert tabuleiroBackground != null;
        
        return tabuleiroBackground;
    }

    private int getSideLength() {
        return 2 * borderThickness + TOTAL_LINHAS * getCellSideLength() + 1;
    }

    private boolean isAvailable(final Casa pCase) {

        assert pCase != null;

        for (int i = availableTargets.length - 1; i >= 0; i--) {
            if (availableTargets[i] == pCase) {
                return true;
            }
        }

        return false;
    }

    public Tabuleiro getTabuleiro(){
        return tabuleiro;
    }
    
    private void setTabuleiro(final Tabuleiro tabuleiro) {

        assert tabuleiro != null;

        this.tabuleiro = tabuleiro;
    }
    
    private void clearState() {

        casaSob = null;
        casaSelecionada = null;
        availableTargets = null;

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        repaint();
    }
    
    public void mouseClicked(final int pX, final int pY) {

        if (!isEnabled()) {
            return;
        }

        if ((casaSelecionada == null) && (availableTargets != null) && (availableTargets.length != 0)) {

            casaSelecionada = casaSob;
            
            if (tabuleiro != null){
                setCursor(desenhaPecas.getCursor(tabuleiro.getPeca(casaSelecionada)));
                repaint();
            }
        } else {

            if (casaSelecionada != null) {
                
                if ((availableTargets != null) && (isAvailable(casaSob))) {
                    
                    if (tabuleiro != null){
                        final Lance mvt = new Lance(tabuleiro.getPeca(casaSelecionada), casaSelecionada, casaSob, tabuleiro.getPeca(casaSob));
                        setTabuleiro(tabuleiro.derive(mvt, true));
                        fireMoveEvent(mvt);
                    }
                }
                
                setCursor(Cursor.getDefaultCursor());
                casaSelecionada = null;
                casaSob     = null;
                mouseOver(pX, pY);
            }
        }
    }

    public void mouseOver(final int pX, final int pY) {

        if (!isEnabled()) {
            return;
        }

        Casa cellule = null;

        if ((pX > borderThickness) && (pY > borderThickness)) {

            final int cote = getSideLength();
            
            if ((pX < cote - borderThickness) && (pY < cote - borderThickness)) {

                final int dimCase = getCellSideLength();

                if (flipView) {
                    cellule = Casa.porIndices(TOTAL_COLUNAS - (pX - borderThickness - 1) / dimCase - 1, (pY - borderThickness - 1) / dimCase);
                } else {
                    cellule = Casa.porIndices((pX - borderThickness - 1) / dimCase, TOTAL_LINHAS - (pY - borderThickness - 1) / dimCase - 1);
                }
            }
        }

        if (cellule != casaSob) {

            casaSob = cellule;

            if (casaSelecionada == null) {

                availableTargets = null;

                if (casaSob != null) {

                    if (tabuleiro != null) {
                        
                        final Peca p = tabuleiro.getPeca(casaSob);
                        
                        if ((p != null) && (p.isWhite() == tabuleiro.isWhiteActive())) {
                            availableTargets = tabuleiro.getMovimentosValidosPeca(casaSob);
                        }
                    }
                }
            }
            
            repaint();
        }
    }
    
    private static final class TabuleiroMouseAdapter extends MouseAdapter {

        private final   DesenhaTabuleiro desenhaTabuleiro;
        private boolean mouseOver;

        TabuleiroMouseAdapter(final DesenhaTabuleiro desenhaTabuleiro) {
            this.desenhaTabuleiro = desenhaTabuleiro;
        }

        @Override
        public void mouseDragged(final MouseEvent pEvent) {
            
            if (mouseOver) {
                desenhaTabuleiro.mouseOver(pEvent.getX(), pEvent.getY());
            }
        }

        @Override
        public void mouseEntered(final MouseEvent pEvent) {
            mouseOver = true;
            desenhaTabuleiro.mouseOver(pEvent.getX(), pEvent.getY());
        }

        @Override
        public void mouseExited(final MouseEvent pEvent) {
            mouseOver = false;
            desenhaTabuleiro.mouseOver(-1, -1);
        }

        @Override
        public void mouseMoved(final MouseEvent pEvent) {
            desenhaTabuleiro.mouseOver(pEvent.getX(), pEvent.getY());
        }

        @Override
        public void mousePressed(final MouseEvent pEvent) {
            
            if (mouseOver) {
                desenhaTabuleiro.mouseClicked(pEvent.getX(), pEvent.getY());
            }
        }
    }
}
