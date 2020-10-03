package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.competicaoheuristica.Partida;
import br.ufpr.inf.utils.UtilsDataTempo;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.*;

public class PanelRelogio {

    private Date date = new Date();
    
    private JComponent component;
    private JLabel     tempoPartida;
    private JLabel     blackCountdown;
    private JLabel     blackMovementCountdown;
    private JLabel     whiteCountdown;
    private JLabel     whiteMovementCountdown;
    
    private Partida partida;

    /**
     * Construtor chamado quando se está mostrando um tempo fixo
     */
    public PanelRelogio() {

        JPanel fond = new JPanel(new GridLayout(1, 1));
        
        tempoPartida = new JLabel();
        tempoPartida.setHorizontalAlignment(SwingConstants.CENTER);
        tempoPartida.setForeground(Color.BLACK);
        tempoPartida.setBackground(Color.WHITE);
        tempoPartida.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        tempoPartida.setOpaque(true);
        
        Font fonte = tempoPartida.getFont().deriveFont(17.5F);
        tempoPartida.setFont(fonte);

        fond.add(tempoPartida);

        component = fond;
    }
    
    /**
     * Construtor chamado para mostrar continuamente o tempo de uma partida
     */
    public PanelRelogio(Partida partida) {

        assert partida != null;

        this.partida = partida;

        JPanel fond = null;
        
        if (partida.getModalidade() == Partida.Modalidade.TEMPO_ILIMITADO) {
            
            fond = new JPanel(new GridLayout(1, 1));
            tempoPartida = new JLabel();
            tempoPartida.setHorizontalAlignment(SwingConstants.CENTER);
            tempoPartida.setForeground(Color.BLACK);
            tempoPartida.setBackground(Color.WHITE);
            tempoPartida.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
            tempoPartida.setOpaque(true);
            Font fonte = tempoPartida.getFont().deriveFont(17.5F);
            tempoPartida.setFont(fonte);
            
            setTempoJogo();
            fond.add(tempoPartida);
            
        } else {
            
            if (partida.getModoRelogio() == Partida.ModoRelogio.RELOGIO_FISCHER){
                fond = new JPanel(new GridLayout(1, 2));
                whiteCountdown = new JLabel();
                whiteCountdown.setHorizontalAlignment(SwingConstants.CENTER);
                whiteCountdown.setForeground(Color.BLACK);
                whiteCountdown.setBackground(Color.WHITE);
                whiteCountdown.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
                whiteCountdown.setOpaque(true);
                Font fonte = whiteCountdown.getFont().deriveFont(17.5F);
                whiteCountdown.setFont(fonte);

                setTempoJogador(true);
                fond.add(whiteCountdown);
            
                blackCountdown = new JLabel();
                blackCountdown.setHorizontalAlignment(SwingConstants.CENTER);
                blackCountdown.setForeground(Color.WHITE);
                blackCountdown.setBackground(Color.BLACK);
                blackCountdown.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                blackCountdown.setOpaque(true);
                blackCountdown.setFont(fonte);

                setTempoJogador(false);
                fond.add(blackCountdown);
                
            }else
                if (partida.getModoRelogio() == Partida.ModoRelogio.RELOGIO_BRONSTEIN){
                    fond = new JPanel(new GridLayout(3, 2));
                    
                    JLabel titulo1 = new JLabel("Resta Partida");
                    titulo1.setHorizontalAlignment(SwingConstants.CENTER);
                    fond.add(titulo1);
                    
                    JLabel titulo2 = new JLabel("Resta Jogada");
                    titulo2.setHorizontalAlignment(SwingConstants.CENTER);
                    fond.add(titulo2);
                    
                    whiteCountdown = new JLabel();
                    whiteCountdown.setHorizontalAlignment(SwingConstants.CENTER);
                    whiteCountdown.setForeground(Color.BLACK);
                    whiteCountdown.setBackground(Color.WHITE);
                    whiteCountdown.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
                    whiteCountdown.setOpaque(true);
                    Font fonte = whiteCountdown.getFont().deriveFont(17.5F);
                    whiteCountdown.setFont(fonte);

                    setTempoJogador(true);
                    fond.add(whiteCountdown);
                    
                    whiteMovementCountdown = new JLabel();
                    whiteMovementCountdown.setHorizontalAlignment(SwingConstants.CENTER);
                    whiteMovementCountdown.setForeground(Color.BLACK);
                    whiteMovementCountdown.setBackground(Color.WHITE);
                    whiteMovementCountdown.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
                    whiteMovementCountdown.setOpaque(true);
                    whiteMovementCountdown.setFont(fonte);        
                    
                    setTempoMovimentoJogador(true);
                    fond.add(whiteMovementCountdown);     
                    
                    blackCountdown = new JLabel();
                    blackCountdown.setHorizontalAlignment(SwingConstants.CENTER);
                    blackCountdown.setForeground(Color.WHITE);
                    blackCountdown.setBackground(Color.BLACK);
                    blackCountdown.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
                    blackCountdown.setOpaque(true);
                    blackCountdown.setFont(fonte);

                    setTempoJogador(false);
                    fond.add(blackCountdown);
                    
                    blackMovementCountdown = new JLabel();
                    blackMovementCountdown.setHorizontalAlignment(SwingConstants.CENTER);
                    blackMovementCountdown.setForeground(Color.WHITE);
                    blackMovementCountdown.setBackground(Color.BLACK);
                    blackMovementCountdown.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
                    blackMovementCountdown.setOpaque(true);
                    blackMovementCountdown.setFont(fonte);

                    setTempoMovimentoJogador(false);
                    fond.add(blackMovementCountdown);
                }
        }
        
        component = fond;

        partida.addPropertyChangeListener("timer", new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent pEvt) {
                
                assert pEvt != null;
                
                if (PanelRelogio.this.partida.getModalidade() == Partida.Modalidade.TEMPO_ILIMITADO){
                    setTempoJogo();
                }else{
                    if (PanelRelogio.this.partida.getModoRelogio() == Partida.ModoRelogio.RELOGIO_FISCHER){
                        setTempoJogador(false);
                        setTempoJogador(true);
                    }else
                        if (PanelRelogio.this.partida.getModoRelogio() == Partida.ModoRelogio.RELOGIO_BRONSTEIN){
                            setTempoJogador(false);
                            setTempoMovimentoJogador(true);
                            setTempoJogador(true);
                            setTempoMovimentoJogador(false);
                        }
                }
            }
        });
    }
    
    public JComponent getComponent() {

        assert component != null;

        return component;
    }

    public void setTexto(String texto){
        tempoPartida.setText(texto);
    }
    
    private void setTempoJogo(){
        tempoPartida.setText(UtilsDataTempo.formataTempoNanossegundos(partida.getTempoNanossegundos(), true));    
    }
    
    private void setTempoJogador(boolean jogadorBranco) {

        JLabel dst;

        long t;

        if (jogadorBranco) {
            dst = whiteCountdown;
            t = partida.getJogadorBranco().getTempoRestanteMilissegundos();
        } else {
            dst = blackCountdown;
            t = partida.getJogadorPreto().getTempoRestanteMilissegundos();
        }

        StringBuilder sb = new StringBuilder();

        if (t <= 0) {
            
            dst.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            
            if (t <= -1000) {                
                t = -t;                
                sb.append('-');
            }else{
                t = 0;
            }
        }

        date.setTime(t);
        
        sb.append(UtilsDataTempo.formataTempo(date));
        
        dst.setText(sb.toString());
    }
    
    private void setTempoMovimentoJogador(boolean jogadorBranco) {

        JLabel dst;

        long t;

        if (jogadorBranco) {
            dst = whiteMovementCountdown;
            t = partida.getJogadorBranco().getTempoRestanteMilissegundosMovimento();
        } else {
            dst = blackMovementCountdown;
            t = partida.getJogadorPreto().getTempoRestanteMilissegundosMovimento();
        }

        /*
        if (t <= 0) {
            dst.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            if (t <= -1000) {
                t = -t;
                sb.append('-');
            }else{
                t = 0;
            }
        }*/

        date.setTime(t);
        
        dst.setText(UtilsDataTempo.formataTempo(date));
    }
}