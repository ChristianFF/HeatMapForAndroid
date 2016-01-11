package com.ff.heatmap.heatmap;

/**
 * 带权值的经纬度位置点,由于不好接入GoogleMap API，点位置暂时直接用屏幕坐标代替
 */
public class WeightedLatLng {
    /**
     * 默认权值1.0
     */
    private static final double DEFAULT_INTENSITY = 1.0;

//    /**
//     * 经纬度
//     */
//    public final LatLng latLng;

    public final int x;

    public final int y;

    /**
     * 权值
     */
    public final double intensity;

    //    /**
//     * 构造函数
//     *
//     * @param latlng    地理位置
//     * @param intensity 权值，大于零；两个权值等于一的位置点等同于一个权值等于二的点
//     */
//    public WeightedLatLng(LatLng latlng, double intensity) {
//        this.latLng = latlng;
//        this.intensity = intensity;
//    }
    public WeightedLatLng(int x, int y, double intensity) {
        this.x = x;
        this.y = y;
        this.intensity = intensity;
    }

//    /**
//     * 构造函数，使用默认的权值
//     *
//     * @param latlng 地理位置
//     */
//    public WeightedLatLng(LatLng latlng) {
//        this.latLng = latlng;
//        this.intensity = DEFAULT_INTENSITY;
//    }
}
