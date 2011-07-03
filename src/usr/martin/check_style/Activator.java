package usr.martin.check_style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import usr.martin.check_style.import_check.InnerClassImportChecker;
import usr.martin.check_style.import_check.StaticImportChecker;
import usr.martin.check_style.import_check.WildcardImportChecker;

public class Activator
        extends AbstractUIPlugin {

    public static final String PLUGIN_NAME = "usr.martin.check_style";

    private static Activator instance = null;

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
    }

    @Override
    public void stop(BundleContext context)
            throws Exception {
        super.stop(context);
        instance = null;
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return PlatformUI.getPreferenceStore();
    }

    private static final AbstractStyleCheck[] getAllStyleChecks(CheckStyleSettings settings) {
        return new AbstractStyleCheck[] {
                new StaticImportChecker(settings),
                new WildcardImportChecker(settings),
                new InnerClassImportChecker(settings),
                };
    }

    public static final List<AbstractStyleCheck> findActiveStyleCheckers(IProject project_) {
        // get the state of all checkers:
        CheckStyleSettings settings = new CheckStyleSettings(project_);

        List<AbstractStyleCheck> styleChecks_ = new ArrayList<AbstractStyleCheck>();
        for (AbstractStyleCheck check : getAllStyleChecks(settings)) {
            if (check.isEnabled()) {
                styleChecks_.add(check);
            }
        }

        return Collections.unmodifiableList(styleChecks_);
    }



}
