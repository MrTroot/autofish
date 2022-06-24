package troy.autofish.config;

import com.google.gson.annotations.Expose;

public class Config {

    @Expose boolean isAutofishEnabled = true;
    @Expose boolean multiRod = false;
    @Expose boolean noBreak = false;
    @Expose boolean persistentMode = false;
    @Expose boolean useSoundDetection = false;
    @Expose boolean forceMPDetection = false;
    @Expose boolean useAutoturning = true;
    @Expose boolean useBoatfishing = true;
    @Expose long recastDelay = 1500;
    @Expose float autoturnAngle = 90;
    @Expose float boatfishingAngle = 180;
    @Expose String clearLagRegex = "\\[ClearLag\\] Removed [0-9]+ Entities!";
    @Expose String autoturningRegex = "(.*)sense(.*)|(.*)过度捕捞(.*)";

    public boolean isAutofishEnabled() {
        return isAutofishEnabled;
    }

    public boolean isMultiRod() {
        return multiRod;
    }

    public boolean isNoBreak() {
        return noBreak;
    }

    public boolean isPersistentMode() { return persistentMode; }

    public boolean isUseSoundDetection() {
        return useSoundDetection;
    }

    public boolean isForceMPDetection() { return forceMPDetection; }

    public boolean isUseAutoturning(){return useAutoturning;}

    public boolean isUseBoatfishing(){return useBoatfishing;}

    public long getRecastDelay() {
        return recastDelay;
    }

    public float getAutoturningAngle(){
        return autoturnAngle;
    }

    public float getBoatfishingAngle(){return boatfishingAngle;}
    public String getClearLagRegex() {
        return clearLagRegex;
    }

    public String getAutoturningRegex() {
        return autoturningRegex;
    }

    public void setAutofishEnabled(boolean autofishEnabled) { isAutofishEnabled = autofishEnabled; }

    public void setMultiRod(boolean multiRod) {
        this.multiRod = multiRod;
    }

    public void setNoBreak(boolean noBreak) {
        this.noBreak = noBreak;
    }

    public void setPersistentMode(boolean persistentMode) {
        this.persistentMode = persistentMode;
    }

    public void setUseSoundDetection(boolean useSoundDetection) {
        this.useSoundDetection = useSoundDetection;
    }

    public void setForceMPDetection(boolean forceMPDetection) { this.forceMPDetection = forceMPDetection; }

    public void setUseAutoturning(boolean useAutoturning)
    {
        this.useAutoturning = useAutoturning;
    }

    public void setUseBoatfishing(boolean useBoatfishing){this.useBoatfishing = useBoatfishing;}

    public void setRecastDelay(long recastDelay) {
        this.recastDelay = recastDelay;
    }

    public void setAutoturnAngle(float autoturnAngle) {
        this.autoturnAngle = autoturnAngle;
    }

    public void setBoatfishingAngle(float boatfishingAngle){this.boatfishingAngle = boatfishingAngle;}

    public void setClearLagRegex(String clearLagRegex) {
        this.clearLagRegex = clearLagRegex;
    }

    public void setAutoturningRegex(String autoturningRegex) {
        this.autoturningRegex = autoturningRegex;
    }

    /**
     * @return true if anything was changed
     */
    public boolean enforceConstraints() {
        boolean changed = false;
        if (recastDelay < 900) {
            recastDelay = 900;
            changed = true;
        }
        if (autoturnAngle < -180 || autoturnAngle > 180) {
            autoturnAngle = 90;
            changed = true;
        }
        if (boatfishingAngle < -180 || boatfishingAngle > 180) {
            boatfishingAngle = 0;
            changed = true;
        }
        if (clearLagRegex == null) {
            clearLagRegex = "";
            changed = true;
        }
        if (autoturningRegex == null) {
            autoturningRegex = "";
            changed = true;
        }

        return changed;
    }
}
