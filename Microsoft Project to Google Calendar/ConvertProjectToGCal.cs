using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

//needed for MPXJ (to handle Microsoft® Project)
using net.sf.mpxj;
using net.sf.mpxj.mpp;
using net.sf.mpxj.reader;

//needed for Google® Data API (to handle Google® Calendar)
using Google.GData.Calendar;
using Google.GData.Client;
using Google.GData.Extensions;

namespace Microsoft_Project_to_Google_Calendar
{
    public partial class ConvertProjectToGCal : Form
    {
        //this will remain null until we press the login button and get a valid handle, test accordingly
        CalendarService calendarService = null;

        //this will be null until well after login, test accordingly
        List<CalendarEntry> calendars = null;

        public ConvertProjectToGCal()
        {
            InitializeComponent();
            //turn on the password char for the Google® password field
            this.toolStripTextBoxPassword.TextBox.UseSystemPasswordChar = true;
            //default to user's documents folder
            this.toolStripComboBoxPath.Text = this.openFileDialog1.InitialDirectory =
                Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
            this.toolStripComboBoxPath.Text += "\\";
        }

        private void toolStripButtonBrowse_Click(object sender, EventArgs e)
        {
            if (this.openFileDialog1.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                this.toolStripComboBoxPath.Text = this.openFileDialog1.FileName;
            }
        }

        private void buttonNext_Click(object sender, EventArgs e)
        {
            //goto the second tab
            this.tabControl1.SelectTab(1); //0-based index
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
            //update status
            if (toolStripComboBoxPath.Text == "" || !System.IO.File.Exists(toolStripComboBoxPath.Text))
            {
                toolStripStatusLabelFile.Text = "Status: Waiting for a Microsoft® Project file to load.";
                Application.DoEvents();
            }

            //check for valid file name
            if (!System.IO.File.Exists(toolStripComboBoxPath.Text))
            {
                //otherwise bounce and let the user keep typing
                return;
            }

            toolStripStatusLabelFile.Text = "Status: Opening file \"" + this.toolStripComboBoxPath.Text + "\". Please stand by...";
            Application.DoEvents();

            //since the file name is valid, let's "try" to open the Microsoft® Project file
            ProjectFile projectFile = null;
            try
            {
                projectFile = this.readProjectFile(this.toolStripComboBoxPath.Text);
            }
            catch (Exception ex)
            {
                toolStripStatusLabelFile.Text = "Error: The file specified is not a valid Microsoft® Project file. " + ex.Message;
                Application.DoEvents();

                //bounce and let the user try again
                return;
            }
            System.Diagnostics.Debug.WriteLine("Step 1: Opened Microsoft® Project file \"" + this.toolStripComboBoxPath.Text + "\".");

            toolStripStatusLabelFile.Text = "Status: Opened Microsoft® Project file \"" + this.toolStripComboBoxPath.Text + "\". Getting tasks...";
            Application.DoEvents();

            //get task list from open Project file
            Task[] tasks = this.getAllProjectTasks(projectFile);
            System.Diagnostics.Debug.WriteLine("Step 2: Returned " + tasks.Length + " tasks.");

            toolStripStatusLabelFile.Text = "Status: Opened Microsoft® Project file \"" + this.toolStripComboBoxPath.Text + "\" and gathered " + tasks.Length + " task candidates. Preparing checklist...";
            Application.DoEvents();

            //erase the last list, if any
            listViewTasks.Items.Clear();

            foreach (Task task in tasks)
            {
                //storage
                DateTime startDate = DateTime.MinValue;
                DateTime finishDate = DateTime.MinValue;

                //write these down
                java.util.Date taskStart = task.getStart();
                java.util.Date taskFinish = task.getFinish();

                //now split them up
                string[] taskStartComponents = new string[] { };
                if (taskStart != null) taskStartComponents = taskStart.toString().Split(new char[] { ' ', ':' }, StringSplitOptions.RemoveEmptyEntries);
                string[] taskFinishComponents = new string[] { };
                if (taskFinish != null) taskFinishComponents = taskFinish.toString().Split(new char[] { ' ', ':' }, StringSplitOptions.RemoveEmptyEntries);

                //now convert them from Java to .Net and tell Google®'s When what's up
                if (taskStart != null) startDate = DateTime.ParseExact(
                    taskStartComponents[7] + " " + //year
                    taskStartComponents[1] + " " + //month
                    taskStartComponents[2],
                    "yyyy MMM dd", System.Globalization.CultureInfo.InvariantCulture
                    );
                if (taskFinish != null) finishDate = DateTime.ParseExact(
                    taskFinishComponents[7] + " " + //year
                    taskFinishComponents[1] + " " + //month
                    taskFinishComponents[2],
                    "yyyy MMM dd", System.Globalization.CultureInfo.InvariantCulture
                    );

                string name = task.getName();

                //debug: print each task found to the stderr
                System.Diagnostics.Debug.Write(" >> Task by name \"" + name + "\"");
                if (taskStart != null && taskFinish != null)
                    System.Diagnostics.Debug.WriteLine(" starting on \"" + startDate.ToString("MMMM dd, yyyy") + "\" and ending on \"" + finishDate.ToString("MMMM dd, yyyy") + "\".");
                else if (taskStart != null)
                    System.Diagnostics.Debug.WriteLine(" starting on \"" + startDate.ToString("MMMM dd, yyyy") + "\".");
                else if (taskFinish  != null)
                    System.Diagnostics.Debug.WriteLine(" ending on \"" + finishDate.ToString("MMMM dd, yyyy") + "\".");
                else
                    System.Diagnostics.Debug.WriteLine(".");

                //in order to be considered data for the production app,
                //it must contain a start date and a task name
                if (name != null && name.Trim().Length > 0 && taskStart != null)
                {
                    //it must contain some valid data, so let's enroll it into our list

                    //first generate a ListViewItem and fill it up
                    ListViewItem item = new ListViewItem();
                    item.Text = task.getName().Trim();
                    item.SubItems.Add(startDate.ToString("yyyy-MM-dd (MMMM dd, yyyy)"));
                    if (taskFinish != null) item.SubItems.Add(finishDate.ToString("yyyy-MM-dd (MMMM dd, yyyy)"));

                    //mark this tag with the Task object, we'll cast it back out later from the checked items list
                    item.Tag = task;

                    //check the item, by default we'd want all the events migrated
                    item.Checked = true;

                    //now add this to the list
                    this.listViewTasks.Items.Add(item);
                }
            }

            // Set the ListViewItemSorter property to a new ListViewItemComparer object.
            this.listViewTasks.ListViewItemSorter = new ListViewItemComparer(1);
            // Call the sort method to manually sort.
            listViewTasks.Sort();

            toolStripStatusLabelFile.Text = "Status: Located " + this.listViewTasks.Items.Count + " validated tasks in Microsoft® Project file \"" + this.toolStripComboBoxPath.Text + "\".";
            Application.DoEvents();
        }

        private void updateGoogleStatus1OnKey()
        {
            if (toolStripTextBoxUserName.Text == "" && toolStripTextBoxPassword.Text == "")
            {
                toolStripStatusLabelGoogle1.Text = "Status: Waiting for the user name and password for your Google® Account.";
            }
            else if (toolStripTextBoxUserName.Text == "" && toolStripTextBoxPassword.Text != "")
            {
                toolStripStatusLabelGoogle1.Text = "Status: Waiting for the user name for your Google® Account.";
            }
            else if (toolStripTextBoxUserName.Text != "" && toolStripTextBoxPassword.Text == "")
            {
                toolStripStatusLabelGoogle1.Text = "Status: Waiting for the password for your Google® Account.";
            }
            else if (toolStripTextBoxUserName.Text != "" && toolStripTextBoxPassword.Text != "")
            {
                toolStripStatusLabelGoogle1.Text = "Status: Standing by for login. Press Login when you are ready.";
            }
            Application.DoEvents();
        }

        private void toolStripTextBoxUserName_KeyPress(object sender, KeyPressEventArgs e)
        {
            this.updateGoogleStatus1OnKey();
        }

        private void toolStripTextBoxPassword_KeyPress(object sender, KeyPressEventArgs e)
        {
            this.updateGoogleStatus1OnKey();
        }

        private void toolStripTextBoxUserName_TextChanged(object sender, EventArgs e)
        {
            this.updateGoogleStatus1OnKey();
        }

        private void toolStripTextBoxPassword_TextChanged(object sender, EventArgs e)
        {
            this.updateGoogleStatus1OnKey();
        }

        private void toolStripButtonLogin_Click(object sender, EventArgs e)
        {
            toolStripStatusLabelGoogle1.Text = "Status: Logging into Google® Calendar with the Google® Account information you provided. Please wait as I retrieve your Google® Calendar list...";
            Application.DoEvents();

            calendarService = new CalendarService(Application.ProductName);

            try
            {
                calendarService.setUserCredentials(toolStripTextBoxUserName.Text, toolStripTextBoxPassword.Text);
                CalendarQuery query = new CalendarQuery();
                query.Uri = new Uri("https://www.google.com/calendar/feeds/default/allcalendars/full");
                CalendarFeed resultFeed = (CalendarFeed)calendarService.Query(query);
                System.Diagnostics.Debug.WriteLine("Your calendars:");
                foreach (CalendarEntry entry in resultFeed.Entries)
                {
                    System.Diagnostics.Debug.WriteLine(" >> \"" + entry.Title.Text + "\"");
                    ListViewItem lvi = new ListViewItem(entry.Title.Text);
                    lvi.Tag = entry;
                    this.listViewCalendars.Items.Add(lvi);
                }
            }
            catch (AuthenticationException ex)
            {
                toolStripStatusLabelGoogle1.Text = "Error: Login to Google® Calendar failed with the Google® Account information you provided. Please check your user name and password and try again. " + ex.Message;
                Application.DoEvents();

                //bounce out to let the user try again
                return;
            }
            catch (GDataRequestException ex)
            {
                toolStripStatusLabelGoogle1.Text = "An error has occurred with the data request. Login failed. Message: " + ex.Message;
                Application.DoEvents();

                //bounce out to let the user try again
                return;
            }
            catch (Exception ex)
            {
                toolStripStatusLabelGoogle1.Text = "An unknown error has occurred. Login failed. Message: " + ex.Message;
                Application.DoEvents();

                //bounce out to let the user try again
                return;
            }


            this.toolStripStatusLabelGoogle1.Text = "Status: You have been logged in and I have imported the names of all your Google® Calendars.";
            Application.DoEvents();
        }

        private void toolStripButtonGo_Click(object sender, EventArgs e)
        {
            if (this.listViewCalendars.SelectedItems.Count > 1)
            {
                this.toolStripStatusLabelGoogle1.Text = "Error: Select only one Calendar.";
                return;
            }
            else if (this.listViewCalendars.SelectedItems.Count == 0)
            {
                this.toolStripStatusLabelGoogle1.Text = "Error: Select only one Calendar.";
                return;
            }
            //get the CalendarEntry from the selected calendar list
            CalendarEntry calendarEntry = (CalendarEntry)this.listViewCalendars.SelectedItems[0].Tag;
            //clear results from prior runs
            this.listBoxResults.Items.Clear();
            this.listBoxResults.SelectedIndex = this.listBoxResults.Items.Add("Beginning process of importing Microsoft® Project tasks into events.");
            foreach (ListViewItem taskItem in this.listViewTasks.CheckedItems)
            {
                //grab the Microsoft® Project Task from the list item
                Task task = (Task)taskItem.Tag;

                //convert to EventEntry
                EventEntry eventEntry = translateProjectTaskToCalendarEntry(task);

                //set the title and content of the entry.
                eventEntry.Title.Text = taskItem.Text;
                eventEntry.Content.Content = taskItem.Text;

                this.toolStripStatusLabelGoogle1.Text = "Status: Adding event " + eventEntry.Title.Text + ". Please wait...";
                Application.DoEvents();

                //do some work here
                try
                {
                    //insert event into Calendar
                    System.Diagnostics.Debug.WriteLine("[*] Adding " + eventEntry.Title.Text + " at " + calendarEntry.Links[0].AbsoluteUri + ".");
                    AtomEntry insertedEntry = calendarService.Insert(new Uri(calendarEntry.Links[0].AbsoluteUri), eventEntry);
                    this.listBoxResults.SelectedIndex = this.listBoxResults.Items.Add("Added " + eventEntry.Title.Text + ". Updated at: " + insertedEntry.Updated.ToString());
                }
                catch (Exception ex)
                {
                    this.listBoxResults.SelectedIndex = this.listBoxResults.Items.Add("Error adding " + eventEntry.Title.Text + ". Details: " + ex.Message);
                }
            }
            this.toolStripStatusLabelGoogle1.Text = "Success: Added all " + this.listBoxResults.Items.Count + " tasks as events to your " + calendarEntry.Title.Text + " Google® Calendar.";
            this.listBoxResults.SelectedIndex = this.listBoxResults.Items.Add("Import is complete! Thank you for using this tool.");
            this.listBoxResults.SelectedIndex = this.listBoxResults.Items.Add("Visit http://www.daball.me/ for more about the author.");
            this.buttonClose.Show();
            Application.DoEvents();
        }

        private void buttonClose_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        /// <summary>
        /// Reads Microsoft® Project file and returns ProjectFile instance.
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
        /// Translates a Microsoft® Project task into a Google® Data API CalendarEntry for Google® Calendar.
        /// </summary>
        /// <param name="task">Microsoft® Project task instance.</param>
        /// <returns>A new EventEntry from Google® Data API.</returns>
        protected EventEntry translateProjectTaskToCalendarEntry(Task task)
        {
            //create EventEntry for Google® API
            EventEntry eventEntry = new EventEntry();

            //assign title to EventEntry from Microsoft® Project Task name
            eventEntry.Title.Text = task.getName();
            eventEntry.Content.Content = task.getName();

            // Set a location for the event.
            Where eventLocation = new Where();
            eventLocation.ValueString = "Microsoft® Project";
            eventEntry.Locations.Add(eventLocation);

            //create When object for Google® API
            When dateTime = new When();

            //write these down
            java.util.Date taskStart = task.getStart();
            java.util.Date taskFinish = task.getFinish();

            //now split them up
            string[] taskStartComponents = new string[] { };
            if (taskStart != null) taskStartComponents = taskStart.toString().Split(new char[] { ' ', ':' }, StringSplitOptions.RemoveEmptyEntries);
            string[] taskFinishComponents = new string[] { };
            if (taskFinish != null) taskFinishComponents = taskFinish.toString().Split(new char[] { ' ', ':' }, StringSplitOptions.RemoveEmptyEntries);

            //now convert them from Java to .Net and tell Google®'s When what's up
            if (taskStart != null) dateTime.StartTime = DateTime.ParseExact(
                taskStartComponents[7] + " " + //year
                taskStartComponents[1] + " " + //month
                taskStartComponents[2],
                "yyyy MMM dd", System.Globalization.CultureInfo.InvariantCulture
                );
            if (taskFinish != null) dateTime.EndTime = DateTime.ParseExact(
                taskFinishComponents[7] + " " + //year
                taskFinishComponents[1] + " " + //month
                taskFinishComponents[2],
                "yyyy MMM dd", System.Globalization.CultureInfo.InvariantCulture
                );

            //add the When to the event entry
            eventEntry.Times.Add(dateTime);

            //return the EventEntry
            return eventEntry;
        }

    }
}
