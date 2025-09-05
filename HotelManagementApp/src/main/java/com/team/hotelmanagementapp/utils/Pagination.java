package com.team.hotelmanagementapp.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Pagination<T> {

    private final List<T> results;
    private final int size;
    private final int current;
    private final int total;
    private final long totalElements;

    public Pagination(List<T> results, long totalElements, Map<String, String> params) {
        int size = safeParseInt(params.getOrDefault("pageSize", "10"), 10);
        int current = safeParseInt(params.getOrDefault("page", "1"), 1);

        this.results = results == null ? Collections.emptyList() : results;
        this.size = size;
        this.current = current;
        this.totalElements = totalElements;
        this.total = (int) Math.ceil((double) totalElements / size);
    }

    public Pagination(long totalElements, Map<String, String> params) {
        this(Collections.emptyList(), totalElements, params);
    }

    public int getCurrent() {
        return current;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getResults() {
        return results;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }
    
    // Add methods for template compatibility
    public int getTotalPages() {
        return total;
    }
    
    public int getCurrentPage() {
        return current;
    }
    
    public List<Integer> getPageNumbers() {
        List<Integer> pages = new ArrayList<>();
        int start = Math.max(1, current - 2);
        int end = Math.min(total, current + 2);
        
        for (int i = start; i <= end; i++) {
            pages.add(i);
        }
        return pages;
    }
    
    public boolean hasPrevious() {
        return current > 1;
    }
    
    public boolean hasNext() {
        return current < total;
    }

    private int safeParseInt(String value, int defaultValue) {
        try {
            int result = Integer.parseInt(value);
            return result > 0 ? result : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
