import com.azure.resourcemanager.iotcentral.IotCentralManager;

public class Main {
    // NOTE: Replace with your own resource group name.
    // Make sure the service principal has "Contributor" permissions on the resource group.
    private static final String RESOURCE_GROUP = "gTest1_26Oct2021";
    
    // NOTE: Replace with your own IoT Central App Name.
    private static final String APP_NAME = "gaviswanapp1";

    public static void main(String[] args) throws Exception {
        runIotcSamples();
    }

    private static void runIotcSamples() throws Exception {
        // Create the IoT Central Manager to operate the management plane APIs.
        IotCentralManager manager = IotCentralMgmtApiSample.CreateIoTCentralManager();

        // Test listing operations.
        IotCentralMgmtApiSample.operationsList(manager);
        
        // Test listing templates and apps inside resource group.
        IotCentralMgmtApiSample.appsListTemplates(manager);
        IotCentralMgmtApiSample.appsListByResourceGroup(manager, RESOURCE_GROUP);

        // Test App Creation.
        String createdAppName = IotCentralMgmtApiSample.appsCreateOrUpdate(manager, RESOURCE_GROUP, APP_NAME);

        // Get app details and update it.
        IotCentralMgmtApiSample.appsGet(manager, RESOURCE_GROUP, createdAppName);
        IotCentralMgmtApiSample.appsUpdate(manager, RESOURCE_GROUP, createdAppName);

        // Delete the application.
        IotCentralMgmtApiSample.appsDelete(manager, RESOURCE_GROUP, createdAppName);

        // Validate expected exceptions.
        IotCentralMgmtApiSample.appsCreateOrUpdateWithInvalidGeoLocation(manager, RESOURCE_GROUP, APP_NAME);
        IotCentralMgmtApiSample.appsCreateOrUpdateWithInvalidSku(manager, RESOURCE_GROUP, APP_NAME, "S1");
        IotCentralMgmtApiSample.appsCreateOrUpdateThrowsWithSubscriptionLessAppAndF1Sku(manager, RESOURCE_GROUP, APP_NAME);
    }
}