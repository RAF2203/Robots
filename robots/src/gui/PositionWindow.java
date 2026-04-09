package gui;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public class PositionWindow extends JInternalFrame implements Observer
{
    private static final String windowID = "PositionWindow";
    private final JLabel label;

    public PositionWindow(){
        super("Координаты робота", true, true, true, true);
        label = new JLabel("X: 100, Y: 100, Направление: 0.0");
        add(label);
    }
    public void setRobotModel(RobotModel model) {
        model.addObserver(this);
    }


    public String getWindowID() {
        return windowID;
    }

    private void updateCoordinates(double x, double y, double direction) {
        SwingUtilities.invokeLater(() -> {
            label.setText(String.format(
                    "X: %.2f, Y: %.2f, Направление: %.1f",
                    x, y, Math.toDegrees(direction)
            ));
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof RobotModel.PositionData) {
            RobotModel.PositionData data = (RobotModel.PositionData) arg;
            updateCoordinates(data.x(), data.y(), data.direction());
        }
    }
}
