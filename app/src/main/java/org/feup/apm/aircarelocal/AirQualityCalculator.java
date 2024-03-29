package org.feup.apm.aircarelocal;

public class AirQualityCalculator {

    // Thresholds for different air quality parameters
    private static final double[] PM25_THRESHOLDS = {12.0, 35.0, 50.0};
    private static final double[] PM10_THRESHOLDS = {54, 254, 354};
    private static final double[] CO_THRESHOLDS = {35, 87, 200};
    private static final double[] VOC_THRESHOLDS = {400, 2200, 30000};

    // Enum representing air quality categories
    public enum AirQualityCategory {
        GOOD,
        MEDIUM,
        BAD,
        VERY_BAD
    }

    // Method to determine the air quality category based on a measure and predefined thresholds
    public static AirQualityCategory getCategory(double measure, double[] thresholds) {
        for (int i = 0; i < thresholds.length; i++) {
            if (measure <= thresholds[i]) {
                return AirQualityCategory.values()[i];
            }
        }
        return AirQualityCategory.values()[thresholds.length];
    }

    // Methods to get air quality category for specific parameters (PM2.5, PM10, CO, VOC)
    public static AirQualityCategory getCategoryForPM25(double pm25) {
        return getCategory(pm25, PM25_THRESHOLDS);
    }
    public static AirQualityCategory getCategoryForPM10(double pm10) {
        return getCategory(pm10, PM10_THRESHOLDS);
    }
    public static AirQualityCategory getCategoryForCO(double co) {
        return getCategory(co, CO_THRESHOLDS);
    }
    public static AirQualityCategory getCategoryForVOC(double voc) {
        return getCategory(voc, VOC_THRESHOLDS);
    }

    // Method to get the overall air quality category based on individual categories
    public static AirQualityCategory getOverallCategory(AirQualityCategory pm25Category, AirQualityCategory pm10Category, AirQualityCategory coCategory, AirQualityCategory vocCategory) {
        if (pm25Category.ordinal() == 3 || pm10Category.ordinal() == 3 || coCategory.ordinal() == 3 || vocCategory.ordinal() == 3) {
            return AirQualityCategory.VERY_BAD;
        } else if (pm25Category.ordinal() == 2 || pm10Category.ordinal() == 2 || coCategory.ordinal() == 2 || vocCategory.ordinal() == 2) {
            return AirQualityCategory.BAD;
        } else if (pm25Category.ordinal() + pm10Category.ordinal() + coCategory.ordinal() + vocCategory.ordinal() == 2 || pm25Category.ordinal() + pm10Category.ordinal() + coCategory.ordinal() + vocCategory.ordinal() == 3) {
            return AirQualityCategory.MEDIUM;
        }else{
            return AirQualityCategory.GOOD;
        }
    }

    // Method to get air quality result based on individual parameter values
    public static AirQualityResult getAirQualityResult(double pm25, double pm10, double co, double voc) {
        AirQualityCategory pm25Category = getCategoryForPM25(pm25);
        AirQualityCategory pm10Category = getCategoryForPM10(pm10);
        AirQualityCategory coCategory = getCategoryForCO(co);
        AirQualityCategory vocCategory = getCategoryForVOC(voc);
        AirQualityCategory overallCategory = getOverallCategory(pm25Category,pm10Category,coCategory,vocCategory);

        return new AirQualityResult(pm25Category, pm10Category, coCategory, vocCategory, overallCategory);
    }

    // Class representing the result of air quality calculations
    public static class AirQualityResult {
        private final AirQualityCategory pm25Category;
        private final AirQualityCategory pm10Category;
        private final AirQualityCategory coCategory;
        private final AirQualityCategory vocCategory;
        private final AirQualityCategory overallCategory;

        // Constructor for creating an air quality result
        public AirQualityResult(AirQualityCategory pm25Category, AirQualityCategory pm10Category, AirQualityCategory coCategory, AirQualityCategory vocCategory, AirQualityCategory overallCategory) {
            this.pm25Category = pm25Category;
            this.pm10Category = pm10Category;
            this.coCategory = coCategory;
            this.vocCategory = vocCategory;
            this.overallCategory = overallCategory;
        }

        // Getter methods for individual and overall air quality categories
        public AirQualityCategory getPm25Category() {
            return pm25Category;
        }

        public AirQualityCategory getPm10Category() {
            return pm10Category;
        }

        public AirQualityCategory getCoCategory() {
            return coCategory;
        }

        public AirQualityCategory getVocCategory() {
            return vocCategory;
        }

        public AirQualityCategory getOverallCategory(){
            return overallCategory;
        }

    }
}
