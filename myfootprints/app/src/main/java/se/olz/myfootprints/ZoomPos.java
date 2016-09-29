package se.olz.myfootprints;

public class ZoomPos {
    private double latitude;
    private double longitude;
    private int occurance;

    ZoomPos() {
        this.latitude = 0;
        this.longitude = 0;
        this.occurance = 1;
    }

    ZoomPos(double latidude, double longitude) {
        this.latitude = latidude;
        this.longitude = longitude;
        this.occurance = 1;
    }

    protected boolean equals(ZoomPos other) {
        return other.latitude == this.latitude && other.longitude == this.longitude;
    }

    protected boolean biggerThan(ZoomPos other) {
        if (this.latitude == other.latitude) {
            return this.longitude > other.longitude;
        }
        return this.latitude > other.latitude;
    }

    protected boolean lessThan(ZoomPos other) {
        if (this.latitude == other.latitude) {
            return this.longitude < other.longitude;
        }
        return this.latitude < other.latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getOccurance() {
        return occurance;
    }

    public void setOccurance(int occurance) {
        this.occurance = occurance;
    }
}
