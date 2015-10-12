package it.alfrescoinaction.lab.awsi.domain;


import java.util.ArrayList;
import java.util.List;

public class SearchFilters {

    private SearchFilterItem searchFilterItem1;
    private SearchFilterItem searchFilterItem2;

    public SearchFilters(){
        searchFilterItem1 = new SearchFilterItem("","","");
        searchFilterItem2 = new SearchFilterItem("","","");
    }

    public SearchFilterItem getSearchFilterItem1() {
        return searchFilterItem1;
    }

    public SearchFilterItem getSearchFilterItem2() {
        return searchFilterItem2;
    }

    public List<SearchFilterItem> getAsList() {
        List<SearchFilterItem> filterItems = new ArrayList<>();
        filterItems.add(searchFilterItem1);
        filterItems.add(searchFilterItem2);

        return filterItems;
    }

    public void setFilter1Data(String name, String id, String type) {
        searchFilterItem1.setName(name);
        searchFilterItem1.setId(id);
        searchFilterItem1.setType(type);
    }

    public void setFilter2Data(String name, String id, String type) {
        searchFilterItem2.setName(name);
        searchFilterItem2.setId(id);
        searchFilterItem2.setType(type);
    }
}
