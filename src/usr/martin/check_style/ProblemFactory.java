package usr.martin.check_style;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

public final class ProblemFactory {

	private final ReconcileContext context;
	
	private final List<CheckStyleProblem> problems = new ArrayList<CheckStyleProblem>();

	private final CompilationUnit compilationUnit;

	public ProblemFactory(ReconcileContext context_, CompilationUnit compilationUnit_) {
		context = context_;
		compilationUnit = compilationUnit_;
	}

	public void createProblem(CheckStyleProblem.Problem type, String message, ASTNode node) {
		problems .add(new CheckStyleProblem(
				context.getWorkingCopy().getResource(),
				type,
				message,
				node.getStartPosition(),
				node.getLength(),
				compilationUnit.getLineNumber(node.getStartPosition())
				));
	}

	public CategorizedProblem[] getProblems() {
		return problems.toArray(new CategorizedProblem[problems.size()]);
	}

	void createProblemMarkers() {
		IResource resource = compilationUnit.getJavaElement().getResource();
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
