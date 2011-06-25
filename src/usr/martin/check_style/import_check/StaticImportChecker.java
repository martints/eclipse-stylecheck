package usr.martin.check_style.import_check;

import org.eclipse.jdt.core.Flags;
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
public class StaticImportChecker 
        extends AbstractStyleCheck {
 
    private boolean enabled;

    public StaticImportChecker(CheckStyleSettings settings) {
        enabled = ! settings.mayUseStaticImports();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    } 

    @Override
    public void process(ICompilationUnit compilationUnit, ProblemFactory problemFactory) 
            throws JavaModelException {
        for (IImportDeclaration d : compilationUnit.getImports()) {
            if ((d.getFlags() & Flags.AccStatic) != 0) {
                problemFactory.createProblem(CheckStyleProblem.Problem.IMPORT, "No static imports allowed", d);
            }
        }
    }

}
