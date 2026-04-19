package com.SmartHealthRemoteSystem.SHSR.Radiology.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class ImageShareRequestDTO {

    @NotBlank(message = "sharedBy must not be blank")
    private String sharedBy;

    // Token lifetime in hours — minimum 1, defaults to 24
    @Min(value = 1, message = "expirationHours must be at least 1")
    private int expirationHours = 24;

    public String getSharedBy()          { return sharedBy; }
    public int getExpirationHours()      { return expirationHours; }

    public void setSharedBy(String sharedBy)          { this.sharedBy = sharedBy; }
    public void setExpirationHours(int expirationHours) { this.expirationHours = expirationHours; }
}
