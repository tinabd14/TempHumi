package com.example.temphumi.Model;

public class Institution {
    private String name;
    private String accessCode;
    private double idealTemp;
    private double idealHum;
    private double idealCarbon;
    private double idealScore;
    private double insideTemp;
    private double insideHum;
    private double insideCarbon;
    private double outsideTemp;
    private double outsideHum;
    private double outsideCarbon;

    public Institution(String name, String accessCode,
                       double idealTemp, double idealHum, double idealCarbon, double idealScore,
                       double insideTemp, double insideHum, double insideCarbon,
                       double outsideTemp, double outsideHum, double outsideCarbon)
    {
        this.name = name;
        this.accessCode = accessCode;
        this.idealTemp = idealTemp;
        this.idealHum = idealHum;
        this.idealCarbon = idealCarbon;
        this.idealScore = idealScore;
        this.insideTemp = insideTemp;
        this.insideHum = insideHum;
        this.insideCarbon = insideCarbon;
        this.outsideTemp = outsideTemp;
        this.outsideHum = outsideHum;
        this.outsideCarbon = outsideCarbon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public double getIdealTemp() {
        return idealTemp;
    }

    public void setIdealTemp(double idealTemp) {
        this.idealTemp = idealTemp;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public double getIdealHum() {
        return idealHum;
    }

    public void setIdealHum(double idealHum) {
        this.idealHum = idealHum;
    }

    public double getIdealCarbon() {
        return idealCarbon;
    }

    public void setIdealCarbon(double idealCarbon) {
        this.idealCarbon = idealCarbon;
    }

    public double getIdealScore() {
        return idealScore;
    }

    public void setIdealScore(double idealScore) {
        this.idealScore = idealScore;
    }

    public double getInsideTemp() {
        return insideTemp;
    }

    public void setInsideTemp(double insideTemp) {
        this.insideTemp = insideTemp;
    }

    public double getInsideHum() {
        return insideHum;
    }

    public void setInsideHum(double insideHum) {
        this.insideHum = insideHum;
    }

    public double getInsideCarbon() {
        return insideCarbon;
    }

    public void setInsideCarbon(double insideCarbon) {
        this.insideCarbon = insideCarbon;
    }

    public double getOutsideTemp() {
        return outsideTemp;
    }

    public void setOutsideTemp(double outsideTemp) {
        this.outsideTemp = outsideTemp;
    }

    public double getOutsideHum() {
        return outsideHum;
    }

    public void setOutsideHum(double outsideHum) {
        this.outsideHum = outsideHum;
    }

    public double getOutsideCarbon() {
        return outsideCarbon;
    }

    public void setOutsideCarbon(double outsideCarbon) {
        this.outsideCarbon = outsideCarbon;
    }
}
