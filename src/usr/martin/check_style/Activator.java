package usr.martin.check_style;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator
        extends AbstractUIPlugin {

    public static final String PLUGIN_NAME = "usr.martin.check_style";

    private static Activator instance = null;

    private Util util;

    public Activator() {
    }

    public static Activator getInstance() {
        return instance;
    }

    @Override
    public void start(BundleContext context)
            throws Exception {
        super.start(context);
        instance = this;
        util = new Util();
    }

    @Override
    public void stop(BundleContext context)
            throws Exception {
        super.stop(context);
        instance = null;
        util = null;
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return PlatformUI.getPreferenceStore();
    }

    public Util getUtil() {
        return util;
    }

}
