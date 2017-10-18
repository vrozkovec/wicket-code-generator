/**
 *
 */
package cz.newforms.wicket.behaviors;

/**
 * K danemu komponentu prida Javascript kod - po kliknuti na komponent bude zobrazen potvrzovaci
 * dialog.
 *
 * @author rozkovec
 *
 */
public class OnClickConfirmationPopupBehavior extends ConfirmationPopupBehavior
{
	/**
	 * Konstruktor se standardni otazkou.
	 *
	 */
	public OnClickConfirmationPopupBehavior()
	{
		this("Opravdu?");
	}

	/**
	 * @param message
	 *            zprava potvrzovaciho dialogu
	 */
	public OnClickConfirmationPopupBehavior(CharSequence message)
	{
		super("onclick", message);
	}


	/**
	 * @return behavior
	 */
	public static OnClickConfirmationPopupBehavior get()
	{
		return new OnClickConfirmationPopupBehavior();
	}

	/**
	 * @param messgage
	 * @return behavior
	 */
	public static OnClickConfirmationPopupBehavior get(CharSequence messgage)
	{
		return new OnClickConfirmationPopupBehavior(messgage);
	}
}
