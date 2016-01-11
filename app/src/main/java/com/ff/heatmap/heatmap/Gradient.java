package com.ff.heatmap.heatmap;

import android.graphics.Color;

import java.util.HashMap;

public class Gradient {

    private static final int DEFAULT_COLOR_MAP_SIZE = 1000;
    public final int colorMapSize;
    public int[] colors;
    public float[] startPoints;

    public Gradient(int[] colors, float[] startPoints) {
        this(colors, startPoints, DEFAULT_COLOR_MAP_SIZE);
    }

    public Gradient(int[] colors, float[] startPoints, int colorMapSize) {
        if (colors.length != startPoints.length) {
            throw new IllegalArgumentException("colors和startPoints数组必须具有相同的长度");
        } else if (colors.length == 0) {
            throw new IllegalArgumentException("颜色数组为空");
        }
        for (int i = 1; i < startPoints.length; i++) {
            if (startPoints[i] <= startPoints[i - 1]) {
                throw new IllegalArgumentException("startPoints数组必须为升序");
            }
        }
        this.colorMapSize = colorMapSize;
        this.colors = new int[colors.length];
        this.startPoints = new float[startPoints.length];
        System.arraycopy(colors, 0, this.colors, 0, colors.length);
        System.arraycopy(startPoints, 0, this.startPoints, 0, startPoints.length);
    }

    static int interpolateColor(int color1, int color2, float ratio) {

        int alpha = (int) ((Color.alpha(color2) - Color.alpha(color1)) * ratio + Color.alpha(color1));

        float[] hsv1 = new float[3];
        Color.RGBToHSV(Color.red(color1), Color.green(color1), Color.blue(color1), hsv1);
        float[] hsv2 = new float[3];
        Color.RGBToHSV(Color.red(color2), Color.green(color2), Color.blue(color2), hsv2);

        if (hsv1[0] - hsv2[0] > 180) {
            hsv2[0] += 360;
        } else if (hsv2[0] - hsv1[0] > 180) {
            hsv1[0] += 360;
        }

        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            result[i] = (hsv2[i] - hsv1[i]) * (ratio) + hsv1[i];
        }

        return Color.HSVToColor(alpha, result);
    }

    private HashMap<Integer, ColorInterval> generateColorIntervals() {
        HashMap<Integer, ColorInterval> colorIntervals = new HashMap<>();
        if (startPoints[0] != 0) {
            int initialColor = Color.argb(
                    0, Color.red(colors[0]), Color.green(colors[0]), Color.blue(colors[0]));
            colorIntervals.put(0, new ColorInterval(initialColor, colors[0], colorMapSize * startPoints[0]));
        }
        for (int i = 1; i < colors.length; i++) {
            colorIntervals.put(((int) (colorMapSize * startPoints[i - 1])),
                    new ColorInterval(colors[i - 1], colors[i],
                            (colorMapSize * (startPoints[i] - startPoints[i - 1]))));
        }
        if (startPoints[startPoints.length - 1] != 1) {
            int i = startPoints.length - 1;
            colorIntervals.put(((int) (colorMapSize * startPoints[i])),
                    new ColorInterval(colors[i], colors[i], colorMapSize * (1 - startPoints[i])));
        }
        return colorIntervals;
    }

    int[] generateColorMap(double opacity) {
        HashMap<Integer, ColorInterval> colorIntervals = generateColorIntervals();
        int[] colorMap = new int[colorMapSize];
        ColorInterval interval = colorIntervals.get(0);
        int start = 0;
        for (int i = 0; i < colorMapSize; i++) {
            if (colorIntervals.containsKey(i)) {
                interval = colorIntervals.get(i);
                start = i;
            }
            float ratio = (i - start) / interval.duration;
            colorMap[i] = interpolateColor(interval.colorStart, interval.colorEnd, ratio);
        }
        if (opacity != 1) {
            for (int i = 0; i < colorMapSize; i++) {
                int c = colorMap[i];
                colorMap[i] = Color.argb((int) (Color.alpha(c) * opacity),
                        Color.red(c), Color.green(c), Color.blue(c));
            }
        }

        return colorMap;
    }

    private class ColorInterval {
        private final int colorStart;
        private final int colorEnd;

        private final float duration;

        private ColorInterval(int color1, int color2, float duration) {
            this.colorStart = color1;
            this.colorEnd = color2;
            this.duration = duration;
        }
    }

}
