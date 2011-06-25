package usr.martin.check_style;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * This class describes a basic style check
 */
public abstract class AbstractStyleCheck {

	public abstract boolean isEnabled();

	public abstract void process(
			CompilationUnit compilationUnit, 
			ProblemFactory problemFactory
			) throws JavaModelException;

}
