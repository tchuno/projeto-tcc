package net.gnfe.bin.rest.request.vo;

import com.google.gson.Gson;

public abstract class SuperVo {

    private String warningMessage;

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
