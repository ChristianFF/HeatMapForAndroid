package com.ff.heatmap.heatmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import java.util.Collection;

public class HeatMapOverlay {
    private static Bitmap backBuffer = null;
    private static int[] pixels;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private Collection<WeightedLatLng> data;
    private int radius = 12;
    private double mMaxIntensity;
    private Gradient mGradient = new Gradient(
            new int[]{Color.TRANSPARENT, Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED},
            new float[]{0f, 0.25f, 0.55f, 0.85f, 1f});
    private double mOpacity = 0.5;
    private int[] colorMap;

    private Canvas myCanvas;

    private HeatMapOverlay() {
        throw new UnsupportedOperationException("don't use this constructor!");
    }

    public HeatMapOverlay(Collection<WeightedLatLng> data, int width, int height, int radius) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.radius = radius;
        this.data = data;
        mMaxIntensity = getMaxIntensities();
        pixels = new int[screenWidth * screenHeight];
        backBuffer = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        colorMap = mGradient.generateColorMap(mOpacity);
    }


    private double getMaxIntensities() {
        double maxIntensity = 0;

        for (WeightedLatLng l : data) {
            double value = l.intensity;
            if (value > maxIntensity) maxIntensity = value;
        }
        return maxIntensity;
    }

    public Bitmap generateMap() {
        backBuffer.eraseColor(Color.TRANSPARENT);
        myCanvas = new Canvas(backBuffer);
        for (WeightedLatLng p : data) {
            drawAlphaCircle(p.x, p.y, p.intensity);
        }
        return colorize();
    }

    private void drawAlphaCircle(float x, float y, double intensity) {
        RadialGradient g = new RadialGradient(x, y, radius, Color.argb(
                (int) (intensity / mMaxIntensity * 255), 0, 0, 0), Color.TRANSPARENT,
                Shader.TileMode.CLAMP);
        Paint gp = new Paint();
        gp.setShader(g);
        myCanvas.drawCircle(x, y, radius, gp);
    }

    private Bitmap colorize() {
        pixels = new int[screenWidth * screenHeight];
        backBuffer.getPixels(pixels, 0, screenWidth, 0, 0, screenWidth, screenHeight);
        for (int i = 0; i < pixels.length; i++) {
            int alpha = pixels[i] >>> 24;
            pixels[i] = colorMap[alpha * 1000 / 256];
        }
        Bitmap tile = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        tile.setPixels(pixels, 0, screenWidth, 0, 0, screenWidth, screenHeight);
        return tile;
    }
}
