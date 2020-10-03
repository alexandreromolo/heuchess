package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.representacao.heuristica.DHJOG;
import br.ufpr.inf.heuchess.representacao.situacaojogo.Lance;
import br.ufpr.inf.utils.UtilsString;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * @since  Sep 12, 2012
 */
public class RenderTreeJogadas extends DefaultTreeCellRenderer implements TreeCellRenderer {
        
    ImageIcon iconeTabuleiro = new ImageIcon(getClass().getResource("/icones/tabuleiro-icone.png"));    
    
    ImageIcon iconeMovimentoPeaoBranco   = new ImageIcon(getClass().getResource("/icones/icone_movimento-peao-branco.png"));
    ImageIcon iconeMovimentoTorreBranca  = new ImageIcon(getClass().getResource("/icones/icone_movimento-torre-branca.png"));
    ImageIcon iconeMovimentoCavaloBranco = new ImageIcon(getClass().getResource("/icones/icone_movimento-cavalo-branco.png"));
    ImageIcon iconeMovimentoBispoBranco  = new ImageIcon(getClass().getResource("/icones/icone_movimento-bispo-branco.png"));
    ImageIcon iconeMovimentoDamaBranca   = new ImageIcon(getClass().getResource("/icones/icone_movimento-dama-branca.png"));
    ImageIcon iconeMovimentoReiBranco    = new ImageIcon(getClass().getResource("/icones/icone_movimento-rei-branco.png"));
    
    ImageIcon iconeMovimentoPeaoPreto    = new ImageIcon(getClass().getResource("/icones/icone_movimento-peao-preto.png"));
    ImageIcon iconeMovimentoTorrePreta   = new ImageIcon(getClass().getResource("/icones/icone_movimento-torre-preta.png"));
    ImageIcon iconeMovimentoCavaloPreto  = new ImageIcon(getClass().getResource("/icones/icone_movimento-cavalo-preto.png"));
    ImageIcon iconeMovimentoBispoPreto   = new ImageIcon(getClass().getResource("/icones/icone_movimento-bispo-preto.png"));
    ImageIcon iconeMovimentoDamaPreta    = new ImageIcon(getClass().getResource("/icones/icone_movimento-dama-preta.png"));
    ImageIcon iconeMovimentoReiPreto     = new ImageIcon(getClass().getResource("/icones/icone_movimento-rei-preto.png"));
    
    ImageIcon iconeMovimentoPeaoBrancoAvaliado   = new ImageIcon(getClass().getResource("/icones/icone_movimento-peao-branco-avaliado.png"));
    ImageIcon iconeMovimentoTorreBrancaAvaliada  = new ImageIcon(getClass().getResource("/icones/icone_movimento-torre-branca-avaliada.png"));
    ImageIcon iconeMovimentoCavaloBrancoAvaliado = new ImageIcon(getClass().getResource("/icones/icone_movimento-cavalo-branco-avaliado.png"));
    ImageIcon iconeMovimentoBispoBrancoAvaliado  = new ImageIcon(getClass().getResource("/icones/icone_movimento-bispo-branco-avaliado.png"));
    ImageIcon iconeMovimentoDamaBrancaAvaliada   = new ImageIcon(getClass().getResource("/icones/icone_movimento-dama-branca-avaliada.png"));
    ImageIcon iconeMovimentoReiBrancoAvaliado    = new ImageIcon(getClass().getResource("/icones/icone_movimento-rei-branco-avaliado.png"));
    
    ImageIcon iconeMovimentoPeaoPretoAvaliado    = new ImageIcon(getClass().getResource("/icones/icone_movimento-peao-preto-avaliado.png"));
    ImageIcon iconeMovimentoTorrePretaAvaliada   = new ImageIcon(getClass().getResource("/icones/icone_movimento-torre-preta-avaliada.png"));
    ImageIcon iconeMovimentoCavaloPretoAvaliado  = new ImageIcon(getClass().getResource("/icones/icone_movimento-cavalo-preto-avaliado.png"));
    ImageIcon iconeMovimentoBispoPretoAvaliado   = new ImageIcon(getClass().getResource("/icones/icone_movimento-bispo-preto-avaliado.png"));
    ImageIcon iconeMovimentoDamaPretaAvaliada    = new ImageIcon(getClass().getResource("/icones/icone_movimento-dama-preta-avaliada.png"));
    ImageIcon iconeMovimentoReiPretoAvaliado     = new ImageIcon(getClass().getResource("/icones/icone_movimento-rei-preto-avaliado.png"));
    
    private boolean usarNotacaoSAN;
    private boolean mostrarValorNode;
    private boolean mostrarTotalFilhos;
    
    private StringBuilder stringBuilder;
    
    public RenderTreeJogadas(boolean usarNotacaoSAN, boolean mostrarValorNode, boolean mostrarTotalFilhos){
        
        this.usarNotacaoSAN     = usarNotacaoSAN;
        this.mostrarValorNode   = mostrarValorNode;
        this.mostrarTotalFilhos = mostrarTotalFilhos;
        
        stringBuilder = new StringBuilder();
    }

    public boolean getUsarNotacaoSAN(){
        return usarNotacaoSAN;
    }
    
    public void setUsarNotacaoSAN(boolean usarNotacaoSAN) {
        this.usarNotacaoSAN = usarNotacaoSAN;
    }

    public boolean getMostrarValorNode(){
        return mostrarValorNode;
    }
    
    public void setMostrarValorNode(boolean mostrarValorNode) {
        this.mostrarValorNode = mostrarValorNode;
    }
    
    public boolean getMostrarTotalFilhos(){
        return mostrarTotalFilhos;
    }
    
    public void setMostrarTotalFilhos(boolean mostrarTotalFilhos){
        this.mostrarTotalFilhos = mostrarTotalFilhos;
    }
    
    @Override
    public synchronized Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        
        Object nodeInfo = node.getUserObject();
        
        if (node instanceof LanceTreeNode) {
            
            LanceTreeNode moveTreeNode = (LanceTreeNode) node;            
            
            boolean jogadorBranco = moveTreeNode.getTabuleiro().isWhiteActive();
            
            Lance move = moveTreeNode.getLance();
            
            stringBuilder.delete(0, stringBuilder.length());
                    
            if (mostrarValorNode) {
                stringBuilder.append('(');
                stringBuilder.append(DHJOG.textoValorTabuleiro(moveTreeNode.getValor(), true));
                stringBuilder.append(") ");
            }
            
            if (moveTreeNode.isLeaf()){
                
                switch (move.getPeca().getTipo()) {
                    case PEAO:
                        setIcon(jogadorBranco ? iconeMovimentoPeaoBrancoAvaliado : iconeMovimentoPeaoPretoAvaliado);
                        break;

                    case TORRE:
                        setIcon(jogadorBranco ? iconeMovimentoTorreBrancaAvaliada : iconeMovimentoTorrePretaAvaliada);
                        break;

                    case CAVALO:
                        setIcon(jogadorBranco ? iconeMovimentoCavaloBrancoAvaliado : iconeMovimentoCavaloPretoAvaliado);
                        break;

                    case BISPO:
                        setIcon(jogadorBranco ? iconeMovimentoBispoBrancoAvaliado : iconeMovimentoBispoPretoAvaliado);
                        break;

                    case DAMA:
                        setIcon(jogadorBranco ? iconeMovimentoDamaBrancaAvaliada : iconeMovimentoDamaPretaAvaliada);
                        break;

                    case REI:
                        setIcon(jogadorBranco ? iconeMovimentoReiBrancoAvaliado : iconeMovimentoReiPretoAvaliado);
                        break;
                }
                
            } else {
                
                if (mostrarTotalFilhos) {
                    stringBuilder.append("[");
                    stringBuilder.append(UtilsString.formataDouble("###,###,###", moveTreeNode.getTotalFilhos()));
                    stringBuilder.append("] ");
                }
                
                switch (move.getPeca().getTipo()) {
                    case PEAO:
                        setIcon(jogadorBranco ? iconeMovimentoPeaoBranco : iconeMovimentoPeaoPreto);
                        break;

                    case TORRE:
                        setIcon(jogadorBranco ? iconeMovimentoTorreBranca : iconeMovimentoTorrePreta);
                        break;

                    case CAVALO:
                        setIcon(jogadorBranco ? iconeMovimentoCavaloBranco : iconeMovimentoCavaloPreto);
                        break;

                    case BISPO:
                        setIcon(jogadorBranco ? iconeMovimentoBispoBranco : iconeMovimentoBispoPreto);
                        break;

                    case DAMA:
                        setIcon(jogadorBranco ? iconeMovimentoDamaBranca : iconeMovimentoDamaPreta);
                        break;

                    case REI:
                        setIcon(jogadorBranco ? iconeMovimentoReiBranco : iconeMovimentoReiPreto);
                        break;
                }
            }
            
            if (usarNotacaoSAN) {
                stringBuilder.append(moveTreeNode.getTextoSAN());
            } else {
                stringBuilder.append(move.toString());
            }
            
            setText(stringBuilder.toString());
            
        }else{            
            setIcon(iconeTabuleiro);
            setText(nodeInfo.toString());
        }        
        
        JLabel label = (JLabel) this;
        label.setDisabledIcon(getIcon());
        
        return this;
    }
}