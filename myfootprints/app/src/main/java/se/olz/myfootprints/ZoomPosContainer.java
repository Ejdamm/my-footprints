package se.olz.myfootprints;

import java.util.ArrayList;

import static java.lang.String.valueOf;

public class ZoomPosContainer {
    public static final String TAG = ZoomPosContainer.class.getSimpleName();
    public ArrayList<ZoomPos> zoomedPositions;
    private int zoomLevel;

    ZoomPosContainer(ArrayList<RawPosition> rawPositions, float zoomLevel, long session) {
        this.zoomLevel = (int)zoomLevel;
        zoomedPositions = new ArrayList<>();
        if (rawPositions.size() > 0) {
            if (session < 0) {  //session is -1 if all positions are to be shown
                insert(rawPositions);
            } else insert(rawPositions, session);
            quickSort(0, zoomedPositions.size()-1);
            mergeEquals();
            defrag();
        }
    }

    private void insert(ArrayList<RawPosition> rawPositions) {
        double latitude, longitude;
        for (int i = 0; i < rawPositions.size(); i++) {
            latitude = roundDown(rawPositions.get(i).getLatitude());
            longitude = roundDown(rawPositions.get(i).getLongitude());
            ZoomPos zoomPos = new ZoomPos(latitude, longitude);
            zoomedPositions.add(zoomPos);
        }
    }

    private void insert(ArrayList<RawPosition> rawPositions, long session) {
        double latitude, longitude;
        for (int i = 0; i < rawPositions.size(); i++) {
            if (rawPositions.get(i).getSession() == session) {
                latitude = roundDown(rawPositions.get(i).getLatitude());
                longitude = roundDown(rawPositions.get(i).getLongitude());
                ZoomPos zoomPos = new ZoomPos(latitude, longitude);
                zoomedPositions.add(zoomPos);
            }
        }
    }

    private void mergeEquals() {
        for (int i = 0; i < zoomedPositions.size()-1; i++) {
            if (zoomedPositions.get(i).equals(zoomedPositions.get(i+1))) {
                zoomedPositions.get(i+1).setOccurance(zoomedPositions.get(i+1).getOccurance()+ zoomedPositions.get(i).getOccurance());
                zoomedPositions.set(i, null);
            }
        }
    }

    private void defrag() {
        for (int i = 0; i < zoomedPositions.size(); i++) {
            while (i < zoomedPositions.size() && zoomedPositions.get(i) == null) {
                zoomedPositions.set(i, zoomedPositions.get(zoomedPositions.size()-1));
                zoomedPositions.remove(zoomedPositions.size()-1);
            }
        }
    }

    private void swap(int i, int j) {
        ZoomPos tmp;
        tmp = zoomedPositions.get(i);
        zoomedPositions.set(i, zoomedPositions.get(j));
        zoomedPositions.set(j, tmp);
    }

    private int partition(int left, int right)
    {
        ZoomPos pivot = zoomedPositions.get((left+right)/2);
        while (left <= right)
        {
            while(zoomedPositions.get(left).lessThan(pivot))
                left++;
            while(zoomedPositions.get(right).biggerThan(pivot))
                right--;
            if (left <= right)
            {
                swap(left, right);
                left++;
                right--;
            }
        }
        return left;
    }

    private void quickSort(int left, int right)
    {
        if (left < right)
        {
            int index = partition(left, right);
            quickSort(left, index - 1);
            quickSort(index, right);
        }
    }

    private double roundDown(double d) {
        double rounded;
        switch (zoomLevel) {
            case 8:
                rounded = (long) (d * 1e2) / 1e2;
                break;
            case 9:
                rounded = (long) (d * 1e3) / 1e3;
                break;
            case 12:
                rounded = (long) (d * 1e4) / 1e4;
                break;
            case 16:
                rounded = (long) (d * 1e5) / 1e5;
                break;
            default:  //4 decimals gives an accuracy of 11 meter
                rounded = (long) (d * 1e4) / 1e4;
                break;
        }
        return rounded;
    }

    public ZoomPos get(int index) {
        return zoomedPositions.get(index);
    }

}
