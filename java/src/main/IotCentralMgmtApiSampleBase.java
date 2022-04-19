import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.iotcentral.IotCentralManager;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;


public class IotCentralMgmtApiSampleBase {
    protected static final String AZURE_CLIENT_ID = "Azure Client ID";
    protected static final String AZURE_CLIENT_SECRET = "Azure Client Secret";
    protected static final String AZURE_TENANT_ID = "Azure Tenant ID";
    protected static final String AZURE_SUBSCRIPTION_ID = "Azure Subscription";

    protected static IotCentralManager IotCManager;
    protected static DeviceCodeCredential credential;
    protected static AzureProfile profile;

    static {
        authenticateToAzure();
    }

    // Creates a manager to handle IOTC applications
    protected static IotCentralManager CreateIoTCentralManager() {
        IotCManager = IotCentralManager.authenticate(credential, profile);
        return IotCManager;
    }

    // Authentication for general IOTC service
    private static void authenticateToAzure() {
        profile = new AzureProfile(AZURE_TENANT_ID, AZURE_SUBSCRIPTION_ID, AzureEnvironment.AZURE);
        credential = new DeviceCodeCredentialBuilder()
            .tenantId(AZURE_TENANT_ID)
            .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
            .build();
    }
}
