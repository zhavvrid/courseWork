package com.example.client;

import com.example.client.Enums.RequestType;
import com.example.client.Models.Entities.FixedAsset;
import com.example.client.Models.TCP.Request;
import com.example.client.Utility.ClientSocket;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.PrintWriter;

public class EditAssetController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField inventoryNumberField;
    @FXML
    private TextField purchaseDateField;
    @FXML
    private TextField initialCostField;
    @FXML
    private TextField usefulLifeField;
    @FXML
    private TextField residualValueField;
    @FXML
    private TextField depreciationMethodField;
    @FXML
    private TextField categoryField;


        // Existing fields
        private FixedAsset asset;
        private FixedAssetController fixedAssetController; // Add this field

        public void setAsset(FixedAsset asset, FixedAssetController controller) {
            this.asset = asset;
            this.fixedAssetController = controller; // Set the controller
            // Populate fields with current asset values
            nameField.setText(asset.getName());
            inventoryNumberField.setText(asset.getInventoryNumber());
            purchaseDateField.setText(asset.getPurchaseDate());
            initialCostField.setText(String.valueOf(asset.getInitialCost()));
            usefulLifeField.setText(String.valueOf(asset.getUsefulLife()));
            residualValueField.setText(String.valueOf(asset.getResidualValue()));
            depreciationMethodField.setText(asset.getDepreciationMethod());
            categoryField.setText(asset.getCategory());
        }

        @FXML
        private void saveAsset() {
            // Update asset with new values
            asset.setName(nameField.getText());
            asset.setInventoryNumber(inventoryNumberField.getText());
            asset.setPurchaseDate(purchaseDateField.getText());
            asset.setInitialCost(Double.parseDouble(initialCostField.getText()));
            asset.setUsefulLife(Integer.parseInt(usefulLifeField.getText()));
            asset.setResidualValue(Double.parseDouble(residualValueField.getText()));
            asset.setDepreciationMethod(depreciationMethodField.getText());
            asset.setCategory(categoryField.getText());

            // Send updated asset to the server
            sendUpdateRequest(asset);

            // Call loadAssets to refresh the table in the FixedAssetController
            fixedAssetController.loadAssets(); // Refresh assets in the main table


            // Close the edit window
            ((Stage) nameField.getScene().getWindow()).close();
        }

        @FXML
        private void cancel() {
            ((Stage) nameField.getScene().getWindow()).close();
        }

        private void sendUpdateRequest(FixedAsset asset) {
            Request request = new Request();
            request.setRequestType(RequestType.UPDATE_ASSET);
            request.setMessage(new Gson().toJson(asset));

            PrintWriter out = ClientSocket.getInstance().getOut();
            out.println(new Gson().toJson(request));
            out.flush();
        }
    }
