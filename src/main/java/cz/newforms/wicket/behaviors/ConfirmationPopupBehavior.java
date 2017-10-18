package cz.newforms.wicket.behaviors;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.core.util.string.JavaScriptUtils;

/**
 * K danemu komponentu prida Javascript kod pro vyskakovací okno. Okno se zobrazí pokud nastane
 * událost předaná v konstruktoru.
 * 
 * @author rozkovec
 * 
 */
public class ConfirmationPopupBehavior extends AttributeModifier
{

	/**
	 * @param event
	 *            nazev udalosti, na kterou navazat vyskakovaci potvrzovaci dialog
	 * @param message
	 *            zprava
	 */
	public ConfirmationPopupBehavior(String event, CharSequence message)
	{
		super(event, "return confirm('" + JavaScriptUtils.escapeQuotes(message) + "');");
	}
}
