package br.ufpr.inf.heuchess.telas.competicaoheuristica;

import br.ufpr.inf.heuchess.competicaoheuristica.Partida;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

public class PanelJogadas implements PropertyChangeListener {

    private final JComponent  jComponent;
    private final JEditorPane jEditorPaneJogadas;
    private final Partida        game;
    
    public PanelJogadas(final Partida game) {

        assert game != null;

        this.game = game;
        
        jEditorPaneJogadas = new JEditorPane();
        jEditorPaneJogadas.setContentType("text/html");
        jEditorPaneJogadas.setEditable(false);

        JScrollPane fond = new JScrollPane(jEditorPaneJogadas,
                                           ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                           ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        jComponent = fond;

        if (game.isPassoAPasso()){
            fond.getViewport().setPreferredSize(new Dimension(175, 265));
        }else{
            fond.getViewport().setPreferredSize(new Dimension(175, 280));
        }
        
        game.addPropertyChangeListener("state", this);
        game.addPropertyChangeListener("position", this);
    }

    public JComponent getComponent() {
        return jComponent;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent pEvt) {

        assert pEvt != null;

        final StringBuilder sb   = new StringBuilder("<html>");
        final String[]      sans = game.getStringsSAN();

        for (int i = 0; i < sans.length; i++) {

            final String debImp;
            final String finImp;

            if (i % 2 == 0) {
                debImp = "<b>";
                finImp = "</b>";
            } else {
                debImp = "";
                finImp = "";
            }

            final String debShow;
            final String finShow;

            if (i == game.getCurrentLanceIndex()) {
                debShow = "<font bgcolor=#E0E0FF><a name=\"show\">";
                finShow = "</a></font>";
            } else {
                debShow = "";
                finShow = "";
            }

            sb.append(debImp).append(debShow);
            sb.append("&nbsp;").append(sans[i]);
            sb.append(finShow).append(finImp);
        }
        sb.append("</html>");

        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        jEditorPaneJogadas.setText(sb.toString());
                        jEditorPaneJogadas.scrollToReference("show");
                    }
                });
    }
}
