package usr.martin.check_style.import_check;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.JavaModelException; 

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
            ICompilationUnit compilationUnit, ProblemFactory problemFactory
            ) throws JavaModelException {
        for (IImportDeclaration d : compilationUnit.getImports()) {
            if (d.isOnDemand()) {
                problemFactory.createProblem(CheckStyleProblem.Problem.IMPORT, "No wildcard imports allowed", d);
            }
        }
    }

}
