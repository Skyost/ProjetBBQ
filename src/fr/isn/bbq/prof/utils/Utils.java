package fr.isn.bbq.prof.utils;

import java.awt.Component;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.text.NumberFormatter;

import fr.isn.bbq.prof.ProjetBBQProf;

public class Utils {
	
	private static final List<Component> MESSAGE_COMPONENTS = new ArrayList<Component>(); // Composants de la boîte de dialogue "message".
	
	/**
	 * Permet d'obtenir le chemin du fichier JAR.
	 * 
	 * @return Le chemin du fichier JAR.
	 * 
	 * @throws URISyntaxException Si la destination n'est pas valide.
	 */

	public static final File getParentFolder() throws URISyntaxException {
		return new File(ProjetBBQProf.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
	}
	
	/**
	 * Permet de vérifier si une chaîne de caractère est numérique.
	 * 
	 * @param string La chaîne de caractère.
	 * 
	 * @return <b>true</b> Si la chaîne est numérique.
	 * <br><b>false/<b> Autrement.
	 */
	
	public static boolean isNumeric(final String string) {
		for(char charr : string.toCharArray()) {
			if(Character.isDigit(charr)) {
				continue;
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Joindre un tableau de chaînes de caractère.
	 * 
	 * @param joiner Le séparateur.
	 * @param strings Le tableau.
	 * 
	 * @return Le tableau de chaînes de caractère joint.
	 */

	public static final String join(final String joiner, final String... strings) {
		final StringBuilder builder = new StringBuilder();
		for(final String string : strings) {
			builder.append(string + joiner);
		}
		builder.setLength(builder.length() - joiner.length());
		return builder.toString();
	}
	
	/**
	 * Chargement des composants de la boîte de message.
	 */
	
	public static final void loadMessagesComponents() {
		if(!MESSAGE_COMPONENTS.isEmpty()) {
			return;
		}
		final JSpinner spinner = new JSpinner();
		MESSAGE_COMPONENTS.add(new JLabel(LanguageManager.getString("dialog.message.enter")));
		MESSAGE_COMPONENTS.add(new JTextField());
		MESSAGE_COMPONENTS.add(new JLabel(LanguageManager.getString("dialog.message.select")));
		MESSAGE_COMPONENTS.add(new JComboBox<String>());
		MESSAGE_COMPONENTS.add(new JLabel(LanguageManager.getString("dialog.message.duration")));
		MESSAGE_COMPONENTS.add(spinner);
		spinner.setModel(new SpinnerNumberModel(5, 1, Integer.MAX_VALUE, 1));
		final JFormattedTextField field = ((NumberEditor)spinner.getEditor()).getTextField();
		((NumberFormatter)field.getFormatter()).setAllowsInvalid(false);
	}
	
	/**
	 * Chargement des messages contenus dans les paramètres "settings.xml".
	 */
	
	public static final void loadMessagesInSettings() {
		final List<String> messages = new ArrayList<String>();
		messages.add("-");
		messages.addAll(ProjetBBQProf.settings.defaultMessages);
		MESSAGE_COMPONENTS.set(3, new JComboBox<String>(messages.toArray(new String[messages.size()])));
	}
	
	/**
	 * Permet de créer un dialogue pour envoyer un message.
	 * 
	 * @param parent L'IHM parent.
	 * 
	 * @return Un object content en colonne 1 la réponse du dialogue (boolean), en colonne 2 le message (String) et en colonne 3 la durée (String).
	 */
	
	@SuppressWarnings("unchecked")
	public static final Object[] createMessageDialog(final JFrame parent) {
		final boolean result = JOptionPane.showConfirmDialog(parent, MESSAGE_COMPONENTS.toArray(new Component[MESSAGE_COMPONENTS.size()]), LanguageManager.getString("dialog.message.title"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
		final List<Object> returned = new ArrayList<Object>();
		returned.add(result);
		final JComboBox<String> comboBox = (JComboBox<String>)MESSAGE_COMPONENTS.get(3);
		returned.add(comboBox.getSelectedIndex() == 0 ? ((JTextField)MESSAGE_COMPONENTS.get(1)).getText() : comboBox.getSelectedItem().toString());
		returned.add(((JSpinner)MESSAGE_COMPONENTS.get(5)).getValue().toString());
		return returned.toArray(new Object[returned.size()]);
	}
	
	/**
	 * Ajout d'un zéro devant un nombre pour les nombres dont la "longueur" est inférieure à 2 (1 -> 01, 4 -> 04, 12 -> 12).
	 * 
	 * @param number Le nombre.
	 * 
	 * @return Le nombre sous forme d'une chaîne de caractètres.
	 */
	
	public static final String addZeroIfMissing(final int number) {
		final String value = String.valueOf(number);
		return value.length() < 2 ? "0" + value : value;
	}

}