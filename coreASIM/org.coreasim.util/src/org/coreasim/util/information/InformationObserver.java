package org.coreasim.util.information;

public interface InformationObserver {

	void informationCreated(InformationObject information);

	void clearInformation(InformationObject information);
}
