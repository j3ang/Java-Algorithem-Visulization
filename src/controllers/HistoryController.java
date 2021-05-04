package controllers;

import application.Main;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.ActivityModel;
import models.DaoModel;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;


/**
 * The type History controller.
 */
public class HistoryController  extends ConfigurationController implements Initializable {

    DaoModel dao = new DaoModel();

    @FXML
    TableView activitiesTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
        setUpTable();
        showActivities();
    }

    /**
     * Show users.
     */
    public void showActivities(){

        ObservableList<ActivityModel> activityModels = getActivitiesList();


            for ( int i = 0; i <  activitiesTable.getColumns().size(); i++ ){
              TableColumn tCol =  (TableColumn)activitiesTable.getColumns().get(i);
               switch (i){
                   case 0:
                       System.out.println("setting PropertyValueFactory: "  + tCol.getText() );
                       tCol.setCellValueFactory(new PropertyValueFactory<ActivityModel, Integer>(tCol.getText()));
                       break;
                   case 1:
                       System.out.println("setting PropertyValueFactory: "  + tCol.getText() );
                       tCol.setCellValueFactory(new PropertyValueFactory<ActivityModel, String>(tCol.getText()));
                       break;
                   default:
                       tCol.setCellValueFactory(new PropertyValueFactory<ActivityModel, String>(tCol.getText()));
                       break;

               }
            }

            activitiesTable.setItems(activityModels);
            // Show latest activity at top
            TableColumn dtCreatedCol = (TableColumn) activitiesTable.getColumns().get(5);
            dtCreatedCol.setComparator(dtCreatedCol.getComparator().reversed());
            activitiesTable.getSortOrder().add(dtCreatedCol);

            System.out.println("HistoryController.showActivities()");
    }

    private ObservableList<ActivityModel> getActivitiesList() {
        ObservableList<ActivityModel> activitiesList = FXCollections.observableArrayList();

        ActivityModel activityModel =  new ActivityModel();
        Vector activities = activityModel.getActivitiesByUserID(Main.userModelLoggedIn.getUser_id());



        for (int i = 0; i < activities.size(); i++){
            System.out.println("size:" + activities.get(i));
            String[] parsedActivities = activities.get(i).toString()
                    .replaceAll("\\[", "")
                    .replaceAll("\\]","")
                    .split(",");

            String logJsonString = "{" +
                    "\"act_id\":"+ parsedActivities[0]  +"," +
                    "\"act_sort_task\":\""+  parsedActivities[2]  +"\"," +
                    "\"act_sort_size\":\"" + parsedActivities[3] + "\"," +
                    "\"act_sort_speed\":\"" + parsedActivities[4] + "\"," +
                    "\"act_sort_duration\":\"" + parsedActivities[5] + "\"," +
                    "\"act_dt_created\":\"" + parsedActivities[6] + "\""
                    + "}";

            // Gson helper
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            ActivityModel log = gson.fromJson(logJsonString, ActivityModel.class); // Activity object created form Json
            activitiesList.add(log); // Populate the user Observable list

            // print to console
            // Print to console
            logJsonString = gson.toJson(log);
            System.out.println(logJsonString);

        }

        return activitiesList;

    }

    public void setUpTable(){
        Vector<String> tableCols = dao.getTableCols(dao.getTableName("activities"));
        System.out.println("Setting up activities table columns... ");
        for( int i = 0 ; i < tableCols.size(); i++ ){

            if ( ! tableCols.get(i).equals("user_id") ){ // exclude user id
                TableColumn tCol = new TableColumn(tableCols.get(i));
                // set col width
                switch ( i ){
                    case 0:
                    case 1: tCol.setPrefWidth(100); break;
                    case 2: tCol.setPrefWidth(250); break;
                    case 3:
                    case 4:
                    case 5:
                    case 6: tCol.setPrefWidth(150); break;
                }
                activitiesTable.getColumns().add(tCol);
            }

        }
    }

    /**
     * History back btn action.
     *
     * @param evt the evt
     */
    public void historyBackBtnAction(ActionEvent evt) {
        Main.loadScene(evt, "configuration", false);
    }

}
