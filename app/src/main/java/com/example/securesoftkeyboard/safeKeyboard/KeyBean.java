package com.example.securesoftkeyboard.safeKeyboard;

/**
 */
public class KeyBean {
    private String label;
    private int code;

    public KeyBean(String label, int code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label == null ? "" : label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
