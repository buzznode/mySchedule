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

import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import myschedule.service.Logging;

/**
 * @author bradd
 * @version 0.5.0
 */
public class LoginController extends AnchorPane {

    @FXML AnchorPane frmLogin;
    @FXML Button btnCancel;
    @FXML Button btnLogin;
    @FXML Label lblFeedback;
    @FXML Label lblPassword;
    @FXML Label lblUsername;
    @FXML TextField txtPassword;
    @FXML TextField txtUsername;

    private App app;
    private MainController main;

    /**
     * Cancel login 
     */
    private void cancelLogin() {

        app.log.write(Level.INFO, "User cancelled login attempt");
        main.endProcess();
    }
    
    /**
     *  Create action event listeners
     */
    private void createActionListeners() {
        btnCancel.setOnAction((ea) -> {
            cancelLogin();
        });
        
        btnLogin.setOnAction((ea) -> {
            userLogin();
        });
        
        txtPassword.setOnMouseClicked((me) -> {
            txtPassword.setText("");
            lblFeedback.setText("");
        });
        
        txtUsername.setOnMouseClicked((me) -> {
            txtUsername.setText("");
            lblFeedback.setText("");
        });
    }

    /**
     * Begin login process
     */
    public void go() {
        app.log.write(Level.INFO, "Attempting login...");
        createActionListeners();
        txtUsername.setPromptText("username");
        txtPassword.setPromptText("password");
        lblFeedback.setText("");
        app.common.loadUsers();
    }

    /**
     * Inject App object
     * @param _app 
     */
    public void injectApp(App _app) {
        this.app = _app;
    }

    /**
     * Inject MainController object
     * @param _main 
     */
    public void injectMainController(MainController _main) {
        main = _main;
    }
    
    /**
     * Process login
     */
    private void userLogin() {
        if (!app.loggedIn()) {
            String user = txtUsername.getText();
            String password = txtPassword.getText();
            
            if (app.common.validateUser(user, password)) {
                app.userName(user);
                app.loggedIn(true);
                main.endProcess();
                app.log.write(Level.INFO, user + " has logged in");
            }
            else {
                lblFeedback.setText("Invalid username / password. Please try again.");
            }
        }
        else {
            app.log.write(Level.INFO, "already logged in");
        }
    }
}
