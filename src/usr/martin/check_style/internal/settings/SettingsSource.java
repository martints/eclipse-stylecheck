package usr.martin.check_style.internal.settings;

public abstract class SettingsSource {

	public final boolean getBoolean(String key, boolean default_) {
		try {
			return Boolean.parseBoolean(getString(key));
		} catch (MissingSettingException e) {
			return default_;
		}
	}
			
	
	public abstract String getString(String key) 
			throws MissingSettingException;
	
	public abstract boolean getBoolean(String key);


	public static final class MissingSettingException 
			extends Exception {

		private static final long serialVersionUID = -5696102732535040743L;
		
	}

}
