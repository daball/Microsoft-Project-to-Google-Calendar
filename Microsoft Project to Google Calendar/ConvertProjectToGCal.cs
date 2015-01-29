using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows.Forms;

//needed for MPXJ (to handle Microsoft® Project)
using net.sf.mpxj;
using net.sf.mpxj.mpp;
using net.sf.mpxj.reader;

//needed for Google® Calendar API 3
using Google.Apis.Calendar.v3;
using Google.Apis.Services;
using Google.Apis.Auth.OAuth2;
using System.Threading;
using Google.Apis.Util.Store;
using Google.Apis.Calendar.v3.Data;

namespace Microsoft_Project_to_Google_Calendar
{
    public partial class ConvertProjectToGCal : Form
    {
        //this will remain null until we press the login button and get a valid handle, test accordingly
        CalendarService calendarService = null;

        //this will be null until well after login, test accordingly
        List<CalendarListEntry> calendars = null;

        public ConvertProjectToGCal()
        {
            InitializeComponent();
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
                java.util.Date taskStart = task.Start;
                java.util.Date taskFinish = task.Finish;

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

                string name = task.Name;

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
                    item.Text = task.Name.Trim();
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
            if (toolStripTextBoxUserName.Text == "")
            {
                toolStripStatusLabelGoogle1.Text = "Status: Waiting for the user name for your Google® Account.";
            }
            else if (toolStripTextBoxUserName.Text != "")
            {
                toolStripStatusLabelGoogle1.Text = "Status: Standing by for login. Press Login when you are ready.";
            }
            Application.DoEvents();
        }

        private void toolStripTextBoxUserName_KeyPress(object sender, KeyPressEventArgs e)
        {
            this.updateGoogleStatus1OnKey();
        }

        private void toolStripTextBoxUserName_TextChanged(object sender, EventArgs e)
        {
            this.updateGoogleStatus1OnKey();
        }

        private void toolStripButtonLogin_Click(object sender, EventArgs e)
        {
            toolStripStatusLabelGoogle1.Text = "Status: Logging into Google® Calendar with the Google® Account information you provided. Please wait as I retrieve your Google® Calendar list...";
            Application.DoEvents();

            UserCredential credential;

            try
            {
                using (var stream = new FileStream("client_secret_151672359587-ar8gftg4563kqk3pbim50cooln5ls6ga.apps.googleusercontent.com.json", FileMode.Open, FileAccess.Read))
                {
                    credential = GoogleWebAuthorizationBroker.AuthorizeAsync(
                        GoogleClientSecrets.Load(stream).Secrets,
                        new [] { CalendarService.Scope.Calendar },
                        toolStripTextBoxUserName.Text, CancellationToken.None,
                        new FileDataStore("Microsoft Project to Google Calendar")).Result;
                }
                calendarService = new CalendarService(new BaseClientService.Initializer()
                {
                    HttpClientInitializer = credential,
                    ApplicationName = "Microsoft Project to Google Calendar"
                });
                //CalendarQuery query = new CalendarQuery();
                //query.Uri = new Uri("https://www.google.com/calendar/feeds/default/allcalendars/full");
                //CalendarFeed resultFeed = (CalendarFeed)calendarService.Query(query);
                System.Diagnostics.Debug.WriteLine("Your calendars:");
                this.listViewCalendars.Items.Clear();
                foreach (CalendarListEntry entry in calendarService.CalendarList.List().Execute().Items)
                {
                    
                    System.Diagnostics.Debug.WriteLine(" >> \"" + entry.Summary + "\"");
                    ListViewItem lvi = new ListViewItem(entry.Summary);
                    lvi.Tag = entry;
                    this.listViewCalendars.Items.Add(lvi);
                }
            }
            //catch (AuthenticationException ex)
            //{
            //    toolStripStatusLabelGoogle1.Text = "Error: Login to Google® Calendar failed with the Google® Account information you provided. Please check your user name and try again. " + ex.Message;
            //    Application.DoEvents();

            //    //bounce out to let the user try again
            //    return;
            //}
            //catch (GDataRequestException ex)
            //{
            //    toolStripStatusLabelGoogle1.Text = "An error has occurred with the data request. Login failed. Message: " + ex.Message;
            //    Application.DoEvents();

            //    //bounce out to let the user try again
            //    return;
            //}
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
            CalendarListEntry calendarEntry = (CalendarListEntry)this.listViewCalendars.SelectedItems[0].Tag;
            //clear results from prior runs
            this.listBoxResults.Items.Clear();
            this.listBoxResults.SelectedIndex = this.listBoxResults.Items.Add("Beginning process of importing Microsoft® Project tasks into events.");
            int addedEvents = 0; //count
            foreach (ListViewItem taskItem in this.listViewTasks.CheckedItems)
            {
                //grab the Microsoft® Project Task from the list item
                Task task = (Task)taskItem.Tag;

                //convert to EventEntry
                Event eventEntry = translateProjectTaskToCalendarEntry(task);

                //set the title and content of the entry.
                eventEntry.Summary = taskItem.Text;
                eventEntry.Description = taskItem.Text;

                this.toolStripStatusLabelGoogle1.Text = "Status: Adding event " + eventEntry.Summary + ". Please wait...";
                Application.DoEvents();

                //do some work here
                try
                {
                    //insert event into Calendar
                    System.Diagnostics.Debug.WriteLine("[*] Adding " + eventEntry.Summary + " to " + calendarEntry.Summary + ".");
                    //AtomEntry insertedEntry = calendarService.Insert(new Uri(calendarEntry.Links[0].AbsoluteUri), eventEntry);
                    Event insertedEntry = calendarService.Events.Insert(eventEntry, calendarEntry.Id).Execute();
                    this.listBoxResults.SelectedIndex = this.listBoxResults.Items.Add("Added " + eventEntry.Summary + " to " + calendarEntry.Summary + ". Updated at: " + insertedEntry.Updated.ToString());
                    addedEvents++;
                }
                catch (Exception ex)
                {
                    this.listBoxResults.SelectedIndex = this.listBoxResults.Items.Add("Error adding " + eventEntry.Summary + ". Details: " + ex.Message);
                }
            }
            this.toolStripStatusLabelGoogle1.Text = "Success: Added all " + addedEvents + " tasks as events to your " + calendarEntry.Summary + " Google® Calendar.";
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
            //read project file in
            byte[] projFile = File.ReadAllBytes(mppFile);
            //get Java InputStream from file stream
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(projFile);
            //get the reader
            ProjectReader reader = ProjectReaderUtility.getProjectReader(mppFile);
            //read the file out
            return reader.read(bais);
        }

        /// <summary>
        /// Gets all tasks from ProjectFile instance.
        /// </summary>
        /// <param name="projectFile">ProjectFile instance.</param>
        /// <returns>List of tasks.</returns>
        protected Task[] getAllProjectTasks(ProjectFile projectFile)
        {
            //get tasks
            java.util.List taskList = projectFile.AllTasks;

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
        protected Event translateProjectTaskToCalendarEntry(Task task)
        {
            //create EventEntry for Google® API
            Event eventEntry = new Event();

            //assign title to EventEntry from Microsoft® Project Task name
            eventEntry.Summary = task.Name;
            eventEntry.Description = task.Name;

            // Set a location for the event.
            eventEntry.Location = "Microsoft® Project";

            //create When object for Google® API
            DateTime startTime = DateTime.MinValue;
            DateTime endTime = DateTime.MinValue;

            //write these down
            java.util.Date taskStart = task.Start;
            java.util.Date taskFinish = task.Finish;

            //now split them up
            string[] taskStartComponents = new string[] { };
            if (taskStart != null) taskStartComponents = taskStart.toString().Split(new char[] { ' ', ':' }, StringSplitOptions.RemoveEmptyEntries);
            string[] taskFinishComponents = new string[] { };
            if (taskFinish != null) taskFinishComponents = taskFinish.toString().Split(new char[] { ' ', ':' }, StringSplitOptions.RemoveEmptyEntries);

            //now convert them from Java to .Net and tell Google®'s When what's up
            if (taskStart != null) startTime = DateTime.ParseExact(
                taskStartComponents[7] + " " + //year
                taskStartComponents[1] + " " + //month
                taskStartComponents[2],
                "yyyy MMM dd", System.Globalization.CultureInfo.InvariantCulture
                );
            if (taskFinish != null) endTime = DateTime.ParseExact(
                taskFinishComponents[7] + " " + //year
                taskFinishComponents[1] + " " + //month
                taskFinishComponents[2],
                "yyyy MMM dd", System.Globalization.CultureInfo.InvariantCulture
                );

            //add the When to the event entry
            if (startTime != DateTime.MinValue)
                eventEntry.Start = new EventDateTime() { DateTime = startTime };
            if (endTime != DateTime.MinValue)
                eventEntry.End = new EventDateTime() { DateTime = endTime };

            //return the EventEntry
            return eventEntry;
        }

    }
}
