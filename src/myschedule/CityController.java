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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import myschedule.model.CityModel;

/**
 * @author bradd
 * @version 0.5.0
 */
public class CityController {
    @FXML private Label lblTitle;
    @FXML private TableView table;
    @FXML private TableColumn<CityModel, Integer> cityIdColumn;
    @FXML private TableColumn<CityModel, String> cityColumn;
    @FXML private TableColumn<CityModel, String> countryColumn;
    @FXML private TextField txtCityId;
    @FXML private TextField txtCity;
    @FXML private ComboBox cboCountry;
    @FXML private Button btnAdd;
    @FXML private Button btnRemove;
    @FXML private Button btnClose;
    @FXML private Button btnCommit;

    private App app;
    private ObservableList<CityModel> cityList = FXCollections.observableArrayList();
    private List countryNameList;
        
    private MainController main;
    private boolean unsavedChanges = false;

    /**
     *  Add action event listeners
     */
    @SuppressWarnings("unchecked")
    private void addListeners() {
        
        btnAdd.setOnAction(e -> { handleAdd(); } );
        btnClose.setOnMouseClicked((ae) -> { closeCityMaint(); } );
        btnCommit.setOnAction(e -> { handleCommit(); } );
        btnRemove.setOnAction(e -> { handleRemove(); } );
    }

    /**
     * Check for un-saved changes; display warning message
     * as needed; close city maintenance function.
     */
    @SuppressWarnings("unchecked")
    private void closeCityMaint() {
        if (unsavedChanges) {
            if (confirmUnsaved()) {
                main.endProcess();
            }
        }
        else {
            main.endProcess();
        }
    }
    
    /**
     * Confirm closing when unsaved data exists
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    private boolean confirmUnsaved() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("Pending city changes exist.");
        alert.setContentText(
            "There have been changes made to the city data that have not been saved.\n\nTo save these changes, " +
            "click \"No\" to close this alert, and then click on the \"Commit\" button to save the changes.\n\n" +
            "Clicking \"Yes\" will result in the pending changes being lost and the city maintenance process ending."
        );
        ButtonType btnYes = new ButtonType("Yes");
        ButtonType btnNo = new ButtonType("No");
        alert.getButtonTypes().setAll(btnYes, btnNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == btnYes;
    }
    
    /**
     * Get next available Country Id to be use for add
     * @param list
     * @return 
     */
    @SuppressWarnings("unchecked")
    private int getNextCityId(ObservableList<CityModel> list) {
        if (list.size() > 0) {
            Optional<CityModel> c = list
                .stream()
                .max(Comparator.comparing(CityModel::getCityId)
            );
            return c.get().getCityId() + 1;
        }
        else {
            return 1;
        }
    }

    /**
     * Handle add action
     */
    private void handleAdd() {
        String rightNow = app.common.rightNow();
        String user = app.userName();
        int userId = app.userId();

        if (validateCityRecord()) {
            cityList.add(new CityModel(
                Integer.parseInt(txtCityId.getText()), txtCity.getText(), (String) cboCountry.getValue(),
                rightNow, user, rightNow, user)
            );
            unsavedChanges = true;
            initializeForm();
        }
        else {
            app.log.write(Level.SEVERE, "Error parsing new city record");
        }
    }

    /**
     * Handle commit action
     */
    private void handleCommit() {
        try {
            app.db.updateCityTable(cityList);
            unsavedChanges = false;
            app.common.alertStatus(1);
            refreshTableView();
        }
        catch (SQLException ex) {
            app.common.alertStatus(0);
        }
    }

    /**
     * Handle remove action
     */
    private void handleRemove() {
        ObservableList<CityModel> citySelected, allCities;
        allCities = table.getItems();
        citySelected = table.getSelectionModel().getSelectedItems();
        citySelected.forEach(allCities::remove);
        unsavedChanges = true;
    }
    
    /**
     * Initialize "add record" form elements
     */
    @SuppressWarnings("unchecked")
    private void initializeForm() {
        int nextCityId = getNextCityId(cityList);
        String rightNow = app.common.rightNow();
        String user = app.userName();
        int userId = app.userId();

        txtCityId.setText(Integer.toString(nextCityId));
        txtCity.setText("");
        cboCountry.getItems().addAll(countryNameList);
        
        txtCityId.setDisable(true);
        txtCity.setDisable(false);
        cboCountry.setDisable(false);
    }
    
    /**
     * Initialize Cell Factories and Cell Value Factories
     */
    @SuppressWarnings("unchecked")
    private void initializeTableColumns() {
        // City Id column
        cityIdColumn.setCellValueFactory(x -> new ReadOnlyObjectWrapper<>(x.getValue().getCityId()));
        
        // City column
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        cityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cityColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<CityModel, String> t) -> {
                ((CityModel) t.getTableView().getItems().get(t.getTablePosition().getRow())).setCity(t.getNewValue());
                ((CityModel) t.getTableView().getItems().get(t.getTablePosition().getRow())).setLastUpdate(app.common.rightNow());
                ((CityModel) t.getTableView().getItems().get(t.getTablePosition().getRow())).setLastUpdateBy(app.userName());
                table.refresh();
                unsavedChanges = true;
            }
        );
        
        // Country column
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        countryColumn.setCellFactory(ComboBoxTableCell.forTableColumn((ObservableList) countryNameList));
        countryColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<CityModel, String> t) -> {
                ((CityModel) t.getTableView().getItems().get(t.getTablePosition().getRow())).setCountry(t.getNewValue());
                ((CityModel) t.getTableView().getItems().get(t.getTablePosition().getRow())).setLastUpdate(app.common.rightNow());
                ((CityModel) t.getTableView().getItems().get(t.getTablePosition().getRow())).setLastUpdateBy(app.userName());
                table.refresh();
                unsavedChanges = true;
            }
        );
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
     * Refresh City TableView
     */
    @SuppressWarnings("unchecked")
    private void refreshTableView() {
        try {
            cityList = app.db.getCityModelList("city", "asc");
            table.setItems(cityList);
        }
        catch (SQLException ex) {
            app.log.write(Level.SEVERE, ex.getMessage());
        }
    }
    
    /**
     * Start country maintenance
     */
    @SuppressWarnings("unchecked")
    public void start() {
        addListeners();
        lblTitle.setText(app.localize("cities"));
        
        try {
            cityList = app.db.getCityModelList("city", "asc");
            countryNameList = app.db.getCountryNameList();
        }
        catch (SQLException ex) {
            app.log.write(Level.SEVERE, ex.getMessage());
        }
        
        initializeForm();
        initializeTableColumns();
        table.setEditable(true);
        table.setItems(cityList);
    }
    
    /**
     * Validate new record data
     * @return 
     */
    @SuppressWarnings("unchecked")
    private boolean validateCityRecord() {
        return app.common.isNumber(txtCityId.getText())
              && app.common.isValidString(txtCity.getText())
              && app.common.isValidString((String) cboCountry.getValue());
    }
}
