package com.xu.ccgv.mynearplaceapplication.Bean.domain;

import java.util.List;

public class MyObject {
    private List<String> html_attributions;
    private String next_page_token = "";
    private List<result> results;

    public List<String> getHtml_attributions() {
        return html_attributions;
    }

    public void setHtml_attributions(List<String> html_attributions) {
        this.html_attributions = html_attributions;
    }

    public String getNext_page_token() {
        return next_page_token;
    }

    public void setNext_page_token(String next_page_token) {
        this.next_page_token = next_page_token;
    }

    public List<result> getResults() {
        return results;
    }

    public void setResults(List<result> results) {
        this.results = results;
    }
}
