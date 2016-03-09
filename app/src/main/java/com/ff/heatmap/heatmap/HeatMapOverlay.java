package com.ff.heatmap.heatmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import java.util.Collection;

public class HeatMapOverlay {
    private static final int DEFAULT_RADIUS = 36;
    private static final int MAX_RADIUS = 50;
    private static final int MIN_RADIUS = 10;
    private static final Gradient DEFAULT_GRADIENT = new Gradient(
            new int[]{Color.TRANSPARENT, Color.rgb(255, 191, 255), Color.rgb(255, 128, 255), Color.rgb(255, 64, 255), Color.rgb(255, 0, 255)},
            new float[]{0f, 0.25f, 0.55f, 0.85f, 1f});
    private static final double DEFAULT_OPACITY = 0.6;

    private static Bitmap backBuffer = null;
    private static int[] pixels;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private Collection<WeightedLatLng> data;
    private int radius;
    private double maxIntensity;
    private Gradient gradient;
    private double opacity;
    private int[] colorMap;

    private Canvas myCanvas;

    public static class Builder {
        private Collection<WeightedLatLng> data;
        private int width;
        private int height;

        private int radius = DEFAULT_RADIUS;
        private Gradient gradient = DEFAULT_GRADIENT;
        private double opacity = DEFAULT_OPACITY;

        public Builder() {
        }

        public Builder weightedData(Collection<WeightedLatLng> val) {
            this.data = val;
            if (this.data.isEmpty()) {
                throw new IllegalArgumentException("No input points.");
            }
            return this;
        }

        public Builder radius(int val) {
            radius = val;
            if (radius < MIN_RADIUS || radius > MAX_RADIUS) {
                throw new IllegalArgumentException("Radius not within bounds.");
            }
            return this;
        }

        /**
         * if u want generate a heat map with different colors,u need to set this
         */
        public Builder gradient(Gradient val) {
            gradient = val;
            return this;
        }

        public Builder opacity(double val) {
            opacity = val;
            if (opacity < 0 || opacity > 1) {
                throw new IllegalArgumentException("Opacity must be in range [0, 1]");
            }
            return this;
        }

        public Builder width(int val) {
            this.width = val;
            if (width <= 0) {
                throw new IllegalArgumentException("Width must be above 0");
            }
            return this;
        }

        public Builder height(int val) {
            this.height = val;
            if (height <= 0) {
                throw new IllegalArgumentException("Height must be above 0");
            }
            return this;
        }

        public HeatMapOverlay build() {
            if (data == null || width == 0 || height == 0) {
                throw new IllegalStateException("No input data: you must use .weightedData&&.width&&.height before building");
            }

            return new HeatMapOverlay(this);
        }
    }

    public HeatMapOverlay(Builder builder) {
        data = builder.data;
        screenWidth = builder.width;
        screenHeight = builder.height;
        radius = builder.radius;
        opacity = builder.opacity;
        gradient = builder.gradient;

        maxIntensity = getMaxIntensities();
        pixels = new int[screenWidth * screenHeight];
        backBuffer = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        colorMap = gradient.generateColorMap(opacity);
    }

    public void setWeightedData(Collection<WeightedLatLng> data) {
        if (this.data.isEmpty()) {
            throw new IllegalArgumentException("No input points.");
        }
        this.data = data;
        maxIntensity = getMaxIntensities();
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
                (int) (intensity / maxIntensity * 255), 0, 0, 0), Color.TRANSPARENT,
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
