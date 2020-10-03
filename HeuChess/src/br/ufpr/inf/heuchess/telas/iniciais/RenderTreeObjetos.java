package br.ufpr.inf.heuchess.telas.iniciais;

import br.ufpr.inf.heuchess.representacao.heuristica.ConjuntoHeuristico;
import br.ufpr.inf.heuchess.representacao.heuristica.Permissao;
import br.ufpr.inf.heuchess.representacao.organizacao.Instituicao;
import br.ufpr.inf.heuchess.representacao.organizacao.Turma;
import br.ufpr.inf.heuchess.representacao.organizacao.Usuario;
import br.ufpr.inf.heuchess.representacao.situacaojogo.SituacaoJogo;
import br.ufpr.inf.utils.gui.ElementoLista;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 20 de Julho de 2006, 12:08
 */
public class RenderTreeObjetos extends DefaultTreeCellRenderer implements TreeCellRenderer {
    
    ImageIcon iconeConjuntoHeuristico = new ImageIcon(getClass().getResource("/icones/icone_conjunto_heuristico.png"));
    ImageIcon iconeSituacao           = new ImageIcon(getClass().getResource("/icones/tabuleiro-icone.png"));
    ImageIcon iconeInstituicao        = new ImageIcon(getClass().getResource("/icones/casa.png"));
    ImageIcon iconeTurma              = new ImageIcon(getClass().getResource("/icones/icone_pessoas.png"));
    ImageIcon iconeUsuarioOnline      = new ImageIcon(getClass().getResource("/icones/icone_pessoa.png"));
    ImageIcon iconeUsuarioOffline     = new ImageIcon(getClass().getResource("/icones/icone_pessoa_offline.png"));
    
    private boolean descricao;
    
    public RenderTreeObjetos(boolean mostrarDescricao){
        this.descricao = mostrarDescricao;
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        
        Object nodeInfo = node.getUserObject();

        if (nodeInfo instanceof ElementoLista) {

            ElementoLista elemento = (ElementoLista) nodeInfo;
            
            if (elemento.getClasse() == SituacaoJogo.class ) {
                
                setIcon(iconeSituacao);
                
                if (descricao){
                    setText(elemento.getNome() + " - \"" + elemento.getDescricao() + "\"");
                }else{
                    setText(elemento.getNome());
                }                 
            }else
                if (elemento.getClasse() == ConjuntoHeuristico.class) {
                    
                    setIcon(iconeConjuntoHeuristico);    
                    
                    if (descricao){
                        setText(elemento.getNome() + " (" + elemento.getTipo().getNome() + ")");
                    }else{
                        setText(elemento.getNome());
                    }
                }else
                    if (elemento.getClasse() == Usuario.class) {
                        
                        setText(elemento.getNome());
                        
                        if (elemento.getDescricao().equals("1")){
                            setIcon(iconeUsuarioOnline);
                        }else{
                            setIcon(iconeUsuarioOffline);
                        }
                    }else
                        if (elemento.getClasse() == Instituicao.class) {
                            
                            setIcon(iconeInstituicao);
                            
                            if (descricao) {
                                setText(elemento.getNome() + " - \"" + elemento.getDescricao() + "\"");
                            } else {
                                setText(elemento.getNome());
                            }
                        }else
                            if (elemento.getClasse() == Turma.class) {
                
                                setIcon(iconeTurma);
                    
                                int permissoes = Integer.parseInt(elemento.getDescricao());

                                if (Permissao.ACESSAR.existe(permissoes)){
                                    
                                    if (Permissao.UTILIZAR.existe(permissoes)){
                                        setText(elemento.getNome());
                                    }else{
                                        setText(elemento.getNome() + " (Uso de Objetos BLOQUEADO)");
                                    }
                                    
                                }else{
                                    setText(elemento.getNome() + " (Acesso a Objetos BLOQUEADO)");
                                }
                            }                        
        }else            
            if (getIcon() == getLeafIcon()){
                setIcon(getClosedIcon());
            }
            
        return this;
    }
}