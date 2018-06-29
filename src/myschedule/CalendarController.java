/*
 * The MIT License
 *
 * Copyright 2018 bradd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package myschedule;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import myschedule.model.AppointmentModel;

/**
 * @author bradd
 * @version 0.5.0
 */
@SuppressWarnings("unchecked")
public class CalendarController {
    @FXML Label lblTitle;
    @FXML Pane calendarPane;

    private App app;
    private MainController main;
    private ObservableList<AppointmentModel> appointmentList = FXCollections.observableArrayList();

    
    /**
     * Add listeners
     */
    @SuppressWarnings("unchecked")
    private void addListeners() {
        
    }
    
    /**
     * Inject App object
     * @param _app 
     */
    @SuppressWarnings("unchecked")
    public void injectApp(App _app) {
        this.app = _app;
    }

    /**
     * Inject MainController object
     * @param _main 
     */
    @SuppressWarnings("unchecked")
    public void injectMainController(MainController _main) {
        main = _main;
    }
    
    /**
     * Start country maintenance
     * @param version (String)
     */
    @SuppressWarnings("unchecked")
    public void start(String version) {
        addListeners();
        lblTitle.setText(app.localize("appointments_month_view"));
        
        if (version.equals("month")) {
            MonthView monthView = new MonthView(YearMonth.now());
            monthView.buildCalendar();
            calendarPane.getChildren().add(monthView.getView());
        }
        else {
            MonthView monthView = new MonthView(YearMonth.now());
            calendarPane.getChildren().add(monthView.getView());
        }
    }
    
    // Define MonthView as inner class of CalendarController class
    public class MonthView {
        private ArrayList<AnchorPaneNode> allCalendarDays = new ArrayList<>(35);
        private VBox view;
        private Text calendarTitle;
        private YearMonth currentYearMonth;
        
        /**
         * Create a calendar view
         * @param yearMonth year month to create the calendar of
         */
        public MonthView(YearMonth yearMonth) {
            currentYearMonth = yearMonth;
        }

        public void buildCalendar() {
            // Create the calendar grid pane
            GridPane calendar = new GridPane();
            calendar.setPrefSize(500, 200);
            calendar.setGridLinesVisible(true);

            // Create rows and columns with each cell being an anchor pane
            // for a given calendar date
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 7; j++) {
                    AnchorPaneNode ap = new AnchorPaneNode();
                    ap.setPrefSize(200,200);
                    calendar.add(ap, j, i);
                    allCalendarDays.add(ap);
                }
            }

            // Days of the week labels
            Text[] dayNames = new Text[]{ new Text("Sun"), new Text("Mon"), new Text("Tue"),
                new Text("Wed"), new Text("Thu"), new Text("Fri"), new Text("Sat") };
            GridPane dayLabels = new GridPane();
            dayLabels.setPrefWidth(500);
            Integer col = 0;

            // Add DOW column headers
            for (Text txt : dayNames) {
                AnchorPane ap = new AnchorPane();
                ap.setPrefSize(200, 10);
                AnchorPane.setBottomAnchor(txt, 5.0);
                ap.getChildren().add(txt);
                dayLabels.add(ap, col++, 0);
            }

            // Create calendarTitle and buttons to change current month
            calendarTitle = new Text();
            Button previousMonth = new Button("<<");
            previousMonth.setOnAction(e -> previousMonth());
            Button nextMonth = new Button(">>");
            nextMonth.setOnAction(e -> nextMonth());
            HBox titleBar = new HBox();
            titleBar.setSpacing(10);
            titleBar.getChildren().addAll(previousMonth, calendarTitle, nextMonth);
            titleBar.setAlignment(Pos.BASELINE_CENTER);

            // Populate calendar with the appropriate day numbers
            populateCalendar(currentYearMonth);

            // Create HBox for spacing between titleBar and dayLabels
            Region spacer = new Region();
            spacer.setPrefSize(100.0, 20.0);

            // Create the calendar view
            view = new VBox(titleBar, spacer, dayLabels, calendar);
        }

        /**
         * Set the days of the calendar to correspond to the appropriate date
         * @param yearMonth year and month of month to render
         */
        public void populateCalendar(YearMonth yearMonth) {
            String mm;
            String yyyy;
    //        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //        String dd = Integer.toString(LocalDate.now().getDayOfMonth());
    //        String mm = Integer.toString(LocalDate.now().getMonthValue());
    //        ResultSet rs;
    //        String today = LocalDate.now().format(formatter);
    //        String yyyy = Integer.toString(LocalDate.now().getYear());

            // Get the date we want to start with on the calendar. calendarDate ends up being the first
            // of the month for the current month (or chosen month) hence the "1" second parameter below 
            LocalDate calendarDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);
            yyyy = Integer.toString(yearMonth.getYear());
            mm = yearMonth.getMonthValue() < 10 ? "0" + Integer.toString(yearMonth.getMonthValue()) : Integer.toString(yearMonth.getMonthValue());

            // Get resultset of appointments for given month / year
            try {
                CalendarController.this.appointmentList = app.db.getAppointments(mm, yyyy);
            }
            catch (SQLException ex) {
                app.common.alertStatus(0);
            }

            // Dial back the day until it is SUNDAY (unless the month starts on a sunday)
            while (!calendarDate.getDayOfWeek().toString().equals("SUNDAY") ) {
                calendarDate = calendarDate.minusDays(1);
            }

            // Populate the calendar with day numbers
            for (AnchorPaneNode ap : allCalendarDays) {
                if (!ap.getChildren().isEmpty()) {
                    ap.getChildren().remove(0);
                }

               // This is where to lookup month & day to see if there's an appointment by using
               // calendar.getMonthValue() - int
               // calendar.getDayOfMonth() - int

                Text txt = new Text(String.valueOf(calendarDate.getDayOfMonth()));
                ap.setDate(calendarDate);
                AnchorPaneNode.setTopAnchor(txt, 5.0);
                AnchorPaneNode.setLeftAnchor(txt, 5.0);
                ap.getChildren().add(txt);
                calendarDate = calendarDate.plusDays(1);
            }
            // Change the title of the calendar
            calendarTitle.setText(yearMonth.getMonth().toString() + " " + String.valueOf(yearMonth.getYear()));
        }

        /**
         * Move the month back by one. Re-populate the calendar with the correct dates.
         */
        private void previousMonth() {
            currentYearMonth = currentYearMonth.minusMonths(1);
            populateCalendar(currentYearMonth);
        }

        /**
         * Move the month forward by one. Re-populate the calendar with the correct dates.
         */
        private void nextMonth() {
            currentYearMonth = currentYearMonth.plusMonths(1);
            populateCalendar(currentYearMonth);
        }

        public VBox getView() {
            return view;
        }

        public ArrayList<AnchorPaneNode> getAllCalendarDays() {
            return allCalendarDays;
        }

        public void setAllCalendarDays(ArrayList<AnchorPaneNode> allCalendarDays) {
            this.allCalendarDays = allCalendarDays;
        }
    }
}

