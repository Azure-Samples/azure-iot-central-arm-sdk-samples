package sample;

import rx.Observable;
import com.microsoft.rest.RestClient;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.AzureResponseBuilder;
import com.microsoft.azure.serializer.AzureJacksonAdapter;
import com.microsoft.azure.credentials.UserTokenCredentials;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import com.microsoft.azure.management.iotcentral.v2018_09_01.App;
import com.microsoft.azure.management.iotcentral.v2018_09_01.AppPatch;
import com.microsoft.azure.management.iotcentral.v2018_09_01.AppSku;
import com.microsoft.azure.management.iotcentral.v2018_09_01.AppSkuInfo;
import com.microsoft.azure.management.iotcentral.v2018_09_01.Operation;
import com.microsoft.azure.management.iotcentral.v2018_09_01.OperationInputs;
import com.microsoft.azure.management.iotcentral.v2018_09_01.AppAvailabilityInfo;
import com.microsoft.azure.management.iotcentral.v2018_09_01.implementation.AppInner;
import com.microsoft.azure.management.iotcentral.v2018_09_01.implementation.AppsInner;
import com.microsoft.azure.management.iotcentral.v2018_09_01.implementation.AppTemplateInner;
import com.microsoft.azure.management.iotcentral.v2018_09_01.implementation.OperationInner;
import com.microsoft.azure.management.iotcentral.v2018_09_01.implementation.OperationsInner;
import com.microsoft.azure.management.iotcentral.v2018_09_01.implementation.IoTCentralManager;
import com.microsoft.azure.management.iotcentral.v2018_09_01.implementation.IotCentralClientImpl;

public class IoTCentralExample {
    public static void main(String[] args) {
        String clientID = ""; // get it from service principal in Azure AD
        String domain = ""; // tenant id
        // please make sure this user has the following permission:
        // Either Owner or Contributor to the sub id for IAM
        // Owner of the the Service Principal (application)
        // please make sure your Service Principal has the following setting:
        // API Permissions: Given ReadWrite.All access for Microsoft IoT Central
        // API Permissions: Given access for Windows Azure Service Management API
        // Note: You would also need to grant admin consent for Default Directory through Enterprise applications tab (requires admin access)
        // Manifest: allowPublicClient should set to true so that we can access the API publicly
        String username = "";
        String password = ""; // for newly created user, make sure to try and login first so that you can change the password to your own instead of generated one
        String subscriptionId = "";
        AzureTokenCredentials azureTokenCredentials = new UserTokenCredentials(
            clientID, domain, username, password, AzureEnvironment.AZURE);
        RestClient restClient = new RestClient.Builder()
            .withBaseUrl(azureTokenCredentials.environment(), AzureEnvironment.Endpoint.RESOURCE_MANAGER)
            .withCredentials(azureTokenCredentials)
            .withSerializerAdapter(new AzureJacksonAdapter())
            .withResponseBuilderFactory(new AzureResponseBuilder.Factory())
            .build();
        IoTCentralManager iotcentralManager = IoTCentralManager.authenticate(restClient, subscriptionId);

        String resourceName = "my-app-name";
        String resourceGroupName = "myResourceGroup";
        OperationInputs operationInputs = new OperationInputs().withName(resourceName);
        Observable<AppAvailabilityInfo> checkNameResult = iotcentralManager.apps().checkNameAvailabilityAsync(operationInputs);
        checkNameResult.subscribe(word->System.out.println("name is available: " + word.nameAvailable())); // check if the resource name is available

        AppInner app = new AppInner();
        app.withDisplayName(resourceName);
        app.withSubdomain(resourceName);
        app.withLocation("unitedstates");
        AppSku appSku = new AppSku().fromString("ST2");
        AppSkuInfo appSkuInfo = new AppSkuInfo().withName(appSku);
        app.withSku(appSkuInfo);

        IotCentralClientImpl iotCentralClientImpl = new IotCentralClientImpl(restClient);
        iotCentralClientImpl.withSubscriptionId(subscriptionId);
        AppsInner appsInner = new AppsInner(restClient.retrofit(), iotCentralClientImpl);

        // create app
        AppInner createdApp = appsInner.createOrUpdate(resourceGroupName, resourceName, app);
        System.out.println("Newly created app id is " + createdApp.applicationId());

        // get app
        AppInner getResult = appsInner.getByResourceGroup(resourceGroupName, resourceName);
        System.out.println("its sku is " + getResult.sku().name());

        AppPatch appPatch = new AppPatch();
        appPatch.withDisplayName(resourceName + "-new-name");

        // update app
        appsInner.update(resourceGroupName, resourceName, appPatch);

        // list all the apps under the resource group
        PagedList<AppInner> apps = appsInner.listByResourceGroup(resourceGroupName);
        for(AppInner eachApp : apps) {
            System.out.println(eachApp.displayName());
        }

        // list all the operations for iotc
        OperationsInner operationsInner = new OperationsInner(restClient.retrofit(), iotCentralClientImpl);
        PagedList<OperationInner> operations = operationsInner.list();
        for(OperationInner eachOperation : operations) {
            System.out.println(eachOperation.name());
        }

        // list all the app templates in iotc
        PagedList<AppTemplateInner> appTemplates = appsInner.listTemplates();
        for(AppTemplateInner eachAppTemplate : appTemplates) {
            System.out.println(eachAppTemplate.name());
        }
        
        // delete app
        // appsInner.delete(resourceGroupName, resourceName);
    }
}
