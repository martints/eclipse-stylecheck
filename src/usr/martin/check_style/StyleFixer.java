package usr.martin.check_style;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

public class StyleFixer
        implements IMarkerResolutionGenerator2 {

    private static final IMarkerResolution[] NOTHING = new IMarkerResolution[0];

    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {
        ICompilationUnit cu = getCompilationUnit(marker);
        if (cu == null) {
            return NOTHING;
        }

        List<IMarkerResolution> updates = new ArrayList<IMarkerResolution>();

        for (AbstractStyleCheck c : getStyleCheckers(cu)) {
            if (c.hasResolutionsFor(marker, cu)) {
                c.getResolutionsFor(marker, cu, updates);
            }
        }
        return updates.toArray(new IMarkerResolution[updates.size()]);
    }

    @Override
    public boolean hasResolutions(IMarker marker) {
        ICompilationUnit cu = getCompilationUnit(marker);
        if (cu == null) {
            return false;
        }
        for (AbstractStyleCheck c : getStyleCheckers(cu)) {
            if (c.hasResolutionsFor(marker, cu)) {
                return true;
            }
        }
        return false;
    }

    private List<AbstractStyleCheck> getStyleCheckers(ICompilationUnit cu) {
        return Activator.getInstance().getUtil().findActiveStyleCheckers(
                cu.getJavaProject().getProject()
                );
    }

    private static ICompilationUnit getCompilationUnit(IMarker marker) {
        IResource res = marker.getResource();
        if (res instanceof IFile && res.isAccessible()) {
            IJavaElement element = JavaCore.create((IFile) res);
            if (element instanceof ICompilationUnit) {
                return (ICompilationUnit) element;
            }
        }
        return null;
    }

}
