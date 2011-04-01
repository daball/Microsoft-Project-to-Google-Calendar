using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;

namespace Microsoft_Project_to_Google_Calendar
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new ConvertProjectToGCal());
        }
    }
}
