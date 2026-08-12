package tech.kayys.erp.kiosk.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.Objects;

/**
 * Hardware configuration for a kiosk device.
 */
public final class HardwareConfig implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final boolean hasScanner;
    private final String scannerModel;
    private final boolean hasScale;
    private final String scaleModel;
    private final boolean hasPrinter;
    private final String printerModel;
    private final boolean hasCashDrawer;
    private final String cashDrawerModel;
    private final boolean hasCardReader;
    private final String cardReaderModel;
    private final boolean hasTouchscreen;
    private final String touchscreenModel;
    private final int screenSizeInches;
    private final String screenResolution;
    private final String osVersion;
    private final String kernelVersion;
    private final String hardwareId;
    private final String macAddress;
    private final String serialNumber;

    public HardwareConfig(
            boolean hasScanner,
            String scannerModel,
            boolean hasScale,
            String scaleModel,
            boolean hasPrinter,
            String printerModel,
            boolean hasCashDrawer,
            String cashDrawerModel,
            boolean hasCardReader,
            String cardReaderModel,
            boolean hasTouchscreen,
            String touchscreenModel,
            int screenSizeInches,
            String screenResolution,
            String osVersion,
            String kernelVersion,
            String hardwareId,
            String macAddress,
            String serialNumber) {
        this.hasScanner = hasScanner;
        this.scannerModel = scannerModel;
        this.hasScale = hasScale;
        this.scaleModel = scaleModel;
        this.hasPrinter = hasPrinter;
        this.printerModel = printerModel;
        this.hasCashDrawer = hasCashDrawer;
        this.cashDrawerModel = cashDrawerModel;
        this.hasCardReader = hasCardReader;
        this.cardReaderModel = cardReaderModel;
        this.hasTouchscreen = hasTouchscreen;
        this.touchscreenModel = touchscreenModel;
        this.screenSizeInches = screenSizeInches;
        this.screenResolution = screenResolution;
        this.osVersion = osVersion;
        this.kernelVersion = kernelVersion;
        this.hardwareId = hardwareId;
        this.macAddress = macAddress;
        this.serialNumber = serialNumber;
    }

    // Getters
    public boolean isHasScanner() { return hasScanner; }
    public String getScannerModel() { return scannerModel; }
    public boolean isHasScale() { return hasScale; }
    public String getScaleModel() { return scaleModel; }
    public boolean isHasPrinter() { return hasPrinter; }
    public String getPrinterModel() { return printerModel; }
    public boolean isHasCashDrawer() { return hasCashDrawer; }
    public String getCashDrawerModel() { return cashDrawerModel; }
    public boolean isHasCardReader() { return hasCardReader; }
    public String getCardReaderModel() { return cardReaderModel; }
    public boolean isHasTouchscreen() { return hasTouchscreen; }
    public String getTouchscreenModel() { return touchscreenModel; }
    public int getScreenSizeInches() { return screenSizeInches; }
    public String getScreenResolution() { return screenResolution; }
    public String getOsVersion() { return osVersion; }
    public String getKernelVersion() { return kernelVersion; }
    public String getHardwareId() { return hardwareId; }
    public String getMacAddress() { return macAddress; }
    public String getSerialNumber() { return serialNumber; }

    public boolean isFullyEquipped() {
        return hasScanner && hasScale && hasPrinter && 
               hasCashDrawer && hasCardReader && hasTouchscreen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HardwareConfig that = (HardwareConfig) o;
        return Objects.equals(hardwareId, that.hardwareId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hardwareId);
    }

    @Override
    public String toString() {
        return "HardwareConfig{" +
                "hasScanner=" + hasScanner +
                ", hasScale=" + hasScale +
                ", hasPrinter=" + hasPrinter +
                ", hasTouchscreen=" + hasTouchscreen +
                ", screenSizeInches=" + screenSizeInches +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean hasScanner = true;
        private String scannerModel;
        private boolean hasScale = true;
        private String scaleModel;
        private boolean hasPrinter = true;
        private String printerModel;
        private boolean hasCashDrawer = true;
        private String cashDrawerModel;
        private boolean hasCardReader = true;
        private String cardReaderModel;
        private boolean hasTouchscreen = true;
        private String touchscreenModel;
        private int screenSizeInches = 22;
        private String screenResolution = "1920x1080";
        private String osVersion;
        private String kernelVersion;
        private String hardwareId;
        private String macAddress;
        private String serialNumber;

        public Builder hasScanner(boolean hasScanner) {
            this.hasScanner = hasScanner;
            return this;
        }

        public Builder scannerModel(String scannerModel) {
            this.scannerModel = scannerModel;
            return this;
        }

        public Builder hasScale(boolean hasScale) {
            this.hasScale = hasScale;
            return this;
        }

        public Builder scaleModel(String scaleModel) {
            this.scaleModel = scaleModel;
            return this;
        }

        public Builder hasPrinter(boolean hasPrinter) {
            this.hasPrinter = hasPrinter;
            return this;
        }

        public Builder printerModel(String printerModel) {
            this.printerModel = printerModel;
            return this;
        }

        public Builder hasCashDrawer(boolean hasCashDrawer) {
            this.hasCashDrawer = hasCashDrawer;
            return this;
        }

        public Builder cashDrawerModel(String cashDrawerModel) {
            this.cashDrawerModel = cashDrawerModel;
            return this;
        }

        public Builder hasCardReader(boolean hasCardReader) {
            this.hasCardReader = hasCardReader;
            return this;
        }

        public Builder cardReaderModel(String cardReaderModel) {
            this.cardReaderModel = cardReaderModel;
            return this;
        }

        public Builder hasTouchscreen(boolean hasTouchscreen) {
            this.hasTouchscreen = hasTouchscreen;
            return this;
        }

        public Builder touchscreenModel(String touchscreenModel) {
            this.touchscreenModel = touchscreenModel;
            return this;
        }

        public Builder screenSizeInches(int screenSizeInches) {
            this.screenSizeInches = screenSizeInches;
            return this;
        }

        public Builder screenResolution(String screenResolution) {
            this.screenResolution = screenResolution;
            return this;
        }

        public Builder osVersion(String osVersion) {
            this.osVersion = osVersion;
            return this;
        }

        public Builder kernelVersion(String kernelVersion) {
            this.kernelVersion = kernelVersion;
            return this;
        }

        public Builder hardwareId(String hardwareId) {
            this.hardwareId = hardwareId;
            return this;
        }

        public Builder macAddress(String macAddress) {
            this.macAddress = macAddress;
            return this;
        }

        public Builder serialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        public HardwareConfig build() {
            return new HardwareConfig(
                hasScanner, scannerModel, hasScale, scaleModel,
                hasPrinter, printerModel, hasCashDrawer, cashDrawerModel,
                hasCardReader, cardReaderModel, hasTouchscreen, touchscreenModel,
                screenSizeInches, screenResolution, osVersion, kernelVersion,
                hardwareId, macAddress, serialNumber
            );
        }
    }
}