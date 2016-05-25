; -- INSTALLATION DE PROJET BBQ PROFESSEUR -- 

[Setup]
; ID de l'application
AppId=ProjetBBQProf
; Nom de l'application :
AppName=Projet BBQ Professeur
; Version de l'application :
AppVersion=0.1
; Dossier d'installation par défaut :
DefaultDirName={pf}\Projet BBQ Professeur
; Dossier dans le menu démarrer par défaut :
DefaultGroupName=Projet BBQ Professeur
; Icône du désinstallateur (dans Ajouter/Supprimer des programmes) :
UninstallDisplayIcon={app}\ProjetBBQProf.exe
; Nom du désinstallateur (dans Ajouter/Supprimer des programmes) :
UninstallDisplayName=Projet BBQ Professeur v0.1
; Icône de l'installateur :
SetupIconFile=res\Icon.ico
; Grande icône sur la gauche :
WizardImageFile=res\WizardImageFile.bmp
; Petite icône en haut à droite :
WizardSmallImageFile=res\WizardSmallImageFile.bmp
; Licence :
LicenseFile=res\LICENSE.txt 
; Autres :
OutputBaseFilename=Installer
DisableWelcomePage=no
UsePreviousAppDir=yes
AllowNoIcons=yes

; Informations sur l'application (dans Ajouter/Supprimer des programmes) :
AppPublisher=Groupe ISN
AppPublisherURL=https://github.com/Skyost/ProjetBBQ
AppContact=Aide, crédits et dépendances : https://github.com/Skyost/ProjetBBQ/wiki.
AppSupportURL=https://github.com/Skyost/ProjetBBQ/wiki
AppUpdatesURL=https://github.com/Skyost/ProjetBBQ/releases

; Informations sur l'application (dans l'explorateur) :
VersionInfoCompany=Groupe ISN
VersionInfoCopyright=Voir liste des dépendances ici : https://github.com/Skyost/ProjetBBQ/wiki. 
VersionInfoDescription=ProjetBBQ Professeur
VersionInfoProductName=ProjetBBQ Professeur
VersionInfoProductTextVersion=v0.1
VersionInfoProductVersion=0.1.0.0
VersionInfoTextVersion=v0.1
VersionInfoVersion=0.1.0.0

[Languages]
; La liste des langages disponibles :
Name: en; MessagesFile: "compiler:Default.isl"
Name: fr; MessagesFile: "compiler:Languages\French.isl"

[CustomMessages]
; Messages anglais :
en.additionals=Additional tasks :
en.addtodesktop=Add an icon on the desktop
en.uninstall=Uninstall
en.runapp=Run Projet BBQ Professeur (will show an error on the first launch)
en.openfolder=Open the installation directory (to edit the configuration and add rooms)
; Messages français :
fr.additionals=Tâches additionnelles :
fr.addtodesktop=Ajouter une icône au bureau
fr.uninstall=Désinstaller
fr.runapp=Éxecuter Projet BBQ Professeur (affichera une erreur au premier lancement)
fr.openfolder=Ouvrir le dossier d'installation (pour éditer la configuration et ajouter des salles)

[Files]
; Le fichier de l'application à inclure dans l'installateur :
Source: "ProjetBBQProf.exe"; DestDir: "{app}"
Source: "Lisez-moi !.txt"; DestDir: "{app}"

[Icons]
; Icône de l'application (menu démarrer) :
Name: "{group}\Projet BBQ Professeur"; Filename: "{app}\ProjetBBQProf.exe"
; Icône de l'application (bureau) :
Name: "{userdesktop}\Projet BBQ Professeur"; Filename: "{app}\ProjetBBQProf.exe"; Tasks: TaskDesktop
; Icône du désinstallateur (menu démarrer) :
Name: "{group}\{cm:uninstall}"; Filename: "{uninstallexe}"

[Tasks]
; La tâche supplémentaire qui permet de d'ajouter une icône sur le bureau :
Name: TaskDesktop; Description: {cm:addtodesktop}; GroupDescription: {cm:additionals}

[Run]
; Permet d'éxecuter le logiciel après l'installation :
Filename: "{app}\ProjetBBQEleve.exe"; Description: {cm:runapp}; Flags: postinstall nowait skipifsilent
; Permet d'ouvrir le dossier d'installation :
Filename: "{app}"; Description: {cm:openfolder}; Flags: postinstall nowait skipifsilent shellexec