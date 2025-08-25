package com.team.hotelmanagementapp.utils;

public class Pagination {
    private final int current;
    private final int total;

    public Pagination(int current, int total) {
        this.current = current;
        this.total = total;
    }

    public int getCurrent() { return current; }
    public int getTotal() { return total; }
}
