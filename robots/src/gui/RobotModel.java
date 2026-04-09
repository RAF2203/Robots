package gui;

import java.util.Observable;

public class RobotModel extends Observable {
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    private static final double MIN_X = 0;
    private static final double MAX_X = 800;
    private static final double MIN_Y = 0;
    private static final double MAX_Y = 800;

    protected void onModelUpdateEvent()
    {
        double distance = distanceToTarget();
        if (distance < 0.5)
        {
            return;
        }
        double velocity = maxVelocity;
        double angleToTarget = angleToTarget();
        double angularVelocity = calculateAngularVelocity(angleToTarget);

        moveRobot(velocity, angularVelocity, 10);
        setChanged();
        notifyObservers(new PositionData(
                m_robotPositionX,
                m_robotPositionY,
                m_robotDirection,
                m_targetPositionX,
                m_targetPositionY
        ));
    }

    private double calculateAngularVelocity(double angleToTarget) {
        double angleDiff = angleToTarget - m_robotDirection;
        angleDiff = asNormalizedRadians(angleDiff);

        if (angleDiff > Math.PI) {
            angleDiff = angleDiff - 2 * Math.PI;
        }
        if (angleDiff < -Math.PI) {
            angleDiff = angleDiff + 2 * Math.PI;
        }

        if (Math.abs(angleDiff) < 0.01) {
            return 0;
        } else if (angleDiff > 0) {
            return maxAngularVelocity;
        } else {
            return -maxAngularVelocity;
        }
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

        double newX = m_robotPositionX;
        double newY = m_robotPositionY;

        if (angularVelocity == 0) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        } else {
            double radius = velocity / angularVelocity;
            newX = m_robotPositionX + radius *
                    (Math.sin(m_robotDirection + angularVelocity * duration) - Math.sin(m_robotDirection));
            newY = m_robotPositionY - radius *
                    (Math.cos(m_robotDirection + angularVelocity * duration) - Math.cos(m_robotDirection));

            if (!Double.isFinite(newX)) {
                newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
            }
            if (!Double.isFinite(newY)) {
                newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
            }
        }

        newX = applyLimits(newX, MIN_X, MAX_X);
        newY = applyLimits(newY, MIN_Y, MAX_Y);

        m_robotPositionX = newX;
        m_robotPositionY = newY;
        m_robotDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private double distanceToTarget() {
        double diffX = m_robotPositionX - m_targetPositionX;
        double diffY = m_robotPositionY - m_targetPositionY;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private double angleToTarget() {
        double diffX = m_targetPositionX - m_robotPositionX;
        double diffY = m_targetPositionY - m_robotPositionY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }

    public double getRobotPositionX() {
        return m_robotPositionX;
    }

    public double getRobotPositionY() {
        return m_robotPositionY;
    }

    public double getRobotDirection() {
        return m_robotDirection;
    }

    public int getTargetPositionX() {
        return m_targetPositionX;
    }

    public int getTargetPositionY() {
        return m_targetPositionY;
    }

    public void setTargetPosition(int x, int y) {
        this.m_targetPositionX = x;
        this.m_targetPositionY = y;

        setChanged();
        notifyObservers(new TargetData(x, y));
    }

    public record PositionData(double x, double y, double direction, int targetX, int targetY) {}

    public record TargetData(int x, int y) {}
}
