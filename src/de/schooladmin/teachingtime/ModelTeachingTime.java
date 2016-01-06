package de.schooladmin.teachingtime;

import de.schooladmin.Model;

public class ModelTeachingTime extends Model implements ModelTeachingTimeInterface {

	public ModelTeachingTime() {
		
	}
	
	@Override
	public void initialize(){
		super.initialize();
		prop = readConfig(ConfigFile);
	}
}
