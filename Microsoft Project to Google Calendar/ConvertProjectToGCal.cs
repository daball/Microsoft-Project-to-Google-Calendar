using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using net.sf.mpxj;
using net.sf.mpxj.mpp;
using net.sf.mpxj.reader;

namespace Microsoft_Project_to_Google_Calendar
{
    public partial class ConvertProjectToGCal : Form
    {
        public ConvertProjectToGCal()
        {
            InitializeComponent();
        }

        private void buttonBrowse_Click(object sender, EventArgs e)
        {
            if (this.openFileDialog1.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                this.textBoxFileName.Text = this.openFileDialog1.FileName;
            }
        }

        private void buttonExit_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void buttonAbout_Click(object sender, EventArgs e)
        {
            AboutForm about = new AboutForm();
            about.ShowDialog();
        }

        private void buttonGo_Click(object sender, EventArgs e)
        {
            //read MS Project file
            ProjectFile projectFile = this.readProjectFile(this.textBoxFileName.Text);
            
            //get task list from open project file
            Task[] tasks = this.getAllProjectTasks(projectFile);

            //debug: print all tasks found
            foreach (Task task in tasks)
            {
                System.Diagnostics.Debug.WriteLine("Task found: " + task.getName());
            }
        }

        /// <summary>
        /// Reads MS Project file and returns ProjectFile instance.
        /// </summary>
        /// <param name="mppFile">MPP file to read.</param>
        /// <returns></returns>
        public ProjectFile readProjectFile(String mppFile)
        {
            //get the reader
            ProjectReader reader = ProjectReaderUtility.getProjectReader(mppFile);
            //read the file out
            return reader.read(mppFile);
        }

        /// <summary>
        /// Gets all tasks from ProjectFile instance.
        /// </summary>
        /// <param name="projectFile">ProjectFile instance.</param>
        /// <returns>List of tasks.</returns>
        public Task[] getAllProjectTasks(ProjectFile projectFile)
        {
            //get tasks
            java.util.List taskList = projectFile.getAllTasks();

            //convert to array
            object[] allTaskObjs = taskList.toArray();

            //create alternate storage
            Task[] allTasks = new Task[allTaskObjs.Length];

            //then cast each object to a Task
            for(int o = 0; o < allTaskObjs.Length; o++)
            {
                //cast
                allTasks[o] = (Task)allTaskObjs[o];
                //then nullify the initial value, be nice to your memory and it WILL return the favor
                allTaskObjs[o] = null;
            }
            
            //nullify primary storage
            allTaskObjs = null;

            //then return the secondary list, otherwise permit a run-time exception
            //this is a wild bunch of magic anyways
            return allTasks;
        }
    }
}
