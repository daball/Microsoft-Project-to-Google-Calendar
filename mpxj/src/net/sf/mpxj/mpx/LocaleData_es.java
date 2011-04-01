/*
 * file:       LocaleData_es.java
 * author:     Agust�n Bart�
 *             Jon Iles
 * copyright:  (c) Packwood Software 2004
 * date:       09/10/2008
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
import java.util.Map;

import net.sf.mpxj.CodePage;
import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.DateOrder;
import net.sf.mpxj.ProjectDateFormat;
import net.sf.mpxj.ProjectTimeFormat;

/**
 * This class defines the Spanish resources required by MPX files.
 */
public final class LocaleData_es extends ListResourceBundle
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
      }, // Minutes - Minutos
      {
         "h"
      }, // Hours - Horas
      {
         "d"
      }, // Days - D�as
      {
         "s"
      }, // Weeks - Semanas
      {
         "ms"
      }, // Months - Meses
      {
         "a"
      }, // Years - A�os
      {
         "%"
      }, // Percent - Porcentaje
      {
         "em"
      }, // Elapsed Minutes - Minutos Transcurridos
      {
         "eh"
      }, // Elapsed Hours - Horas Transcurridos
      {
         "ed"
      }, // Elapsed Days - Días Transcurridos
      {
         "es"
      }, // Elapsed Weeks - Semanas Transcurridas
      {
         "ems"
      }, // Elapsed Months - Meses Transcurridos
      {
         "ea"
      }, // Elapsed Years - A�os Transcurridos
      {
         "e%"
      }
   // Elapsed Percent - Porcentaje Transcurridos
   };

   private static final Map<String, Integer> TIME_UNITS_MAP_DATA = new HashMap<String, Integer>();

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
      "Comienzo", // Start
      "Fin", // End
      "Prorrateo" // Prorated
   };

   private static final String[] RELATION_TYPES_DATA =
   {
      "FF", // FF - Fin a fin (FF)
      "FC", // FS - Fin a comienzo (FC)
      "CF", // SF - Comienzo a fin (CF) 
      "CC" // SS - Comienzo a comienzo (CC)
   };

   private static final String[] PRIORITY_TYPES_DATA =
   {
      "M�nima", // Lowest
      "Muy Baja", // Very Low
      "M�s Baja", // Lower
      "Baja", // Low
      "Media", // Medium
      "Alta", // High
      "M�s Alta", // Higher
      "Muy Alta", // Very High
      "M�xima", // Highest
      "No Redistribuir" // Do Not Level
   };

   private static final String[] CONSTRAINT_TYPES_DATA =
   {
      "Lo antes posible", // As Soon As Possible
      "Lo m�s tarde posible", // As Late As Possible
      "Debe comenzar el", // Must Start On 
      "Debe finalizar el", // Must Finish On
      "No comenzar antes del", // Start No Earlier Than
      "No comenzar despu�s del", // Start No Later Than
      "No finalizar antes del", // Finish No Earlier Than
      "No finalizar despu�s del" // Finish No Later Than 
   };

   // TODO Complete TASK_NAMES_DATA translation
   private static final String[] TASK_NAMES_DATA =
   {
      null, // ???
      "Nombre", // Name
      "WBS", // TODO Translate "WBS"
      "Nivel de Esquema", // Outline Level
      "Texto1", // Text1
      "Texto2", // Text2
      "Texto3", // Text3
      "Texto4", // Text4
      "Texto5", // Text5
      "Texto6", // Text6
      "Texto7", // Text7
      "Texto8", // Text8
      "Texto9", // Text9
      "Texto10", // Text10
      "Notas", // Notes
      "Contacto", // Contact
      "Grupo de Recursos", // Resource Group
      null, // ???
      null, // ???
      null, // ???
      "Trabajo", // Work
      "L�nea de Base de Trabajo", // Baseline Work
      "Trabajo Real", // Actual Work
      "Trabajo Restante", // Remaining Work
      "Variaci�n de Trabajo", // Work Variance
      "% Trabajo Completado", // % Work Complete
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "Costo", // Cost
      "L�nea de Base de Costo", // Baseline Cost
      "Costo Real", // Actual Cost
      "Costo Restante", // Remaining Cost
      "Variaci�n de Costo", // Cost Variance
      "Costo Fijo", // Fixed Cost
      "Costo1", // Cost1
      "Costo2", // Cost2
      "Costo3", // Cost3
      null, // ???
      "Duraci�n", // Duration
      "L�nea Base de Duraci�n", // Baseline Duration
      "Duraci�n Real", // Actual Duration
      "Duraci�n Restante", // Remaining Duration
      "% Completado", // % Complete
      "Variaci�n de Duraci�n", // Duration Variance
      "Duraci�n1", // Duration1
      "Duraci�n2", // Duration2
      "Duraci�n3", // Duration3
      null, // ???
      "Comienzo", // Start
      "Fin", // Finish
      "Comienzo Temprano", // Early Start
      "Fin Temprano", // Early Finish
      "Comienzo Tard�o", // Late Start
      "Fin Tardío", // Late Finish
      "L�nea de Base de Comienzo", // Baseline Start
      "L�nea de Base de Fin", // Baseline Finish
      "Comienzo Real", // Actual Start
      "Fin Real", // Actual Finish
      "Comienzo1", // Start1
      "Fin1", // Finish1
      "Comienzo2", // Start2
      "Fin2", // Finish2
      "Comienzo3", // Start3
      "Fin3", // Finish3
      "Variaci�n de Comienzo", // Start Variance
      "Variaci�n de Fin", // Finish Variance
      "Fecha de Restricci�n", // Constraint Date
      null, // ???
      "Predecesoras", // Predecessors
      "Sucesoras", // Successors
      "Nombres de Recursos", // Resource Names
      "Iniciales de Recursos", // Resource Initials
      "Predecesoras de ID �nico", // Unique ID Predecessors
      "Sucesoras de ID �nico", // Unique ID Successors
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "Fijo", // Fixed
      "Hito", // Milestone
      "Cr�tico", // Critical
      "Marked", // TODO Translate "Marked"
      "Rollup", // TODO Translate "Rollup"
      "BCWS", // TODO Translate "BCWS"
      "BCWP", // TODO Translate "BCWP"
      "SV", // TODO Translate "SV"
      "CV", // TODO Translate "CV"
      null, // ???
      "ID", // TODO Translate "ID"
      "Tipo de Restricci�n", // Constraint Type
      "Demora", // Delay
      "Free Slack", // TODO Translate "Free Slack"
      "Total Slack", // TODO Translate "Total Slack"
      "Prioridad", // Priority
      "Subproject File", // TODO Translate "Subproject File"
      "Proyecto", // Project
      "ID �nico", // Unique ID
      "N�mero de Esquema", // Outline Number
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "Indicador1", // Flag1
      "Indicador2", // Flag2
      "Indicador3", // Flag3
      "Indicador4", // Flag4
      "Indicador5", // Flag5
      "Indicador6", // Flag6
      "Indicador7", // Flag7
      "Indicador8", // Flag8
      "Indicador9", // Flag9
      "Indicador10", // Flag10
      "Summary", // TODO Translate "Summary"
      "Objetos", // Objects
      "Campos Enlazados", // Linked Fields
      "Ocultar Barra", // Hide Bar
      null, // ???
      "Creada", // Created
      "Comienzo4", // Start4
      "Fin4", // Finish4
      "Comienzo5", // Start5
      "Fin5", // Finish5
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "Confirmed", // TODO Translate "Confirmed"
      "Update Needed", // TODO Translate "Update Needed"
      null, // ???
      null, // ???
      null, // ???
      "N�mero1", // Number1
      "N�mero2", // Number2
      "N�mero3", // Number3
      "N�mero4", // Number4
      "N�mero5", // Number5
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "Stop", // TODO Translate "Stop"
      "Resume No Earlier Than", // TODO Translate "Resume No Earlier Than"
      "Resume" // TODO Translate "Resume"
   };

   // TODO Complete RESOURCE_NAMES_DATA translation
   private static final String[] RESOURCE_NAMES_DATA =
   {
      null, // ???
      "Nombre", // Name
      "Iniciales", // Initials
      "Grupo", // Group
      "C�digo", // Code
      "Texto1", // Text1
      "Texto2", // Text2
      "Texto3", // Text3
      "Texto4", // Text4
      "Texto5", // Text5
      "Notas", // Notes
      "Correo electr�nico", // Email Address
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "Trabajo", // Work
      "L�nea de Base de Trabajo", // Baseline Work
      "Trabajo Real", // Actual Work
      "Trabajo Restante", // Remaining Work
      "Overtime Work", // TODO Translate "Overtime Work"
      "Variaci�n de Trabajo", // Work Variance
      "% Trabajo Completado", // % Work Complete
      null, // ???
      null, // ???
      null, // ???
      "Costo", // Cost
      "L�nea de Base de Costo", // Baseline Cost
      "Costo Real", // Actual Cost
      "Costo Restante", // Remaining Cost
      "Variaci�n de Costo", // Cost Variance
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      null, // ???
      "ID", // TODO Translate "ID"
      "Max Units", // TODO Translate "Max Units"
      "Standard Rate", // TODO Translate "Standard Rate"
      "Overtime Rate", // TODO Translate "Overtime Rate"
      "Costo Por Uso", // Cost Per Use
      "Acumulaci�n de costos", // Accrue At
      "Sobreasignado", // Overallocated
      "Pico", // Peak
      "Calendario Base", // Base Calendar
      "ID �nico", // Unique ID
      "Objetos", // Objects
      "Campos Enlazados", // Linked Fields
   };

   private static final Object[][] RESOURCE_DATA =
   {
      {
         LocaleData.FILE_DELIMITER,
         ","
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
         "$"
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
         LocaleData.DEFAULT_START_TIME,
         Integer.valueOf(480)
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
         "am"
      },
      {
         LocaleData.PM_TEXT,
         "pm"
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
         "S�"
      },
      {
         LocaleData.NO,
         "No"
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
