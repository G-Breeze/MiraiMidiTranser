package org.miditranser.data;

public class CalculateDurationConfiguration implements Cloneable {
    public void setDivision(int division) {
        this.division = division;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }



    int division;
    double accuracy = .1;

    // rest and gap
    boolean restUseGap = !true;
    boolean restOnlyClearGapOnce = true;

    public boolean isRestUseGap() {
        return restUseGap;
    }

    public void setRestUseGap(boolean restUseGap) {
        this.restUseGap = restUseGap;
    }

    public boolean isRestOnlyClearGapOnce() {
        return restOnlyClearGapOnce;
    }

    public void setRestOnlyClearGapOnce(boolean restOnlyClearGapOnce) {
        this.restOnlyClearGapOnce = restOnlyClearGapOnce;
    }

    public boolean isRestGapUseDurationSymbolsForm() {
        return restGapUseDurationSymbolsForm;
    }

    public void setRestGapUseDurationSymbolsForm(boolean restGapUseDurationSymbolsForm) {
        this.restGapUseDurationSymbolsForm = restGapUseDurationSymbolsForm;
    }

    public boolean isRestAutoCloseGap() {
        return restAutoCloseGap;
    }

    public void setRestAutoCloseGap(boolean restAutoCloseGap) {
        this.restAutoCloseGap = restAutoCloseGap;
    }

    boolean restGapUseDurationSymbolsForm = false;
    boolean restAutoCloseGap = false;

    @Override
    public CalculateDurationConfiguration clone() {
        try {
            CalculateDurationConfiguration clone = (CalculateDurationConfiguration) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            clone.setAccuracy(accuracy);
            clone.setDivision(division);
            // rest and gap
            clone.setRestAutoCloseGap(restAutoCloseGap);
            clone.setRestUseGap(restUseGap);
            clone.setRestGapUseDurationSymbolsForm(restGapUseDurationSymbolsForm);
            clone.setRestOnlyClearGapOnce(restOnlyClearGapOnce);


            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
