package it.alfrescoinaction.lab.awsi.domain;


import java.util.ArrayList;
import java.util.List;

public class SearchFilters {

    private List<SearchFilterItem> filterItems;

    public SearchFilters(){
        filterItems = new ArrayList<>();
    }

    public List<SearchFilterItem> getFilterItems() {
        return filterItems;
    }

    public void addFilterItem(String name, String id, String type) {
        filterItems.add(new SearchFilterItem(name, id, type));
    }

}
