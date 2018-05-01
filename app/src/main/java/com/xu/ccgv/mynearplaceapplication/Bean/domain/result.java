package com.xu.ccgv.mynearplaceapplication.Bean.domain;

public class result {
    private String icon;
    private String name;
    private String vicinity;
    private geometry geometry;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public com.xu.ccgv.mynearplaceapplication.Bean.domain.geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(com.xu.ccgv.mynearplaceapplication.Bean.domain.geometry geometry) {
        this.geometry = geometry;
    }
}
