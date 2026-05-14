package com.SmartHealthRemoteSystem.SHSR.RadiologyReport.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UpdateReportStatusRequest {

    @NotBlank(message = "status must not be blank")
    @Pattern(regexp = "Draft|Finalized", message = "status must be 'Draft' or 'Finalized'")
    private String status;

    public String getStatus()            { return status; }
    public void   setStatus(String status) { this.status = status; }
}
