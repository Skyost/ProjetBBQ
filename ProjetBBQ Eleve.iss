; -- INSTALLATION DE PROJET BBQ ÉLÈVE -- 

[Setup]
; Nom de l'application :
AppName=Projet BBQ Élève
; Version de l'application :
AppVersion=0.1
; Dossier d'installation par défaut :
DefaultDirName={pf}\ProjetBBQ
; Dossier dans le menu démarrer par défaut :
DefaultGroupName=ProjetBBQ
; Icône du désinstallateur :
UninstallDisplayIcon={app}\ProjetBBQEleve.exe
; Autres :
AppPublisher=Groupe ISN
AppPublisherURL=https://github.com/Skyost/ProjetBBQ/wiki
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

[Files]
; Le fichier de l'application à inclure dans l'installateur :
Source: "ProjetBBQEleve.exe"; DestDir: "{app}"

[Icons]
; Icône à ajouter sur le bureau, dans le menu démarrer, etc... :
Name: "{group}\Projet BBQ Élève"; Filename: "{app}\ProjetBBQEleve.exe"

[Registry]
; On souhaite démarrer l'application au démarrage de Windows :
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"; ValueType: string; ValueName: "ProjetBBQEleve"; ValueData: """{app}\ProjetBBQEleve.exe"""; Flags: uninsdeletevalue
