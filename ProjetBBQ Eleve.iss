; -- INSTALLATION DE PROJET BBQ �L�VE -- 

[Setup]
; Nom de l'application :
AppName=Projet BBQ �l�ve
; Version de l'application :
AppVersion=0.1
; Dossier d'installation par d�faut :
DefaultDirName={pf}\ProjetBBQ
; Dossier dans le menu d�marrer par d�faut :
DefaultGroupName=ProjetBBQ
; Ic�ne du d�sinstallateur :
UninstallDisplayIcon={app}\ProjetBBQEleve.exe
; Autres :
AppPublisher=Groupe ISN
AppPublisherURL=https://github.com/Skyost/ProjetBBQ/wiki
VersionInfoCompany=Groupe ISN
VersionInfoCopyright=Voir liste des d�pendances ici : https://github.com/Skyost/ProjetBBQ/wiki. 
VersionInfoDescription=ProjetBBQ �l�ve
VersionInfoProductName=ProjetBBQ �l�ve
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
; Messages fran�ais :
fr.additionals=T�ches additionnelles :
fr.taskstart=D�marrer avec Windows

[Files]
; Le fichier de l'application � inclure dans l'installateur :
Source: "ProjetBBQEleve.exe"; DestDir: "{app}"

[Icons]
; Ic�ne � ajouter sur le bureau, dans le menu d�marrer, etc... :
Name: "{group}\Projet BBQ �l�ve"; Filename: "{app}\ProjetBBQEleve.exe"

[Tasks]
; La t�che suppl�mentaire qui permet de d�marrer l'application avec Windows :
Name: TaskStart; Description: {cm:taskstart}; GroupDescription: {cm:additionals}

[Registry]
; On souhaite d�marrer l'application au d�marrage de Windows si la t�che TaskStart est s�l�ctionn�e :
Root: HKLM; Subkey: "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"; ValueType: string; ValueName: "ProjetBBQEleve"; ValueData: """{app}\ProjetBBQEleve.exe"""; Flags: uninsdeletevalue; Tasks: TaskStart