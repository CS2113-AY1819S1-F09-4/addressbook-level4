package seedu.recruit.ui;

import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import seedu.recruit.commons.core.Config;
import seedu.recruit.commons.core.GuiSettings;
import seedu.recruit.commons.core.LogsCenter;
import seedu.recruit.commons.events.ui.ExitAppRequestEvent;
import seedu.recruit.commons.events.ui.ShowCandidateBookRequestEvent;
import seedu.recruit.commons.events.ui.ShowCompanyBookRequestEvent;
import seedu.recruit.commons.events.ui.ShowEmailPreviewEvent;
import seedu.recruit.commons.events.ui.ShowHelpRequestEvent;
import seedu.recruit.logic.Logic;
import seedu.recruit.model.UserPrefs;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private static String currentBook = "companyBook";

    private static CandidateDetailsPanel candidateDetailsPanel;

    private static CompanyJobDetailsPanel companyJobDetailsPanel;

    private static ShortlistPanel shortlistPanel;

    private static StackPane staticPanelViewPlaceholder;

    private static Logic logic;

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;

    // Independent Ui parts residing in this Ui container
    private BrowserPanel browserPanel;
    private Config config;
    private UserPrefs prefs;
    private HelpWindow helpWindow;
    private EmailPreview emailPreview;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private MenuItem companyBook;

    @FXML
    private MenuItem candidateBook;

    @FXML
    private StackPane panelViewPlaceholder;

    @FXML
    private StackPane shortlistPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    public MainWindow(Stage primaryStage, Config config, UserPrefs prefs, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.config = config;
        this.prefs = prefs;

        // Configure the UI
        setTitle(config.getAppTitle());
        setWindowDefaultSize(prefs);

        setAccelerators();
        registerAsAnEventHandler(this);

        helpWindow = new HelpWindow();
        emailPreview = new EmailPreview();

        staticPanelViewPlaceholder = panelViewPlaceholder;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * RecruitBook's default panelViewPlaceHolder shows the list of
     * companies and their list of jobs, and at the same time
     * fills up all the other placeholders of this window.
     */
    void fillInnerParts() {
        panelViewPlaceholder.getChildren().add(getCompanyJobDetailsPanel().getRoot());

        ResultDisplay resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(
                prefs.getCandidateBookFilePath(), prefs.getCompanyBookFilePath(),
                logic.getFilteredPersonList().size(), logic.getFilteredCompanyList().size());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(logic);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    void hide() {
        primaryStage.hide();
    }

    private void setTitle(String appTitle) {
        primaryStage.setTitle(appTitle);
    }

    /**
     * Sets the default size based on user preferences.
     */
    private void setWindowDefaultSize(UserPrefs prefs) {
        primaryStage.setHeight(prefs.getGuiSettings().getWindowHeight());
        primaryStage.setWidth(prefs.getGuiSettings().getWindowWidth());
        if (prefs.getGuiSettings().getWindowCoordinates() != null) {
            primaryStage.setX(prefs.getGuiSettings().getWindowCoordinates().getX());
            primaryStage.setY(prefs.getGuiSettings().getWindowCoordinates().getY());
        }
    }

    /**
     * Returns the current size and the position of the main Window.
     */
    GuiSettings getCurrentGuiSetting() {
        return new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
    }

    /**
     * Handles the menu Switch Book from Company Book
     * to Candidate Book event, {@code event}.
     */
    @FXML
    public void handleChangeToCandidateDetailsPanel() {
        candidateBook.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchToCandidateBook();
            }
        });
    }

    /**
     * Handles the menu Switch Book from Candidate Book
     * to Company Book event, {@code event}.
     */
    @FXML
    public void handleChangeToCompanyJobDetailsPanel() {
        companyBook.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switchToCompanyBook();
            }
        });
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    /**
     * Opens the email preview window
     */
    public void handleEmailPreview(String preview) {
        emailPreview.setEmailPreview(preview);

        if (!emailPreview.isShowing()) {
            emailPreview.show();
        } else {
            emailPreview.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        raise(new ExitAppRequestEvent());
    }

    public static CandidateDetailsPanel getCandidateDetailsPanel() {
        candidateDetailsPanel = new CandidateDetailsPanel(logic.getFilteredPersonList());
        return candidateDetailsPanel;
    }

    public static CompanyJobDetailsPanel getCompanyJobDetailsPanel() {
        companyJobDetailsPanel = new CompanyJobDetailsPanel(logic.getFilteredCompanyList(),
                logic.getFilteredCompanyJobList());
        return companyJobDetailsPanel;
    }

    public static ShortlistPanel getShortlistPanel() {
        shortlistPanel = new ShortlistPanel(logic.getFilteredPersonList(), logic.getFilteredCompanyList(),
                logic.getFilteredCompanyJobList());
        return shortlistPanel;
    }

    public static String getDisplayedBook() {
        if (currentBook.contentEquals("companyBook")) {
            return "companyBook";
        } else if (currentBook.contentEquals("candidateBook")) {
            return "candidateBook";
        } else {
            return "Error in Switching Book";
        }
    }

    public static StackPane getStaticPanelViewPlaceholder() {
        return staticPanelViewPlaceholder;
    }

    /**
     * Switches the view on panelViewPlaceholder
     * from Company Book to Candidate Book.
     */
    public static void switchToCandidateBook() {
        if (!getStaticPanelViewPlaceholder().getChildren().isEmpty()) {
            getStaticPanelViewPlaceholder().getChildren().remove(0);
            getStaticPanelViewPlaceholder().getChildren().add(getCandidateDetailsPanel().getRoot());
            currentBook = "candidateBook";
        }
    }

    /**
     * Switches the view on panelViewPlaceholder
     * from Candidate Book to Company Book.
     */
    public static void switchToCompanyBook() {
        if (!getStaticPanelViewPlaceholder().getChildren().isEmpty()) {
            getStaticPanelViewPlaceholder().getChildren().remove(0);
            getStaticPanelViewPlaceholder().getChildren().add(getCompanyJobDetailsPanel().getRoot());
            currentBook = "companyBook";
        }
    }

    /**
     * Switches the view on panelViewPlaceholder
     * to the last viewed book.
     */
    public static void switchToLastViewedBook() {
        switch (currentBook) {
        case "companyBook":
            if (!getStaticPanelViewPlaceholder().getChildren().isEmpty()) {
                getStaticPanelViewPlaceholder().getChildren().remove(0);
                getStaticPanelViewPlaceholder().getChildren().add(getCompanyJobDetailsPanel().getRoot());
            }
            break;

        case "candidateBook":
            if (!getStaticPanelViewPlaceholder().getChildren().isEmpty()) {
                getStaticPanelViewPlaceholder().getChildren().remove(0);
                getStaticPanelViewPlaceholder().getChildren().add(getCandidateDetailsPanel().getRoot());
            }
            break;
        default:
        }
    }

    /**
     * Switches the view on panelViewPlaceholder
     * from Candidate/Company Book to carry out the Shortlist command.
     */
    public static void switchToShortlistPanel() {
        if (!getStaticPanelViewPlaceholder().getChildren().isEmpty()) {
            getStaticPanelViewPlaceholder().getChildren().remove(0);
            getStaticPanelViewPlaceholder().getChildren().add(getShortlistPanel().getRoot());
        }
    }

    void releaseResources() {
        browserPanel.freeResources();
    }

    @Subscribe
    private void handleShowHelpEvent(ShowHelpRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleHelp();
    }

    @Subscribe
    private void handleEmailPreviewEvent(ShowEmailPreviewEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleEmailPreview(event.getEmailPreview());
    }

    @Subscribe
    private void handleShowCandidateBookEvent(ShowCandidateBookRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleChangeToCandidateDetailsPanel();
    }

    @Subscribe
    private void handleShowCompanyBookEvent(ShowCompanyBookRequestEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        handleChangeToCompanyJobDetailsPanel();
    }
}
