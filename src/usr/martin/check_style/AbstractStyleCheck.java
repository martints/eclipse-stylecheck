package usr.martin.check_style;

import java.util.Collection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IMarkerResolution;

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
     *
     * This method is intended to be overwritten by subclasses.
     * Its implementation should be rather cheap, as it may be called often.
     */
    @SuppressWarnings("unused")
    public boolean hasResolutionsFor(IMarker marker, ICompilationUnit cu) {
        return false;
    }

    /**
     * Add all resolutions for the marker given to the resolutions array
     *
     * This method is intended to be overwritten by subclasses
     */
    @SuppressWarnings("unused")
    public void getResolutionsFor(
            IMarker marker, ICompilationUnit cu, Collection<? extends IMarkerResolution> resolutions
            ) {
    }

}
