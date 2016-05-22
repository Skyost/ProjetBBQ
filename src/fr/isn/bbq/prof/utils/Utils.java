package fr.isn.bbq.prof.utils;

import java.awt.Component;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
	
	public static final Object[] createMessageDialog(final JFrame parent) {
		final JTextField textField = new JTextField();
		final JSpinner spinner = new JSpinner();
		final List<Component> components = new ArrayList<Component>(); // Composants de la boîte de dialogue.
		components.add(new JLabel("Message :"));
		components.add(textField);
		components.add(new JLabel("Durée d'affichage (en secondes) :"));
		components.add(spinner);
		spinner.setModel(new SpinnerNumberModel(5, 1, Integer.MAX_VALUE, 1));
		final JFormattedTextField field = ((NumberEditor)spinner.getEditor()).getTextField();
		((NumberFormatter)field.getFormatter()).setAllowsInvalid(false);
		return new Object[]{JOptionPane.showConfirmDialog(parent, components.toArray(new Object[components.size()]), "Envoyer un message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION, components};
	}

}