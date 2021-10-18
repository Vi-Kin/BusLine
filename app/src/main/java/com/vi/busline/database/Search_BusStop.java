package com.vi.busline.database;

public class Search_BusStop {
    private String stop_name;
    private int position;

    public Search_BusStop(String stop_name, int position) {
        this.stop_name = stop_name;
        this.position = position;
    }

    public String getStopName() {
        return stop_name;
    }

    public int getPosition() {
        return position;
    }
}