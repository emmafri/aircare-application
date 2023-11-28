package org.feup.apm.aircarelocal;

public class AirQualityCalculator {
    private float co2, voc, pm10, pm25;

    public AirQualityCalculator(float co2, float voc, float pm10, float pm25) {
        this.co2 = co2;
        this.voc = voc;
        this.pm10 = pm10;
        this.pm25 = pm25;

    }

    public float index() {

        //algorithm to calculate score/index

        return 0;
    }

}
