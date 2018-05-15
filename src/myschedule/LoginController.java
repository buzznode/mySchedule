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
    private MainController mainController;

    private void cancelLogin() {
        app.log.write(Level.INFO, "User cancelled login attemp");
        this.mainController.endProcess();
    }
    
    /**
     *  Define action event listeners
     */
    private void createActionListeners() {
        btnCancel.setOnAction((ea) -> {
            cancelLogin();
        });
        
        btnLogin.setOnAction((ea) -> {
            userLogin();
        });
    }

    public void go() {
        createActionListeners();
        txtUsername.setPromptText("username");
        txtPassword.setPromptText("password");
        lblFeedback.setText("");
        app.common.USERS.clear();
        app.common.loadUsers();
        
    }

    /**
     * @param _app 
     */
    public void injectApp(App _app) {
        this.app = _app;
    }

    /**
     * @param _mainController 
     */
    public void injectMainController(MainController _mainController) {
        this.mainController = _mainController;
        System.out.println("MainController: " + mainController);
    }
    
    private void userLogin() {
        if (!app.loggedIn()) {
            app.log.write(Level.INFO, "not already logged in");
        }
        else {
            app.log.write(Level.INFO, "already logged in");
        }
    }
    
//    public void processLogin() {
//        if (! Authenticate.validate( txtUsername.getText(), txtPassword.getText() )) {
//            lblFeedback.setText("Invalid Username / Password combination");
//            LOGGER.log(Level.INFO, "Invalid username: ({0}) / password: ({1}) combination", new Object[]{ txtUsername.getText(), txtPassword.getText() } );
//        }
//        else {
//            lblFeedback.setText("Looks good!!! Congrats!");
//        }
//    }
}
