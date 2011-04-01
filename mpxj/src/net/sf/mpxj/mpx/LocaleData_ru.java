/*
 * file:       LocaleData_ru.java
 * author:     Roman Bilous
 *             Jon Iles
 * copyright:  (c) Packwood Software 2004
 * date:       11/05/2010
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

import net.sf.mpxj.CurrencySymbolPosition;
import net.sf.mpxj.DateOrder;
import net.sf.mpxj.ProjectDateFormat;
import net.sf.mpxj.ProjectTimeFormat;

/**
 * This class defines the Russian translation of resource required by MPX files.
 */
public final class LocaleData_ru extends ListResourceBundle
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
         "������"
      },
      {
         "���"
      },
      {
         "����"
      },
      {
         "������"
      },
      {
         "�����"
      },
      {
         "���"
      },
      {
         "%"
      },
      {
         "������ ������"
      },
      {
         "������ ���"
      },
      {
         "������ ����"
      },
      {
         "������ ������"
      },
      {
         "������ �����"
      },
      {
         "������ ���"
      },
      {
         "������ %"
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
      "������", //   "Start",
      "�����", //   "End",
      "������������" //   "Prorated"
   };

   private static final String[] RELATION_TYPES_DATA =
   {
      "��", //   "FF",
      "��", //   "FS",
      "��", //   "SF",
      "��" //   "SS"
   };

   private static final String[] PRIORITY_TYPES_DATA =
   {
      "����� ������", //   "Lowest",
      "����� ������", //   "Very Low",
      "������", //   "Lower",
      "���� ��������", //   "Low",
      "�������", //   "Medium",
      "���� ��������", //   "High",
      "�������", //   "Higher",
      "����� �������", //   "Very High",
      "���������", //   "Highest",
      "��� ����������" //   "Do Not Level"
   };

   private static final String[] CONSTRAINT_TYPES_DATA =
   {
      "��� ����� ������", //   "As Soon As Possible",
      "��� ����� �����", //   "As Late As Possible",
      "������ ��������", //   "Must Start On",
      "������ �����������", //   "Must Finish On",
      "�������� �� ������", //   "Start No Earlier Than",
      "�������� �� �����", //   "Start No Later Than",
      "����������� �� ������", //   "Finish No Earlier Than",
      "����������� �� �����" //   "Finish No Later Than"
   };

   private static final String[] TASK_NAMES_DATA =
   {
      null, //
      "��������", //   "Name",
      "WBS", //   "WBS",
      "������� �������", //   "Outline Level",
      "�����1", //   "Text1",
      "�����2", //   "Text2",
      "�����3", //   "Text3",
      "�����4", //   "Text4",
      "�����5", //   "Text5",
      "�����6", //   "Text6",
      "�����7", //   "Text7",
      "�����8", //   "Text8",
      "�����9", //   "Text9",
      "�����10", //   "Text10",
      "����������", //   "Notes",
      "�������", //   "Contact",
      "������ ��������", //   "Resource Group",
      null, //
      null, //
      null, //
      "������", //   "Work",
      "������������� �����", //   "Baseline Work",
      "��������� �����", //   "Actual Work",
      "�������� �����", //   "Remaining Work",
      "����������� �����", //   "Work Variance",
      "% ����� ���������", //   "% Work Complete",
      null, //
      null, //
      null, //
      null, //
      "���������", //   "Cost",
      "��������������� ���������", //   "Baseline Cost",
      "�������� ���������", //   "Actual Cost",
      "��������� ���������", //   "Remaining Cost",
      "������������ ���������", //   "Cost Variance",
      "������������� ���������", //   "Fixed Cost",
      "���������1", //   "Cost1",
      "���������2", //   "Cost2",
      "���������3", //   "Cost3",
      null, //
      "������������", //   "Duration",
      "��������������� ������������", //   "Baseline Duration",
      "�������� ������������", //   "Actual Duration",
      "��������� ������������", //   "Remaining Duration",
      "% ���������", //   "% Complete",
      "������������ ������������", //   "Duration Variance",
      "������������1", //   "Duration1",
      "������������2", //   "Duration2",
      "������������3", //   "Duration3",
      null, //
      "������", //   "Start",
      "���������", //   "Finish",
      "���������� ������", //   "Early Start",
      "���������� ���������", //   "Early Finish",
      "��������� ������", //   "Late Start",
      "��������� ���������", //   "Late Finish",
      "�������������� ������", //   "Baseline Start",
      "��������������� ���������", //   "Baseline Finish",
      "�������� ������", //   "Actual Start",
      "�������� ���������", //   "Actual Finish",
      "������1", //   "Start1",
      "���������", //   "Finish1",
      "������2", //   "Start2",
      "���������2", //   "Finish2",
      "������3", //   "Start3",
      "���������3", //   "Finish3",
      "�������� ������", //   "Start Variance",
      "�������� ���������", //   "Finish Variance",
      "�������������� ����", //   "Constraint Date",
      null, //
      "���������������", //   "Predecessors",
      "����������", //   "Successors",
      "��� �������", //   "Resource Names",
      "�������� �������", //   "Resource Initials",
      "���������� ID ���������������",//   "Unique ID Predecessors",
      "���������� ID ����������", //   "Unique ID Successors",
      null, //
      null, //
      null, //
      null, //
      "�������������", //   "Fixed",
      "����", //   "Milestone",
      "�����������", //   "Critical",
      "����������", //   "Marked",
      "����������", //   "Rollup",
      "��������� ��������������� �����", //   "BCWS",
      "��������� ����������� �����", //   "BCWP",
      "��������� ���������� �����", //   "SV",
      "����������� ����������", //   "CV",
      null, //
      "ID", //   "ID",
      "�������������� ���", //   "Constraint Type",
      "��������", //   "Delay",
      "��������", //   "Free Slack",
      "����� ��������", //   "Total Slack",
      "���������", //   "Priority",
      "���� ����������", //   "Subproject File",
      "������", //   "Project",
      "���������� ID", //   "Unique ID",
      "������� �����", //   "Outline Number",
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
      "����1", //   "Flag1",
      "����2", //   "Flag2",
      "����3", //   "Flag3",
      "����4", //   "Flag4",
      "����5", //   "Flag5",
      "����6", //   "Flag6",
      "����7", //   "Flag7",
      "����8", //   "Flag8",
      "����9", //   "Flag9",
      "����10", //   "Flag10",
      "�����", //   "Summary",
      "�������", //   "Objects",
      "�������� �����", //   "Linked Fields",
      "������� ���� ", //   "Hide Bar",
      null, //
      "�������", //   "Created",
      "������4", //   "Start4",
      "���������4", //   "Finish4",
      "������5", //   "Start5",
      "���������5", //   "Finish5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "������������", //   "Confirmed",
      "��������� � ����������", //   "Update Needed",
      null, //
      null, //
      null, //
      "�����1", //   "Number1",
      "�����2", //   "Number2",
      "�����3", //   "Number3",
      "�����4", //   "Number4",
      "�����5", //   "Number5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "����", //   "Stop",
      "������ �� ������ ���", //   "Resume No Earlier Than",
      "������" //   "Resume"
   };

   private static final String[] RESOURCE_NAMES_DATA =
   {
      null, //
      "���", //   "Name",
      "��������", //   "Initials",
      "������", //   "Group",
      "���", //   "Code",
      "�����1", //   "Text1",
      "�����2", //   "Text2",
      "�����3", //   "Text3",
      "�����4", //   "Text4",
      "�����5", //   "Text5",
      "����������", //   "Notes",
      "Email", //   "Email Address",
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      "������", //   "Work",
      "������������� �����", //   "Baseline Work",
      "��������� �����", //   "Actual Work",
      "�������� �����", //   "Remaining Work",
      "����������� �����", //   "Work Variance",
      "% ����� ���������", //   "% Work Complete",
      null, //
      null, //
      null, //
      "���������", //   "Cost",
      "��������������� ���������", //   "Baseline Cost",
      "�������� ���������", //   "Actual Cost",
      "��������� ���������", //   "Remaining Cost",
      "������������ ���������", //   "Cost Variance",
      null, //
      null, //
      null, //
      null, //
      null, //
      "ID", //   "ID",
      "������������ ��������", //   "Max Units",
      "����������� ��������", //   "Standard Rate",
      "������������ ��������", //   "Overtime Rate",
      "��������� �������������", //   "Cost Per Use",
      "���������", //   "Accrue At",
      "�������������", //   "Overallocated",
      "���", //   "Peak",
      "�������� ���������", //   "Base Calendar",
      "���������� ID", //   "Unique ID",
      "������", //   "Objects",
      "��������� ����", //   "Linked Fields",
   };

   private static final Object[][] RESOURCE_DATA =
   {
      {
         LocaleData.FILE_DELIMITER,
         ";"
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
         "����������"
      },

      {
         LocaleData.YES,
         "��"
      },
      {
         LocaleData.NO,
         "���"
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
