package usr.martin.check_style.import_check;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;

import usr.martin.check_style.AbstractStyleCheck;
import usr.martin.check_style.CheckStyleProblem;
import usr.martin.check_style.CheckStyleSettings;
import usr.martin.check_style.ProblemFactory;

/**
 * Eventually disallow static imports
 */
public class WildcardImportChecker 
		extends AbstractStyleCheck {

	private boolean enabled;

	public WildcardImportChecker(CheckStyleSettings settings) {
		enabled = ! settings.mayUseWildcardImports();
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void process(
			ReconcileContext context,
			CompilationUnit compilationUnit, ProblemFactory problemFactory
			) throws JavaModelException {
		for (Object o : compilationUnit.imports()) {
			ImportDeclaration d = (ImportDeclaration) o;
			if (d.isOnDemand()) {
				problemFactory.createProblem(CheckStyleProblem.Problem.IMPORT, "No wildcard imports allowed", d);
				}
		}
	}

}
