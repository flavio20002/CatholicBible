package com.barisi.flavio.bibbiacattolica.model;


public class Bibbia {

    private String file;
    private String addOn;
    private int minimaversioneAddOn;
    private String linguaBibbia;

    public Bibbia(String file, String addOn, int minimaversioneAddOn, String linguaBibbia) {
        this.file = file;
        this.addOn = addOn;
        this.minimaversioneAddOn = minimaversioneAddOn;
        this.linguaBibbia = linguaBibbia;
    }

    public String getFile() {
        return file;
    }

    public String getAddOn() {
        return addOn;
    }


    public int getMinimaversioneAddOn() {
        return minimaversioneAddOn;
    }

    public String getLinguaBibbia() {
        return linguaBibbia;
    }

}
