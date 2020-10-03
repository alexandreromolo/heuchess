package br.ufpr.inf.heuchess.telas.editorheuristica;

import br.ufpr.inf.heuchess.HeuChess;
import br.ufpr.inf.heuchess.representacao.heuristica.*;
import br.ufpr.inf.heuchess.representacao.situacaojogo.TipoPeca;
import br.ufpr.inf.utils.gui.AlignedListCellRenderer;
import br.ufpr.inf.utils.gui.ModalFrameHierarchy;
import br.ufpr.inf.utils.gui.ModalFrameUtil;
import br.ufpr.inf.utils.gui.UtilsGUI;
import java.awt.CardLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;

/**
 *
 * @author Alexandre Rômolo Moreira Feitosa - alexandreromolo@hotmail.com
 * Created on 28 de Junho de 2006, 14:55
 */
public class TelaPreencheCondicao extends javax.swing.JFrame implements AcessoTelaRegiao, AcessoTelaPreencheFuncao, AcessoTelaEscolheFuncao {
          
    public  TelaHeuristica telaHeuristica;   
    
    private CondicaoHeuristica   condicao;    
    private CondicaoHeuristica   condicaoOriginal;    
    
    private ModelListaComponentes model;
    private int                  posicaoOriginal;
    
    private boolean nova;
    
    private CardLayout cardPrincipal;
    private CardLayout cardParametros;
    private int        passoAtual;
    
    private boolean iniciandoAutomaticoInvisivel = false;
    
    /**
     * Construtor chamado quando se está criando uma nova Condição sem nehuma função já associada
     */
    public TelaPreencheCondicao(TelaHeuristica telaHeuristica, ModelListaComponentes model) {           
        
        this.telaHeuristica = telaHeuristica;
        this.model = model;
        
        nova       = true;
        passoAtual = 1;
        
        montarInterface();
        
        iniciandoAutomaticoInvisivel = true;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaEscolheFuncao tela = new TelaEscolheFuncao(TelaPreencheCondicao.this,null);
            }
        });
    }
        
    /**
     * Construtor chamado quando se está criando uma nova Condição com uma função já associada
     */
    public TelaPreencheCondicao(TelaHeuristica telaHeuristica, ModelListaComponentes model, final Funcao funcao, final Object tipo){
        
        this.telaHeuristica = telaHeuristica;
        this.model = model;
        
        nova       = true;
        passoAtual = 1;
        
        montarInterface();
                
        iniciandoAutomaticoInvisivel = true;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaPreencheFuncao tela = new TelaPreencheFuncao(TelaPreencheCondicao.this.telaHeuristica, TelaPreencheCondicao.this,funcao,tipo);
            }
        });
    }
    
    /**
     * Construtor chamado quando se está abrindo uma condição que já existe
     */
    public TelaPreencheCondicao(TelaHeuristica telaHeuristica, ModelListaComponentes model, int posicao){
        
        this.telaHeuristica = telaHeuristica;
        this.model = model;
        
        nova       = false;
        passoAtual = 1;
        
        iniciandoAutomaticoInvisivel = false;
                
        /////////////////////////////////////////
        // Copia os dados da condição Original //
        /////////////////////////////////////////
        
        posicaoOriginal = posicao;
        
        try{
            condicaoOriginal = (CondicaoHeuristica) model.get(posicao);         
            condicao = condicaoOriginal.geraClone();
                    
            montarInterface();
        
            adaptaInterface(null);      
        
        }catch(Exception e){
            HeuChess.registraExcecao(e);
            UtilsGUI.dialogoErro(telaHeuristica.getFrame(),"Erro ao iniciar tela Preenche Condição!\n" + e.getMessage());
            dispose();
            return;
        }
        
        ModalFrameUtil.showAsModalDontBlock(this);
    }
    
    private void montarInterface(){
        
        initComponents();
        
        cardPrincipal  = (CardLayout) jPanelPrincipal.getLayout();
        cardParametros = (CardLayout) jPanelEntadaDados.getLayout();
        
        cardPrincipal.show(jPanelPrincipal, "Operando1");
        
        jButtonAnterior.setVisible(false);
        
        if (!telaHeuristica.panelEtapa.editor.podeAlterar()){
            jButtonEscolherFuncaoOperando1.setVisible(false);
            jButtonAlterarParametrosOperando1.setVisible(false);
         
            jComboBoxOperadorRelacional.setEnabled(false);            
            
            jRadioButtonPreencherValor.setEnabled(false);
            jRadioButtonPreencherFuncao.setEnabled(false);
            
            jComboBoxValorLogico.setEnabled(false);
            
            jComboBoxValorJogador.setEnabled(false);
            
            jSpinnerValorInteiro.setEnabled(false);
         
            jSpinnerValorReal.setEnabled(false);
            
            jCheckBoxTipoPeao.setEnabled(false);
            jCheckBoxTipoTorre.setEnabled(false);
            jCheckBoxTipoCavalo.setEnabled(false);
            jCheckBoxTipoBispo.setEnabled(false);
            jCheckBoxTipoDama.setEnabled(false);
            jCheckBoxTipoRei.setEnabled(false);
            
            jCheckBoxPeaoMEU.setEnabled(false);
            jCheckBoxTorreMINHA.setEnabled(false);
            jCheckBoxCavaloMEU.setEnabled(false);
            jCheckBoxBispoMEU.setEnabled(false);
            jCheckBoxDamaMINHA.setEnabled(false);
            jCheckBoxReiMEU.setEnabled(false);
            
            jCheckBoxPeaoOPONENTE.setEnabled(false);
            jCheckBoxTorreOPONENTE.setEnabled(false);
            jCheckBoxCavaloOPONENTE.setEnabled(false);
            jCheckBoxBispoOPONENTE.setEnabled(false);
            jCheckBoxDamaOPONENTE.setEnabled(false);
            jCheckBoxReiOPONENTE.setEnabled(false);
            
            jComboBoxRegiao.setEnabled(false);
            jButtonNovaRegiao.setVisible(false);
            
            jButtonEscolherFuncaoOperando2.setVisible(false);
            jButtonAlterarParametrosOperando2.setVisible(false);
            
            jRadioButtonOperadorLogicoE.setEnabled(false);
            jRadioButtonOperadorLogicoOU.setEnabled(false);
            jCheckBoxOperadorLogicoNAO.setEnabled(false);
            
            jButtonCancelar.setText("Fechar");
            jButtonCancelar.setToolTipText("Fecha a janela");
            jButtonCancelar.setMnemonic('f');
            jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_fechar_janela.png")));
        }
    }
    
    private void adaptaInterface(Object tipo) throws Exception {        

        tentaCompletarAutomaticamenteOperando2(tipo);

        if (passoAtual == 1) {

            jTextAreaFuncaoPreenchidaOperando1.setText(((FuncaoPreenchida) condicao.getValorOperando1()).toString());
            
            if (((FuncaoPreenchida) condicao.getValorOperando1()).totalParametros() != 0) {
                jButtonAlterarParametrosOperando1.setEnabled(true);
            } else {
                jButtonAlterarParametrosOperando1.setEnabled(false);
            }
            
            jCheckBoxOperadorLogicoNAO.setSelected(condicao.isOperadorLogicoNao());

            if (condicao.getOperadorLogico() != null && condicao.getOperadorLogico() == DHJOG.OperadorLogico.OU) {
                jRadioButtonOperadorLogicoOU.setSelected(true);
            } else {
                jRadioButtonOperadorLogicoE.setSelected(true);
            }

        } else 
            if (passoAtual == 3) {
                montaPanelOperando2();
            }       
    }          
    
    @Override
    public Frame getFrame(){
        
        if (iniciandoAutomaticoInvisivel) {
            return telaHeuristica.getFrame();
        } else {
            return this;
        }        
    }
    
    @Override
    public ModalFrameHierarchy getModalOwner(){
        
        if (iniciandoAutomaticoInvisivel) {
            return telaHeuristica.getModalOwner();
        } else {
            return telaHeuristica;
        }
    }
    
    @Override
    public void fechandoTelaEscolheFuncao(final Funcao funcaoEscolhida){
        
        if (funcaoEscolhida != null) {
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaPreencheFuncao tela = new TelaPreencheFuncao(telaHeuristica, TelaPreencheCondicao.this, funcaoEscolhida, null);
                }
            });
            
        } else {
            
            if (iniciandoAutomaticoInvisivel) {
                dispose();
                telaHeuristica.fechandoTelaPreencheCondicao(false);
            }
        }
    }
        
    @Override
    public void fechandoTelaPreencheFuncao(FuncaoPreenchida funcaoPreenchida, Object elemento){        
        
        if (funcaoPreenchida != null) {

            if (condicao == null) {
                condicao = new CondicaoHeuristica(telaHeuristica.heuristica,funcaoPreenchida);
            } else {
                
                try{
                    if (passoAtual == 1) {
                        condicao.setValorOperando1(funcaoPreenchida);
                    } else 
                        if (passoAtual == 3) {
                            condicao.setValorOperando2(funcaoPreenchida);
                        }
                    
                }catch(Exception e){
                    HeuChess.registraExcecao(e);
                    UtilsGUI.dialogoErro(this,"Erro ao preencher parâmetro da Condição com Função!\n" + e.getMessage());
                    return;
                }
            }

            try{
                adaptaInterface(elemento);
            }catch(Exception e){
                HeuChess.registraExcecao(e);
                UtilsGUI.dialogoErro(this,"Erro ao fechar Tela Preenche Função!\n" + e.getMessage());
                return;
            }
        }

        if (iniciandoAutomaticoInvisivel) {
            
            if (funcaoPreenchida == null) {
                dispose();
                telaHeuristica.fechandoTelaPreencheCondicao(false);
            } else {
                iniciandoAutomaticoInvisivel = false;
                ModalFrameUtil.showAsModalDontBlock(this);
            }
        }
    }
    
    @Override
    public void fechandoTelaRegiao(Regiao regiao){
        
        if (regiao != null){
            
            atualizaComboRegioes();
            
            jComboBoxRegiao.setSelectedItem(regiao);
            
            jButtonAbrirRegiao.setEnabled(true);
        }        
    }
    
    public void confirmaCancelar(){
        
        if (telaHeuristica.panelEtapa.editor.podeAlterar()) {
            
            boolean sofreuAlteracao;

            if (nova) {
                sofreuAlteracao = true;
            } else {

                try {
                    salvarEntrada();

                    sofreuAlteracao = !condicao.toString().equals(condicaoOriginal.toString());

                } catch (Exception e) {
                    HeuChess.registraExcecao(e);
                    sofreuAlteracao = true;
                }
            }

            if (sofreuAlteracao) {
                int resposta = UtilsGUI.dialogoConfirmacao(this, "Deseja realmente cancelar as alterações feitas?", "Confirmação Cancelamento");
                if (resposta == JOptionPane.NO_OPTION || resposta == -1) {
                    return;
                }
            }
        }
        
        dispose();
        telaHeuristica.fechandoTelaPreencheCondicao(false);        
    }
   
    private void atualizaComboRegioes(){
        
        ArrayList<Regiao> regioes = telaHeuristica.panelEtapa.etapa.getRegioes();
        
        jComboBoxRegiao.removeAllItems();
        
        jComboBoxRegiao.addItem(DHJOG.TODO_TABULEIRO);
                
        Collections.sort(regioes);
        
        for (Regiao regiao : regioes){
            jComboBoxRegiao.addItem(regiao);
        }
        
        jComboBoxRegiao.setSelectedIndex(-1);
    }
    
    private void opcoesOperadores(){
                
        jComboBoxOperadorRelacional.removeAllItems();
        
        DHJOG.TipoDado tipo= condicao.getParametroOperando2().getTipo();
        
        switch(tipo){
            
            case JOGADOR:
            case LOGICO:
                jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.IGUAL);
                jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.DIFERENTE);
                break;
                
            case INTEIRO:
            case REAL:
                jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.MAIOR);
                jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.MENOR);
                jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.MAIOR_IGUAL);
                jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.MENOR_IGUAL);
                jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.IGUAL);
                jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.DIFERENTE);
                break;
                
            case CASAS:    
            case PECAS:    
            case TIPO_PECAS:
                jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.CONTEM);
                jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.CONTIDO);
                jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.IGUAL);
                jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.DIFERENTE);
                break;
                
            default:
                throw new IllegalArgumentException("Tipo desconhecido de Parâmetro [" + tipo + "]");
                
        }
    }
    
    private void configuraDescricaoOperador(){
        
        DHJOG.OperadorRelacional operador = (DHJOG.OperadorRelacional) jComboBoxOperadorRelacional.getSelectedItem();
        
        if (operador != null){
            jTextAreaDescricaoOperadorRelacional.setText(operador.explicacao());
        }else{
            jTextAreaDescricaoOperadorRelacional.setText(null);
        }
    }
                     
    private void tentaCompletarAutomaticamenteOperando2(Object elemento) throws Exception {
        
       if (elemento != null){            
           
           if (elemento instanceof TipoPeca){
               
               if (condicao.getParametroOperando2().getTipo() == DHJOG.TipoDado.TIPO_PECAS){
                   TipoPeca[] tipos = {(TipoPeca) elemento};
                   condicao.setValorOperando2(tipos);                   
               }
           
           }else
               if (elemento instanceof Regiao){   
                    
                    if (condicao.getParametroOperando2().getTipo() == DHJOG.TipoDado.CASAS){
                        condicao.setValorOperando2(elemento);
                    }                    
                    
                }else
                    if (elemento instanceof String){                
                
                        String constante = (String) elemento;            
                
                        if (constante.equalsIgnoreCase(DHJOG.TODO_TABULEIRO)){
                        
                            if (condicao.getParametroOperando2().getTipo() == DHJOG.TipoDado.CASAS){
                                condicao.setValorOperando2(constante);
                            }
                            
                        }else{
                            throw new IllegalArgumentException("PreencheCondicao. Texto não suportado para arrastar e soltar [" + elemento.toString() + "]");
                        }
                    }else{
                        throw new IllegalArgumentException("PreencheCondicao. Tipo de Objeto não suportado para arrastar e soltar [" + elemento.getClass().getName() + "]");
                    }
        }
    }    
    
    private void montaPanelOperando2(){    
        
        ParametroPreenchido operando2 = condicao.getParametroOperando2();
        
        jTextFieldTipoOperando2.setText(operando2.getTipo().toString());
        
        Object valor = operando2.getValor();
        
        if (operando2.isPreenchidaValor()){
            
            jRadioButtonPreencherValor.setSelected(true);
            
            switch(operando2.getTipo()){
                    
                case INTEIRO:
                    cardParametros.show(jPanelEntadaDados,"Inteiro");    
                        
                    if (valor instanceof Integer){                            
                        jSpinnerValorInteiro.setValue(valor);
                    }
                    break;
                    
                case REAL:
                    cardParametros.show(jPanelEntadaDados,"Real");    
                            
                    if (valor instanceof Double){                            
                        jSpinnerValorReal.setValue(valor);
                    }                    
                    break;
                    
                case LOGICO:
                    cardParametros.show(jPanelEntadaDados,"Logico");    
                    
                    if (valor instanceof DHJOG.VALOR_LOGICO){
                        jComboBoxValorLogico.setSelectedItem(valor);
                    }   
                    break;    
                    
                case JOGADOR:                    
                    cardParametros.show(jPanelEntadaDados,"Jogador");    
                
                    if (valor instanceof DHJOG.VALOR_JOGADOR){
                        jComboBoxValorJogador.setSelectedItem(valor);
                    }
                    break;    
                    
                case CASAS:
                    cardParametros.show(jPanelEntadaDados,"Regiao");    
                    
                    atualizaComboRegioes();
                    
                    if (valor != null){   
                        jComboBoxRegiao.setSelectedItem(valor);
                    }
                    break;
                    
                case PECAS:
                    cardParametros.show(jPanelEntadaDados, "Peca");

                    if (valor instanceof DHJOG.Peca[]) {
                        
                        DHJOG.Peca[] pecas = (DHJOG.Peca[]) valor;
                        
                        for (DHJOG.Peca peca : pecas){
                        
                            switch(peca){
                                case PEAO_MEU:
                                    jCheckBoxPeaoMEU.setSelected(true);
                                    break;
                                case TORRE_MINHA:
                                    jCheckBoxTorreMINHA.setSelected(true);
                                    break;
                                case CAVALO_MEU:
                                    jCheckBoxCavaloMEU.setSelected(true);
                                    break;
                                case BISPO_MEU:
                                    jCheckBoxBispoMEU.setSelected(true);
                                    break;
                                case DAMA_MINHA:
                                    jCheckBoxDamaMINHA.setSelected(true);
                                    break;
                                case REI_MEU:
                                    jCheckBoxReiMEU.setSelected(true);
                                    break;
                                case PEAO_OPONENTE:
                                    jCheckBoxPeaoOPONENTE.setSelected(true);
                                    break;
                                case TORRE_OPONENTE:
                                    jCheckBoxTorreOPONENTE.setSelected(true);
                                    break;
                                case CAVALO_OPONENTE:
                                    jCheckBoxCavaloOPONENTE.setSelected(true);
                                    break;
                                case BISPO_OPONENTE:
                                    jCheckBoxBispoOPONENTE.setSelected(true);
                                    break;
                                case DAMA_OPONENTE:
                                    jCheckBoxDamaOPONENTE.setSelected(true);
                                    break;
                                case REI_OPONENTE:
                                    jCheckBoxReiOPONENTE.setSelected(true);
                                    break;
                                default:
                                    throw new IllegalArgumentException("Peça DHJOG não suportada [" + peca + "]");
                            
                            }
                        }
                    }
                    break;
                    
                case TIPO_PECAS:
                    cardParametros.show(jPanelEntadaDados, "TipoPeca");

                    if (valor instanceof TipoPeca[]) {
                        
                        TipoPeca[] tiposPecas = (TipoPeca[]) valor;
                        
                        for (TipoPeca tipoPeca : tiposPecas){
                        
                            switch(tipoPeca){
                                case PEAO:
                                    jCheckBoxTipoPeao.setSelected(true);
                                    break;
                                case TORRE:
                                    jCheckBoxTipoTorre.setSelected(true);
                                    break;
                                case CAVALO:
                                    jCheckBoxTipoCavalo.setSelected(true);
                                    break;
                                case BISPO:
                                    jCheckBoxTipoBispo.setSelected(true);
                                    break;
                                case DAMA:
                                    jCheckBoxTipoDama.setSelected(true);
                                    break;
                                case REI:
                                    jCheckBoxTipoRei.setSelected(true);
                                    break;    
                                default:
                                    throw new IllegalArgumentException("Tipo Peça DHJOG não suportada [" + tipoPeca + "]");
                            }
                        }
                    }
                    break;
                    
                default:
                    throw new IllegalArgumentException("Tipo de dado não suportado [" + operando2.getTipo() + "]");
            }
            
        }else{
            jRadioButtonPreencherFuncao.setSelected(true);
            
            cardParametros.show(jPanelEntadaDados,"Funcao");
            
            if (valor instanceof FuncaoPreenchida){
                
                jTextAreaFuncaoPreenchidaOperando2.setText(((FuncaoPreenchida)valor).toString());                
                
                if (((FuncaoPreenchida)valor).totalParametros() != 0){
                    jButtonAlterarParametrosOperando2.setEnabled(true);
                }else{
                    jButtonAlterarParametrosOperando2.setEnabled(false);
                }
                
                jButtonEscolherFuncaoOperando2.setText("Trocar Função");
                jButtonDescricaoFuncaoOperando2.setEnabled(true);
                
            }else{
                jTextAreaFuncaoPreenchidaOperando2.setText(null);
                jButtonAlterarParametrosOperando2.setEnabled(false);
                jButtonEscolherFuncaoOperando2.setText("Escolher Função");                
                jButtonDescricaoFuncaoOperando2.setEnabled(false);
            }
        }        
    }   
  
    private void salvarEntrada() throws Exception {

        switch (passoAtual) {

            case 1:// Operando 1 //

                if (condicao.getValorOperando1() == null) {
                    throw new IllegalArgumentException("É preciso preencher uma Função para o primeiro operando da Condição!");
                }
                break;

            case 2:// Operador Relacional //

                if (jComboBoxOperadorRelacional.getSelectedIndex() == -1) {
                    throw new IllegalArgumentException("É preciso escolher um Operador Relacional para a Condição!");
                }
                condicao.setOperadorRelacional((DHJOG.OperadorRelacional) jComboBoxOperadorRelacional.getSelectedItem());
                break;

            case 3:// Operando 2 //

                salvarValorOperando2();
                
                if (condicao.getValorOperando2() == null) {
                    throw new IllegalArgumentException("É preciso preencher um valor para o segundo operando da Condição!");
                }
                break;
        }
    }
    
    private void salvarValorOperando2() throws Exception {
        
        ParametroPreenchido operando2 = condicao.getParametroOperando2();
        
        if (operando2.isPreenchidaValor()){
            
            switch(operando2.getTipo()){
            
                case INTEIRO:
                    operando2.setValor((Integer) jSpinnerValorInteiro.getValue());
                    break;
                    
                case REAL:
                    operando2.setValor((Double) jSpinnerValorReal.getValue());
                    break;
                    
                case LOGICO:
                    operando2.setValor((DHJOG.VALOR_LOGICO) jComboBoxValorLogico.getSelectedItem());
                    break;
                    
                case JOGADOR:
                    operando2.setValor((DHJOG.VALOR_JOGADOR) jComboBoxValorJogador.getSelectedItem());
                    break;
                    
                case CASAS:
                    if (jComboBoxRegiao.getSelectedIndex() != -1){
                        operando2.setValor(jComboBoxRegiao.getSelectedItem());
                    }else{
                        throw new IllegalArgumentException("Nenhuma Região selecionada!");
                    }                 
                    break;
                    
                case PECAS:
                    ArrayList<DHJOG.Peca> pecas = new ArrayList();
                    
                    if (jCheckBoxPeaoMEU.isSelected()){
                        pecas.add(DHJOG.Peca.PEAO_MEU);
                    }
                    if (jCheckBoxTorreMINHA.isSelected()){
                        pecas.add(DHJOG.Peca.TORRE_MINHA);
                    }
                    if (jCheckBoxCavaloMEU.isSelected()){
                        pecas.add(DHJOG.Peca.CAVALO_MEU);
                    }
                    if (jCheckBoxBispoMEU.isSelected()){
                        pecas.add(DHJOG.Peca.BISPO_MEU);
                    }
                    if (jCheckBoxDamaMINHA.isSelected()){
                        pecas.add(DHJOG.Peca.DAMA_MINHA);
                    }
                    if (jCheckBoxReiMEU.isSelected()){
                        pecas.add(DHJOG.Peca.REI_MEU);
                    }
                    if (jCheckBoxPeaoOPONENTE.isSelected()){
                        pecas.add(DHJOG.Peca.PEAO_OPONENTE);
                    }
                    if (jCheckBoxTorreOPONENTE.isSelected()){
                        pecas.add(DHJOG.Peca.TORRE_OPONENTE);
                    }
                    if (jCheckBoxCavaloOPONENTE.isSelected()){
                        pecas.add(DHJOG.Peca.CAVALO_OPONENTE);
                    }
                    if (jCheckBoxBispoOPONENTE.isSelected()){
                        pecas.add(DHJOG.Peca.BISPO_OPONENTE);
                    }
                    if (jCheckBoxDamaOPONENTE.isSelected()){
                        pecas.add(DHJOG.Peca.DAMA_OPONENTE);
                    }
                    if (jCheckBoxReiOPONENTE.isSelected()){
                        pecas.add(DHJOG.Peca.REI_OPONENTE);
                    }
                    if (pecas.isEmpty()){
                        throw new IllegalArgumentException("Nenhuma Peça selecionada!");
                    }
                    
                    operando2.setValor(pecas.toArray(new DHJOG.Peca[pecas.size()]));
                    break;
                    
                case TIPO_PECAS:
                    ArrayList<TipoPeca> tiposPecas = new ArrayList();
                    
                    if (jCheckBoxTipoPeao.isSelected()){
                        tiposPecas.add(TipoPeca.PEAO);
                    }
                    if (jCheckBoxTipoTorre.isSelected()){
                        tiposPecas.add(TipoPeca.TORRE);
                    }
                    if (jCheckBoxTipoCavalo.isSelected()){
                        tiposPecas.add(TipoPeca.CAVALO);
                    }
                    if (jCheckBoxTipoBispo.isSelected()){
                        tiposPecas.add(TipoPeca.BISPO);
                    }
                    if (jCheckBoxTipoDama.isSelected()){
                        tiposPecas.add(TipoPeca.DAMA);
                    }
                    if (jCheckBoxTipoRei.isSelected()){
                        tiposPecas.add(TipoPeca.REI);
                    }
                    if (tiposPecas.isEmpty()){
                        throw new IllegalArgumentException("Nenhum Tipo de Peça selecionado!");
                    }
                    
                    operando2.setValor(tiposPecas.toArray(new TipoPeca[tiposPecas.size()]));
                    break;
                    
                default:
                    throw new IllegalArgumentException("Tipo de dado não suportado [" + operando2.getTipo() + "]");
            }
        }else{
            ////////////////////////////////////////////////////////////////
            // Função Preenchida - Já é salva quando se preenche a função //
            ////////////////////////////////////////////////////////////////
        }        
    }    
  
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupOpcaoPreenchimento = new javax.swing.ButtonGroup();
        buttonGroupConectores = new javax.swing.ButtonGroup();
        jPanelPrincipal = new javax.swing.JPanel();
        jPanelOperando1 = new javax.swing.JPanel();
        jLabelPassoInicial = new javax.swing.JLabel();
        jButtonEscolherFuncaoOperando1 = new javax.swing.JButton();
        jButtonAlterarParametrosOperando1 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaFuncaoPreenchidaOperando1 = new javax.swing.JTextArea();
        jButtonDetalhesFuncaoOperando1 = new javax.swing.JButton();
        jPanelOperadorRelacional = new javax.swing.JPanel();
        jLabelPassoInicial1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanelComboOpRelacional = new javax.swing.JPanel();
        jPanelCentralizaRelacional = new javax.swing.JPanel();
        jComboBoxOperadorRelacional = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDescricaoOperadorRelacional = new javax.swing.JTextArea();
        jPanelOperando2 = new javax.swing.JPanel();
        jLabelPassoParametro = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldTipoOperando2 = new javax.swing.JTextField();
        jPanelOpcoesPreenchimento = new javax.swing.JPanel();
        jRadioButtonPreencherValor = new javax.swing.JRadioButton();
        jRadioButtonPreencherFuncao = new javax.swing.JRadioButton();
        jPanelEntadaDados = new javax.swing.JPanel();
        jPanelLogico = new javax.swing.JPanel();
        jPanelCentraliza = new javax.swing.JPanel();
        jComboBoxValorLogico = new javax.swing.JComboBox();
        jPanelJogador = new javax.swing.JPanel();
        jPanelCentraliza1 = new javax.swing.JPanel();
        jComboBoxValorJogador = new javax.swing.JComboBox();
        jPanelInteiro = new javax.swing.JPanel();
        jPanelCentraliza2 = new javax.swing.JPanel();
        jSpinnerValorInteiro = new JSpinner(new SpinnerNumberModel(1, -10000, 10000, 1)); ;
        jLabel1 = new javax.swing.JLabel();
        jPanelReal = new javax.swing.JPanel();
        jPanelCentraliza3 = new javax.swing.JPanel();
        jSpinnerValorReal = new JSpinner(new SpinnerNumberModel(1.0, -10000.0, 10000.0, 0.1)); ;
        jLabel9 = new javax.swing.JLabel();
        jPanelTipoPeca = new javax.swing.JPanel();
        jPanelCentraliza4 = new javax.swing.JPanel();
        jLabelTipoPeao = new javax.swing.JLabel();
        jLabelTipoTorre = new javax.swing.JLabel();
        jLabelTipoCavalo = new javax.swing.JLabel();
        jLabelTipoBispo = new javax.swing.JLabel();
        jLabelTipoDama = new javax.swing.JLabel();
        jLabelTipoRei = new javax.swing.JLabel();
        jCheckBoxTipoPeao = new javax.swing.JCheckBox();
        jCheckBoxTipoCavalo = new javax.swing.JCheckBox();
        jCheckBoxTipoTorre = new javax.swing.JCheckBox();
        jCheckBoxTipoBispo = new javax.swing.JCheckBox();
        jCheckBoxTipoDama = new javax.swing.JCheckBox();
        jCheckBoxTipoRei = new javax.swing.JCheckBox();
        jPanelPeca = new javax.swing.JPanel();
        jPanelCentraliza6 = new javax.swing.JPanel();
        jPanelPecasMINHAS = new javax.swing.JPanel();
        jCheckBoxPeaoMEU = new javax.swing.JCheckBox();
        jLabelPeaoMEU = new javax.swing.JLabel();
        jCheckBoxTorreMINHA = new javax.swing.JCheckBox();
        jLabelTorreMINHA = new javax.swing.JLabel();
        jCheckBoxCavaloMEU = new javax.swing.JCheckBox();
        jLabelCavaloMEU = new javax.swing.JLabel();
        jCheckBoxBispoMEU = new javax.swing.JCheckBox();
        jLabelBispoMEU = new javax.swing.JLabel();
        jCheckBoxDamaMINHA = new javax.swing.JCheckBox();
        jLabelDamaMINHA = new javax.swing.JLabel();
        jCheckBoxReiMEU = new javax.swing.JCheckBox();
        jLabelReiMEU = new javax.swing.JLabel();
        jPanelPecasOPONENTE = new javax.swing.JPanel();
        jCheckBoxPeaoOPONENTE = new javax.swing.JCheckBox();
        jLabelPeaoOPONENTE = new javax.swing.JLabel();
        jCheckBoxTorreOPONENTE = new javax.swing.JCheckBox();
        jLabelTorreOPONENTE = new javax.swing.JLabel();
        jCheckBoxCavaloOPONENTE = new javax.swing.JCheckBox();
        jLabelCavaloOPONENTE = new javax.swing.JLabel();
        jCheckBoxBispoOPONENTE = new javax.swing.JCheckBox();
        jLabelBispoOPONENTE = new javax.swing.JLabel();
        jCheckBoxDamaOPONENTE = new javax.swing.JCheckBox();
        jLabelDamaOPONENTE = new javax.swing.JLabel();
        jCheckBoxReiOPONENTE = new javax.swing.JCheckBox();
        jLabelReiOPONENTE = new javax.swing.JLabel();
        jPanelRegiao = new javax.swing.JPanel();
        jPanelCentraliza5 = new javax.swing.JPanel();
        jComboBoxRegiao = new javax.swing.JComboBox();
        jButtonNovaRegiao = new javax.swing.JButton();
        jButtonAbrirRegiao = new javax.swing.JButton();
        jPanelFuncao = new javax.swing.JPanel();
        jButtonEscolherFuncaoOperando2 = new javax.swing.JButton();
        jButtonAlterarParametrosOperando2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaFuncaoPreenchidaOperando2 = new javax.swing.JTextArea();
        jButtonDescricaoFuncaoOperando2 = new javax.swing.JButton();
        jPanelFinaliza = new javax.swing.JPanel();
        jLabelPassoFinal = new javax.swing.JLabel();
        jPanelConectores = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jRadioButtonOperadorLogicoE = new javax.swing.JRadioButton();
        jRadioButtonOperadorLogicoOU = new javax.swing.JRadioButton();
        jPanelOperadorNAO = new javax.swing.JPanel();
        jCheckBoxOperadorLogicoNAO = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextAreaCondicaoResultante = new javax.swing.JTextArea();
        jButtonCancelar = new javax.swing.JButton();
        jButtonProximo = new javax.swing.JButton();
        jButtonAnterior = new javax.swing.JButton();
        jButtonAjuda = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Preenchendo Condição de uma Heurística");
        setIconImage(new ImageIcon(getClass().getResource("/icones/icone_heuristica.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelPrincipal.setLayout(new java.awt.CardLayout());

        jLabelPassoInicial.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelPassoInicial.setText("Passo 1 de 4 - Preencha uma Função para ser o primeiro Operando");

        jButtonEscolherFuncaoOperando1.setMnemonic('t');
        jButtonEscolherFuncaoOperando1.setText("Trocar Função");
        jButtonEscolherFuncaoOperando1.setToolTipText("Escolhe a Função a ser Preenchida");
        jButtonEscolherFuncaoOperando1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEscolherFuncaoOperando1ActionPerformed(evt);
            }
        });

        jButtonAlterarParametrosOperando1.setMnemonic('l');
        jButtonAlterarParametrosOperando1.setText("Alterar Parâmetros");
        jButtonAlterarParametrosOperando1.setToolTipText("Altera os Parâmetros da Função");
        jButtonAlterarParametrosOperando1.setEnabled(false);
        jButtonAlterarParametrosOperando1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAlterarParametrosOperando1ActionPerformed(evt);
            }
        });

        jLabel8.setText("Função Preenchida");

        jTextAreaFuncaoPreenchidaOperando1.setBackground(java.awt.SystemColor.control);
        jTextAreaFuncaoPreenchidaOperando1.setColumns(20);
        jTextAreaFuncaoPreenchidaOperando1.setEditable(false);
        jTextAreaFuncaoPreenchidaOperando1.setLineWrap(true);
        jTextAreaFuncaoPreenchidaOperando1.setRows(3);
        jTextAreaFuncaoPreenchidaOperando1.setWrapStyleWord(true);
        jTextAreaFuncaoPreenchidaOperando1.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane2.setViewportView(jTextAreaFuncaoPreenchidaOperando1);

        jButtonDetalhesFuncaoOperando1.setMnemonic('d');
        jButtonDetalhesFuncaoOperando1.setText("Detalhes da Função");
        jButtonDetalhesFuncaoOperando1.setToolTipText("Exibe todos os Detalhes da Função");
        jButtonDetalhesFuncaoOperando1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetalhesFuncaoOperando1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelOperando1Layout = new javax.swing.GroupLayout(jPanelOperando1);
        jPanelOperando1.setLayout(jPanelOperando1Layout);
        jPanelOperando1Layout.setHorizontalGroup(
            jPanelOperando1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOperando1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOperando1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                    .addGroup(jPanelOperando1Layout.createSequentialGroup()
                        .addComponent(jButtonEscolherFuncaoOperando1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAlterarParametrosOperando1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDetalhesFuncaoOperando1))
                    .addComponent(jLabel8)
                    .addComponent(jLabelPassoInicial))
                .addContainerGap())
        );

        jPanelOperando1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAlterarParametrosOperando1, jButtonDetalhesFuncaoOperando1, jButtonEscolherFuncaoOperando1});

        jPanelOperando1Layout.setVerticalGroup(
            jPanelOperando1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOperando1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPassoInicial)
                .addGap(46, 46, 46)
                .addGroup(jPanelOperando1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEscolherFuncaoOperando1)
                    .addComponent(jButtonAlterarParametrosOperando1)
                    .addComponent(jButtonDetalhesFuncaoOperando1))
                .addGap(24, 24, 24)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelOperando1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAlterarParametrosOperando1, jButtonDetalhesFuncaoOperando1, jButtonEscolherFuncaoOperando1});

        jPanelPrincipal.add(jPanelOperando1, "Operando1");

        jLabelPassoInicial1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelPassoInicial1.setText("Passo 2 de 4 - Escolha um Operador para relacionar os dois Operandos");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Relação");

        jPanelComboOpRelacional.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.MAIOR);
        jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.MAIOR_IGUAL);
        jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.MENOR);
        jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.MENOR_IGUAL);
        jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.IGUAL);
        jComboBoxOperadorRelacional.addItem(DHJOG.OperadorRelacional.DIFERENTE);
        jComboBoxOperadorRelacional.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        jComboBoxOperadorRelacional.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxOperadorRelacionalItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelCentralizaRelacionalLayout = new javax.swing.GroupLayout(jPanelCentralizaRelacional);
        jPanelCentralizaRelacional.setLayout(jPanelCentralizaRelacionalLayout);
        jPanelCentralizaRelacionalLayout.setHorizontalGroup(
            jPanelCentralizaRelacionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralizaRelacionalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxOperadorRelacional, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelCentralizaRelacionalLayout.setVerticalGroup(
            jPanelCentralizaRelacionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralizaRelacionalLayout.createSequentialGroup()
                .addComponent(jComboBoxOperadorRelacional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelComboOpRelacional.add(jPanelCentralizaRelacional);

        jLabel4.setText("Descrição do funcionamento do Operador");

        jTextAreaDescricaoOperadorRelacional.setBackground(java.awt.SystemColor.control);
        jTextAreaDescricaoOperadorRelacional.setColumns(20);
        jTextAreaDescricaoOperadorRelacional.setEditable(false);
        jTextAreaDescricaoOperadorRelacional.setLineWrap(true);
        jTextAreaDescricaoOperadorRelacional.setRows(5);
        jTextAreaDescricaoOperadorRelacional.setWrapStyleWord(true);
        jTextAreaDescricaoOperadorRelacional.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane1.setViewportView(jTextAreaDescricaoOperadorRelacional);

        javax.swing.GroupLayout jPanelOperadorRelacionalLayout = new javax.swing.GroupLayout(jPanelOperadorRelacional);
        jPanelOperadorRelacional.setLayout(jPanelOperadorRelacionalLayout);
        jPanelOperadorRelacionalLayout.setHorizontalGroup(
            jPanelOperadorRelacionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelOperadorRelacionalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOperadorRelacionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                    .addComponent(jPanelComboOpRelacional, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                    .addComponent(jLabelPassoInicial1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanelOperadorRelacionalLayout.setVerticalGroup(
            jPanelOperadorRelacionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOperadorRelacionalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPassoInicial1)
                .addGap(16, 16, 16)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelComboOpRelacional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelPrincipal.add(jPanelOperadorRelacional, "OperadorRelacional");

        jLabelPassoParametro.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelPassoParametro.setText("Passo 3 de 4 - Preencha um valor para o segundo Operando");

        jLabel2.setText("Tipo de Valor Esperado");

        jTextFieldTipoOperando2.setEditable(false);

        buttonGroupOpcaoPreenchimento.add(jRadioButtonPreencherValor);
        jRadioButtonPreencherValor.setSelected(true);
        jRadioButtonPreencherValor.setText("Preencher com Valor   ");
        jRadioButtonPreencherValor.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonPreencherValor.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonPreencherValor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPreencherValorActionPerformed(evt);
            }
        });
        jPanelOpcoesPreenchimento.add(jRadioButtonPreencherValor);

        buttonGroupOpcaoPreenchimento.add(jRadioButtonPreencherFuncao);
        jRadioButtonPreencherFuncao.setText("Preencher com Função");
        jRadioButtonPreencherFuncao.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonPreencherFuncao.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonPreencherFuncao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPreencherFuncaoActionPerformed(evt);
            }
        });
        jPanelOpcoesPreenchimento.add(jRadioButtonPreencherFuncao);

        jPanelEntadaDados.setLayout(new java.awt.CardLayout());

        jComboBoxValorLogico.addItem(DHJOG.VALOR_LOGICO.VERDADEIRO);
        jComboBoxValorLogico.addItem(DHJOG.VALOR_LOGICO.FALSO);
        jComboBoxValorLogico.setToolTipText("Escolha o Valor Lógico");
        jComboBoxValorLogico.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));

        javax.swing.GroupLayout jPanelCentralizaLayout = new javax.swing.GroupLayout(jPanelCentraliza);
        jPanelCentraliza.setLayout(jPanelCentralizaLayout);
        jPanelCentralizaLayout.setHorizontalGroup(
            jPanelCentralizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCentralizaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxValorLogico, 0, 207, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelCentralizaLayout.setVerticalGroup(
            jPanelCentralizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentralizaLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jComboBoxValorLogico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jPanelLogico.add(jPanelCentraliza);

        jPanelEntadaDados.add(jPanelLogico, "Logico");

        jComboBoxValorJogador.addItem(DHJOG.VALOR_JOGADOR.EU);
        jComboBoxValorJogador.addItem(DHJOG.VALOR_JOGADOR.OPONENTE);
        jComboBoxValorJogador.setToolTipText("Escolha o Jogador");
        jComboBoxValorJogador.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));

        javax.swing.GroupLayout jPanelCentraliza1Layout = new javax.swing.GroupLayout(jPanelCentraliza1);
        jPanelCentraliza1.setLayout(jPanelCentraliza1Layout);
        jPanelCentraliza1Layout.setHorizontalGroup(
            jPanelCentraliza1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCentraliza1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxValorJogador, 0, 207, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelCentraliza1Layout.setVerticalGroup(
            jPanelCentraliza1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza1Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jComboBoxValorJogador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jPanelJogador.add(jPanelCentraliza1);

        jPanelEntadaDados.add(jPanelJogador, "Jogador");

        jSpinnerValorInteiro.setToolTipText("Entre com um Valor Inteiro");
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorInteiro, null);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Valores válidos de -10.000 a 10.000");

        javax.swing.GroupLayout jPanelCentraliza2Layout = new javax.swing.GroupLayout(jPanelCentraliza2);
        jPanelCentraliza2.setLayout(jPanelCentraliza2Layout);
        jPanelCentraliza2Layout.setHorizontalGroup(
            jPanelCentraliza2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelCentraliza2Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jSpinnerValorInteiro, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80))
        );
        jPanelCentraliza2Layout.setVerticalGroup(
            jPanelCentraliza2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jSpinnerValorInteiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jLabel1))
        );

        jPanelInteiro.add(jPanelCentraliza2);

        jPanelEntadaDados.add(jPanelInteiro, "Inteiro");

        jPanelCentraliza3.setPreferredSize(new java.awt.Dimension(260, 67));

        jSpinnerValorReal.setToolTipText("Entre com um Valor Real");
        UtilsGUI.centralizaAutoValidaValorJSpinner(jSpinnerValorReal, "#,##0.0");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Valores válidos de -10.000,0 a 10.000,0");

        javax.swing.GroupLayout jPanelCentraliza3Layout = new javax.swing.GroupLayout(jPanelCentraliza3);
        jPanelCentraliza3.setLayout(jPanelCentraliza3Layout);
        jPanelCentraliza3Layout.setHorizontalGroup(
            jPanelCentraliza3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza3Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(jSpinnerValorReal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelCentraliza3Layout.setVerticalGroup(
            jPanelCentraliza3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jSpinnerValorReal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jLabel9))
        );

        jPanelReal.add(jPanelCentraliza3);

        jPanelEntadaDados.add(jPanelReal, "Real");

        jPanelTipoPeca.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 20));

        jLabelTipoPeao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/peao.png"))); // NOI18N
        jLabelTipoPeao.setText("Peão");
        jLabelTipoPeao.setEnabled(false);

        jLabelTipoTorre.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/torre.png"))); // NOI18N
        jLabelTipoTorre.setText("Torre");
        jLabelTipoTorre.setEnabled(false);

        jLabelTipoCavalo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/cavalo.png"))); // NOI18N
        jLabelTipoCavalo.setText("Cavalo");
        jLabelTipoCavalo.setEnabled(false);

        jLabelTipoBispo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/bispo.png"))); // NOI18N
        jLabelTipoBispo.setText("Bispo");
        jLabelTipoBispo.setEnabled(false);

        jLabelTipoDama.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/dama.png"))); // NOI18N
        jLabelTipoDama.setText("Dama");
        jLabelTipoDama.setEnabled(false);

        jLabelTipoRei.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pecas/rei.png"))); // NOI18N
        jLabelTipoRei.setText("Rei");
        jLabelTipoRei.setEnabled(false);

        jCheckBoxTipoPeao.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTipoPeao.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTipoPeao.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTipoPeaoItemStateChanged(evt);
            }
        });

        jCheckBoxTipoCavalo.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTipoCavalo.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTipoCavalo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTipoCavaloItemStateChanged(evt);
            }
        });

        jCheckBoxTipoTorre.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTipoTorre.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTipoTorre.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTipoTorreItemStateChanged(evt);
            }
        });

        jCheckBoxTipoBispo.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTipoBispo.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTipoBispo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTipoBispoItemStateChanged(evt);
            }
        });

        jCheckBoxTipoDama.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTipoDama.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTipoDama.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTipoDamaItemStateChanged(evt);
            }
        });

        jCheckBoxTipoRei.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTipoRei.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTipoRei.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTipoReiItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelCentraliza4Layout = new javax.swing.GroupLayout(jPanelCentraliza4);
        jPanelCentraliza4.setLayout(jPanelCentraliza4Layout);
        jPanelCentraliza4Layout.setHorizontalGroup(
            jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTipoPeao)
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jCheckBoxTipoPeao)))
                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTipoTorre))
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jCheckBoxTipoTorre)))
                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTipoCavalo))
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBoxTipoCavalo)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addComponent(jLabelTipoBispo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTipoDama)
                        .addGap(6, 6, 6)
                        .addComponent(jLabelTipoRei)
                        .addContainerGap(20, Short.MAX_VALUE))
                    .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jCheckBoxTipoBispo)
                        .addGap(63, 63, 63)
                        .addComponent(jCheckBoxTipoDama)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                        .addComponent(jCheckBoxTipoRei)
                        .addGap(54, 54, 54))))
        );
        jPanelCentraliza4Layout.setVerticalGroup(
            jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelTipoCavalo)
                    .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                            .addGap(19, 19, 19)
                            .addComponent(jLabelTipoTorre))
                        .addGroup(jPanelCentraliza4Layout.createSequentialGroup()
                            .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jCheckBoxTipoPeao)
                                    .addComponent(jCheckBoxTipoTorre)
                                    .addComponent(jCheckBoxTipoCavalo))
                                .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jCheckBoxTipoBispo)
                                    .addComponent(jCheckBoxTipoDama)
                                    .addComponent(jCheckBoxTipoRei)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanelCentraliza4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelTipoPeao)
                                .addComponent(jLabelTipoBispo)
                                .addComponent(jLabelTipoDama)
                                .addComponent(jLabelTipoRei)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelTipoPeca.add(jPanelCentraliza4);

        jPanelEntadaDados.add(jPanelTipoPeca, "TipoPeca");

        jPanelPecasMINHAS.setBorder(javax.swing.BorderFactory.createTitledBorder("PEÇAS MINHAS"));

        jCheckBoxPeaoMEU.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxPeaoMEU.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxPeaoMEU.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxPeaoMEUItemStateChanged(evt);
            }
        });

        jLabelPeaoMEU.setText("Peão");
        jLabelPeaoMEU.setEnabled(false);

        jCheckBoxTorreMINHA.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTorreMINHA.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTorreMINHA.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTorreMINHAItemStateChanged(evt);
            }
        });

        jLabelTorreMINHA.setText("Torre");
        jLabelTorreMINHA.setEnabled(false);

        jCheckBoxCavaloMEU.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxCavaloMEU.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxCavaloMEU.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxCavaloMEUItemStateChanged(evt);
            }
        });

        jLabelCavaloMEU.setText("Cavalo");
        jLabelCavaloMEU.setEnabled(false);

        jCheckBoxBispoMEU.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxBispoMEU.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxBispoMEU.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxBispoMEUItemStateChanged(evt);
            }
        });

        jLabelBispoMEU.setText("Bispo");
        jLabelBispoMEU.setEnabled(false);

        jCheckBoxDamaMINHA.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxDamaMINHA.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxDamaMINHA.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxDamaMINHAItemStateChanged(evt);
            }
        });

        jLabelDamaMINHA.setText("Dama");
        jLabelDamaMINHA.setEnabled(false);

        jCheckBoxReiMEU.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxReiMEU.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxReiMEU.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxReiMEUItemStateChanged(evt);
            }
        });

        jLabelReiMEU.setText("Rei");
        jLabelReiMEU.setEnabled(false);

        javax.swing.GroupLayout jPanelPecasMINHASLayout = new javax.swing.GroupLayout(jPanelPecasMINHAS);
        jPanelPecasMINHAS.setLayout(jPanelPecasMINHASLayout);
        jPanelPecasMINHASLayout.setHorizontalGroup(
            jPanelPecasMINHASLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPecasMINHASLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxPeaoMEU)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelPeaoMEU)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxTorreMINHA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTorreMINHA, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxCavaloMEU)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelCavaloMEU)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxBispoMEU)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelBispoMEU, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxDamaMINHA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelDamaMINHA)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxReiMEU)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelReiMEU, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelPecasMINHASLayout.setVerticalGroup(
            jPanelPecasMINHASLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPecasMINHASLayout.createSequentialGroup()
                .addGroup(jPanelPecasMINHASLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPecasMINHASLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelPecasMINHASLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelCavaloMEU)
                            .addComponent(jCheckBoxCavaloMEU)
                            .addComponent(jLabelTorreMINHA)
                            .addComponent(jCheckBoxTorreMINHA)
                            .addComponent(jLabelPeaoMEU)
                            .addComponent(jCheckBoxPeaoMEU)))
                    .addGroup(jPanelPecasMINHASLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanelPecasMINHASLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelPecasMINHASLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jCheckBoxBispoMEU)
                                .addComponent(jLabelBispoMEU))
                            .addGroup(jPanelPecasMINHASLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabelDamaMINHA)
                                .addComponent(jCheckBoxDamaMINHA))))
                    .addGroup(jPanelPecasMINHASLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanelPecasMINHASLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxReiMEU)
                            .addComponent(jLabelReiMEU))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelPecasOPONENTE.setBorder(javax.swing.BorderFactory.createTitledBorder("PEÇAS DO OPONENTE"));

        jCheckBoxPeaoOPONENTE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxPeaoOPONENTE.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxPeaoOPONENTE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxPeaoOPONENTEItemStateChanged(evt);
            }
        });

        jLabelPeaoOPONENTE.setText("Peão");
        jLabelPeaoOPONENTE.setEnabled(false);

        jCheckBoxTorreOPONENTE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxTorreOPONENTE.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxTorreOPONENTE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTorreOPONENTEItemStateChanged(evt);
            }
        });

        jLabelTorreOPONENTE.setText("Torre");
        jLabelTorreOPONENTE.setEnabled(false);

        jCheckBoxCavaloOPONENTE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxCavaloOPONENTE.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxCavaloOPONENTE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxCavaloOPONENTEItemStateChanged(evt);
            }
        });

        jLabelCavaloOPONENTE.setText("Cavalo");
        jLabelCavaloOPONENTE.setEnabled(false);

        jCheckBoxBispoOPONENTE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxBispoOPONENTE.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxBispoOPONENTE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxBispoOPONENTEItemStateChanged(evt);
            }
        });

        jLabelBispoOPONENTE.setText("Bispo");
        jLabelBispoOPONENTE.setEnabled(false);

        jCheckBoxDamaOPONENTE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxDamaOPONENTE.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxDamaOPONENTE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxDamaOPONENTEItemStateChanged(evt);
            }
        });

        jLabelDamaOPONENTE.setText("Dama");
        jLabelDamaOPONENTE.setEnabled(false);

        jCheckBoxReiOPONENTE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxReiOPONENTE.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxReiOPONENTE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxReiOPONENTEItemStateChanged(evt);
            }
        });

        jLabelReiOPONENTE.setText("Rei");
        jLabelReiOPONENTE.setEnabled(false);

        javax.swing.GroupLayout jPanelPecasOPONENTELayout = new javax.swing.GroupLayout(jPanelPecasOPONENTE);
        jPanelPecasOPONENTE.setLayout(jPanelPecasOPONENTELayout);
        jPanelPecasOPONENTELayout.setHorizontalGroup(
            jPanelPecasOPONENTELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPecasOPONENTELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxPeaoOPONENTE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelPeaoOPONENTE)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxTorreOPONENTE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTorreOPONENTE, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxCavaloOPONENTE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelCavaloOPONENTE)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxBispoOPONENTE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelBispoOPONENTE, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxDamaOPONENTE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelDamaOPONENTE)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxReiOPONENTE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelReiOPONENTE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelPecasOPONENTELayout.setVerticalGroup(
            jPanelPecasOPONENTELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPecasOPONENTELayout.createSequentialGroup()
                .addGroup(jPanelPecasOPONENTELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPecasOPONENTELayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelPecasOPONENTELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelPeaoOPONENTE)
                            .addComponent(jCheckBoxPeaoOPONENTE)
                            .addGroup(jPanelPecasOPONENTELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabelTorreOPONENTE)
                                .addComponent(jCheckBoxTorreOPONENTE)
                                .addComponent(jCheckBoxCavaloOPONENTE))
                            .addGroup(jPanelPecasOPONENTELayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(jPanelPecasOPONENTELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jCheckBoxBispoOPONENTE)
                                    .addComponent(jLabelCavaloOPONENTE)))))
                    .addGroup(jPanelPecasOPONENTELayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(jPanelPecasOPONENTELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jCheckBoxReiOPONENTE)
                            .addGroup(jPanelPecasOPONENTELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelPecasOPONENTELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jCheckBoxDamaOPONENTE)
                                    .addComponent(jLabelBispoOPONENTE))
                                .addGroup(jPanelPecasOPONENTELayout.createSequentialGroup()
                                    .addGap(1, 1, 1)
                                    .addComponent(jLabelDamaOPONENTE)))
                            .addComponent(jLabelReiOPONENTE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelCentraliza6Layout = new javax.swing.GroupLayout(jPanelCentraliza6);
        jPanelCentraliza6.setLayout(jPanelCentraliza6Layout);
        jPanelCentraliza6Layout.setHorizontalGroup(
            jPanelCentraliza6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPecasMINHAS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelPecasOPONENTE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelCentraliza6Layout.setVerticalGroup(
            jPanelCentraliza6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelPecasMINHAS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelPecasOPONENTE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelPeca.add(jPanelCentraliza6);

        jPanelEntadaDados.add(jPanelPeca, "Peca");

        jPanelRegiao.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 30));

        jComboBoxRegiao.setToolTipText("Escolha uma Região");
        jComboBoxRegiao.setRenderer(new AlignedListCellRenderer(SwingConstants.CENTER));
        jComboBoxRegiao.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxRegiaoItemStateChanged(evt);
            }
        });

        jButtonNovaRegiao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/mais.png"))); // NOI18N
        jButtonNovaRegiao.setText("Nova Região");
        jButtonNovaRegiao.setToolTipText("Cria uma Nova Região");
        jButtonNovaRegiao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNovaRegiaoActionPerformed(evt);
            }
        });

        jButtonAbrirRegiao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/alterar.png"))); // NOI18N
        jButtonAbrirRegiao.setText("Abrir Região");
        jButtonAbrirRegiao.setToolTipText("Abre a Região Selecionada");
        jButtonAbrirRegiao.setEnabled(false);
        jButtonAbrirRegiao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAbrirRegiaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCentraliza5Layout = new javax.swing.GroupLayout(jPanelCentraliza5);
        jPanelCentraliza5.setLayout(jPanelCentraliza5Layout);
        jPanelCentraliza5Layout.setHorizontalGroup(
            jPanelCentraliza5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCentraliza5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxRegiao, 0, 299, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCentraliza5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonNovaRegiao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonAbrirRegiao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelCentraliza5Layout.setVerticalGroup(
            jPanelCentraliza5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCentraliza5Layout.createSequentialGroup()
                .addGroup(jPanelCentraliza5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCentraliza5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonNovaRegiao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAbrirRegiao))
                    .addGroup(jPanelCentraliza5Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jComboBoxRegiao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelRegiao.add(jPanelCentraliza5);

        jPanelEntadaDados.add(jPanelRegiao, "Regiao");

        jButtonEscolherFuncaoOperando2.setMnemonic('e');
        jButtonEscolherFuncaoOperando2.setText("Escoher Função");
        jButtonEscolherFuncaoOperando2.setToolTipText("Escolhe a Função a ser Preenchida");
        jButtonEscolherFuncaoOperando2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEscolherFuncaoOperando2ActionPerformed(evt);
            }
        });

        jButtonAlterarParametrosOperando2.setMnemonic('l');
        jButtonAlterarParametrosOperando2.setText("Alterar Parâmetros");
        jButtonAlterarParametrosOperando2.setToolTipText("Altera os Parâmetros da Função");
        jButtonAlterarParametrosOperando2.setEnabled(false);
        jButtonAlterarParametrosOperando2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAlterarParametrosOperando2ActionPerformed(evt);
            }
        });

        jLabel6.setText("Função Preenchida");

        jTextAreaFuncaoPreenchidaOperando2.setBackground(java.awt.SystemColor.control);
        jTextAreaFuncaoPreenchidaOperando2.setColumns(20);
        jTextAreaFuncaoPreenchidaOperando2.setEditable(false);
        jTextAreaFuncaoPreenchidaOperando2.setLineWrap(true);
        jTextAreaFuncaoPreenchidaOperando2.setRows(3);
        jTextAreaFuncaoPreenchidaOperando2.setWrapStyleWord(true);
        jTextAreaFuncaoPreenchidaOperando2.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane3.setViewportView(jTextAreaFuncaoPreenchidaOperando2);

        jButtonDescricaoFuncaoOperando2.setMnemonic('d');
        jButtonDescricaoFuncaoOperando2.setText("Detalhes da Função");
        jButtonDescricaoFuncaoOperando2.setToolTipText("Exibe todos os Detalhes da Função");
        jButtonDescricaoFuncaoOperando2.setEnabled(false);
        jButtonDescricaoFuncaoOperando2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDescricaoFuncaoOperando2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFuncaoLayout = new javax.swing.GroupLayout(jPanelFuncao);
        jPanelFuncao.setLayout(jPanelFuncaoLayout);
        jPanelFuncaoLayout.setHorizontalGroup(
            jPanelFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFuncaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelFuncaoLayout.createSequentialGroup()
                        .addComponent(jButtonEscolherFuncaoOperando2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAlterarParametrosOperando2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDescricaoFuncaoOperando2))
                    .addComponent(jLabel6)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanelFuncaoLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAlterarParametrosOperando2, jButtonDescricaoFuncaoOperando2, jButtonEscolherFuncaoOperando2});

        jPanelFuncaoLayout.setVerticalGroup(
            jPanelFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFuncaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFuncaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEscolherFuncaoOperando2)
                    .addComponent(jButtonAlterarParametrosOperando2)
                    .addComponent(jButtonDescricaoFuncaoOperando2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelFuncaoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAlterarParametrosOperando2, jButtonDescricaoFuncaoOperando2, jButtonEscolherFuncaoOperando2});

        jPanelEntadaDados.add(jPanelFuncao, "Funcao");

        javax.swing.GroupLayout jPanelOperando2Layout = new javax.swing.GroupLayout(jPanelOperando2);
        jPanelOperando2.setLayout(jPanelOperando2Layout);
        jPanelOperando2Layout.setHorizontalGroup(
            jPanelOperando2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelEntadaDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelOperando2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOperando2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelOperando2Layout.createSequentialGroup()
                        .addComponent(jLabelPassoParametro)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelOperando2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTipoOperando2))
                    .addComponent(jPanelOpcoesPreenchimento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelOperando2Layout.setVerticalGroup(
            jPanelOperando2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOperando2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPassoParametro)
                .addGap(30, 30, 30)
                .addGroup(jPanelOperando2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldTipoOperando2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanelOpcoesPreenchimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelEntadaDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelPrincipal.add(jPanelOperando2, "Operando2");

        jLabelPassoFinal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelPassoFinal.setText("Passo 4 de 4 - Escolha os operadores Lógicos aplicados a esta Condição");

        jLabel5.setText("Conector entre esta Condição e as demais       ");
        jPanelConectores.add(jLabel5);

        buttonGroupConectores.add(jRadioButtonOperadorLogicoE);
        jRadioButtonOperadorLogicoE.setSelected(true);
        jRadioButtonOperadorLogicoE.setText(" E ");
        jRadioButtonOperadorLogicoE.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonOperadorLogicoE.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonOperadorLogicoE.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonOperadorLogicoEItemStateChanged(evt);
            }
        });
        jPanelConectores.add(jRadioButtonOperadorLogicoE);

        buttonGroupConectores.add(jRadioButtonOperadorLogicoOU);
        jRadioButtonOperadorLogicoOU.setText(" OU ");
        jRadioButtonOperadorLogicoOU.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButtonOperadorLogicoOU.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButtonOperadorLogicoOU.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonOperadorLogicoOUItemStateChanged(evt);
            }
        });
        jPanelConectores.add(jRadioButtonOperadorLogicoOU);

        jCheckBoxOperadorLogicoNAO.setSelected(true);
        jCheckBoxOperadorLogicoNAO.setText("Usar operador NÃO e inverter o valor Lógico encontrado    ");
        jCheckBoxOperadorLogicoNAO.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jCheckBoxOperadorLogicoNAO.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jCheckBoxOperadorLogicoNAO.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jCheckBoxOperadorLogicoNAO.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxOperadorLogicoNAOItemStateChanged(evt);
            }
        });
        jPanelOperadorNAO.add(jCheckBoxOperadorLogicoNAO);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Após o preenchimento a Condição ficou desta forma");

        jTextAreaCondicaoResultante.setColumns(20);
        jTextAreaCondicaoResultante.setEditable(false);
        jTextAreaCondicaoResultante.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextAreaCondicaoResultante.setLineWrap(true);
        jTextAreaCondicaoResultante.setRows(5);
        jTextAreaCondicaoResultante.setWrapStyleWord(true);
        jTextAreaCondicaoResultante.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jScrollPane4.setViewportView(jTextAreaCondicaoResultante);

        javax.swing.GroupLayout jPanelFinalizaLayout = new javax.swing.GroupLayout(jPanelFinaliza);
        jPanelFinaliza.setLayout(jPanelFinalizaLayout);
        jPanelFinalizaLayout.setHorizontalGroup(
            jPanelFinalizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFinalizaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFinalizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                    .addComponent(jPanelOperadorNAO, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                    .addComponent(jPanelConectores, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 547, Short.MAX_VALUE)
                    .addComponent(jLabelPassoFinal, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanelFinalizaLayout.setVerticalGroup(
            jPanelFinalizaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFinalizaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelPassoFinal)
                .addGap(18, 18, 18)
                .addComponent(jPanelConectores, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelOperadorNAO, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelPrincipal.add(jPanelFinaliza, "Finaliza");

        jButtonCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_cancelar.png"))); // NOI18N
        jButtonCancelar.setMnemonic('c');
        jButtonCancelar.setText("Cancelar");
        jButtonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarActionPerformed(evt);
            }
        });

        jButtonProximo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_avancar.png"))); // NOI18N
        jButtonProximo.setMnemonic('p');
        jButtonProximo.setText("Próximo");
        jButtonProximo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProximoActionPerformed(evt);
            }
        });

        jButtonAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/icone_voltar.png"))); // NOI18N
        jButtonAnterior.setMnemonic('n');
        jButtonAnterior.setText("Anterior");
        jButtonAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnteriorActionPerformed(evt);
            }
        });

        jButtonAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icones/ajuda-pesquisar.png"))); // NOI18N
        jButtonAjuda.setMnemonic('a');
        jButtonAjuda.setText("Ajuda");
        jButtonAjuda.setToolTipText("Consulta o texto de ajuda desta tela");
        jButtonAjuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAjudaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelPrincipal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonAjuda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonAnterior)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonProximo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancelar)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButtonAjuda, jButtonAnterior, jButtonCancelar, jButtonProximo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancelar)
                    .addComponent(jButtonProximo)
                    .addComponent(jButtonAnterior)
                    .addComponent(jButtonAjuda))
                .addGap(10, 10, 10))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonAjuda, jButtonAnterior, jButtonCancelar, jButtonProximo});

        setSize(new java.awt.Dimension(599, 385));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDescricaoFuncaoOperando2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDescricaoFuncaoOperando2ActionPerformed
        
        final ParametroPreenchido operando2 = condicao.getParametroOperando2();
        
        if (operando2.isPreenchidaValor() == false) {
            
            final FuncaoPreenchida funcaoPreenchida = (FuncaoPreenchida) operando2.getValor();
            
            if (funcaoPreenchida != null) {
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaFuncao tela = new TelaFuncao(TelaPreencheCondicao.this, funcaoPreenchida.getFuncao(), telaHeuristica.panelEtapa.editor.conjuntoHeuristico.getTipo());
                    }
                });
            }
        }
    }//GEN-LAST:event_jButtonDescricaoFuncaoOperando2ActionPerformed

    private void jButtonDetalhesFuncaoOperando1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetalhesFuncaoOperando1ActionPerformed
        
        if (condicao != null){
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaFuncao tela = new TelaFuncao(TelaPreencheCondicao.this,
                                                     ((FuncaoPreenchida)condicao.getValorOperando1()).getFuncao(),
                                                     telaHeuristica.panelEtapa.editor.conjuntoHeuristico.getTipo());  
                }
            });
        }                
    }//GEN-LAST:event_jButtonDetalhesFuncaoOperando1ActionPerformed

    private void jButtonEscolherFuncaoOperando2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEscolherFuncaoOperando2ActionPerformed
        
        final ParametroPreenchido operando2 = condicao.getParametroOperando2();
        
        if (operando2.isPreenchidaValor() == false){
            
            final FuncaoPreenchida funcaoPreenchida = (FuncaoPreenchida) operando2.getValor();    
            
            if (funcaoPreenchida != null){                     
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaEscolheFuncao tela = new TelaEscolheFuncao(TelaPreencheCondicao.this, operando2.getTipo(), funcaoPreenchida.getFuncao());
                    }
                });

            }else{        
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaEscolheFuncao tela = new TelaEscolheFuncao(TelaPreencheCondicao.this,operando2.getTipo());
                    }
                });
            }
        }
    }//GEN-LAST:event_jButtonEscolherFuncaoOperando2ActionPerformed

    private void jButtonAlterarParametrosOperando2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAlterarParametrosOperando2ActionPerformed
        
        final ParametroPreenchido operando2 = condicao.getParametroOperando2();
        
        if (operando2.isPreenchidaValor() == false){
            
            final FuncaoPreenchida funcaoPreenchida = (FuncaoPreenchida) operando2.getValor();    
            
            if (funcaoPreenchida != null){            
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TelaPreencheFuncao tela = new TelaPreencheFuncao(telaHeuristica,TelaPreencheCondicao.this,funcaoPreenchida);
                    }
                });
            }
        }
    }//GEN-LAST:event_jButtonAlterarParametrosOperando2ActionPerformed

    private void jButtonAlterarParametrosOperando1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAlterarParametrosOperando1ActionPerformed
        
        if (condicao != null){            
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaPreencheFuncao tela = new TelaPreencheFuncao(telaHeuristica,TelaPreencheCondicao.this,(FuncaoPreenchida) condicao.getValorOperando1());
                }
            });
        }
    }//GEN-LAST:event_jButtonAlterarParametrosOperando1ActionPerformed

    private void jButtonEscolherFuncaoOperando1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEscolherFuncaoOperando1ActionPerformed
        
        if (condicao != null) {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaEscolheFuncao tela = new TelaEscolheFuncao(TelaPreencheCondicao.this, null, ((FuncaoPreenchida) condicao.getValorOperando1()).getFuncao());
                }
            });

        } else {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaEscolheFuncao tela = new TelaEscolheFuncao(TelaPreencheCondicao.this, null);
                }
            });
        }
    }//GEN-LAST:event_jButtonEscolherFuncaoOperando1ActionPerformed

    private void jCheckBoxOperadorLogicoNAOItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxOperadorLogicoNAOItemStateChanged
        condicao.setOperadorLogicoNao(jCheckBoxOperadorLogicoNAO.isSelected());
        jTextAreaCondicaoResultante.setText(condicao.toDHJOG(false));
    }//GEN-LAST:event_jCheckBoxOperadorLogicoNAOItemStateChanged

    private void jRadioButtonOperadorLogicoOUItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonOperadorLogicoOUItemStateChanged
        condicao.setOperadorLogico(DHJOG.OperadorLogico.OU);
        jTextAreaCondicaoResultante.setText(condicao.toDHJOG(false));
    }//GEN-LAST:event_jRadioButtonOperadorLogicoOUItemStateChanged

    private void jRadioButtonOperadorLogicoEItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonOperadorLogicoEItemStateChanged
        condicao.setOperadorLogico(DHJOG.OperadorLogico.E);
        jTextAreaCondicaoResultante.setText(condicao.toDHJOG(false));
    }//GEN-LAST:event_jRadioButtonOperadorLogicoEItemStateChanged

    private void jComboBoxOperadorRelacionalItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxOperadorRelacionalItemStateChanged
        configuraDescricaoOperador();
    }//GEN-LAST:event_jComboBoxOperadorRelacionalItemStateChanged

    private void jButtonAbrirRegiaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAbrirRegiaoActionPerformed
        
        final int indicePosicao = jComboBoxRegiao.getSelectedIndex();
        
        if (indicePosicao > 0){
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TelaRegiao tela = new TelaRegiao(telaHeuristica.panelEtapa,
                                                     TelaPreencheCondicao.this,
                                                    (Regiao) jComboBoxRegiao.getSelectedItem(),
                                                     false);
                }
            });
        }
    }//GEN-LAST:event_jButtonAbrirRegiaoActionPerformed
    
    private void jButtonNovaRegiaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNovaRegiaoActionPerformed
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TelaRegiao tela = new TelaRegiao(telaHeuristica.panelEtapa,TelaPreencheCondicao.this);
            }
        });
    }//GEN-LAST:event_jButtonNovaRegiaoActionPerformed

    private void jComboBoxRegiaoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxRegiaoItemStateChanged
        
        if (jComboBoxRegiao.getSelectedIndex() > 0){
            jButtonAbrirRegiao.setEnabled(true);
        }else{
            jButtonAbrirRegiao.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxRegiaoItemStateChanged

    private void jRadioButtonPreencherFuncaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPreencherFuncaoActionPerformed
         
         ParametroPreenchido operando2 = condicao.getParametroOperando2();
        
         operando2.setPreenchidaValor(false);
         
         montaPanelOperando2(); 
    }//GEN-LAST:event_jRadioButtonPreencherFuncaoActionPerformed

    private void jRadioButtonPreencherValorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPreencherValorActionPerformed
         
         ParametroPreenchido operando2 = condicao.getParametroOperando2();
         
         operando2.setPreenchidaValor(true);
         
         montaPanelOperando2();  
    }//GEN-LAST:event_jRadioButtonPreencherValorActionPerformed

    private void jCheckBoxTipoReiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTipoReiItemStateChanged
        
        if (jCheckBoxTipoRei.isSelected()){            
            jLabelTipoRei.setEnabled(true);            
        }else{
            jLabelTipoRei.setEnabled(false);
        }        
    }//GEN-LAST:event_jCheckBoxTipoReiItemStateChanged

    private void jCheckBoxTipoDamaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTipoDamaItemStateChanged
        
        if (jCheckBoxTipoDama.isSelected()){            
            jLabelTipoDama.setEnabled(true);            
        }else{
            jLabelTipoDama.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxTipoDamaItemStateChanged

    private void jCheckBoxTipoBispoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTipoBispoItemStateChanged
        
        if (jCheckBoxTipoBispo.isSelected()){            
            jLabelTipoBispo.setEnabled(true);            
        }else{
            jLabelTipoBispo.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxTipoBispoItemStateChanged

    private void jCheckBoxTipoTorreItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTipoTorreItemStateChanged
        
        if (jCheckBoxTipoTorre.isSelected()){            
            jLabelTipoTorre.setEnabled(true);            
        }else{
            jLabelTipoTorre.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxTipoTorreItemStateChanged

    private void jCheckBoxTipoCavaloItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTipoCavaloItemStateChanged
        
        if (jCheckBoxTipoCavalo.isSelected()){            
            jLabelTipoCavalo.setEnabled(true);            
        }else{
            jLabelTipoCavalo.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxTipoCavaloItemStateChanged

    private void jCheckBoxTipoPeaoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTipoPeaoItemStateChanged
        
        if (jCheckBoxTipoPeao.isSelected()){            
            jLabelTipoPeao.setEnabled(true);            
        }else{
            jLabelTipoPeao.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxTipoPeaoItemStateChanged
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmaCancelar();
    }//GEN-LAST:event_formWindowClosing
            
    private void jButtonAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnteriorActionPerformed
        
       passoAtual--;
        
       switch(passoAtual){
           case 1: // Operando 1 //
                  jButtonAnterior.setVisible(false);
                  cardPrincipal.show(jPanelPrincipal,"Operando1");
                  break;
               
           case 2: // Operador Relacional //
                  jButtonAnterior.setVisible(true);
                  cardPrincipal.show(jPanelPrincipal,"OperadorRelacional");   
                  break;
               
           case 3: // Operando 2 //
                  jButtonProximo.setText("Próximo");
                  jButtonProximo.setMnemonic('p'); 
                  jButtonProximo.setVisible(true);
                  cardPrincipal.show(jPanelPrincipal,"Operando2");   
                  break;
       }            
    }//GEN-LAST:event_jButtonAnteriorActionPerformed
    
    private void jButtonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarActionPerformed
        confirmaCancelar();
    }//GEN-LAST:event_jButtonCancelarActionPerformed
    
    private void jButtonProximoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProximoActionPerformed
        
        switch(passoAtual){
            
            case 1:// Operando 1 //
            case 2:// Operador Relacional //
            case 3:// Operando 2 //
                
                   try{
                       salvarEntrada();                        
                   }catch(Exception e){
                       //Utils.registraException(e);
                       UtilsGUI.dialogoErro(this,e.getMessage());
                       return;
                   }
                   break;
                
            case 4:// Finalizacao //                         
                
                   if (nova){                                                                                      
                       model.add(model.size(),condicao);
                   }else{
                       if (!condicao.toString().equals(condicaoOriginal.toString())){                        
                           model.set(posicaoOriginal,condicao);
                       }
                   }
                   
                   dispose();
                   telaHeuristica.fechandoTelaPreencheCondicao(true);
                   return;                   
        }
        
        passoAtual++;
        
        switch(passoAtual){
            
            case 2:// Operador Relacional //
                
                   cardPrincipal.show(jPanelPrincipal,"OperadorRelacional");   
                   
                   opcoesOperadores();
                   
                   jButtonAnterior.setVisible(true);
                   jComboBoxOperadorRelacional.setSelectedItem(condicao.getOperadorRelacional());
                   
                   configuraDescricaoOperador();
                   break;
                
            case 3:// Operando 2 //
                
                   cardPrincipal.show(jPanelPrincipal,"Operando2");
                   
                   montaPanelOperando2();
                   break;
                
            case 4:// Finalizacao //                   
                
                   if (telaHeuristica.panelEtapa.editor.podeAlterar()) {
                       jButtonProximo.setText("Concluir");
                       jButtonProximo.setMnemonic('n');
                   }else{
                       jButtonProximo.setVisible(false);
                   }
                   
                   cardPrincipal.show(jPanelPrincipal,"Finaliza");                
                   
                   jTextAreaCondicaoResultante.setText(condicao.toDHJOG(false));
                   break;
        }
    }//GEN-LAST:event_jButtonProximoActionPerformed

    private void jButtonAjudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjudaActionPerformed
        HeuChess.ajuda.abre(this,"TelaPreencheCondicao");
    }//GEN-LAST:event_jButtonAjudaActionPerformed

    private void jCheckBoxPeaoMEUItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxPeaoMEUItemStateChanged

        if (jCheckBoxPeaoMEU.isSelected()){            
            jLabelPeaoMEU.setEnabled(true);            
        }else{
            jLabelPeaoMEU.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxPeaoMEUItemStateChanged

    private void jCheckBoxCavaloMEUItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxCavaloMEUItemStateChanged
        
        if (jCheckBoxCavaloMEU.isSelected()){            
            jLabelCavaloMEU.setEnabled(true);            
        }else{
            jLabelCavaloMEU.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxCavaloMEUItemStateChanged

    private void jCheckBoxTorreMINHAItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTorreMINHAItemStateChanged
        
        if (jCheckBoxTorreMINHA.isSelected()){            
            jLabelTorreMINHA.setEnabled(true);            
        }else{
            jLabelTorreMINHA.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxTorreMINHAItemStateChanged

    private void jCheckBoxBispoMEUItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxBispoMEUItemStateChanged
        
        if (jCheckBoxBispoMEU.isSelected()){            
            jLabelBispoMEU.setEnabled(true);            
        }else{
            jLabelBispoMEU.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxBispoMEUItemStateChanged

    private void jCheckBoxDamaMINHAItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxDamaMINHAItemStateChanged
        
        if (jCheckBoxDamaMINHA.isSelected()){            
            jLabelDamaMINHA.setEnabled(true);            
        }else{
            jLabelDamaMINHA.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxDamaMINHAItemStateChanged

    private void jCheckBoxReiMEUItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxReiMEUItemStateChanged
        
        if (jCheckBoxReiMEU.isSelected()){            
            jLabelReiMEU.setEnabled(true);            
        }else{
            jLabelReiMEU.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxReiMEUItemStateChanged

    private void jCheckBoxPeaoOPONENTEItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxPeaoOPONENTEItemStateChanged
        
        if (jCheckBoxPeaoOPONENTE.isSelected()){            
            jLabelPeaoOPONENTE.setEnabled(true);            
        }else{
            jLabelPeaoOPONENTE.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxPeaoOPONENTEItemStateChanged

    private void jCheckBoxTorreOPONENTEItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTorreOPONENTEItemStateChanged
        
        if (jCheckBoxTorreOPONENTE.isSelected()){            
            jLabelTorreOPONENTE.setEnabled(true);            
        }else{
            jLabelTorreOPONENTE.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxTorreOPONENTEItemStateChanged

    private void jCheckBoxCavaloOPONENTEItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxCavaloOPONENTEItemStateChanged
        
        if (jCheckBoxCavaloOPONENTE.isSelected()){            
            jLabelCavaloOPONENTE.setEnabled(true);            
        }else{
            jLabelCavaloOPONENTE.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxCavaloOPONENTEItemStateChanged

    private void jCheckBoxBispoOPONENTEItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxBispoOPONENTEItemStateChanged
        
        if (jCheckBoxBispoOPONENTE.isSelected()){            
            jLabelBispoOPONENTE.setEnabled(true);            
        }else{
            jLabelBispoOPONENTE.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxBispoOPONENTEItemStateChanged

    private void jCheckBoxDamaOPONENTEItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxDamaOPONENTEItemStateChanged
        
        if (jCheckBoxDamaOPONENTE.isSelected()){            
            jLabelDamaOPONENTE.setEnabled(true);            
        }else{
            jLabelDamaOPONENTE.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxDamaOPONENTEItemStateChanged

    private void jCheckBoxReiOPONENTEItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxReiOPONENTEItemStateChanged
        
        if (jCheckBoxReiOPONENTE.isSelected()){            
            jLabelReiOPONENTE.setEnabled(true);            
        }else{
            jLabelReiOPONENTE.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxReiOPONENTEItemStateChanged
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupConectores;
    private javax.swing.ButtonGroup buttonGroupOpcaoPreenchimento;
    private javax.swing.JButton jButtonAbrirRegiao;
    private javax.swing.JButton jButtonAjuda;
    private javax.swing.JButton jButtonAlterarParametrosOperando1;
    private javax.swing.JButton jButtonAlterarParametrosOperando2;
    private javax.swing.JButton jButtonAnterior;
    private javax.swing.JButton jButtonCancelar;
    private javax.swing.JButton jButtonDescricaoFuncaoOperando2;
    private javax.swing.JButton jButtonDetalhesFuncaoOperando1;
    private javax.swing.JButton jButtonEscolherFuncaoOperando1;
    private javax.swing.JButton jButtonEscolherFuncaoOperando2;
    private javax.swing.JButton jButtonNovaRegiao;
    private javax.swing.JButton jButtonProximo;
    private javax.swing.JCheckBox jCheckBoxBispoMEU;
    private javax.swing.JCheckBox jCheckBoxBispoOPONENTE;
    private javax.swing.JCheckBox jCheckBoxCavaloMEU;
    private javax.swing.JCheckBox jCheckBoxCavaloOPONENTE;
    private javax.swing.JCheckBox jCheckBoxDamaMINHA;
    private javax.swing.JCheckBox jCheckBoxDamaOPONENTE;
    private javax.swing.JCheckBox jCheckBoxOperadorLogicoNAO;
    private javax.swing.JCheckBox jCheckBoxPeaoMEU;
    private javax.swing.JCheckBox jCheckBoxPeaoOPONENTE;
    private javax.swing.JCheckBox jCheckBoxReiMEU;
    private javax.swing.JCheckBox jCheckBoxReiOPONENTE;
    private javax.swing.JCheckBox jCheckBoxTipoBispo;
    private javax.swing.JCheckBox jCheckBoxTipoCavalo;
    private javax.swing.JCheckBox jCheckBoxTipoDama;
    private javax.swing.JCheckBox jCheckBoxTipoPeao;
    private javax.swing.JCheckBox jCheckBoxTipoRei;
    private javax.swing.JCheckBox jCheckBoxTipoTorre;
    private javax.swing.JCheckBox jCheckBoxTorreMINHA;
    private javax.swing.JCheckBox jCheckBoxTorreOPONENTE;
    private javax.swing.JComboBox jComboBoxOperadorRelacional;
    private javax.swing.JComboBox jComboBoxRegiao;
    private javax.swing.JComboBox jComboBoxValorJogador;
    private javax.swing.JComboBox jComboBoxValorLogico;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelBispoMEU;
    private javax.swing.JLabel jLabelBispoOPONENTE;
    private javax.swing.JLabel jLabelCavaloMEU;
    private javax.swing.JLabel jLabelCavaloOPONENTE;
    private javax.swing.JLabel jLabelDamaMINHA;
    private javax.swing.JLabel jLabelDamaOPONENTE;
    private javax.swing.JLabel jLabelPassoFinal;
    private javax.swing.JLabel jLabelPassoInicial;
    private javax.swing.JLabel jLabelPassoInicial1;
    private javax.swing.JLabel jLabelPassoParametro;
    private javax.swing.JLabel jLabelPeaoMEU;
    private javax.swing.JLabel jLabelPeaoOPONENTE;
    private javax.swing.JLabel jLabelReiMEU;
    private javax.swing.JLabel jLabelReiOPONENTE;
    private javax.swing.JLabel jLabelTipoBispo;
    private javax.swing.JLabel jLabelTipoCavalo;
    private javax.swing.JLabel jLabelTipoDama;
    private javax.swing.JLabel jLabelTipoPeao;
    private javax.swing.JLabel jLabelTipoRei;
    private javax.swing.JLabel jLabelTipoTorre;
    private javax.swing.JLabel jLabelTorreMINHA;
    private javax.swing.JLabel jLabelTorreOPONENTE;
    private javax.swing.JPanel jPanelCentraliza;
    private javax.swing.JPanel jPanelCentraliza1;
    private javax.swing.JPanel jPanelCentraliza2;
    private javax.swing.JPanel jPanelCentraliza3;
    private javax.swing.JPanel jPanelCentraliza4;
    private javax.swing.JPanel jPanelCentraliza5;
    private javax.swing.JPanel jPanelCentraliza6;
    private javax.swing.JPanel jPanelCentralizaRelacional;
    private javax.swing.JPanel jPanelComboOpRelacional;
    private javax.swing.JPanel jPanelConectores;
    private javax.swing.JPanel jPanelEntadaDados;
    private javax.swing.JPanel jPanelFinaliza;
    private javax.swing.JPanel jPanelFuncao;
    private javax.swing.JPanel jPanelInteiro;
    private javax.swing.JPanel jPanelJogador;
    private javax.swing.JPanel jPanelLogico;
    private javax.swing.JPanel jPanelOpcoesPreenchimento;
    private javax.swing.JPanel jPanelOperadorNAO;
    private javax.swing.JPanel jPanelOperadorRelacional;
    private javax.swing.JPanel jPanelOperando1;
    private javax.swing.JPanel jPanelOperando2;
    private javax.swing.JPanel jPanelPeca;
    private javax.swing.JPanel jPanelPecasMINHAS;
    private javax.swing.JPanel jPanelPecasOPONENTE;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JPanel jPanelReal;
    private javax.swing.JPanel jPanelRegiao;
    private javax.swing.JPanel jPanelTipoPeca;
    private javax.swing.JRadioButton jRadioButtonOperadorLogicoE;
    private javax.swing.JRadioButton jRadioButtonOperadorLogicoOU;
    private javax.swing.JRadioButton jRadioButtonPreencherFuncao;
    private javax.swing.JRadioButton jRadioButtonPreencherValor;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSpinner jSpinnerValorInteiro;
    private javax.swing.JSpinner jSpinnerValorReal;
    private javax.swing.JTextArea jTextAreaCondicaoResultante;
    private javax.swing.JTextArea jTextAreaDescricaoOperadorRelacional;
    private javax.swing.JTextArea jTextAreaFuncaoPreenchidaOperando1;
    private javax.swing.JTextArea jTextAreaFuncaoPreenchidaOperando2;
    private javax.swing.JTextField jTextFieldTipoOperando2;
    // End of variables declaration//GEN-END:variables
}
