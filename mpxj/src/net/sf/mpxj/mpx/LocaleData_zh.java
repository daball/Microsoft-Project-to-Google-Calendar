/*
 * file:       LocaleData_zh.java
 * author:     Felix Tian
 *             Jon Iles
 * copyright:  (c) Packwood Software 2007
 * date:       15/11/2005
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
 * This class defines the Chinese translation of resource required by MPX files.
 */
public final class LocaleData_zh extends ListResourceBundle
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
         "d"
      },
      {
         "w"
      },
      {
         "mon"
      },
      {
         "y"
      },
      {
         "%"
      },
      {
         "em"
      },
      {
         "eh"
      },
      {
         "ed"
      },
      {
         "ew"
      },
      {
         "emon"
      },
      {
         "ey"
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
      "��ʼ", // "Start",
      "����", // "End",
      "������" // "Prorated"
   };

   private static final String[] RELATION_TYPES_DATA =
   {
      "FF", //   "FF",
      "FS", //   "FS",
      "SF", //   "SF",
      "SS" //   "SS"
   };

   private static final String[] PRIORITY_TYPES_DATA =
   {
      "Lowest",
      "Very Low",
      "Lower",
      "Low",
      "Medium",
      "High",
      "Higher",
      "Very High",
      "Highest",
      "Do Not Level"
   };

   private static final String[] CONSTRAINT_TYPES_DATA =
   {
      "Խ��Խ��", //   "As Soon As Possible",
      "Խ��Խ��", //   "As Late As Possible",
      "���뿪ʼ��", //   "Must Start On",
      "���������", //   "Must Finish On",
      "��������...��ʼ", //   "Start No Earlier Than",
      "��������...��ʼ", //   "Start No Later Than",
      "��������...���", //   "Finish No Earlier Than",
      "��������...���" //   "Finish No Later Than"
   };

   private static final String[] TASK_NAMES_DATA =
   {
      null, //
      "����", //   "Name",
      "WBS", //   "WBS",
      "��ټ���", //   "Outline Level",
      "�ı�1", //   "Text1",
      "�ı�2", //   "Text2",
      "�ı�3", //   "Text3",
      "�ı�4", //   "Text4",
      "�ı�5", //   "Text5",
      "�ı�6", //   "Text6",
      "�ı�7", //   "Text7",
      "�ı�8", //   "Text8",
      "�ı�9", //   "Text9",
      "�ı�10", //   "Text10",
      "��ע", //  "Notes",
      "��ϵ��", //  "Contact",
      "��Դ��", //   "Resource Group",
      null, //
      null, //
      null, //
      "��ʱ", //   "Work",
      "�Ƚϻ�׼��ʱ", //   "Baseline Work",
      "ʵ�ʹ�ʱ", //   "Actual Work",
      "ʣ�๤ʱ", //   "Remaining Work",
      "��ʱ����", //   "Work Variance",
      "��ʱ��ɰٷֱ�", //   "% Work Complete",
      null, //
      null, //
      null, //
      null, //
      "�ɱ�", //   "Cost",
      "�Ƚϻ�׼�ɱ�", //   "Baseline Cost",
      "ʵ�ʳɱ�", //   "Actual Cost",
      "ʣ��ɱ�", //   "Remaining Cost",
      "�ɱ�����", //   "Cost Variance",
      "�̶��ɱ�", //   "Fixed Cost",
      "�ɱ�1", //   "Cost1",
      "�ɱ�2", //   "Cost2",
      "�ɱ�3", //   "Cost3",
      null, //
      "����", //   "Duration",
      "�Ƚϻ�׼����", //   "Baseline Duration",
      "ʵ�ʹ���", //   "Actual Duration",
      "ʣ�๤��", //   "Remaining Duration",
      "��ɰٷֱ�", //   "% Complete",
      "���ڲ���", //   "Duration Variance",
      "����1", //   "Duration1",
      "����2", //   "Duration2",
      "����3", //   "Duration3",
      null, //
      "��ʼʱ��", //   "Start",
      "���ʱ��", //   "Finish",
      "���翪ʼʱ��", //   "Early Start",
      "�������ʱ��", //   "Early Finish",
      "����ʼʱ��", //   "Late Start",
      "�������ʱ��", //   "Late Finish",
      "�Ƚϻ�׼��ʼʱ��", //   "Baseline Start",
      "�Ƚϻ�׼���ʱ��", //   "Baseline Finish",
      "ʵ�ʿ�ʼʱ��", //   "Actual Start",
      "ʵ�����ʱ��", //   "Actual Finish",
      "��ʼʱ��1", //   "Start1",
      "���ʱ��1", //   "Finish1",
      "��ʼʱ��2", //   "Start2",
      "���ʱ��2", //   "Finish2",
      "��ʼʱ��3", //   "Start3",
      "���ʱ��3", //   "Finish3",
      "��ʱ�����", //   "Start Variance",
      "���ʱ�����", //   "Finish Variance",
      "��������", //   "Constraint Date",
      null, //
      "ǰ������", //   "Predecessors",
      "��������", //   "Successors",
      "��Դ����", //   "Resource Names",
      "��Դ��д", //   "Resource Initials",
      "Ψһ��ʶ��ǰ������", //   "Unique ID Predecessors",
      "Ψһ��ʶ�ź�������", //   "Unique ID Successors",
      null, //
      null, //
      null, //
      null, //
      "�̶�", //No such field named "Fixed" in project 2003
      "��̱�", //   "Milestone",
      "�ؼ�", //   "Critical",
      "�ѱ��", //   "Marked",
      "�ܳ�������", //   "Rollup",
      "BCWS", //   "BCWS",
      "BCWP", //   "BCWP",
      "SV", //   "SV",
      "CV", //   "CV",
      null, //
      "��ʶ��", //   "ID",
      "��������", //   "Constraint Type",
      "�ӳ�", //No such field named   "Delay" in project 2003
      "����ʱ��", //   "Free Slack",
      "��ʱ��", //   "Total Slack",
      "���ȼ�'", //   "Priority",
      "����Ŀ�ļ�", //   "Subproject File",
      "��Ŀ", //   "Project",
      "Ψһ��ʶ��", //   "Unique ID",
      "�������", //   "Outline Number",
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
      "��־1", //   "Flag1",
      "��־2", //   "Flag2",
      "��־3", //   "Flag3",
      "��־4", //   "Flag4",
      "��־5", //   "Flag5",
      "��־6", //   "Flag6",
      "��־7", //   "Flag7",
      "��־8", //   "Flag8",
      "��־9", //   "Flag9",
      "��־10", //   "Flag10",
      "ժҪ", //   "Summary",
      "������Ŀ", //   "Objects",
      "������", //   "Linked Fields",
      "��������ͼ", //   "Hide Bar",
      null, //
      "��������", //   "Created",
      "��ʼʱ��4", //   "Start4",
      "���ʱ��4", //   "Finish4",
      "��ʼʱ��5", //   "Start5",
      "���ʱ��5", //   "Finish5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "��ȷ��", //   "Confirmed",
      "��Ҫ����", //   "Update Needed",
      null, //
      null, //
      null, //
      "����1", //   "Number1",
      "����2", //   "Number2",
      "����3", //   "Number3",
      "����4", //   "Number4",
      "����5", //   "Number5",
      null, //
      null, //
      null, //
      null, //
      null, //
      "ֹͣ", //   "Stop",
      "������...���¿�ʼ", //   "Resume No Earlier Than",
      "���¿�ʼ" //   "Resume"
   };

   private static final String[] RESOURCE_NAMES_DATA =
   {
      null, //
      "����", //   "Name",
      "��д", //   "Initials",
      "��", //   "Group",
      "����", //   "Code",
      "�ı�1", //   "Text1",
      "�ı�2", //   "Text2",
      "�ı�3", //   "Text3",
      "�ı�4", //   "Text4",
      "�ı�5", //   "Text5",
      "��ע", //   "Notes",
      "�����ʼ���ַ", //   "Email Address",
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      null, //
      "��ʱ", //   "Work",
      "�Ƚϻ�׼��ʱ", //   "Baseline Work",
      "ʣ�๤ʱ", //   "Actual Work",
      "�Ӱ๤ʱ", //   "Remaining Work",
      "��ʱ��ɰٷֱ�", //   "Overtime Work",
      "��ʱ����", //   "Work Variance",
      "��ʱ��ɰٷֱ�", //   "% Work Complete",
      null, //
      null, //
      null, //
      "�ɱ�", //   "Cost",
      "�Ƚϻ�׼�ɱ�", //   "Baseline Cost",
      "ʵ�ʳɱ�", //   "Actual Cost",
      "ʣ��ɱ�", //   "Remaining Cost",
      "�ɱ�����", //   "Cost Variance",
      null, //
      null, //
      null, //
      null, //
      null, //
      "��ʶ��", //   "ID",
      "���λ", //   "Max Units",
      "��׼����", //   "Standard Rate",
      "�Ӱ����", //   "Overtime Rate",
      "ÿ��ʹ�óɱ�", //   "Cost Per Use",
      "�ɱ�����", //   "Accrue At",
      "���ȷ���", //   "Overallocated",
      "���ʹ����", //   "Peak",
      "��׼����", //   "Base Calendar",
      "Ψһ��ʶ��", //   "Unique ID",
      "������Ŀ", //   "Objects",
      "������", //   "Linked Fields",
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
         CodePage.ZH
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
         "��"
      },
      {
         LocaleData.NO,
         "��"
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
