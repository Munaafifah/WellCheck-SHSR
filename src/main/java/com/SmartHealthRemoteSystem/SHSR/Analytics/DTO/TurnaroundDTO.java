package com.SmartHealthRemoteSystem.SHSR.Analytics.DTO;

import java.util.List;

public class TurnaroundDTO {

    // Appointment turnaround: request created → appointment scheduled
    private double avgAppointmentTurnaroundDays;
    private double medianAppointmentTurnaroundDays;
    private double fastestAppointmentTurnaroundDays;
    private double slowestAppointmentTurnaroundDays;

    // Report turnaround: request created → report finalized
    private double avgReportTurnaroundDays;
    private double medianReportTurnaroundDays;
    private double fastestReportTurnaroundDays;
    private double slowestReportTurnaroundDays;

    // Monthly trend data for the line chart
    private List<String> monthLabels;
    private List<Double> appointmentTurnaroundByMonth;
    private List<Double> reportTurnaroundByMonth;

    public TurnaroundDTO() {}

    // --- Getters ---
    public double       getAvgAppointmentTurnaroundDays()     { return avgAppointmentTurnaroundDays; }
    public double       getMedianAppointmentTurnaroundDays()  { return medianAppointmentTurnaroundDays; }
    public double       getFastestAppointmentTurnaroundDays() { return fastestAppointmentTurnaroundDays; }
    public double       getSlowestAppointmentTurnaroundDays() { return slowestAppointmentTurnaroundDays; }
    public double       getAvgReportTurnaroundDays()          { return avgReportTurnaroundDays; }
    public double       getMedianReportTurnaroundDays()       { return medianReportTurnaroundDays; }
    public double       getFastestReportTurnaroundDays()      { return fastestReportTurnaroundDays; }
    public double       getSlowestReportTurnaroundDays()      { return slowestReportTurnaroundDays; }
    public List<String> getMonthLabels()                      { return monthLabels; }
    public List<Double> getAppointmentTurnaroundByMonth()     { return appointmentTurnaroundByMonth; }
    public List<Double> getReportTurnaroundByMonth()          { return reportTurnaroundByMonth; }

    // --- Setters ---
    public void setAvgAppointmentTurnaroundDays(double v)    { this.avgAppointmentTurnaroundDays = v; }
    public void setMedianAppointmentTurnaroundDays(double v) { this.medianAppointmentTurnaroundDays = v; }
    public void setFastestAppointmentTurnaroundDays(double v){ this.fastestAppointmentTurnaroundDays = v; }
    public void setSlowestAppointmentTurnaroundDays(double v){ this.slowestAppointmentTurnaroundDays = v; }
    public void setAvgReportTurnaroundDays(double v)         { this.avgReportTurnaroundDays = v; }
    public void setMedianReportTurnaroundDays(double v)      { this.medianReportTurnaroundDays = v; }
    public void setFastestReportTurnaroundDays(double v)     { this.fastestReportTurnaroundDays = v; }
    public void setSlowestReportTurnaroundDays(double v)     { this.slowestReportTurnaroundDays = v; }
    public void setMonthLabels(List<String> v)               { this.monthLabels = v; }
    public void setAppointmentTurnaroundByMonth(List<Double> v){ this.appointmentTurnaroundByMonth = v; }
    public void setReportTurnaroundByMonth(List<Double> v)   { this.reportTurnaroundByMonth = v; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final TurnaroundDTO dto = new TurnaroundDTO();
        public Builder avgAppointmentTurnaroundDays(double v)    { dto.avgAppointmentTurnaroundDays = v;    return this; }
        public Builder medianAppointmentTurnaroundDays(double v) { dto.medianAppointmentTurnaroundDays = v; return this; }
        public Builder fastestAppointmentTurnaroundDays(double v){ dto.fastestAppointmentTurnaroundDays = v; return this; }
        public Builder slowestAppointmentTurnaroundDays(double v){ dto.slowestAppointmentTurnaroundDays = v; return this; }
        public Builder avgReportTurnaroundDays(double v)         { dto.avgReportTurnaroundDays = v;         return this; }
        public Builder medianReportTurnaroundDays(double v)      { dto.medianReportTurnaroundDays = v;      return this; }
        public Builder fastestReportTurnaroundDays(double v)     { dto.fastestReportTurnaroundDays = v;     return this; }
        public Builder slowestReportTurnaroundDays(double v)     { dto.slowestReportTurnaroundDays = v;     return this; }
        public Builder monthLabels(List<String> v)               { dto.monthLabels = v;                     return this; }
        public Builder appointmentTurnaroundByMonth(List<Double> v){ dto.appointmentTurnaroundByMonth = v;  return this; }
        public Builder reportTurnaroundByMonth(List<Double> v)   { dto.reportTurnaroundByMonth = v;         return this; }
        public TurnaroundDTO build()                             { return dto; }
    }
}
