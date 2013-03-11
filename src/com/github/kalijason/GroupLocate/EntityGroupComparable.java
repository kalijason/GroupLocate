package com.github.kalijason.GroupLocate;

import java.util.Comparator;

public class EntityGroupComparable implements Comparator<EntityGroup> {

	@Override
	public int compare(EntityGroup arg0, EntityGroup arg1) {
		if (arg0 != null && arg1 != null) {
			if (arg0.getSize() < arg1.getSize()) {
				return 1;
			} else if (arg0.getSize() == arg1.getSize()) {
				return 0;
			} else {
				return -1;
			}

		}
		return 0;
	}

}
