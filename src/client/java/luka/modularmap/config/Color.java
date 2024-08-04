package luka.modularmap.config;

public class Color implements Cloneable {
    private int value;

    public Color(int value) {
        this.value = value;
    }

    public Color(byte r, byte g, byte b, byte a) {
        this(a << 24 | r << 16 | g << 8 | b);
    }

    public Color(String hex) {
        if (hex.startsWith("#"))
            hex = hex.substring(1);
        else if (hex.startsWith("0x"))
            hex = hex.substring(2);

        this.value = Integer.parseUnsignedInt(hex, 16);
    }

    public int getValue() {
        return value;
    }

    public byte getRed() {
        return (byte) (value >> 16 & 0xFF);
    }

    public byte getGreen() {
        return (byte) (value >> 8 & 0xFF);
    }

    public byte getBlue() {
        return (byte) (value & 0xFF);
    }

    public byte getAlpha() {
        return (byte) (value >> 24 & 0xFF);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setRed(byte r) {
        value = (value & 0xFF00FFFF) | (r << 16);
    }

    public void setGreen(byte g) {
        value = (value & 0xFFFF00FF) | (g << 8);
    }

    public void setBlue(byte b) {
        value = (value & 0xFFFFFF00) | b;
    }

    public void setAlpha(byte a) {
        value = (value & 0x00FFFFFF) | (a << 24);
    }

    public void setHex(String hex) {
        if (hex.startsWith("#"))
            hex = hex.substring(1);
        else if (hex.startsWith("0x"))
            hex = hex.substring(2);

        value = Integer.parseUnsignedInt(hex, 16);
    }

    public String toHex() {
        return Integer.toHexString(value);
    }

    @Override
    public String toString() {
        return String.format("(%b, %b, %b, %b)", getRed(), getGreen(), getBlue(), getAlpha());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        Color color = (Color) obj;
        return value == color.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public Color clone() {
        try {
            return (Color) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
