package org.feup.apm.aircarelocal;

public class AirQualityCalculator {

    private static final double[] PM25_THRESHOLDS = {12.0, 35.0, 50.0};
    private static final double[] PM10_THRESHOLDS = {54.0, 254.0, 354.0};
    private static final double[] CO2_THRESHOLDS = {800.0, 1200.0, 2000.0};
    private static final double[] VOC_THRESHOLDS = {0.4, 2.2, 30};

    public enum AirQualityCategory {
        GOOD,
        MEDIUM,
        BAD,
        VERY_BAD
    }


    public static AirQualityCategory getCategory(double measure, double[] thresholds) {
        for (int i = 0; i < thresholds.length; i++) {
            if (measure <= thresholds[i]) {
                return AirQualityCategory.values()[i];
            }
        }
        return AirQualityCategory.values()[thresholds.length];
    }


    public static AirQualityCategory getCategoryForPM25(double pm25) {
        return getCategory(pm25, PM25_THRESHOLDS);
    }
    public static AirQualityCategory getCategoryForPM10(double pm10) {
        return getCategory(pm10, PM10_THRESHOLDS);
    }
    public static AirQualityCategory getCategoryForCO2(double co2) {
        return getCategory(co2, CO2_THRESHOLDS);
    }
    public static AirQualityCategory getCategoryForVOC(double voc) {
        return getCategory(voc, VOC_THRESHOLDS);
    }

    public static AirQualityCategory getOverallCategory(AirQualityCategory pm25Category, AirQualityCategory pm10Category, AirQualityCategory co2Category, AirQualityCategory vocCategory) {
        if (pm25Category.ordinal() == 3 || pm10Category.ordinal() == 3 || co2Category.ordinal() == 3 || vocCategory.ordinal() == 3) {
            return AirQualityCategory.VERY_BAD;
        } else if (pm25Category.ordinal() == 2 || pm10Category.ordinal() == 2 || co2Category.ordinal() == 2 || vocCategory.ordinal() == 2) {
            return AirQualityCategory.BAD;
        } else if (pm25Category.ordinal() + pm10Category.ordinal() + co2Category.ordinal() + vocCategory.ordinal() == 2 || pm25Category.ordinal() + pm10Category.ordinal() + co2Category.ordinal() + vocCategory.ordinal() == 3) {
            return AirQualityCategory.MEDIUM;
        }else{
            return AirQualityCategory.GOOD;
        }
    }

    public static AirQualityResult getAirQualityResult(double pm25, double pm10, double co2, double voc) {
        AirQualityCategory pm25Category = getCategoryForPM25(pm25);
        AirQualityCategory pm10Category = getCategoryForPM10(pm10);
        AirQualityCategory co2Category = getCategoryForCO2(co2);
        AirQualityCategory vocCategory = getCategoryForVOC(voc);
        AirQualityCategory overallCategory = getOverallCategory(pm25Category,pm10Category,co2Category,vocCategory);


        return new AirQualityResult(pm25Category, pm10Category, co2Category, vocCategory, overallCategory);
    }

    public static class AirQualityResult {
        private final AirQualityCategory pm25Category;
        private final AirQualityCategory pm10Category;
        private final AirQualityCategory co2Category;
        private final AirQualityCategory vocCategory;

        private final AirQualityCategory overallCategory;


        public AirQualityResult(AirQualityCategory pm25Category, AirQualityCategory pm10Category, AirQualityCategory co2Category, AirQualityCategory vocCategory, AirQualityCategory overallCategory) {
            this.pm25Category = pm25Category;
            this.pm10Category = pm10Category;
            this.co2Category = co2Category;
            this.vocCategory = vocCategory;
            this.overallCategory = overallCategory;
        }

        public AirQualityCategory getPm25Category() {
            return pm25Category;
        }

        public AirQualityCategory getPm10Category() {
            return pm10Category;
        }

        public AirQualityCategory getCo2Category() {
            return co2Category;
        }

        public AirQualityCategory getVocCategory() {
            return vocCategory;
        }

        public AirQualityCategory getOverallCategory(){
            return overallCategory;
        }

    }


}
