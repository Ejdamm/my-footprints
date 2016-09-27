package se.olz.myfootprints;

public class CoordinatesContainer {
    private int id;
    private long session;
    private long accessedTimestamp;
    private double latitude;
    private double longitude;
    private int occurance;

    CoordinatesContainer(
            int id,
            long session,
            long accessedTimestamp,
            double latitude,
            double longitude,
            int occurance) {
        this.id = id;
        this.session = session;
        this.accessedTimestamp = accessedTimestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.occurance = occurance;
    }

    public String toString() {
        return id + ", " + session + ", " + accessedTimestamp + ", " + latitude + ", " + longitude + ", " + occurance;
    }

    public int getId() {
        return id;
    }

    public long getSession() {
        return session;
    }

    public long getAccessedTimestamp() {
        return accessedTimestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getOccurance() {return occurance;}
}
