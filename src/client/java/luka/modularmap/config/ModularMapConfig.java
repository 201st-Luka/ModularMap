package luka.modularmap.config;

import java.util.Objects;

public class ModularMapConfig implements Cloneable{
    public Color backgroundColor = new Color("0x80FFFFFF");
    public Color footerColor = new Color("0xB0FFFFFF");
    public Color headerColor = new Color("0xB0FFFFFF");
    public Color headerTextColor = new Color("0xFF808080");

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        ModularMapConfig config = (ModularMapConfig) obj;
        return backgroundColor.equals(config.backgroundColor) &&
                footerColor.equals(config.footerColor) &&
                headerColor.equals(config.headerColor) &&
                headerTextColor.equals(config.headerTextColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backgroundColor, footerColor, headerColor, headerTextColor);
    }

    @Override
    public ModularMapConfig clone() {
        try {
            ModularMapConfig clone = (ModularMapConfig) super.clone();

            clone.backgroundColor = backgroundColor.clone();
            clone.footerColor = footerColor.clone();
            clone.headerColor = headerColor.clone();
            clone.headerTextColor = headerTextColor.clone();

            return clone;

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
