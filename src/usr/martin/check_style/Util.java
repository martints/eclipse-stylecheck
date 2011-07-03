package usr.martin.check_style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;

import usr.martin.check_style.import_check.InnerClassImportChecker;
import usr.martin.check_style.import_check.StaticImportChecker;
import usr.martin.check_style.import_check.WildcardImportChecker;

public class Util {

    private final AbstractStyleCheck[] getAllStyleChecks(CheckStyleSettings settings) {
        return new AbstractStyleCheck[] {
                new StaticImportChecker(settings),
                new WildcardImportChecker(settings),
                new InnerClassImportChecker(settings),
                };
    }

    public final List<AbstractStyleCheck> findActiveStyleCheckers(IProject project_) {
        // FIXME: This should actually cache the active checkers ...

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
