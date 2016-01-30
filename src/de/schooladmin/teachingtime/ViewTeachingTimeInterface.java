package de.schooladmin.teachingtime;

import de.schooladmin.ViewInterface;

public interface ViewTeachingTimeInterface extends ViewInterface {

	void exportTeacherDataToCVS(String className, boolean fileChooser);

}
