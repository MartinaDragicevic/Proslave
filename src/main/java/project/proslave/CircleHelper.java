package project.proslave;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class CircleHelper {
    public static List<Point2D> getEquallySpacedPoints(double centerX, double centerY, double radius, int numPoints) {
        List<Point2D> points = new ArrayList<>();
        double angleStep = 2 * Math.PI / numPoints;  // Ugao između tačaka
        double scaleFactor = 2.0;  // Povećan faktor skaliranja za veće razmake (prilagodi po potrebi)

        for (int i = 0; i < numPoints; i++) {
            double angle = i * angleStep;  // Trenutni ugao
            double scaledRadius = radius * scaleFactor;  // Povećaj poluprečnik
            double x = centerX + scaledRadius * Math.cos(angle);
            double y = centerY + scaledRadius * Math.sin(angle);
            points.add(new Point2D(x, y));
        }

        return points;
    }
}