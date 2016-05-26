package fr.isn.bqq.killeleve;

import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

public class KillEleve {

	public static final void main(final String[] args) {
		try {
			System.out.println("Recherche du processus...");
			Integer targetPid = null;
			final MonitoredHost host = MonitoredHost.getMonitoredHost("localhost"); // L'host est situé sur la machine locale.
			for(final int pid : host.activeVms()) { // On va chercher les JVM démarrées sur l'host.
				final MonitoredVm vm = host.getMonitoredVm(new VmIdentifier("//" + pid + "?mode=r"), 0);
				if(MonitoredVmUtil.mainClass(vm, true).equals("fr.isn.bbq.eleve.ProjetBBQEleve")) { // Si la classe principale de la JVM correspond.
					targetPid = pid; // On enregistre le PID.
					break;
				}
			}
			if(targetPid == null) { // Si le PID n'est pas trouvé, on s'en va.
				System.out.println("ProjetBBQ Élève non trouvé. Est-il en train de s'exécuter ?");
				return;
			}
			System.out.println("ProjetBBQ Élève trouvé, envoi du signal de terminaison...");
			final Process process = new ProcessBuilder("taskkill", "/PID", String.valueOf(targetPid), "/F").start(); // On va tuer le processus de la JVM trouvée.
			process.waitFor(); // On attends avant de s'en aller.
			System.out.println(process.exitValue() == 0 ? "Tâche tuée avec succès !" : "Impossible de tuer la tâche.");
		}
		catch(final Exception ex) {
			System.err.println("Erreur ! Veuillez essayer de relancer l'utilitaire ou de tuer le processus manuellement.");
			ex.printStackTrace();
		}
	}

}