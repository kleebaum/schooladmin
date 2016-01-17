package de.schooladmin.deployment;

import de.schooladmin.ViewInterface;

/**
 * @author Anja Kleebaum
 *
 */
public interface ViewDeploymentInterface extends ViewInterface {
	void createOverView();
	void createClassesView();
	void exportTableDataToCVS(String className, boolean fileChooser);

}
