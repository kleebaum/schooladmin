package de.schooladmin.deployment;

import de.schooladmin.SchoolClass;
import de.schooladmin.ViewInterface;

/**
 * @author Anja Kleebaum
 *
 */
public interface ViewDeploymentInterface extends ViewInterface {
	void createOverView();
	void createClassesView();
	void createClassView(SchoolClass schoolClass);
}
