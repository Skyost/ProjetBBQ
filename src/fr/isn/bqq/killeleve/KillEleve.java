package fr.isn.bqq.killeleve;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.StringMonitor;
import sun.jvmstat.monitor.VmIdentifier;

public class KillEleve {

	public static final void main(final String[] args) {
		try {
			System.out.println("Recherche du processus...");
			Integer targetPid = null;
			final MonitoredHost host = MonitoredHost.getMonitoredHost("localhost"); // L'host est situé sur la machine locale.
			for(final int pid : host.activeVms()) { // On va chercher les JVM démarrées sur l'host.
				final MonitoredVm vm = host.getMonitoredVm(new VmIdentifier("//" + pid + "?mode=r"), 0);
				final String mainClass = mainClass(vm, true);
				System.out.println("Classe : \"" + mainClass + "\", PID : " + pid);
				if(mainClass.contains("ProjetBBQEleve")) { // Si la classe principale de la JVM correspond.
					targetPid = pid; // On enregistre le PID.
					break;
				}
			}
			System.out.println();
			if(targetPid == null) { // Si le PID n'est pas trouvé, on s'en va.
				System.out.println("ProjetBBQ Élève non trouvé. Est-il en train de s'exécuter ?");
			}
			else {
				System.out.println("ProjetBBQ Élève trouvé, envoi du signal de terminaison...");
				final Process process = new ProcessBuilder("taskkill", "/PID", String.valueOf(targetPid), "/F").start(); // On va tuer le processus de la JVM trouvée.
				process.waitFor(); // On attends avant de s'en aller.
				System.out.println(process.exitValue() == 0 ? "Tâche tuée avec succès !" : "Impossible de tuer la tâche.");
			}
			System.out.println();
			System.out.println("Veuillez appuyer sur \"Entrer\" pour terminer le programme :");
			System.in.read();
		}
		catch(final Exception ex) {
			System.err.println("Erreur ! Veuillez essayer de relancer l'utilitaire ou de tuer le processus manuellement.");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Méthode permettant de retrouver la classe principale d'une JVM corrigée et prise d'OpenJDK.
	 * 
	 * @param vm La VM.
	 * @param fullPath Si vous souhaitez le chemin complet.
	 * 
	 * @return La classe principale de la JVM.
	 * 
	 * @throws MonitorException Si une exception se produit.
	 */
	
    public static String mainClass(MonitoredVm vm, boolean fullPath) throws MonitorException {
    	StringMonitor cmd = (StringMonitor)vm.findByName("sun.rt.javaCommand");
		String arg0 = cmd == null ? "Unknown" : cmd.stringValue();
		
		if (!fullPath) {
			/*
			* can't use File.separator() here because the separator
			* for the target jvm may be different than the separator
			* for the monitoring jvm.
			*/
			int lastFileSeparator = arg0.lastIndexOf('/');
			if (lastFileSeparator > 0) {
			    return arg0.substring(lastFileSeparator + 1);
			}
			
			lastFileSeparator = arg0.lastIndexOf('\\');
			if (lastFileSeparator > 0) {
			    return arg0.substring(lastFileSeparator + 1);
			}
			
			int lastPackageSeparator = arg0.lastIndexOf('.');
			if (lastPackageSeparator > 0) {
			    return arg0.substring(lastPackageSeparator + 1);
			}
		}
		return arg0;
    }

}