/*
 * Copyright (c) 2000, 2005, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package sun.applet.resources;

import java.util.ListResourceBundle;

public class MsgAppletViewer_fr extends ListResourceBundle {

    public Object[][] getContents() {
        return new Object[][] {
            {"textframe.button.dismiss", "Annuler"},
            {"appletviewer.tool.title", "AppletViewer : {0}"},
            {"appletviewer.menu.applet", "Applet"},
            {"appletviewer.menuitem.restart", "Red\u00e9marrer"},
            {"appletviewer.menuitem.reload", "Recharger"},
            {"appletviewer.menuitem.stop", "Arr\u00eater"},
            {"appletviewer.menuitem.save", "Enregistrer..."},
            {"appletviewer.menuitem.start", "D\u00e9marrer"},
            {"appletviewer.menuitem.clone", "Dupliquer..."},
            {"appletviewer.menuitem.tag", "Marquer..."},
            {"appletviewer.menuitem.info", "Informations..."},
            {"appletviewer.menuitem.edit", "Editer"},
            {"appletviewer.menuitem.encoding", "Codage des caract\u00e8res"},
            {"appletviewer.menuitem.print", "Imprimer..."},
            {"appletviewer.menuitem.props", "Propri\u00e9t\u00e9s..."},
            {"appletviewer.menuitem.close", "Fermer"},
            {"appletviewer.menuitem.quit", "Quitter"},
            {"appletviewer.label.hello", "Bonjour..."},
            {"appletviewer.status.start", "d\u00e9marrage de l'applet"},
            {"appletviewer.appletsave.filedialogtitle","S\u00e9rialiser un applet en fichier"},
            {"appletviewer.appletsave.err1", "num\u00e9rotation d''un {0} vers {1}"},
            {"appletviewer.appletsave.err2", "dans appletSave : {0}"},
            {"appletviewer.applettag", "Etiquette affich\u00e9e"},
            {"appletviewer.applettag.textframe", "Etiquette HTML applet"},
            {"appletviewer.appletinfo.applet", "-- aucune information applet --"},
            {"appletviewer.appletinfo.param", "-- aucune information de param\u00e8tre --"},
            {"appletviewer.appletinfo.textframe", "Information applet"},
            {"appletviewer.appletprint.fail", "Echec de l'impression."},
            {"appletviewer.appletprint.finish", "Impression achev\u00e9e."},
            {"appletviewer.appletprint.cancel", "Impression annul\u00e9e."},
            {"appletviewer.appletencoding", "Codage de caract\u00e8re : {0}"},
            {"appletviewer.parse.warning.requiresname", "Avertissement : l'\u00e9tiquette <param name=... value=...> n\u00e9cessite un attribut name."},
            {"appletviewer.parse.warning.paramoutside", "Avertissement : \u00e9tiquette <param> en dehors de <applet> ... </applet>."},
            {"appletviewer.parse.warning.applet.requirescode", "Avertissement : l'\u00e9tiquette <applet> exige un attribut de code."},
            {"appletviewer.parse.warning.applet.requiresheight", "Avertissement : l'\u00e9tiquette <applet> exige un attribut de hauteur."},
            {"appletviewer.parse.warning.applet.requireswidth", "Avertissement : l'\u00e9tiquette <applet> exige un attribut de largeur."},
            {"appletviewer.parse.warning.object.requirescode", "Avertissement : l'\u00e9tiquette <object> exige un attribut de code."},
            {"appletviewer.parse.warning.object.requiresheight", "Avertissement : l'\u00e9tiquette <object> exige un attribut de hauteur."},
            {"appletviewer.parse.warning.object.requireswidth", "Avertissement : l'\u00e9tiquette <object> exige un attribut de largeur."},
            {"appletviewer.parse.warning.embed.requirescode", "Avertissement : l'\u00e9tiquette <embed> exige un attribut de code."},
            {"appletviewer.parse.warning.embed.requiresheight", "Avertissement : l'\u00e9tiquette <embed> exige un attribut de hauteur."},
            {"appletviewer.parse.warning.embed.requireswidth", "Avertissement : l'\u00e9tiquette <embed> exige un attribut de largeur."},
            {"appletviewer.parse.warning.appnotLongersupported", "Avertissement : l'\u00e9tiquette <app> n'est plus prise en charge ; utilisez <applet> \u00e0 la place :"},
            {"appletviewer.usage", "Syntaxe : appletviewer <options> url(s)\n\nO\u00f9 les <options> sont :\n  -debug                  Lancer le visualiseur d'applet dans le d\u00e9bogueur Java\n  -encoding <codage>    Sp\u00e9cifier le codage de caract\u00e8res utilis\u00e9 par les fichiers HTML\n  -J<indicateur d'ex\u00e9cution>        Transmettre l'argument \u00e0 l'interpr\u00e9teur Java\n\nL'option -J n'est pas standard et peut \u00eatre modifi\u00e9e sans pr\u00e9avis."},
            {"appletviewer.main.err.unsupportedopt", "Option non prise en charge : {0}"},
            {"appletviewer.main.err.unrecognizedarg", "Argument inconnu : {0}"},
            {"appletviewer.main.err.dupoption", "Option en double : {0}"},
            {"appletviewer.main.err.inputfile", "Aucun fichier d'entr\u00e9e n'a \u00e9t\u00e9 sp\u00e9cifi\u00e9."},
            {"appletviewer.main.err.badurl", "URL incorrect : {0} ( {1} )"},
            {"appletviewer.main.err.io", "Exception d''E/S pendant la lecture de {0}"},
            {"appletviewer.main.err.readablefile", "{0} doit \u00eatre un fichier accessible en lecture."},
            {"appletviewer.main.err.correcturl", "{0} est-il l''URL correct ?"},
            {"appletviewer.main.prop.store", "Propri\u00e9t\u00e9s AppletViewer propres \u00e0 l'utilisateur"},
            {"appletviewer.main.err.prop.cantread", "Echec de lecture du fichier de propri\u00e9t\u00e9s des utilisateurs : {0}"},
            {"appletviewer.main.err.prop.cantsave", "Echec de sauvegarde du fichier de propri\u00e9t\u00e9s des utilisateurs : {0}"},
            {"appletviewer.main.warn.nosecmgr", "Avertissement : d\u00e9sactivation de la s\u00e9curit\u00e9."},
            {"appletviewer.main.debug.cantfinddebug", "D\u00e9bogueur introuvable !"},
            {"appletviewer.main.debug.cantfindmain", "M\u00e9thode principale introuvable dans le d\u00e9bogueur !"},
            {"appletviewer.main.debug.exceptionindebug", "Exception dans le d\u00e9bogueur !"},
            {"appletviewer.main.debug.cantaccess", "D\u00e9bogueur inaccessible !"},
            {"appletviewer.main.nosecmgr", "Avertissement : SecurityManager n'est pas install\u00e9 !"},
            {"appletviewer.main.warning", "Avertissement : aucun applet n'a \u00e9t\u00e9 d\u00e9marr\u00e9. Assurez-vous que l'entr\u00e9e contient une \u00e9tiquette <applet>."},
            {"appletviewer.main.warn.prop.overwrite", "Avertissement : remplacement temporaire de propri\u00e9t\u00e9 de syst\u00e8me \u00e0 la demande de l''utilisateur : cl\u00e9 : {0} ancienne valeur : {1} nouvelle valeur : {2}"},
            {"appletviewer.main.warn.cantreadprops", "Avertissement : \u00e9chec de lecture du fichier de propri\u00e9t\u00e9s AppletViewer : {0} Utilisation des valeurs par d\u00e9faut."},
            {"appletioexception.loadclass.throw.interrupted", "chargement de classe interrompu : {0}"},
            {"appletioexception.loadclass.throw.notloaded", "classe non charg\u00e9e : {0}"},
            {"appletclassloader.loadcode.verbose", "Ouverture d''un flux vers {0} pour obtenir {1}"},
            {"appletclassloader.filenotfound", "Fichier introuvable pendant la recherche de {0}"},
            {"appletclassloader.fileformat", "Exception de format de fichier pendant le chargement de {0}"},
            {"appletclassloader.fileioexception", "Exception d''E/S pendant le chargement de {0}"},
            {"appletclassloader.fileexception", "exception {0} pendant le chargement de : {1}"},
            {"appletclassloader.filedeath", "{0} \u00e9limin\u00e9 pendant le chargement de {1}"},
            {"appletclassloader.fileerror", "erreur {0} pendant le chargement de {1}"},
            {"appletclassloader.findclass.verbose.findclass", "{0} rechercher la classe {1}"},
            {"appletclassloader.findclass.verbose.openstream", "Ouverture d''un flux vers {0} pour obtenir {1}"},
            {"appletclassloader.getresource.verbose.forname", "AppletClassLoader.getResource pour le nom {0}"},
            {"appletclassloader.getresource.verbose.found", "Ressource {0} trouv\u00e9e en tant que ressource syst\u00e8me"},
            {"appletclassloader.getresourceasstream.verbose", "Ressource {0} trouv\u00e9e en tant que ressource syst\u00e8me"},
            {"appletpanel.runloader.err", "Param\u00e8tre d'objet ou de code !"},
            {"appletpanel.runloader.exception", "exception pendant la d\u00e9num\u00e9rotation de {0}"},
            {"appletpanel.destroyed", "Applet d\u00e9truit."},
            {"appletpanel.loaded", "Applet charg\u00e9."},
            {"appletpanel.started", "Applet d\u00e9marr\u00e9."},
            {"appletpanel.inited", "Applet initialis\u00e9."},
            {"appletpanel.stopped", "Applet arr\u00eat\u00e9."},
            {"appletpanel.disposed", "Applet jet\u00e9."},
            {"appletpanel.nocode", "Param\u00e8tre CODE manquant dans une \u00e9tiquette APPLET."},
            {"appletpanel.notfound", "charger : classe {0} introuvable."},
            {"appletpanel.nocreate", "charger : {0} ne peut pas \u00eatre instanci\u00e9."},
            {"appletpanel.noconstruct", "charger : {0} n''est pas public ou n''a pas de concepteur public."},
            {"appletpanel.death", "\u00e9limin\u00e9"},
            {"appletpanel.exception", "exception : {0}."},
            {"appletpanel.exception2", "exception : {0} : {1}."},
            {"appletpanel.error", "erreur : {0}."},
            {"appletpanel.error2", "erreur : {0} : {1}."},
            {"appletpanel.notloaded", "Initialiser : applet non charg\u00e9."},
            {"appletpanel.notinited", "D\u00e9marrer : applet non initialis\u00e9."},
            {"appletpanel.notstarted", "Arr\u00eater : applet non d\u00e9marr\u00e9."},
            {"appletpanel.notstopped", "D\u00e9truire : applet non arr\u00eat\u00e9."},
            {"appletpanel.notdestroyed", "Jeter : applet non d\u00e9truit."},
            {"appletpanel.notdisposed", "Charger : applet non jet\u00e9."},
            {"appletpanel.bail", "Interrompu : fin de bail."},
            {"appletpanel.filenotfound", "Fichier introuvable pendant la recherche de {0}"},
            {"appletpanel.fileformat", "Exception de format de fichier pendant le chargement de {0}"},
            {"appletpanel.fileioexception", "Exception d''E/S pendant le chargement de {0}"},
            {"appletpanel.fileexception", "exception {0} pendant le chargement de : {1}"},
            {"appletpanel.filedeath", "{0} \u00e9limin\u00e9 pendant le chargement de {1}"},
            {"appletpanel.fileerror", "erreur {0} pendant le chargement de {1}"},
            {"appletpanel.badattribute.exception", "Analyse HTML\u00a0: valeur incorrecte pour l'attribut de largeur/hauteur"},
            {"appletillegalargumentexception.objectinputstream", "AppletObjectInputStream n\u00e9cessite un chargeur 'non null'"},
            {"appletprops.title", "Propri\u00e9t\u00e9s de AppletViewer"},
            {"appletprops.label.http.server", "Serveur proxy http :"},
            {"appletprops.label.http.proxy", "Port proxy http :"},
            {"appletprops.label.network", "Acc\u00e8s r\u00e9seau :"},
            {"appletprops.choice.network.item.none", "Aucun"},
            {"appletprops.choice.network.item.applethost", "H\u00f4te applet"},
            {"appletprops.choice.network.item.unrestricted", "Illimit\u00e9"},
            {"appletprops.label.class", "Acc\u00e8s \u00e0 la classe :"},
            {"appletprops.choice.class.item.restricted", "Limit\u00e9"},
            {"appletprops.choice.class.item.unrestricted", "Illimit\u00e9"},
            {"appletprops.label.unsignedapplet", "Autoriser les applets non sign\u00e9s"},
            {"appletprops.choice.unsignedapplet.no", "Non"},
            {"appletprops.choice.unsignedapplet.yes", "Oui"},
            {"appletprops.button.apply", "Appliquer"},
            {"appletprops.button.cancel", "Annuler"},
            {"appletprops.button.reset", "Restaurer"},
            {"appletprops.apply.exception", "Echec de l''enregistrement des propri\u00e9t\u00e9s : {0}"},
            /* 4066432 */
            {"appletprops.title.invalidproxy", "Entr\u00e9e non valide"},
            {"appletprops.label.invalidproxy", "Le num\u00e9ro de port du proxy doit \u00eatre un entier positif."},
            {"appletprops.button.ok", "OK"},
            /* end 4066432 */
            {"appletprops.prop.store", "Propri\u00e9t\u00e9s AppletViewer propres \u00e0 l'utilisateur"},
            {"appletsecurityexception.checkcreateclassloader", "Exception de s\u00e9curit\u00e9 : chargeur de classes"},
            {"appletsecurityexception.checkaccess.thread", "Exception de s\u00e9curit\u00e9 : unit\u00e9 d'ex\u00e9cution"},
            {"appletsecurityexception.checkaccess.threadgroup", "Exception de s\u00e9curit\u00e9 : groupe d''unit\u00e9s d''ex\u00e9cution : {0}"},
            {"appletsecurityexception.checkexit", "Exception de s\u00e9curit\u00e9 : exit : {0}"},
            {"appletsecurityexception.checkexec", "Exception de s\u00e9curit\u00e9 : exec : {0}"},
            {"appletsecurityexception.checklink", "Exception de s\u00e9curit\u00e9 : link : {0}"},
            {"appletsecurityexception.checkpropsaccess", "Exception de s\u00e9curit\u00e9 : propri\u00e9t\u00e9s"},
            {"appletsecurityexception.checkpropsaccess.key", "Exception de s\u00e9curit\u00e9 : acc\u00e8s aux propri\u00e9t\u00e9s {0}"},
            {"appletsecurityexception.checkread.exception1", "Exception de s\u00e9curit\u00e9 : {0}, {1}"},
            {"appletsecurityexception.checkread.exception2", "Exception de s\u00e9curit\u00e9 : file.read : {0}"},
            {"appletsecurityexception.checkread", "Exception de s\u00e9curit\u00e9 : file.read : {0} == {1}"},
            {"appletsecurityexception.checkwrite.exception", "Exception de s\u00e9curit\u00e9 : {0}, {1}"},
            {"appletsecurityexception.checkwrite", "Exception de s\u00e9curit\u00e9 : file.write : {0} == {1}"},
            {"appletsecurityexception.checkread.fd", "Exception de s\u00e9curit\u00e9 : fd.read"},
            {"appletsecurityexception.checkwrite.fd", "Exception de s\u00e9curit\u00e9 : fd.write"},
            {"appletsecurityexception.checklisten", "Exception de s\u00e9curit\u00e9 : socket.listen : {0}"},
            {"appletsecurityexception.checkaccept", "Exception de s\u00e9curit\u00e9 : socket.accept : {0}:{1}"},
            {"appletsecurityexception.checkconnect.networknone", "Exception de s\u00e9curit\u00e9 : socket.connect : {0}->{1}"},
            {"appletsecurityexception.checkconnect.networkhost1", "Exception de s\u00e9curit\u00e9 : \u00e9chec de la connexion \u00e0 {0} avec une origine de {1}."},
            {"appletsecurityexception.checkconnect.networkhost2", "Exception de s\u00e9curit\u00e9 : impossible de r\u00e9soudre l''adresse IP pour l''h\u00f4te {0} ou pour {1}. "},
            {"appletsecurityexception.checkconnect.networkhost3", "Exception de s\u00e9curit\u00e9 : impossible de r\u00e9soudre l''adresse IP pour l''h\u00f4te {0}. Voir la propri\u00e9t\u00e9 trustProxy."},
            {"appletsecurityexception.checkconnect", "Exception de s\u00e9curit\u00e9 : connect : {0}->{1}"},
            {"appletsecurityexception.checkpackageaccess", "Exception de s\u00e9curit\u00e9 : impossible d''acc\u00e9der au module : {0}"},
            {"appletsecurityexception.checkpackagedefinition", "Exception de s\u00e9curit\u00e9 : impossible de d\u00e9finir le module : {0}"},
            {"appletsecurityexception.cannotsetfactory", "Exception de s\u00e9curit\u00e9 : impossible de d\u00e9finir les param\u00e8tres d'usine"},
            {"appletsecurityexception.checkmemberaccess", "Exception de s\u00e9curit\u00e9 : v\u00e9rifier l'acc\u00e8s des membres"},
            {"appletsecurityexception.checkgetprintjob", "Exception de s\u00e9curit\u00e9 : getPrintJob"},
            {"appletsecurityexception.checksystemclipboardaccess", "Exception de s\u00e9curit\u00e9 : getSystemClipboard"},
            {"appletsecurityexception.checkawteventqueueaccess", "Exception de s\u00e9curit\u00e9 : getEventQueue"},
            {"appletsecurityexception.checksecurityaccess", "Exception de s\u00e9curit\u00e9 : op\u00e9ration de s\u00e9curit\u00e9 : {0}"},
            {"appletsecurityexception.getsecuritycontext.unknown", "type de chargeur de classe inconnu ; impossible de v\u00e9rifier getContext"},
            {"appletsecurityexception.checkread.unknown", "type de chargeur de classe inconnu ; impossible de v\u00e9rifier la lecture de contr\u00f4le {0}"},
            {"appletsecurityexception.checkconnect.unknown", "type de chargeur de classe inconnu ; impossible de v\u00e9rifier la connexion de contr\u00f4le {0}"},
        };
    }
}