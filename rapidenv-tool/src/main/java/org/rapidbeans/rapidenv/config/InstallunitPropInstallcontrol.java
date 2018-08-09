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
		if (col.get(0) != null) {
			final InstallControl superValue = (InstallControl) col.get(0);
			if (superValue != InstallControl.discontinued) {
				final Installunit unit = (Installunit) this.getBean();
				if (unit.getParentBean() instanceof Installunit) {
					final Installunit parentUnit = (Installunit) unit.getParentBean();
					if (parentUnit.getInstallcontrol() == InstallControl.discontinued) {
						unit.setInstallcontrol(InstallControl.discontinued);
						col = super.getValue();
					}
				}
			}
		}
		return col;
	}
}
