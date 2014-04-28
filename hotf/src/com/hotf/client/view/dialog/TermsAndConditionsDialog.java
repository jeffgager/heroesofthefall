package com.hotf.client.view.dialog;


/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author Jeff Gager
 */
public interface TermsAndConditionsDialog extends DialogView {
	
	void setPresenter(Presenter presenter);

	public interface Presenter {
		void notacceptTac();
		void acceptTac();
		void closeTac();
	}

}