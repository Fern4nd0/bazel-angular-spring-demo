package com.app.stack.trackings.domain.entities;

public class GeoPoint {
    private Double latitude;
    private Double longitude;
    private Double altitudeMeters;
    private Double accuracyMeters;
    private Double headingDegrees;
    private Double speedMps;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitudeMeters() {
        return altitudeMeters;
    }

    public void setAltitudeMeters(Double altitudeMeters) {
        this.altitudeMeters = altitudeMeters;
    }

    public Double getAccuracyMeters() {
        return accuracyMeters;
    }

    public void setAccuracyMeters(Double accuracyMeters) {
        this.accuracyMeters = accuracyMeters;
    }

    public Double getHeadingDegrees() {
        return headingDegrees;
    }

    public void setHeadingDegrees(Double headingDegrees) {
        this.headingDegrees = headingDegrees;
    }

    public Double getSpeedMps() {
        return speedMps;
    }

    public void setSpeedMps(Double speedMps) {
        this.speedMps = speedMps;
    }
}
