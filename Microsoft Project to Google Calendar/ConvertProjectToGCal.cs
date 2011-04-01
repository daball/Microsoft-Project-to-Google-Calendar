using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

//needed for MPXJ (to handle MS Project)
using net.sf.mpxj;
using net.sf.mpxj.mpp;
using net.sf.mpxj.reader;

//needed for Google Data API (to handle Google Calendar)
using Google.GData.Calendar;
using Google.GData.Client;
using Google.GData.Extensions;

namespace Microsoft_Project_to_Google_Calendar
{
    public partial class ConvertProjectToGCal : Form
    {
        public ConvertProjectToGCal()
        {
            InitializeComponent();
        }

        private void toolStripButtonBrowse_Click(object sender, EventArgs e)
        {
            if (this.openFileDialog1.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                this.toolStripComboBoxPath.Text = this.openFileDialog1.FileName;
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

        private void toolStripComboBoxPath_TextChanged(object sender, EventArgs e)
        {
            //check for valid file name
            if (!System.IO.File.Exists(toolStripComboBoxPath.Text))
            {
                //otherwise bounce and let the user keep typing
                return;
            }

            //since the file name is valid, let's "try" to open the MS Project file
            ProjectFile projectFile = null;
            try
            {
                projectFile = this.readProjectFile(this.toolStripComboBoxPath.Text);
            }
            catch (Exception ex)
            {
                MessageBox.Show("This file exists but it is not a valid Microsoft Project file.\n\nInner exception details:\n"+ex.ToString());

                //bounce and let the user try again
                return;
            }
            System.Diagnostics.Debug.WriteLine("Step 1: Opened MS Project File \"" + this.toolStripComboBoxPath.Text + "\".");

            //get task list from open project file
            Task[] tasks = this.getAllProjectTasks(projectFile);
            System.Diagnostics.Debug.WriteLine("Step 2: Returned " + tasks.Length + " tasks.");

            //erase the last list, if any
            listViewTasks.Items.Clear();

            foreach (Task task in tasks)
            {
                //storage
                java.util.Date startDate = task.getStart();
                java.util.Date stopDate = task.getStop();
                string name = task.getName();

                //debug: print each task found to the stderr
                System.Diagnostics.Debug.Write(" >> Task by name \"" + name + "\"");
                if (startDate != null && stopDate != null)
                    System.Diagnostics.Debug.WriteLine(" starting on \"" + startDate.toString() + "\" and ending on \"" + stopDate + "\".");
                else if (startDate != null)
                    System.Diagnostics.Debug.WriteLine(" starting on \"" + startDate.toString() + "\".");
                else if (stopDate != null)
                    System.Diagnostics.Debug.WriteLine(" ending on \"" + startDate.toString() + "\".");
                else
                    System.Diagnostics.Debug.WriteLine(".");

                //in order to be considered data for the production app,
                //it must contain a start date and a task name
                if (name != null && name.Trim().Length > 0 && startDate != null)
                {
                    //it must contain some valid data, so let's enroll it into our list

                    //first generate a ListViewItem and fill it up
                    ListViewItem item = new ListViewItem();
                    item.Text = task.getName().Trim();
                    item.SubItems.Add(startDate.toString());
                    if (stopDate != null) item.SubItems.Add(stopDate.toString());                        

                    //mark this tag with the Task object, we'll cast it back out later from the checked items list
                    item.Tag = task;

                    //check the item, by default we'd want all the events migrated
                    item.Checked = true;

                    //now add this to the list
                    this.listViewTasks.Items.Add(item);
                }
            }
        }

        private void buttonGo_Click(object sender, EventArgs e)
        {            
        }

        /// <summary>
        /// Reads MS Project file and returns ProjectFile instance.
        /// </summary>
        /// <param name="mppFile">MPP file to read.</param>
        /// <returns></returns>
        protected ProjectFile readProjectFile(String mppFile)
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
        protected Task[] getAllProjectTasks(ProjectFile projectFile)
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

        /// <summary>
        /// Translates a MS Project task into a Google Data API CalendarEntry for Google Calendar.
        /// </summary>
        /// <param name="task">MS Project task instance.</param>
        /// <returns>A new EventEntry from Google Data API.</returns>
        protected EventEntry translateProjectTaskToCalendarEntry(Task task)
        {
            //create EventEntry for Google API
            EventEntry eventEntry = new EventEntry();

            //create title, assign MS Project task name as title
            AtomTextConstruct atomTitle = new AtomTextConstruct(AtomTextConstructElementType.Title, task.getName());

            //assign title to EventEntry
            eventEntry.Title = atomTitle;

            //create When object for Google API
            When dateTime = new When();

            //write these down
            java.util.Date taskStart = task.getStart();
            java.util.Date taskStop = task.getStop();

            //now convert them from Java to .Net and tell When what's up
            dateTime.StartTime = new DateTime(taskStart.getYear(), taskStart.getMonth(), taskStart.getDate(), taskStart.getHours(), taskStart.getMinutes(), taskStart.getSeconds(), DateTimeKind.Local);
            dateTime.EndTime = new DateTime(taskStop.getYear(), taskStop.getMonth(), taskStop.getDate(), taskStop.getHours(), taskStop.getMinutes(), taskStop.getSeconds(), DateTimeKind.Local);

            //add the When to the event entry
            eventEntry.Times.Add(dateTime);

            //return the EventEntry
            return eventEntry;
        }

        /// <summary>
        /// Updates an open Google Calendar with all the MS Project tasks.
        /// </summary>
        /// <param name="tasks"></param>
        /// <param name="calendarService"></param>
        protected void insertProjectTasksIntoGoogleCalendar(Task[] tasks, CalendarService calendarService)
        {

        }

    }
}
