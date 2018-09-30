package me.artuto.endless;

import java.util.ResourceBundle;

/**
 * @author Artuto
 */

public enum Locale
{
    EN_US("en_US", "English (US)", "English (US)"),
    ES_MX("es_MX", "Spanish (Mexico)", "Español (México)"),
    DE_DE("de_DE", "German (Germany)", "Deutsch, (Deutschland)"),
    FR_FR("fr_FR", "French (France)", "Francais (France)");

    private java.util.Locale locale;
    private ResourceBundle bundle;
    private String code, englishName, localizedName;

    Locale(String code, String englishName, String localizedName)
    {
        this.locale = new java.util.Locale(code);
        this.bundle = ResourceBundle.getBundle("Endless", locale);
        this.code = code;
        this.englishName = englishName;
        this.localizedName = localizedName;
    }

    public java.util.Locale getLocale()
    {
        return locale;
    }

    public ResourceBundle getBundle()
    {
        return bundle;
    }

    public String getCode()
    {
        return code;
    }

    public String getEnglishName()
    {
        return englishName;
    }

    public String getLocalizedName()
    {
        return localizedName;
    }
}
