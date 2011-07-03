package usr.martin.check_style;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;

/**
 * This class describes a basic style check
 */
public abstract class AbstractStyleCheck {

    public abstract boolean isEnabled();

    public abstract void process(
            ICompilationUnit compilationUnit,
            ProblemFactory problemFactory
            ) throws JavaModelException;

    /**
     * Check if this Style-Check has any solutions for the problem presented.
     */
    @SuppressWarnings("unused")
    public boolean hasResolutionsFor(IMarker marker, ICompilationUnit cu) {
        return false;
    }

}
