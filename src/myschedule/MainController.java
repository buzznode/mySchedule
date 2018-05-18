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

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

/**
 * @author bradd
 * @version 0.5.0
 */
public class MainController {

    private App app;
    private final MenuBar menuBar = new MenuBar();
    
    // FXML Components
    @FXML protected BorderPane mainContainer;

    // Protected MenuItem variables
    protected MenuItem miFileNew;     
    protected MenuItem miFileOpen;
    protected MenuItem miFileSave;
    protected MenuItem miFileSaveAs;
    protected MenuItem miFileExit;
    protected MenuItem miEditDelete;
    protected MenuItem miUserLogin;
    protected MenuItem miUserLogout;
    protected MenuItem miHelpAbout;

    /**
     * Create action listeners
     */
    private void createActionListeners() {
        miFileExit.setOnAction((ea) -> {
            System.exit(0);
        });
        
        miUserLogin.setOnAction((ae) -> {
            try {
                startLogin();
            }
            catch(Exception ex) {
            }
        });
    }

    /**
     * Disable Users.Login
     */
    public void disableLogin() {
        menuBar.getMenus().get(2).getItems().get(0).setDisable(true);
    }
    
    /**
     * Disable Users.Logout
     */
    public void disableLogout() {
        menuBar.getMenus().get(2).getItems().get(1).setDisable(false);
    }
    
    /**
     * Disable menu
     */
    public void disableMenu() {
        menuBar.getMenus().forEach(( m ) -> {
            m.getItems().forEach(( mi ) -> {
                mi.setDisable( true );
            });
        });
    }

    /**
     * Enable Help.About
     */
    public void enableAbout() {
        menuBar.getMenus().get(3).getItems().get(0).setDisable(false);
    }
    
    /**
     * Enable Menu.Exit
     */
    public void enableExit() {
        menuBar.getMenus().get(0).getItems().get(4).setDisable(false);
    }
    
    /**
     * Enable Users.Login
     */
    public void enableLogin() {
        menuBar.getMenus().get(2).getItems().get(0).setDisable(false);
    }
    
    /**
     * Enable Users.Logout
     */
    public void enableLogout() {
        menuBar.getMenus().get(2).getItems().get(1).setDisable(false);
    }
    
    /**
     * Enable menu
     */
    public void enableMenu() {
        menuBar.getMenus().forEach((m) -> {
            m.getItems().forEach((mi) -> {
                mi.setDisable(false);
            });
        });
    }
    
    /**
     * End currently running process
     */
    protected void endProcess() {
        Node node = mainContainer.getCenter();
        mainContainer.getChildren().removeAll(node);
    }
    
    /**
     * Start the MainController
     */
    protected void go() {

        // File menu [0]
        Menu menuFile = new Menu(app.localize("file"));
        miFileNew = new MenuItem(app.localize("new"));          // [0]             
        miFileOpen = new MenuItem(app.localize("open"));        // [1]
        miFileSave = new MenuItem(app.localize("save"));        // [2]
        miFileSaveAs = new MenuItem(app.localize("save_as"));   // [3]
        miFileExit = new MenuItem(app.localize("exit"));        // [4]
        menuFile.getItems().addAll(miFileNew, miFileOpen, miFileSave, miFileSaveAs, miFileExit);

        // Edit menu [1]
        Menu menuEdit = new Menu(app.localize("edit"));
        miEditDelete = new MenuItem(app.localize("delete"));    // [0]
        menuEdit.getItems().addAll(miEditDelete); 

        // User menu [2]
        Menu menuUser = new Menu(app.localize("user"));
        miUserLogin = new MenuItem(app.localize("login"));      // [0]
        miUserLogout = new MenuItem(app.localize("logout"));    // [1]
        menuUser.getItems().addAll(miUserLogin, miUserLogout);

        // Help menu [3]
        Menu menuHelp = new Menu(app.localize("help"));
        miHelpAbout = new MenuItem(app.localize("about"));      // [0]
        menuHelp.getItems().addAll(miHelpAbout);

        createActionListeners();
        menuBar.getMenus().addAll(menuFile, menuEdit, menuUser, menuHelp);
        mainContainer.setTop(menuBar);
        
        if (!app.loggedIn()) {
            disableMenu();
            enableAbout();
            enableExit();
            enableLogin();
            
            try {
                startLogin();
            }
            catch(Exception ex) {
                
            }
        }
        else {
            enableMenu();
            disableLogin();
        }
    }

    /**
     * Inject App object
     * @param _app 
     */
    protected void injectApp(App _app) {
        app = _app;
    }
    
    /**
     * Start the login process
     */
    private void startLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainController.this.getClass().getResource("Login.fxml"));
        Node node = loader.load();
        LoginController login = loader.getController();
        login.injectMainController(this);
        login.injectApp(app);
        mainContainer.setCenter(node);
        login.go();
    }
}

