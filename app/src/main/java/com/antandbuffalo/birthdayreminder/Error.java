package com.antandbuffalo.birthdayreminder;

/**
 * Created by i677567 on 29/12/15.
 */
public class Error {
    private String code;
    private String description;
    private Boolean status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
