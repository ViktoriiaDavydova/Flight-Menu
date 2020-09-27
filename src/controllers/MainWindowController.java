package controllers;

import com.tickettoride.exceptions.InvalidCitizenshipException;
import com.tickettoride.exceptions.InvalidNameException;
import com.tickettoride.exceptions.NoMoreSeatsException;
import com.tickettoride.managers.TicketToRideTravelManager;
import com.tickettoride.problemdomain.Airport;
import com.tickettoride.problemdomain.Flight;
import com.tickettoride.problemdomain.Reservation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Handles user interaction with the GUI window. The GUI window shows
 * information about flights, lets the user to find a flight by choosing
 * destination from, destination to and the day of week. User can make a
 * reservation of the chosen flight, save reservation, find reservation by its
 * code, or airline, or client name, update reservation, making changes in the
 * client name, citizenship or reservation status.
 *
 * @author Nick Hamnett
 * @author Viktoriia Davydova
 * 
 * @version 2019-06-27
 */
public class MainWindowController {

	/**
	 * Holds the flight and reservation manager.
	 */
	private TicketToRideTravelManager flightManager;
	private Reservation foundReservation;
	private ArrayList<Reservation> reservationArrayList;
	private ArrayList<Flight> foundFlights;

	private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
	private boolean flag = false;

	/**
	 * Contains references to the flights and reservations ListViews.
	 */
	@FXML
	private ListView<String> flightsListView;
	@FXML
	private ListView<String> reservationsListView;

	/**
	 * Contains references to the ComboBoxes
	 */
	@FXML
	private ComboBox<String> flightFinderToCombobox;
	@FXML
	private ComboBox<String> flightFinderFromCombobox;
	@FXML
	private ComboBox<String> flightFinderDayCombobox;
	@FXML
	private ComboBox<String> reservationsStatusCombobox;

	/**
	 * Contains references to the TextFields
	 */
	@FXML
	private TextField flightFinderFlightTextField;
	@FXML
	private TextField flightFinderAirlineTextField;
	@FXML
	private TextField flightFinderDayTextField;
	@FXML
	private TextField flightFinderTimeTextField;
	@FXML
	private TextField flightFinderCostTextField;
	@FXML
	private TextField flightFinderNameTextField;
	@FXML
	private TextField flightFinderCitizenshipTextField;
	@FXML
	private TextField reservationsCodeTextField;
	@FXML
	private TextField reservationsFlightTextField;
	@FXML
	private TextField reservationsAirlineTextField;
	@FXML
	private TextField reservationsCostTextField;
	@FXML
	private TextField reservationsNameTextField;
	@FXML
	private TextField reservationsCitizenshipTextField;
	@FXML
	private TextField reservationsBottomCodeTextField;
	@FXML
	private TextField reservationsBottomAirlineTextField;
	@FXML
	private TextField reservationsBottomNameTextField;

	/**
	 * Contains references to the Buttons
	 */
	@FXML
	private Button flightFinderMakeReservationButton;
	@FXML
	private Button flightFinderFindFlightsButton;
	@FXML
	private Button reservationsUpdateReservationButton;
	@FXML
	private Button reservationsFindReservationsButton;

	/**
	 * Default constructor.
	 */
	public MainWindowController() {
		this.flightManager = new TicketToRideTravelManager();
	}

	/**
	 * This method is called when the window is created.
	 */
	public void onCreated() {

		loadFlight();
		loadFlightFinderCombo();
		readFlightsViewList();
		createReservation();
		searchReservation();
		readReservationViewList();
		updateReservation();

		this.flightsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			/**
			 * This method is called when the user selects an item in the list.
			 */
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

			}
		});

		reservationsStatusCombobox.getItems().addAll("Active", "Inactive");

	}

	/**
	 * Method loads information about all the flights from the database file to the
	 * ListView when the GUI window is created
	 */
	private void loadFlight() {

		ArrayList<Flight> flightsArrayList = flightManager.getFlights();
		int i;

		for (i = 0; i < flightsArrayList.size(); i++) {
			flightsListView.getItems().addAll(flightsArrayList.get(i).toString() + ", From: "
					+ flightsArrayList.get(i).getFrom().toString() + ", To: "
					+ flightsArrayList.get(i).getTo().toString() + ", Day: " + flightsArrayList.get(i).getWeekday()
					+ ", Cost: " + decimalFormat.format(flightsArrayList.get(i).getCostPerSeat()));
		}
	}

	/**
	 * Method creates Airport objects and loads them to the ComboBoxes. Information
	 * about Airports from, Airports to and Weekdays will be displayed in the
	 * ComboBoxes for user to select. Information about found flights will be
	 * displayed in the ListView
	 */
	private void loadFlightFinderCombo() {
		ArrayList<Airport> airportAll = flightManager.getAirports();
		int i;
		String[] week_days = { TicketToRideTravelManager.WEEKDAY_ANY, TicketToRideTravelManager.WEEKDAY_SUNDAY,
				TicketToRideTravelManager.WEEKDAY_MONDAY, TicketToRideTravelManager.WEEKDAY_TUESDAY,
				TicketToRideTravelManager.WEEKDAY_WEDNESDAY, TicketToRideTravelManager.WEEKDAY_THURSDAY,
				TicketToRideTravelManager.WEEKDAY_FRIDAY, TicketToRideTravelManager.WEEKDAY_SATURDAY };

		for (i = 0; i < airportAll.size(); i++) {
			this.flightFinderFromCombobox.getItems().addAll(airportAll.get(i).toString());
			this.flightFinderToCombobox.getItems().addAll(airportAll.get(i).toString());
		}
		this.flightFinderDayCombobox.getItems().addAll(FXCollections.observableArrayList(week_days));

		flightFinderFromCombobox.getSelectionModel().selectedItemProperty().addListener((ov, oldvalue, newvalue) -> {
		});

		flightFinderToCombobox.getSelectionModel().selectedItemProperty().addListener((ov, oldvalue, newvalue) -> {
		});

		flightFinderDayCombobox.getSelectionModel().selectedItemProperty().addListener((ov, oldvalue, newvalue) -> {
		});

		flightFinderFindFlightsButton.setOnAction(e -> {

			String parameterFrom = flightFinderFromCombobox.getSelectionModel().getSelectedItem();
			String parameterTo = flightFinderToCombobox.getSelectionModel().getSelectedItem();
			String parameterWeekDay = flightFinderDayCombobox.getSelectionModel().getSelectedItem();

			Airport from = flightManager.findAirportByCode(parameterFrom);
			Airport to = flightManager.findAirportByCode(parameterTo);
			foundFlights = flightManager.findFlights(from, to, parameterWeekDay);
			if (foundFlights.size() > 0) {
				flag = true;
			}
			flightsListView.getItems().clear();

			for (int j = 0; j < foundFlights.size(); j++) {
				flightsListView.getItems()
						.addAll(foundFlights.get(j).toString() + ", From: " + foundFlights.get(j).getFrom().toString()
								+ ", To: " + foundFlights.get(j).getTo().toString() + ", Day: "
								+ foundFlights.get(j).getWeekday() + ", Cost: "
								+ decimalFormat.format(foundFlights.get(j).getCostPerSeat()));
			}
		});
	}

	/**
	 * Method retrieves information about flight and populates the TextFields with
	 * it
	 */
	private void readFlightsViewList() {

		flightsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				Flight foundFlight;
				int selectedIndex = flightsListView.getSelectionModel().getSelectedIndex();
				if (flag) {

					foundFlight = foundFlights.get(selectedIndex);
				} else {
					foundFlight = flightManager.getFlights().get(selectedIndex);
				}
				flightFinderFlightTextField.setText(foundFlight.getCode());
				flightFinderFlightTextField.setEditable(false);
				flightFinderAirlineTextField.setText(foundFlight.getAirline());
				flightFinderAirlineTextField.setEditable(false);
				flightFinderDayTextField.setText(foundFlight.getWeekday());
				flightFinderDayTextField.setEditable(false);
				flightFinderTimeTextField.setText(foundFlight.getTime());
				flightFinderTimeTextField.setEditable(false);
				flightFinderCostTextField.setText("$" + (decimalFormat.format(foundFlight.getCostPerSeat())));
				flightFinderCostTextField.setEditable(false);
			}
		});
	}

	/**
	 * Method creates the Reservation object and saves it in a binary file.
	 * NoMoreSeatsException, InvalidNameException and InvalidCitizenshipException
	 * exceptions will be handled
	 */
	private void createReservation() {

		flightFinderMakeReservationButton.setOnAction(e -> {
			Flight flight = flightManager.findFlightByCode(flightFinderFlightTextField.getText());
			String name = flightFinderNameTextField.getText();
			String citizenship = flightFinderCitizenshipTextField.getText();
			if (!(name.isEmpty() || citizenship.isEmpty())) {
				Reservation createdReservation;
				try {
					createdReservation = new Reservation(flight, name, citizenship);
					try {
						createdReservation = flightManager.makeReservation(flight, name, citizenship);
					} catch (NoMoreSeatsException ex) {
						ex.printStackTrace();
					}
					System.out.println(createdReservation + " Reservation object is created");
				} catch (InvalidNameException | InvalidCitizenshipException e1) {
					e1.printStackTrace();
				}
			}
			else {
				alert();
			}
		});
	}

	/**
	 * Method to find the Reservation by code, or airline, or client's name and
	 * display the list of found Reservations in the ListView
	 */
	private void searchReservation() {

		reservationsFindReservationsButton.setOnAction(e -> {
			String reservationCodeToSearch = reservationsBottomCodeTextField.getText();
			String airlineToSearch = reservationsBottomAirlineTextField.getText();
			String nameToSearch = reservationsBottomNameTextField.getText();

			reservationArrayList = flightManager.findReservations(reservationCodeToSearch, airlineToSearch,
					nameToSearch);
			reservationsListView.getItems().clear();
			for (int i = 0; i < reservationArrayList.size(); i++) {
				reservationsListView.getItems().addAll(reservationArrayList.get(i).toString());
			}
		});
	}

	/**
	 * Method retrieves information about reservation and populates the TextFields
	 * with it
	 */
	private void readReservationViewList() {

		reservationsListView.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					if (newValue != null) {

						int selectedIndex = reservationsListView.getSelectionModel().getSelectedIndex();
						foundReservation = reservationArrayList.get(selectedIndex);

						reservationsCodeTextField.setText(foundReservation.getCode());
						reservationsCodeTextField.setEditable(false);
						reservationsFlightTextField.setText(foundReservation.getFlight().toString());
						reservationsFlightTextField.setEditable(false);
						reservationsAirlineTextField.setText(foundReservation.getFlight().getAirline());
						reservationsAirlineTextField.setEditable(false);
						reservationsCostTextField
								.setText("$" + (decimalFormat.format(foundReservation.getFlight().getCostPerSeat())));
						reservationsCostTextField.setEditable(false);
						reservationsNameTextField.setText(foundReservation.getName());
						reservationsCitizenshipTextField.setText(foundReservation.getCitizenship());
						if (foundReservation.isActive()) {
							reservationsStatusCombobox.getSelectionModel().select(0);
						} else {
							reservationsStatusCombobox.getSelectionModel().select(1);
						}
					}
				});
	}

	/**
	 * Method to update the existing reservation in the binary file. Client's name,
	 * citizenship and status only can be changed. InvalidNameException and
	 * InvalidCitizenshipException exceptions will be handled
	 */
	private void updateReservation() {

		reservationsUpdateReservationButton.setOnAction(e -> {
			if (!(reservationsNameTextField.getText().isEmpty()
					|| reservationsCitizenshipTextField.getText().isEmpty())) {
				try {
					foundReservation.setName(reservationsNameTextField.getText());
				} catch (InvalidNameException ex) {
					ex.printStackTrace();
				}
				try {
					foundReservation.setCitizenship(reservationsCitizenshipTextField.getText());
				} catch (InvalidCitizenshipException ex) {
					ex.printStackTrace();
				}

				if (reservationsStatusCombobox.getValue().equals("Active")) {
					foundReservation.setActive(true);
				} else {
					foundReservation.setActive(false);
				}
				System.out.println(foundReservation.getCode() + " Reservation object is updated");
			}
			else {
				alert();
			}

		});
	}

	/**
	 * Method to display the alert window when there are empty text fields left
	 */
	private void alert() {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Warning Dialog");
		alert.setHeaderText("All fields must be filled in.");
		alert.setContentText("Try again.");
		alert.showAndWait();
	}

	/**
	 * This method is called before the window is closed.
	 */
	public void onClosing() {
		this.flightManager.persist();
	}
}