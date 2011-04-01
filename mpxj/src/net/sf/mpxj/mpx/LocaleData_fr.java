/*
 * file:       LocaleData_fr.java
 * author:     Benoit Baranne
 *             Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       12/04/2005
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package net.sf.mpxj.mpx;

import java.util.HashMap;
import java.util.ListResourceBundle;

import net.sf.mpxj.CodePage;
import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.DateOrder;
import net.sf.mpxj.ProjectDateFormat;
import net.sf.mpxj.ProjectTimeFormat;

/**
 * This class defines the French translation of resource required by MPX files.
 */
public final class LocaleData_fr extends ListResourceBundle
{
   /**
    * {@inheritDoc}
    */
   @Override public Object[][] getContents()
   {
      return (RESOURCE_DATA);
   }

   private static final String[][] TIME_UNITS_ARRAY_DATA =
   {
      {
         "m"
      },
      {
         "h"
      },
      {
         "j"
      },
      {
         "s"
      },
      {
         "ms"
      },
      {
         "a"
      },
      {
         "%"
      },
      {
         "me"
      },
      {
         "he"
      },
      {
         "je"
      },
      {
         "se"
      },
      {
         "mse"
      },
      {
         "???"
      },
      {
         "e%"
      }
   };
   private static final HashMap<String, Integer> TIME_UNITS_MAP_DATA = new HashMap<String, Integer>();

   static
   {
      for (int loop = 0; loop < TIME_UNITS_ARRAY_DATA.length; loop++)
      {
         Integer value = Integer.valueOf(loop);
         for (String name : TIME_UNITS_ARRAY_DATA[loop])
         {
            TIME_UNITS_MAP_DATA.put(name, value);
         }
      }
   }

   private static final String[] ACCRUE_TYPES_DATA =
   {
      "D�but", //   "Start",
      "Fin", //   "End",
      "Proportion" //   "Prorated"
   };

   private static final String[] RELATION_TYPES_DATA =
   {
      "FF", //   "FF",
      "FD", //   "FS",
      "DF", //   "SF",
      "DD" //   "SS"
   };

   private static final String[] PRIORITY_TYPES_DATA =
   {
      "Le Plus Bas", //   "Lowest",
      "Tr�s Bas", //   "Very Low",
      "Plus Bas", //   "Lower",
      "Bas", //   "Low",
      "Moyen", //   "Medium",
      "Elev�", //   "High",
      "Plus Elev�", //   "Higher",
      "Tr�s Elev�", //   "Very High",
      "Le Plus Elev�", //   "Highest",
      "Ne Pas Niveler" //   "Do Not Level"
   };

   private static final String[] CONSTRAINT_TYPES_DATA =
   {
      "D�s Que Possible", //   "As Soon As Possible",
      "Le Plus Tard Possible", //   "As Late As Possible",
      "Doit Commencer Le", //   "Must Start On",
      "Doit Finir Le", //   "Must Finish On",
      "D�but Au Plus T�t Le", //   "Start No Earlier Than",
      "D�but Au Plus Tard Le", //   "Start No Later Than",
      "Fin Au Plus T�t Le", //   "Finish No Earlier Than",
      "Fin Au Plus Tard Le" //   "Finish No Later Than"
   };

   private static final String[] TASK_NAMES_DATA =
   {
      null, //
      "Nom", //   "Name",
      "WBS", //   "WBS",
      "N�vel Externo", //   "Outline Level",
      "Texte1", //   "Text1",
      "Texte2", //   "Text2",
      "Texte3", //   "Text3",
      "Texte4", //   "Text4",
      "Texte5", //   "Text5",
      "Texte6", //   "Text6",
      "Texte7", //   "Text7",
      "Texte8", //   "Text8",
      "Texte9", //   "Text9",
      "Texte10", //   "Text10",
      "Notes", //   "Notes",
      "Contact", //   "Contact",
      "Groupe de Ressources", //   "Resource Group",
      null, //
      null, //
      null, //
      "Travail", //   "Work",
      "Travail Normal", //   "Baseline Work",
      "Travail R�el", //   "Actual Work",
      "Travail Restant", //   "Remaining Work",
      "Variation de Travail", //   "Work Variance",
      "% Travail achev�", //   "% Work Complete",
      null, //
      null, //
      null, //
      null, //
      "Co�t", //   "Cost",
      "Co�t Planifi�", //   "Baseline Cost",
      "Co�t R�el", //   "Actual Cost",
      "Co�t Restant", //   "Remaining Cost",
      "Variation de Co�t", //   "Cost Variance",
      "Co�t Fixe", //   "Fixed Cost",
      "Co�t1", //   "Cost1",
      "Co�t2", //   "Cost2",
      "Co�t3", //   "Cost3",
      null, //
      "Dur�e", //   "Duration",
      "Dur�e Planifi�e", //   "Baseline Duration",
      "Dur�e R�elle", //   "Actual Duration",
      "Dur�e Restante", //   "Remaining Duration",
      "% Achev�", //   "% Complete",
      "Variation de Dur�e", //   "Duration Variance",
      "Dur�e1", //   "Duration1",
      "Dur�e2", //   "Duration2",
      "Dur�e3", //   "Duration3",
      null, //
      "D�but", //   "Start",
      "Fin", //   "Finish",
      "D�but Au Plus T�t", //   "Early Start",
      "Fin Au Plus T�t", //   "Early Finish",
      "D�but Au Plus Tard", //   "Late Start",
      "Fin Au Plus Tard", //   "Late Finish",
      "D�but Planifi�", //   "Baseline Start",
      "Fin Planifi�e", //   "Baseline Finish",
      "D�but R�el", //   "Actual Start",
      "Fin R�elle", //   "Actual Finish",
      "D�but1", //   "Start1",
      "Fin1", //   "Finish1",
      "D�but2", //   "Start2",
      "Fin2", //   "Finish2",
      "D�but3", //   "Start3",
      "Fin3", //   "Finish3",
      "Marge de D�but", //   "Start Variance",
      "Marge de Fin", //   "Finish Variance",
      "Date Contrainte", //   "Constraint Date",
      null, //
      "Pr�d�cesseurs", //   "Predecessors",
      "Successeurs", //   "Successors",
      "Noms Ressources", //   "Resource Names",
      "Ressources Initiales", //   "Resource Initials",
      "ID Unique des Pr�d�cesseurs",//   "Unique ID Predecessors",
      "ID Unique des Successeurs", //   "Unique ID Successors",
      null, //
      null, //
      null, //
      null, //
      "Fixe", //   "Fixed",
      "Jalon", //   "Milestone",
      "Critique", //   "Critical",
      "Marqu�", //   "Marked",
      "Rollup", //   "Rollup",
      "BCWS", //   "BCWS",
      "BCWP", //   "BCWP",
      "SV", //   "SV",
      "CV", //   "CV",
      null, //
      "ID", //   "ID",
      "Type de Contrainte", //   "Constraint Type",
      "D�lai", //   "Delay",
      "Marge Libre", //   "Free Slack",
      "Marge Totale", //   "Total Slack",
      "Priorit�", //   "Priority",
      "Arquivo Subprojeto", //   "Subproject File",
      "Projet", //   "Project",
      "ID Unique", //   "Unique ID",
      "Num�ro Externe", //   "Outline Number",
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      "Indicateur1", //   "Flag1",
      "Indicateur2", //   "Flag2",
      "Indicateur3", //   "Flag3",
      "Indicateur4", //   "Flag4",
      "Indicateur5", //   "Flag5",
      "Indicateur6", //   "Flag6",
      "Indicateur7", //   "Flag7",
      "Indicateur8", //   "Flag8",
      "Indicateur9", //   "Flag9",
      "Indicateur10", //   "Flag10",
      "Sommaire", //   "Summary",
      "Objets", //   "Objects",
      "Champs Li�s", //   "Linked Fields",
      "Cacher Barre", //   "Hide Bar",
      null, //
      "Cr��", //   "Created",
      "D�but4", //   "Start4",
      "Fin4", //   "Finish4",
      "D�but5", //   "Start5",
      "Fin5", //   "Finish5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "Confirm�", //   "Confirmed",
      "Mise � Jour N�cessaire", //   "Update Needed",
      null, //
      null, //
      null, //
      "Numero1", //   "Number1",
      "Numero2", //   "Number2",
      "Numero3", //   "Number3",
      "Numero4", //   "Number4",
      "Numero5", //   "Number5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "Stop", //   "Stop",
      "Continuer Pas Plus T�t Que", //   "Resume No Earlier Than",
      "Continuer" //   "Resume"
   };

   private static final String[] RESOURCE_NAMES_DATA =
   {
      null, //
      "Nom", //   "Name",
      "Initiales", //   "Initials",
      "Groupe", //   "Group",
      "Code", //   "Code",
      "Texte1", //   "Text1",
      "Texte2", //   "Text2",
      "Texte3", //   "Text3",
      "Texte4", //   "Text4",
      "Texte5", //   "Text5",
      "Notes", //   "Notes",
      "Adresse de messagerie", //   "Email Address",
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      "Travail", //   "Work",
      "Travail Planifi�", //   "Baseline Work",
      "Travail R�el", //   "Actual Work",
      "Travail Restant", //   "Remaining Work",
      "Heures sup.", //   "Overtime Work",
      "Variation de Travail", //   "Work Variance",
      "% Travail achev�", //   "% Work Complete",
      null, //
      null, //
      null, //
      "Co�t", //   "Cost",
      "Co�t Planifi�", //   "Baseline Cost",
      "Co�t R�el", //   "Actual Cost",
      "Co�t Restant", //   "Remaining Cost",
      "Variation de Co�t", //   "Cost Variance",
      null, //
      null, //
      null, //
      null, //
      null, //
      "ID", //   "ID",
      "Unit�s Maximales", //   "Max Units",
      "Taux Standard", //   "Standard Rate",
      "Taux heures sup.", //   "Overtime Rate",
      "Co�t par Utilisation", //   "Cost Per Use",
      "Resulte em", //   "Accrue At",
      "En Surcharge", //   "Overallocated",
      "Pointe", //   "Peak",
      "Calendrier de Base", //   "Base Calendar",
      "ID Unique", //   "Unique ID",
      "Objets", //   "Objects",
      "Champs Li�s", //   "Linked Fields",
   };

   private static final Object[][] RESOURCE_DATA =
   {
      {
         LocaleData.FILE_DELIMITER,
         ";"
      },
      {
         LocaleData.PROGRAM_NAME,
         "Microsoft Project for Windows"
      },
      {
         LocaleData.FILE_VERSION,
         "4.0"
      },
      {
         LocaleData.CODE_PAGE,
         CodePage.ANSI
      },

      {
         LocaleData.CURRENCY_SYMBOL,
         ""
      },
      {
         LocaleData.CURRENCY_SYMBOL_POSITION,
         CurrencySymbolPosition.BEFORE
      },
      {
         LocaleData.CURRENCY_DIGITS,
         Integer.valueOf(2)
      },
      {
         LocaleData.CURRENCY_THOUSANDS_SEPARATOR,
         "."
      },
      {
         LocaleData.CURRENCY_DECIMAL_SEPARATOR,
         ","
      },

      {
         LocaleData.DATE_ORDER,
         DateOrder.DMY
      },
      {
         LocaleData.TIME_FORMAT,
         ProjectTimeFormat.TWENTY_FOUR_HOUR
      },
      {
         LocaleData.DATE_SEPARATOR,
         "/"
      },
      {
         LocaleData.TIME_SEPARATOR,
         ":"
      },
      {
         LocaleData.AM_TEXT,
         ""
      },
      {
         LocaleData.PM_TEXT,
         ""
      },
      {
         LocaleData.DATE_FORMAT,
         ProjectDateFormat.DD_MM_YYYY
      },
      {
         LocaleData.BAR_TEXT_DATE_FORMAT,
         Integer.valueOf(0)
      },
      {
         LocaleData.NA,
         "NA"
      },

      {
         LocaleData.YES,
         "Oui"
      },
      {
         LocaleData.NO,
         "Non"
      },

      {
         LocaleData.TIME_UNITS_ARRAY,
         TIME_UNITS_ARRAY_DATA
      },
      {
         LocaleData.TIME_UNITS_MAP,
         TIME_UNITS_MAP_DATA
      },

      {
         LocaleData.ACCRUE_TYPES,
         ACCRUE_TYPES_DATA
      },
      {
         LocaleData.RELATION_TYPES,
         RELATION_TYPES_DATA
      },
      {
         LocaleData.PRIORITY_TYPES,
         PRIORITY_TYPES_DATA
      },
      {
         LocaleData.CONSTRAINT_TYPES,
         CONSTRAINT_TYPES_DATA
      },

      {
         LocaleData.TASK_NAMES,
         TASK_NAMES_DATA
      },
      {
         LocaleData.RESOURCE_NAMES,
         RESOURCE_NAMES_DATA
      }
   };
}
