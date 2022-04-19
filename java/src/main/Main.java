import com.azure.resourcemanager.iotcentral.IotCentralManager;

public class Main {
    // NOTE: Replace with your own resource group name.
    // Make sure the service principal has "Contributor" permissions on the resource group.
    private static final String RESOURCE_GROUP = "myResourceGroup";
    
    // NOTE: Replace with your own IoT Central App Name.
    private static final String APP_NAME = "sample-app";

    public static void main(String[] args) throws Exception {
        runIotcSamples();
    }

    private static void runIotcSamples() throws Exception {
        // Create the IoT Central Manager to operate the management plane APIs.
        IotCentralManager manager = IotCentralMgmtApiSample.CreateIoTCentralManager();
        // List available operations.
        IotCentralMgmtApiSample.operationsList(manager);
        // List available templates.
        IotCentralMgmtApiSample.appsListTemplates(manager);
        // List all applications in resource group.
        IotCentralMgmtApiSample.appsListByResourceGroup(manager, RESOURCE_GROUP);
        // Test App Creation.
        String createdAppName = IotCentralMgmtApiSample.appsCreateOrUpdate(manager, RESOURCE_GROUP, APP_NAME);
        // Get app details and show info.
        IotCentralMgmtApiSample.appsGet(manager, RESOURCE_GROUP, createdAppName);
        // Updates the application with a new display name, location, and networking properties
        IotCentralMgmtApiSample.appsUpdate(manager, RESOURCE_GROUP, createdAppName);
        // Get app details and show info.
        IotCentralMgmtApiSample.appsGet(manager, RESOURCE_GROUP, createdAppName);
        // Get private link details and show the info.
        IotCentralMgmtApiSample.appsGetPrivateLinks(manager, RESOURCE_GROUP, createdAppName);
        // Get private endpoint connection details and show the info.
        IotCentralMgmtApiSample.appsGetPrivateEndpointConnections(manager, RESOURCE_GROUP, createdAppName);
        // Delete the application.
        // IotCentralMgmtApiSample.appsDelete(manager, RESOURCE_GROUP, createdAppName);

        // Validate expected exceptions.
        IotCentralMgmtApiSample.appsCreateOrUpdateWithInvalidGeoLocation(manager, RESOURCE_GROUP, APP_NAME);
        IotCentralMgmtApiSample.appsCreateOrUpdateWithInvalidSku(manager, RESOURCE_GROUP, APP_NAME, "S1");
        IotCentralMgmtApiSample.appsCreateOrUpdateThrowsWithSubscriptionLessAppAndF1Sku(manager, RESOURCE_GROUP, APP_NAME);
    }
}