using System;
using System.Collections;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Microsoft_Project_to_Google_Calendar
{
    //C#
    // Implements the manual sorting of items by column.
    // from http://msdn.microsoft.com/en-us/library/ms996467.aspx
    class ListViewItemComparer : IComparer
    {
        private int col;
        public ListViewItemComparer()
        {
            col = 0;
        }
        public ListViewItemComparer(int column)
        {
            col = column;
        }
        public int Compare(object x, object y)
        {
            int returnVal = -1;
            returnVal = String.Compare(((ListViewItem)x).SubItems[col].Text,
            ((ListViewItem)y).SubItems[col].Text);
            return returnVal;
        }
    }
}
