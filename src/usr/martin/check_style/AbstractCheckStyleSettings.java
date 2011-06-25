package usr.martin.check_style;

import org.eclipse.core.resources.IProject;

import usr.martin.check_style.internal.settings.PreferencesSettingsSource;
import usr.martin.check_style.internal.settings.ProjectSettingsSource;
import usr.martin.check_style.internal.settings.SettingsNames;
import usr.martin.check_style.internal.settings.SettingsSource;

/**
 * This class is responsible for the management of the
 * settings for the different style checks. The settings
 * will be stored with the project, or in the global
 * settings, while the former are used when enabled.
 */
public abstract class AbstractCheckStyleSettings 
        implements SettingsNames {

    protected final SettingsSource settingsSource;
    
    public AbstractCheckStyleSettings(IProject project_) { 
        SettingsSource preferencesSource = new PreferencesSettingsSource();
        SettingsSource projectSource = new ProjectSettingsSource(project_, preferencesSource);
        
        if (projectSource.getBoolean(USE_PROJECT_SETTINGS, false)) {
            settingsSource =  projectSource;
        } else {
            settingsSource = preferencesSource;
        }
    }
    
    public boolean useProjectSettings() {
        return (settingsSource instanceof ProjectSettingsSource) 
                && settingsSource.getBoolean(USE_PROJECT_SETTINGS, false);
    }
    
}
