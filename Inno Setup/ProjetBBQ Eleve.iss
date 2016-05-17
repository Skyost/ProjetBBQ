; -- INSTALLATION DE PROJET BBQ ÉLÈVE -- 

[Setup]
; ID de l'application
AppId=ProjetBBQEleve
; Nom de l'application :
AppName=Projet BBQ Élève
; Version de l'application :
AppVersion=0.1
; Dossier d'installation par défaut :
DefaultDirName={pf}\Projet BBQ Élève
; Dossier dans le menu démarrer par défaut :
DefaultGroupName=Projet BBQ Élève
; Icône du désinstallateur (dans Ajouter/Supprimer des programmes) :
UninstallDisplayIcon={app}\ProjetBBQEleve.exe
; Nom du désinstallateur (dans Ajouter/Supprimer des programmes) :
UninstallDisplayName=Projet BBQ Élève v0.1
; Icône de l'installateur :
SetupIconFile=res\Icon.ico
; Grande icône sur la gauche :
WizardImageFile=res\WizardImageFile.bmp
; Petite icône en haut à droite :
WizardSmallImageFile=res\WizardSmallImageFile.bmp
; Licence :
LicenseFile=res\LICENSE.txt 
; Autres :
OutputBaseFilename=ProjetBBQEleve
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
VersionInfoDescription=ProjetBBQ Élève
VersionInfoProductName=ProjetBBQ Élève
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
en.taskstart=Start with Windows
en.addtodesktop=Add an icon on the desktop
en.uninstall=Uninstall
en.runapp=Run Projet BBQ Élève
; Messages français :
fr.additionals=Tâches additionnelles :
fr.taskstart=Démarrer avec Windows
fr.addtodesktop=Ajouter une icône au bureau
fr.uninstall=Désinstaller
fr.runapp=Éxecuter Projet BBQ Élève

[Files]
; Le fichier de l'application à inclure dans l'installateur :
Source: "ProjetBBQEleve.exe"; DestDir: "{app}"

[Icons]
; Icône de l'application (menu démarrer) :
Name: "{group}\Projet BBQ Élève"; Filename: "{app}\ProjetBBQEleve.exe"
; Icône de l'application (bureau) :
Name: "{userdesktop}\Projet BBQ Élève"; Filename: "{app}\ProjetBBQEleve.exe"; Tasks: TaskDesktop
; Icône du désinstallateur (menu démarrer) :
Name: "{group}\{cm:uninstall}"; Filename: "{uninstallexe}"

[Tasks]
; La tâche supplémentaire qui permet de démarrer l'application avec Windows :
Name: TaskStart; Description: {cm:taskstart}; GroupDescription: {cm:additionals}
; La tâche supplémentaire qui permet de d'ajouter une icône sur le bureau :
Name: TaskDesktop; Description: {cm:addtodesktop}; GroupDescription: {cm:additionals}

[Registry]
; On souhaite démarrer l'application au démarrage de Windows si la tâche TaskStart est séléctionnée :
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"; ValueType: string; ValueName: "ProjetBBQEleve"; ValueData: """{app}\ProjetBBQEleve.exe"""; Flags: uninsdeletevalue; Tasks: TaskStart

[Run]
Filename: "{app}\ProjetBBQEleve.exe"; Description: {cm:runapp}; Flags: postinstall nowait skipifsilent