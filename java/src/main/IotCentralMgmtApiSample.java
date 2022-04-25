import java.io.IOException;
import java.util.Arrays;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.exception.ManagementException;
import com.azure.core.util.Context;
import com.azure.resourcemanager.iotcentral.IotCentralManager;
import com.azure.resourcemanager.iotcentral.models.App;
import com.azure.resourcemanager.iotcentral.models.AppSku;
import com.azure.resourcemanager.iotcentral.models.AppSkuInfo;
import com.azure.resourcemanager.iotcentral.models.AppTemplate;
import com.azure.resourcemanager.iotcentral.models.NetworkAction;
import com.azure.resourcemanager.iotcentral.models.NetworkRuleSetIpRule;
import com.azure.resourcemanager.iotcentral.models.NetworkRuleSets;
import com.azure.resourcemanager.iotcentral.models.Operation;
import com.azure.resourcemanager.iotcentral.models.PublicNetworkAccess;
import com.azure.resourcemanager.iotcentral.models.SystemAssignedServiceIdentity;
import com.azure.resourcemanager.iotcentral.models.SystemAssignedServiceIdentityType;
import com.azure.resourcemanager.iotcentral.models.PrivateEndpointConnection;
import com.azure.resourcemanager.iotcentral.models.PrivateLinkResource;

public class IotCentralMgmtApiSample extends IotCentralMgmtApiSampleBase {
    private static String defaultLocation = "eastus2";

    public IotCentralMgmtApiSample() throws IOException {
        super();
    }

    /** 
     * Lists the available operations in IoT Central Management Plan. 
    **/
    public static void operationsList(IotCentralManager manager) {
        PagedIterable < Operation > operations = manager.operations().list(Context.NONE);

        printHeader("Operations List:");
        for (Operation operation: operations) {
            System.out.println(operation.name());
        }
        System.out.println();
    }

    /** 
     * Lists the app templates in IoT Central. 
    **/
    public static void appsListTemplates(IotCentralManager manager) {
        PagedIterable < AppTemplate > templates = manager.apps().listTemplates(Context.NONE);

        printHeader("IOT Central App Templates:");
        for (AppTemplate template: templates) {
            System.out.println(template.manifestId());
        }
        System.out.println();
    }

    /** 
     * Lists the apps in a given resource group in IoT Central. 
    **/
    public static void appsListByResourceGroup(IotCentralManager manager, String resourceGroupName) {
        PagedIterable < App > apps = manager.apps().listByResourceGroup(resourceGroupName, Context.NONE);

        printHeader("Apps in Resource Group:");
        for (App app: apps) {
            System.out.println("Name:" + app.name() + ", Location: " + app.regionName());
        }
        System.out.println();
    }
    
    /**
     * Retrieves PrivateLink information about the IoT Central application.
     **/
    public static void appsGetPrivateLinks(
        IotCentralManager manager,
        String resourceGroupName,
        String appName) {
            
        PagedIterable < PrivateLinkResource > privateLinkList = manager.privateLinks().list(resourceGroupName, appName);

        printHeader("Printing Available Private Links");
        for (PrivateLinkResource privateLink: privateLinkList) {
            System.out.println(privateLink.name() + " " + privateLink.id());
        }
        System.out.println();
    }

    /**
     * Retrieves PrivateEndpointConnection information about the IoT Central application.
     **/
    public static void appsGetPrivateEndpointConnections(
        IotCentralManager manager,
        String resourceGroupName,
        String appName) {

        PagedIterable < PrivateEndpointConnection > privateEndpointConnectionList = manager.privateEndpointConnections().list(resourceGroupName, appName);
    
        printHeader("Printing Available Private Endpoint Connections");
        for (PrivateEndpointConnection priveateEndPointConncetion: privateEndpointConnectionList) {
            System.out.println(priveateEndPointConncetion.name() + " " + priveateEndPointConncetion.id());
        }
        System.out.println();
    }

    /** 
     * Creates a new IoT Central application using the IoT Central Management Plane APIs
     * and system assigned managed identity.
    **/
    public static String appsCreateOrUpdate(
        IotCentralManager manager,
        String resourceGroupName,
        String appName) throws InterruptedException {

        printHeader("Create/Update App:");

        String randomizedAppName = getRandomizedName(appName);
        manager
            .apps()
            .define(randomizedAppName)
            .withRegion(defaultLocation)
            .withExistingResourceGroup(resourceGroupName)
            .withSku(new AppSkuInfo().withName(AppSku.ST2))
            // Validating System Assigned Managed Identity for app creation.
            .withIdentity(
                new SystemAssignedServiceIdentity()
                .withType(SystemAssignedServiceIdentityType.SYSTEM_ASSIGNED))
            .withDisplayName(randomizedAppName + "-Display")
            .withSubdomain(randomizedAppName)
            .withTemplate("iotc-distribution")
            .create();

        System.out.println("App created successfully.");
        return randomizedAppName;
    }

    /** 
     * Retrieves information about an IoT Central application.
    **/
    public static void appsGet(
        IotCentralManager manager,
        String resourceGroupName,
        String appName) {

        printHeader("Get App:");

        com.azure.resourcemanager.iotcentral.models.App app =
            manager.apps().getByResourceGroupWithResponse(resourceGroupName, appName, Context.NONE).getValue();

        boolean hasNetworkingRules = app.networkRuleSets().ipRules().isEmpty();
        if(hasNetworkingRules) {
            System.out.println("Name:" + app.name() + ", Display Name: " + app.displayName() +", Location: " + app.regionName() + ", " + app.toString());
        } else {
            String filterName = app.networkRuleSets().ipRules().get(0).filterName();
            String ipMask = app.networkRuleSets().ipRules().get(0).ipMask();
            System.out.println("Name:" + app.name() + ", Display Name: " + app.displayName() +", Location: " + app.regionName() + ", IP Rules: " + filterName + " : " + ipMask +  ", " + app.toString());
        }
        System.out.println();
    }
    
    /** 
     * Updates application details.
    **/
    public static void appsUpdate(
        IotCentralManager manager,
        String resourceGroupName,
        String appName) throws InterruptedException {

        printHeader("Update App:");

        NetworkRuleSets networkRuleSets = setNetworkRules();
        App resource =
            manager.apps().getByResourceGroupWithResponse(resourceGroupName, appName, Context.NONE).getValue();
        resource
            .update()
            .withIdentity(
                new SystemAssignedServiceIdentity().withType(SystemAssignedServiceIdentityType.SYSTEM_ASSIGNED))
            .withDisplayName(appName + "- new display name")
            .withPublicNetworkAccess(PublicNetworkAccess.ENABLED)
            .withNetworkRuleSets(networkRuleSets)
            .apply();

        System.out.println("App updated successfully.\n");
    }

    /** 
     * Deletes the IoT Central application.
    **/
    public static void appsDelete(
        IotCentralManager manager,
        String resourceGroupName,
        String appName) throws InterruptedException {

        printHeader("Delete App:");

        manager.apps().delete(resourceGroupName, appName, Context.NONE);

        Thread.sleep(10000);
        System.out.println("App deleted!\n");
    }

    /** 
     * Validates error when using geography instead of region for location when creating
     * a new IoT Central application. This change was introduced in 2021-06-01 APIs.
    **/
    public static void appsCreateOrUpdateWithInvalidGeoLocation(
        IotCentralManager manager,
        String resourceGroupName,
        String appName) {

        printHeader("Validating Exceptions:");

        String invalidGeoLocationForIotcApp = "unitedstates";

        try {
            manager
                .apps()
                .define(getRandomizedName(appName))
                .withRegion(invalidGeoLocationForIotcApp)
                .withExistingResourceGroup(resourceGroupName)
                .withSku(new AppSkuInfo().withName(AppSku.ST2))
                .withIdentity(
                    new SystemAssignedServiceIdentity()
                    .withType(SystemAssignedServiceIdentityType.SYSTEM_ASSIGNED))
                .withDisplayName(appName + "-Display")
                .withSubdomain(appName)
                .withTemplate("iotc-distribution")
                .create();
        } catch (ManagementException mex) {
            if (mex.getValue().getMessage().contains("No registered resource provider found for location 'unitedstates'")) {
                System.out.println("Validated error with use of geo location.");
            } else {
                System.err.println("ERROR: Expected exception thrown for use of geo location!");
            }
        }
    }
    
    /** 
     * Validates error when using an invalid SKU. This change (lack of support for S1 sku) was introduced in 2021-06-01 APIs.
    **/
    public static void appsCreateOrUpdateWithInvalidSku(
        IotCentralManager manager,
        String resourceGroupName,
        String appName,
        String skuName) {

        printHeader("Validating Exceptions:");

        AppSku invalidSkuForIotcApp = AppSku.fromString(skuName);
        try {
            manager
                .apps()
                .define(getRandomizedName(appName))
                .withRegion(defaultLocation)
                .withExistingResourceGroup(resourceGroupName)
                .withSku(new AppSkuInfo().withName(invalidSkuForIotcApp))
                .withIdentity(
                    new SystemAssignedServiceIdentity()
                    .withType(SystemAssignedServiceIdentityType.SYSTEM_ASSIGNED))
                .withDisplayName(appName + "-Display")
                .withSubdomain(appName)
                .withTemplate("iotc-distribution")
                .create();
        } catch (ManagementException mex) {
            if (mex.getValue().getMessage().contains("The sku S1 is invalid, allowed skus are ST0, ST1, ST2")) {
                System.out.println(String.format("Validated error with invalid sku '%s'.", skuName));
            } else {
                System.err.println("ERROR: Expected exception thrown for invalid sku!");
            }
        }
    }

    /** 
     * Validates error when using invalid inputs when creating IoT Central application.
    **/
    public static void appsCreateOrUpdateThrowsWithSubscriptionLessAppAndF1Sku(
        IotCentralManager manager,
        String resourceGroupName,
        String appName) {

        printHeader("Validating Exceptions:");

        AppSku invalidSkuForIotcApp = com.azure.resourcemanager.iotcentral.models.AppSku.fromString("F1");
        try {
            manager
                .apps()
                .define(getRandomizedName(appName))
                .withRegion(defaultLocation)
                .withExistingResourceGroup(resourceGroupName)
                .withSku(new AppSkuInfo().withName(invalidSkuForIotcApp))
                .withIdentity(
                    new SystemAssignedServiceIdentity()
                    .withType(com.azure.resourcemanager.iotcentral.models.SystemAssignedServiceIdentityType.SYSTEM_ASSIGNED))
                .withDisplayName(appName + "-Display")
                .withSubdomain(appName)
                .withTemplate("iotc-distribution")
                .create();
        } catch (ManagementException mex) {
            if (mex.getValue().getMessage().contains("Cannot create a subscription less application with SKU F1")) {
                System.out.println("Validated error with F1 sku.");
            } else {
                System.err.println("ERROR: Expected correct exception for F1 sku!");
            }
        }
    }

    private static NetworkRuleSets setNetworkRules() {
        NetworkRuleSetIpRule ipRules = new NetworkRuleSetIpRule()
            .withFilterName("Localhost")
            .withIpMask("127.0.0.1");
        NetworkRuleSets setNetworkRules = new NetworkRuleSets()
            .withApplyToDevices(true)
            .withApplyToIoTCentral(false)
            .withDefaultAction(NetworkAction.ALLOW)
            .withIpRules(Arrays.asList(ipRules));
        return setNetworkRules;
    }

    private static void printHeader(String headerName) {
        System.out.println("------------------");
        System.out.println(headerName);
        System.out.println("------------------");
        System.out.println();
    }

    private static String getRandomizedName(String name) {
        String randomizedName = name + System.currentTimeMillis() / 1000L;
        System.out.println("randomizedName: " + randomizedName);
        return randomizedName;
    }
}