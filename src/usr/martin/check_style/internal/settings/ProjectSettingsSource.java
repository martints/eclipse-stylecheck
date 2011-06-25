package usr.martin.check_style.internal.settings;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import usr.martin.check_style.Activator;


/**
 * This class describes settings stored in the project. 
 * It will fall back to global settings, when the local ones 
 * are not available.
 */
public class ProjectSettingsSource 
        extends SettingsSource {
    
    private final IProject project;
    
    private final SettingsSource defaultSource;

    public ProjectSettingsSource(IProject project_, SettingsSource defaultSource_) {
        project = project_;
        defaultSource = defaultSource_;
        
    }

    @Override
    public boolean getBoolean(String key) {
        try {
            return Boolean.parseBoolean(getString(key));
        } catch (MissingSettingException e) {
            return defaultSource.getBoolean(key);
        }
    }

    @Override
    public String getString(String key) 
            throws MissingSettingException {
        String result;
        try {
            result = project.getPersistentProperty(
                    new QualifiedName(Activator.PLUGIN_NAME, key)
                    );
        } catch (CoreException e) {
            result = null;
        }
        if (result == null) {
            throw new MissingSettingException();
        } else {
            return result;
        }
    }

}
