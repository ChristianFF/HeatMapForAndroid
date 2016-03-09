package com.ff.heatmap.heatmap;

/**
 * Point coordinates of the LatLng and the intensity
 */
public class WeightedLatLng {

    /**
     * default intensity
     */
    private static final double DEFAULT_INTENSITY = 1.0;

//    /**
//     * latitude and longitude
//     */
//    public final LatLng latLng;

    public final int x;

    public final int y;

    /**
     * intensity must be over zero
     */
    public final double intensity;

//
//    public WeightedLatLng(LatLng latlng, double intensity) {
//        this.latLng = latlng;
//        if (intensity < 0) {
//            throw new IllegalStateException("Intensity must be over zero!");
//        }
//        this.intensity = intensity;
//    }

//    public WeightedLatLng(LatLng latlng) {
//        this.latLng = latlng;
//        this.intensity = DEFAULT_INTENSITY;
//    }

    //I don't have points of geo-coordinates,so I use points of screen coordinates instead
    //If you want generate a heat map use LatLng data,you should use the constructor above this
    //And you should transform LatLng into screen coordinates when generate the heat map
    public WeightedLatLng(int x, int y, double intensity) {
        this.x = x;
        this.y = y;
        if (intensity < 0) {
            throw new IllegalStateException("Intensity must be over zero!");
        }
        this.intensity = intensity;
    }
}
