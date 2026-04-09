package gui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame
{
    private static final String windowID = "GameWindow";
    private final GameVisualizer m_visualizer;
    public GameWindow(RobotModel model)
    {
        super("Игровое поле", true, true, true, true);
        m_visualizer = new GameVisualizer();
        m_visualizer.setRobotModel(model);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
    public String getWindowID() {
        return windowID;
    }
}
