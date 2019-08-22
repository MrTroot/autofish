package troy.autofish.config;

import com.google.gson.annotations.Expose;

public class Config {

    @Expose boolean isAutofishEnabled = true;
    @Expose boolean multirod = false;
    @Expose boolean noBreak = false;
    @Expose boolean useSoundDetection = false;
    @Expose long recastDelay = 1500;
    @Expose String clearLagRegex = "\\[ClearLag\\] Removed [0-9]+ Entities!";

    public boolean isAutofishEnabled() {
        return isAutofishEnabled;
    }

    public boolean isMultirod() {
        return multirod;
    }

    public boolean isNoBreak() {
        return noBreak;
    }

    public boolean isUseSoundDetection() {
        return useSoundDetection;
    }

    public long getRecastDelay() {
        return recastDelay;
    }

    public String getClearLagRegex() {
        return clearLagRegex;
    }

    public void setAutofishEnabled(boolean autofishEnabled) {
        isAutofishEnabled = autofishEnabled;
    }

    public void setMultirod(boolean multirod) {
        this.multirod = multirod;
    }

    public void setNoBreak(boolean noBreak) {
        this.noBreak = noBreak;
    }

    public void setUseSoundDetection(boolean useSoundDetection) {
        this.useSoundDetection = useSoundDetection;
    }

    public void setRecastDelay(long recastDelay) {
        this.recastDelay = recastDelay;
    }

    public void setClearLagRegex(String clearLagRegex) {
        this.clearLagRegex = clearLagRegex;
    }

    /**
     * @return true if anything was changed
     */
    public boolean enforceConstraints() {
        boolean changed = false;
        if (recastDelay < 300) {
            recastDelay = 300;
            changed = true;
        }
        if (clearLagRegex == null) {
            clearLagRegex = "";
            changed = true;
        }
        return changed;
    }
}
