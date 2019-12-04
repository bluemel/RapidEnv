package org.rapidbeans.rapidenv.config;

import org.rapidbeans.core.basic.PropertyChoice;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.ReadonlyListCollection;
import org.rapidbeans.core.type.TypeProperty;

public class InstallunitPropInstallcontrol extends PropertyChoice {

	public InstallunitPropInstallcontrol(TypeProperty type, RapidBean parentBean) {
		super(type, parentBean);
	}

	@Override
	public ReadonlyListCollection<?> getValue() {
		ReadonlyListCollection<?> col = super.getValue();
		return col;
	}
}
