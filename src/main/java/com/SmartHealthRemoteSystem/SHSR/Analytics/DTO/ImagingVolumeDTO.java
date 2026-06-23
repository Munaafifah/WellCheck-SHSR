package com.SmartHealthRemoteSystem.SHSR.Analytics.DTO;

import java.util.List;

public class ImagingVolumeDTO {

    private String       period;
    private List<String> labels;

    private List<Long>   xrayData;
    private List<Long>   ctData;
    private List<Long>   mriData;
    private List<Long>   ultrasoundData;

    private long   totalXray;
    private long   totalCT;
    private long   totalMRI;
    private long   totalUltrasound;
    private long   grandTotal;
    private double growthPercent;

    public ImagingVolumeDTO() {}

    // --- Getters ---
    public String       getPeriod()         { return period; }
    public List<String> getLabels()         { return labels; }
    public List<Long>   getXrayData()       { return xrayData; }
    public List<Long>   getCtData()         { return ctData; }
    public List<Long>   getMriData()        { return mriData; }
    public List<Long>   getUltrasoundData() { return ultrasoundData; }
    public long         getTotalXray()      { return totalXray; }
    public long         getTotalCT()        { return totalCT; }
    public long         getTotalMRI()       { return totalMRI; }
    public long         getTotalUltrasound(){ return totalUltrasound; }
    public long         getGrandTotal()     { return grandTotal; }
    public double       getGrowthPercent()  { return growthPercent; }

    // --- Setters ---
    public void setPeriod(String v)              { this.period = v; }
    public void setLabels(List<String> v)        { this.labels = v; }
    public void setXrayData(List<Long> v)        { this.xrayData = v; }
    public void setCtData(List<Long> v)          { this.ctData = v; }
    public void setMriData(List<Long> v)         { this.mriData = v; }
    public void setUltrasoundData(List<Long> v)  { this.ultrasoundData = v; }
    public void setTotalXray(long v)             { this.totalXray = v; }
    public void setTotalCT(long v)               { this.totalCT = v; }
    public void setTotalMRI(long v)              { this.totalMRI = v; }
    public void setTotalUltrasound(long v)       { this.totalUltrasound = v; }
    public void setGrandTotal(long v)            { this.grandTotal = v; }
    public void setGrowthPercent(double v)       { this.growthPercent = v; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ImagingVolumeDTO dto = new ImagingVolumeDTO();
        public Builder period(String v)             { dto.period = v;         return this; }
        public Builder labels(List<String> v)       { dto.labels = v;         return this; }
        public Builder xrayData(List<Long> v)       { dto.xrayData = v;       return this; }
        public Builder ctData(List<Long> v)         { dto.ctData = v;         return this; }
        public Builder mriData(List<Long> v)        { dto.mriData = v;        return this; }
        public Builder ultrasoundData(List<Long> v) { dto.ultrasoundData = v; return this; }
        public Builder totalXray(long v)            { dto.totalXray = v;      return this; }
        public Builder totalCT(long v)              { dto.totalCT = v;        return this; }
        public Builder totalMRI(long v)             { dto.totalMRI = v;       return this; }
        public Builder totalUltrasound(long v)      { dto.totalUltrasound = v; return this; }
        public Builder grandTotal(long v)           { dto.grandTotal = v;     return this; }
        public Builder growthPercent(double v)      { dto.growthPercent = v;  return this; }
        public ImagingVolumeDTO build()             { return dto; }
    }
}
