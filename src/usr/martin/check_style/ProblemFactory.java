package usr.martin.check_style;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.SourceRange;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

public final class ProblemFactory {

    private final IFile file;

    private final List<CheckStyleProblem> problems = new ArrayList<CheckStyleProblem>();

    private final ICompilationUnit compilationUnit;

    public ProblemFactory(IFile file_, ICompilationUnit compilationUnit_) {
        file = file_;
        compilationUnit = compilationUnit_;
    }

    public void createProblem(CheckStyleProblem.Problem type, String message, ISourceReference node) {
        int lineNumber;
        ISourceRange sourceRange = null;
        try {
            sourceRange = node.getSourceRange();
            Document document = new Document(compilationUnit.getSource());
            lineNumber = document.getLineOfOffset(sourceRange.getOffset());
        } catch (BadLocationException e) {
            lineNumber = -1;
        } catch (JavaModelException e1) {
            sourceRange = new SourceRange(0, -1);
            lineNumber = -1;
        }
        problems .add(new CheckStyleProblem(
                file,
                type,
                message,
                sourceRange.getOffset(),
                sourceRange.getLength(),
                lineNumber
                ));
    }

    public CategorizedProblem[] getProblems() {
        return problems.toArray(new CategorizedProblem[problems.size()]);
    }

    void createProblemMarkers() {
        IResource resource = compilationUnit.getResource();
        // Create markers for each entry
        try {
            for (CategorizedProblem problem : problems) {
                // Create a Marker
                IMarker marker = resource.createMarker(problem.getMarkerType());

                // Set Marker Attributes
                marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
                marker.setAttribute(IMarker.CHAR_START, problem.getSourceStart());
                marker.setAttribute(IMarker.CHAR_END, problem.getSourceEnd());
                marker.setAttribute(IMarker.LINE_NUMBER, problem.getSourceLineNumber());

                if (problem.isWarning()) {
                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                } else if (problem.isError()) {
                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                } else {
                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
                }
            }
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    boolean isEmpty() {
        return problems.isEmpty();
    }

}
