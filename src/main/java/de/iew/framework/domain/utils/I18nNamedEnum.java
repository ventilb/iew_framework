package de.iew.framework.domain.utils;

/**
 * Specifies an interface to declare an Enum as "named". This means the Enum has a name an can be used as a value list
 * in the frontend.
 * <p>
 * This interface should be used to resolve the Enum name through a message bundle.
 * </p>
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 23.02.13 - 23:24
 */
public interface I18nNamedEnum {

    /**
     * Gets the name message key.
     *
     * @return the name message key
     */
    public String getNameKey();
}
