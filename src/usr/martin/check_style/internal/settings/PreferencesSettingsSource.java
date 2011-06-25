package usr.martin.check_style.internal.settings;

import org.eclipse.jface.preference.IPreferenceStore;

import usr.martin.check_style.Activator;

/**
 * This class extracts all informations from the global preferences
 */
public class PreferencesSettingsSource 
        extends SettingsSource {
    private IPreferenceStore preferenceStore;

    public PreferencesSettingsSource() {
        preferenceStore = Activator.getInstance().getPreferenceStore();
    }

    @Override
    public String getString(String key) 
            throws MissingSettingException {
        return preferenceStore.getString(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return preferenceStore.getBoolean(key);
    }

}
